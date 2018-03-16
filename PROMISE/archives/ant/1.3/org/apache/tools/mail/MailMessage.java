package org.apache.tools.mail;

import java.io.*;
import java.net.*;
import java.util.*;

/** 
 * A class to help send SMTP email.
 * This class is an improvement on the sun.net.smtp.SmtpClient class 
 * found in the JDK.  This version has extra functionality, and can be used
 * with JVMs that did not extend from the JDK.  It's not as robust as
 * the JavaMail Standard Extension classes, but it's easier to use and 
 * easier to install, and has an Open Source license.
 * <p>
 * It can be used like this:
 * <blockquote><pre>
 * String from = "Mail Message Servlet &lt;MailMessage@server.com&gt;";
 * String to = "to@you.com";
 * String cc1 = "cc1@you.com";
 * String cc2 = "cc2@you.com";
 * String bcc = "bcc@you.com";
 * &nbsp;
 * MailMessage msg = new MailMessage(mailhost);
 * msg.from(from);
 * msg.to(to);
 * msg.cc(cc1);
 * msg.cc(cc2);
 * msg.bcc(bcc);
 * msg.setSubject("Test subject");
 * PrintStream out = msg.getPrintStream();
 * &nbsp;
 * Enumeration enum = req.getParameterNames();
 * while (enum.hasMoreElements()) {
 *   String name = (String)enum.nextElement();
 *   String value = req.getParameter(name);
 *   out.println(name + " = " + value);
 * }
 * &nbsp;
 * msg.sendAndClose();
 * </pre></blockquote>
 * <p>
 * Be sure to set the from address, then set the recepient 
 * addresses, then set the subject and other headers, then get the 
 * PrintStream, then write the message, and finally send and close.
 * The class does minimal error checking internally; it counts on the mail
 * host to complain if there's any malformatted input or out of order 
 * execution.  
 * <p>
 * An attachment mechanism based on RFC 1521 could be implemented on top of
 * this class.  In the meanwhile, JavaMail is the best solution for sending
 * email with attachments.
 * <p>
 * Still to do:
 * <ul>
 * <li>Figure out how to close the connection in case of error
 * </ul>
 *
 * @author Jason Hunter
 * @version 1.1, 2000/03/19, added angle brackets to address, helps some servers
 * version 1.0, 1999/12/29
 */
public class MailMessage {

  String host;
  String from;
  Vector to, cc;
  Hashtable headers;
  MailPrintStream out;
  SmtpResponseReader in;
  Socket socket;

  /**
   * Constructs a new MailMessage to send an email.
   * Use localhost as the mail server.
   *
   * @exception IOException if there's any problem contacting the mail server
   */
  public MailMessage() throws IOException {
    this("localhost");
  }

  /**
   * Constructs a new MailMessage to send an email.
   * Use the given host as the mail server.
   *
   * @param host the mail server to use
   * @exception IOException if there's any problem contacting the mail server
   */
  public MailMessage(String host) throws IOException {
    this.host = host;
    to = new Vector();
    cc = new Vector();
    headers = new Hashtable();
    setHeader("X-Mailer", "org.apache.tools.mail.MailMessage (jakarta.apache.org)");
    connect();
    sendHelo();
  }

  /**
   * Sets the from address.  Also sets the "From" header.  This method should
   * be called only once.
   *
   * @exception IOException if there's any problem reported by the mail server
   */
  public void from(String from) throws IOException {
    sendFrom(from);
    this.from = from;
  }

  /**
   * Sets the to address.  Also sets the "To" header.  This method may be
   * called multiple times.
   *
   * @exception IOException if there's any problem reported by the mail server
   */
  public void to(String to) throws IOException {
    sendRcpt(to);
    this.to.addElement(to);
  }

  /**
   * Sets the cc address.  Also sets the "Cc" header.  This method may be
   * called multiple times.
   *
   * @exception IOException if there's any problem reported by the mail server
   */
  public void cc(String cc) throws IOException {
    sendRcpt(cc);
    this.cc.addElement(cc);
  }

  /**
   * Sets the bcc address.  Does NOT set any header since it's a *blind* copy.
   * This method may be called multiple times.
   *
   * @exception IOException if there's any problem reported by the mail server
   */
  public void bcc(String bcc) throws IOException {
    sendRcpt(bcc);
  }

  /**
   * Sets the subject of the mail message.  Actually sets the "Subject" 
   * header.
   */
  public void setSubject(String subj) {
    headers.put("Subject", subj);
  }

  /**
   * Sets the named header to the given value.  RFC 822 provides the rules for
   * what text may constitute a header name and value.
   */
  public void setHeader(String name, String value) {
    headers.put(name, value);
  }

  /**
   * Returns a PrintStream that can be used to write the body of the message.
   * A stream is used since email bodies are byte-oriented.  A writer could 
   * be wrapped on top if necessary for internationalization.
   *
   * @exception IOException if there's any problem reported by the mail server
   */
  public PrintStream getPrintStream() throws IOException {
    setFromHeader();
    setToHeader();
    setCcHeader();
    sendData();
    flushHeaders();
    return out;
  }

  void setFromHeader() {
    setHeader("From", from);
  }

  void setToHeader() {
    setHeader("To", vectorToList(to));
  }

  void setCcHeader() {
    setHeader("Cc", vectorToList(cc));
  }

  String vectorToList(Vector v) {
    StringBuffer buf = new StringBuffer();
    Enumeration e = v.elements();
    while (e.hasMoreElements()) {
      buf.append(e.nextElement());
      if (e.hasMoreElements()) {
        buf.append(", ");
      }
    }
    return buf.toString();
  }

  void flushHeaders() throws IOException {
    Enumeration e = headers.keys();
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      String value = (String) headers.get(name);
      out.println(name + ": " + value);
    }
    out.println();
    out.flush();
  }

  /**
   * Sends the message and closes the connection to the server.
   * The MailMessage object cannot be reused.
   *
   * @exception IOException if there's any problem reported by the mail server
   */
  public void sendAndClose() throws IOException {
    sendDot();
    sendQuit();
    disconnect();
  }

  static String sanitizeAddress(String s) {
    int paramDepth = 0;
    int start = 0;
    int end = 0;
    int len = s.length();

    for (int i = 0; i < len; i++) {
      char c = s.charAt(i);
      if (c == '(') {
        paramDepth++;
        if (start == 0) {
        }
      }
      else if (c == ')') {
        paramDepth--;
        if (end == 0) {
        }
      }
      else if (paramDepth == 0 && c == '<') {
        start = i + 1;
      }
      else if (paramDepth == 0 && c == '>') {
        end = i;
      }
    }

    if (end == 0) {
      end = len;
    }

    return s.substring(start, end);
  }


  void connect() throws IOException {
    socket = new Socket(host, 25);
    out = new MailPrintStream(
          new BufferedOutputStream(
          socket.getOutputStream())); 
    in = new SmtpResponseReader(socket.getInputStream());
    getReady();
  }

  void getReady() throws IOException {
    String response = in.getResponse();
    int[] ok = { 220 };
    if (!isResponseOK(response, ok)) {
      throw new IOException(
        "Didn't get introduction from server: " + response);
    }
  }

  void sendHelo() throws IOException {
    String local = InetAddress.getLocalHost().getHostName();
    int[] ok = { 250 };
    send("HELO " + local, ok);
  }

  void sendFrom(String from) throws IOException {
    int[] ok = { 250 };
    send("MAIL FROM: " + "<" + sanitizeAddress(from) + ">", ok);
  }

  void sendRcpt(String rcpt) throws IOException {
    int[] ok = { 250, 251 };
    send("RCPT TO: " + "<" + sanitizeAddress(rcpt) + ">", ok);
  }

  void sendData() throws IOException {
    int[] ok = { 354 };
    send("DATA", ok);
  }

  void sendDot() throws IOException {
    int[] ok = { 250 };
  }

  void sendQuit() throws IOException {
    int[] ok = { 221 };
    send("QUIT", ok);
  }

  void send(String msg, int[] ok) throws IOException {
    String response = in.getResponse();
    if (!isResponseOK(response, ok)) {
      throw new IOException(
        "Unexpected reply to command: " + msg + ": " + response);
    }
  }

  boolean isResponseOK(String response, int[] ok) {
    for (int i = 0; i < ok.length; i++) {
      if (response.startsWith("" + ok[i])) {
        return true;
      }
    }
    return false;
  }

  void disconnect() throws IOException {
    if (out != null) out.close(); 
    if (in != null) in.close(); 
    if (socket != null) socket.close();
  }
}

class MailPrintStream extends PrintStream {

  int lastChar;

  public MailPrintStream(OutputStream out) {
  }

  public void write(int b) {
    if (b == '\n' && lastChar != '\r') {
      rawWrite(b);
    }
    else if (b == '.' && lastChar == '\n') {
      rawWrite(b);
    }
    else {
      rawWrite(b);
    }
    lastChar = b;
  }

  public void write(byte buf[], int off, int len) {
    for (int i = 0; i < len; i++) {
      write(buf[off + i]);
    }
  }

  void rawWrite(int b) {
    super.write(b);
  }

  void rawPrint(String s) {
    int len = s.length();
    for (int i = 0; i < len; i++) {
      rawWrite(s.charAt(i));
    }
  }
}

