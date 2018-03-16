package org.apache.log4j.test;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.NDC;
import org.apache.log4j.Priority;
/**
   This class is a shallow test of the various appenders and
   layouts. It also tests their reading of the configuration file.
   @author  Ceki G&uuml;lc&uuml;
*/
public class Shallow {

  static Category cat = Category.getInstance(Shallow.class);

  public
  static
  void main(String argv[]) {
    if(argv.length == 1)
      init(argv[0]);
    else
      usage("Wrong number of arguments.");
    test();
  }

  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java "+ Shallow.class.getName()+"configFile");
    System.exit(1);
  }

  static
  void init(String configFile) {
    if(configFile.endsWith(".xml"))
      DOMConfigurator.configure(configFile);
    else
      PropertyConfigurator.configure(configFile);
  }

  static
  void test() {
    int i = -1;
    NDC.push("NDC");
    Category root = Category.getRoot();
    cat.debug("Message " + ++i);
    root.debug("Message " + i);

    cat.info ("Message " + ++i);
    root.info("Message " + i);

    cat.warn ("Message " + ++i);
    root.warn("Message " + i);

    cat.error("Message " + ++i);
    root.error("Message " + i);

    cat.log(Priority.FATAL, "Message " + ++i);
    root.log(Priority.FATAL, "Message " + i);

    Exception e = new Exception("Just testing");
    cat.debug("Message " + ++i, e);
    root.debug("Message " + i, e);

    cat.info("Message " + ++i, e);
    root.info("Message " + i, e);

    cat.warn("Message " + ++i , e);
    root.warn("Message " + i , e);

    cat.error("Message " + ++i, e);
    root.error("Message " + i, e);

    cat.log(Priority.FATAL, "Message " + ++i, e);
    root.log(Priority.FATAL, "Message " + i, e);

    root.setPriority(Priority.FATAL);

    Category.shutdown();
  }


  static
  void delay(int amount) {
    try {
      Thread.currentThread().sleep(amount);
    }
    catch(Exception e) {}
  }
}
