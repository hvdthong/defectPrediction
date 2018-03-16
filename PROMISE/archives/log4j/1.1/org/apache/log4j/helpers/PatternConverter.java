package org.apache.log4j.helpers;

import org.apache.log4j.spi.LoggingEvent;

/**

   <p>PatternConverter is an abtract class that provides the
   formatting functionality that derived classes need.

   <p>Conversion specifiers in a conversion patterns are parsed to
   individual PatternConverters. Each of which is responsible for
   converting a logging event in a converter specific manner.

   @author <a href="mailto:cakalijp@Maritz.com">James P. Cakalic</a>
   @author Ceki G&uuml;lc&uuml;

   @since 0.8.2
 */
public abstract class PatternConverter {
  public PatternConverter next;
  int min = -1;
  int max = 0x7FFFFFFF;
  boolean leftAlign = false;

  protected
  PatternConverter() {  }
  
  protected
  PatternConverter(FormattingInfo fi) {
    min = fi.min;
    max = fi.max;
    leftAlign = fi.leftAlign;
  }

  /**
     Derived pattern converters must override this method in order to
     convert conversion specifiers in the correct way.
  */
  abstract
  protected
  String convert(LoggingEvent event);

  /**
     A template method for formatting in a converter specific way.
   */
  public
  void format(StringBuffer sbuf, LoggingEvent e) {
    String s = convert(e);

    if(s == null) {
      if(0 < min)
	spacePad(sbuf, min);
      return;
    }

    int len = s.length();

    if(len > max)
      sbuf.append(s.substring(len-max));
    else if(len < min) {
      if(leftAlign) {	
	sbuf.append(s);
	spacePad(sbuf, min-len);
      }
      else {
	spacePad(sbuf, min-len);
	sbuf.append(s);
      }
    }
    else
      sbuf.append(s);
  }	


  /**
     Fast space padding method.
  */
  public
  void spacePad(StringBuffer sbuf, int length) {
    while(length >= 32) {
      sbuf.append(SPACES[5]);
      length -= 32;
    }
    
    for(int i = 4; i >= 0; i--) {	
      if((length & (1<<i)) != 0) {
	sbuf.append(SPACES[i]);
      }
    }
  }
}