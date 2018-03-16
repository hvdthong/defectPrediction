package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;


/**
 * This class writes ASCII to a byte stream as quickly as possible.  For the
 * moment it does not do buffering, though I reserve the right to do some
 * buffering down the line if I can prove that it will be faster even if the
 * output stream is buffered.
 */
public final class WriterToUTF8Buffered extends Writer
{
    
  /** number of characters that the buffer can hold.
   * This is a fixed constant is used rather than buf.lenght for performance.
   */
  private static final int buf_length=16*1024;
  
  /** The byte stream to write to. (sc & sb remove final to compile in JDK 1.1.8) */
  private final OutputStream m_os;

  /**
   * The internal buffer where data is stored.
   * (sc & sb remove final to compile in JDK 1.1.8)
   */
  private final byte buf[];

  /**
   * The number of valid bytes in the buffer. This value is always
   * in the range <tt>0</tt> through <tt>buf.length</tt>; elements
   * <tt>buf[0]</tt> through <tt>buf[count-1]</tt> contain valid
   * byte data.
   */
  private int count;

  /**
   * Create an buffered UTF-8 writer.
   *
   *
   * @param   out    the underlying output stream.
   *
   * @throws UnsupportedEncodingException
   */
  public WriterToUTF8Buffered(OutputStream out)
          throws UnsupportedEncodingException
  {
      m_os = out;
      buf = new byte[buf_length + 3];
      count = 0;
      
  }

  /**
   * Create an buffered UTF-8 writer to write data to the
   * specified underlying output stream with the specified buffer
   * size.
   *
   * @param   out    the underlying output stream.
   * @param   size   the buffer size.
   * @exception IllegalArgumentException if size <= 0.
   */

  /**
   * Write a single character.  The character to be written is contained in
   * the 16 low-order bits of the given integer value; the 16 high-order bits
   * are ignored.
   *
   * <p> Subclasses that intend to support efficient single-character output
   * should override this method.
   *
   * @param c  int specifying a character to be written.
   * @exception  IOException  If an I/O error occurs
   */
  public void write(final int c) throws IOException
  {
    
    /* If we are close to the end of the buffer then flush it.
     * Remember the buffer can hold a few more characters than buf_length
     */ 
    if (count >= buf_length)
        flushBuffer();

    if (c < 0x80)
    {
       buf[count++] = (byte) (c);
    }
    else if (c < 0x800)
    {
      buf[count++] = (byte) (0xc0 + (c >> 6));
      buf[count++] = (byte) (0x80 + (c & 0x3f));
    }
    else
    {
      buf[count++] = (byte) (0xe0 + (c >> 12));
      buf[count++] = (byte) (0x80 + ((c >> 6) & 0x3f));
      buf[count++] = (byte) (0x80 + (c & 0x3f));
    }
  }

  /**
   * Write a portion of an array of characters.
   *
   * @param  chars  Array of characters
   * @param  start   Offset from which to start writing characters
   * @param  length   Number of characters to write
   *
   * @exception  IOException  If an I/O error occurs
   *
   * @throws java.io.IOException
   */
  private final void writeDirect(
          final char chars[], final int start, final int length)
            throws java.io.IOException
  {

    final OutputStream os = m_os;

    final int n = length+start;
    for (int i = start; i < n; i++)
    {
      final char c = chars[i];

      if (c < 0x80)
        os.write(c);
      else if (c < 0x800)
      {
        os.write(0xc0 + (c >> 6));
        os.write(0x80 + (c & 0x3f));
      }
      else
      {
        os.write(0xe0 + (c >> 12));
        os.write(0x80 + ((c >> 6) & 0x3f));
        os.write(0x80 + (c & 0x3f));
      }
    }
  }

  /**
   * Write a string.
   *
   * @param  s  String to be written
   *
   * @exception  IOException  If an I/O error occurs
   */
  private final void writeDirect(final String s) throws IOException
  {

    final int n = s.length();
    final OutputStream os = m_os;

    for (int i = 0; i < n; i++)
    {
      final char c = s.charAt(i);

      if (c < 0x80)
        os.write(c);
      else if (c < 0x800)
      {
        os.write(0xc0 + (c >> 6));
        os.write(0x80 + (c & 0x3f));
      }
      else
      {
        os.write(0xe0 + (c >> 12));
        os.write(0x80 + ((c >> 6) & 0x3f));
        os.write(0x80 + (c & 0x3f));
      }
    }
  }

  /**
   * Write a portion of an array of characters.
   *
   * @param  chars  Array of characters
   * @param  start   Offset from which to start writing characters
   * @param  length   Number of characters to write
   *
   * @exception  IOException  If an I/O error occurs
   *
   * @throws java.io.IOException
   */
  public void write(final char chars[], final int start, final int length)
          throws java.io.IOException
  {


    int lengthx3 = (length << 1) + length;

    if (lengthx3 >= buf_length)
    {

      /* If the request length exceeds the size of the output buffer,
         flush the output buffer and then write the data directly.
         In this way buffered streams will cascade harmlessly. */
      flushBuffer();
      writeDirect(chars, start, length);

      return;
    }

    if (lengthx3 > buf_length - count)
    {
      flushBuffer();
    }

    final OutputStream os = m_os;
    final int n = length+start;
    for (int i = start; i < n; i++)
    {
      final char c = chars[i];

      if (c < 0x80)
        buf[count++] = (byte) (c);
      else if (c < 0x800)
      {
        buf[count++] = (byte) (0xc0 + (c >> 6));
        buf[count++] = (byte) (0x80 + (c & 0x3f));
      }
      else
      {
        buf[count++] = (byte) (0xe0 + (c >> 12));
        buf[count++] = (byte) (0x80 + ((c >> 6) & 0x3f));
        buf[count++] = (byte) (0x80 + (c & 0x3f));
      }
    }

  }

  /**
   * Write a string.
   *
   * @param  s  String to be written
   *
   * @exception  IOException  If an I/O error occurs
   */
  public void write(final String s) throws IOException
  {

    final int length = s.length();


    int lengthx3 = (length << 1) + length;

    if (lengthx3 >= buf_length)
    {

      /* If the request length exceeds the size of the output buffer,
         flush the output buffer and then write the data directly.
         In this way buffered streams will cascade harmlessly. */
      flushBuffer();
      writeDirect(s);

      return;
    }

    if (lengthx3 > buf_length - count)
    {
      flushBuffer();
    }

    final OutputStream os = m_os;

    for (int i = 0; i < length; i++)
    {
      final char c = s.charAt(i);

      if (c < 0x80)
        buf[count++] = (byte) (c);
      else if (c < 0x800)
      {
        buf[count++] = (byte) (0xc0 + (c >> 6));
        buf[count++] = (byte) (0x80 + (c & 0x3f));
      }
      else
      {
        buf[count++] = (byte) (0xe0 + (c >> 12));
        buf[count++] = (byte) (0x80 + ((c >> 6) & 0x3f));
        buf[count++] = (byte) (0x80 + (c & 0x3f));
      }
    }

  }

  /**
   * Flush the internal buffer
   *
   * @throws IOException
   */
  public void flushBuffer() throws IOException
  {

    if (count > 0)
    {
      m_os.write(buf, 0, count);

      count = 0;
    }
  }

  /**
   * Flush the stream.  If the stream has saved any characters from the
   * various write() methods in a buffer, write them immediately to their
   * intended destination.  Then, if that destination is another character or
   * byte stream, flush it.  Thus one flush() invocation will flush all the
   * buffers in a chain of Writers and OutputStreams.
   *
   * @exception  IOException  If an I/O error occurs
   *
   * @throws java.io.IOException
   */
  public void flush() throws java.io.IOException
  {
    flushBuffer();
    m_os.flush();
  }

  /**
   * Close the stream, flushing it first.  Once a stream has been closed,
   * further write() or flush() invocations will cause an IOException to be
   * thrown.  Closing a previously-closed stream, however, has no effect.
   *
   * @exception  IOException  If an I/O error occurs
   *
   * @throws java.io.IOException
   */
  public void close() throws java.io.IOException
  {
    flushBuffer();
    m_os.close();
  }

  /**
   * Get the output stream where the events will be serialized to.
   *
   * @return reference to the result stream, or null of only a writer was
   * set.
   */
  public OutputStream getOutputStream()
  {
    return m_os;
  }
}
