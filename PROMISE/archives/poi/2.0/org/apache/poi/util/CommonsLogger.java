package org.apache.poi.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * A logger class that strives to make it as easy as possible for
 * developers to write log calls, while simultaneously making those
 * calls as cheap as possible by performing lazy evaluation of the log
 * message.<p>
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 * @author Glen Stampoultzis (glens at apache.org)
 * @author Nicola Ken Barozzi (nicolaken at apache.org)
 */

public class CommonsLogger extends POILogger
{

    private static LogFactory   _creator = LogFactory.getFactory();
    private Log             log   = null;

   
    public void initialize(final String cat)
    {
        this.log = _creator.getInstance(cat);
    }   
     
    /**
     * Log a message
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 The object to log.
     */

    public void log(final int level, final Object obj1)
    {
        if(level==FATAL)
        {
          if(log.isFatalEnabled())
          {
            log.fatal(obj1);
          }
        }
        else if(level==ERROR)
        {
          if(log.isErrorEnabled())
          {
            log.error(obj1);
          }
        }
        else if(level==WARN)
        {
          if(log.isWarnEnabled())
          {
            log.warn(obj1);
          }
        }
        else if(level==INFO)
        {
          if(log.isInfoEnabled())
          {
            log.info(obj1);
          }
        }
        else if(level==DEBUG)
        {
          if(log.isDebugEnabled())
          {
            log.debug(obj1);
          }
        }
        else
        {
          if(log.isTraceEnabled())
          {
            log.trace(obj1);
          }
        }

    }

    /**
     * Check if a logger is enabled to log at the specified level
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 The logger to check.
     */

    public boolean check(final int level)
    {
        if(level==FATAL)
        {
          if(log.isFatalEnabled())
          {
            return true;
          }
        }
        else if(level==ERROR)
        {
          if(log.isErrorEnabled())
          {
            return true;
          }
        }
        else if(level==WARN)
        {
          if(log.isWarnEnabled())
          {
            return true;
          }
        }
        else if(level==INFO)
        {
          if(log.isInfoEnabled())
          {
            return true;
          }
        }
        else if(level==DEBUG)
        {
          if(log.isDebugEnabled())
          {
            return true;
          }
        }

        return false;

    }

 

