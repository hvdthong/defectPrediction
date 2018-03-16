package org.apache.xalan.lib.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * For internal connectiones, i.e. Connection information supplies in the
 * Stylesheet. The Default Connection Pool will be used.
 */
public class DefaultConnectionPool implements ConnectionPool
{
  /**
   */
  private static final boolean DEBUG = false;

  /**
   * The basic information to make a JDBC Connection
   */
  private String m_driver = new String("");
  /**
   */
  private String m_url = new String("");


  /**
   * The mimimum size of the connection pool, if the
   * number of available connections falls below this
   * mark, min connections will be allocated. The Connection
   * Pool will always be somewhere between MinSize and MinSize*2
   */
  private int m_PoolMinSize = 1;


  /**
   * Always implement the properties mechinism, if the Password
   * or Username is set seperatly then we will add them to the
   * property manually.
   */
  private Properties m_ConnectionProtocol = new Properties();

  /**
   * Storage for the PooledConnections
   */
  private Vector m_pool = new Vector();

  /**
   * Are we active ??
   */
  private boolean m_IsActive = false;

  /**
   */
  public DefaultConnectionPool( ) {}


  /**
   * Return our current Active state
   * @return
   */
  public boolean isEnabled( )
  {
    return m_IsActive;
  }

  /**
   * Set the driver call to be used to create connections
   * @param d
   * @return
   */
  public void setDriver( String d )
  {
    m_driver = d;
  }

  /**
   * Set the url used to connect to the database
   * @param url
   * @return
   */
  public void setURL( String url )
  {
    m_url = url;
  }

  /**
   * Go through the connection pool and release any connections
   * that are not InUse;
   * @return
   */
  public void freeUnused( )
  {
    for ( int x = 0; x < m_pool.size(); x++ )
    {


      PooledConnection pcon =
        (PooledConnection) m_pool.elementAt(x);

      if ( pcon.inUse() == false )
      {
        if (DEBUG)
        {
          System.err.println("Closing JDBC Connection " + x);
        }

        pcon.close();
      }
    }

  }

  /**
   * Is our ConnectionPool have any connections that are still in Use ??
   * @return
   */
  public boolean hasActiveConnections( )
  {
    return (m_pool.size() > 0);
  }


  /**
   * Set the password in the property set.
   * @param p
   * @return
   */
  public void setPassword( String p )
  {
    m_ConnectionProtocol.put("password", p);
  }

  /**
   * Set the user name in the property set
   * @param u
   * @return
   */
  public void setUser( String u )
  {
    m_ConnectionProtocol.put("user", u);
  }

  /**
   * The Protocol string is used to pass in other connection
   * properties. A properties file is a general purpose container
   *
   * @param p
   * @return
   */
  public void setProtocol( Properties p )
  {
    Enumeration e = p.keys();
    while (e.hasMoreElements())
    {
      String key = (String) e.nextElement();
      m_ConnectionProtocol.put(key, p.getProperty(key));
    }
  }


  /**
   * Override the current number of connections to keep in the pool. This
   * setting will only have effect on a new pool or when a new connection
   * is requested and there is less connections that this setting.
   * @param n
   * @return
   */
  public void setMinConnections( int n )
  {
    m_PoolMinSize = n;
  }

  /**
   * Try to aquire a new connection, if it succeeds then return
   * true, else return false.
   * Note: This method will cause the connection pool to be built.
   * @return
   */
  public boolean testConnection( )
  {
    try
    {
      if (DEBUG)
      {
        System.out.println("Testing Connection");
      }

      Connection conn = getConnection();

      if (DEBUG)
      {
        DatabaseMetaData dma = conn.getMetaData();

        System.out.println("\nConnected to " + dma.getURL());
        System.out.println("Driver   " + dma.getDriverName());
        System.out.println("Version  " + dma.getDriverVersion());
        System.out.println("");
      }

      if (conn == null) return false;

      releaseConnection(conn);

      if (DEBUG)
      {
        System.out.println("Testing Connection, SUCCESS");
      }

      return true;
    }
    catch(Exception e)
    {
      if (DEBUG)
      {
        System.out.println("Testing Connection, FAILED");
        e.printStackTrace();
      }

      return false;
    }

  }


  /**
   * @return Connection
   * @throws SQLException
   * @throws IllegalArgumentException
   */
  public synchronized Connection getConnection( )throws IllegalArgumentException, SQLException
  {

    PooledConnection pcon = null;

    if ( m_pool.size() < m_PoolMinSize ) { initializePool(); }

    for ( int x = 0; x < m_pool.size(); x++ )
    {

      pcon = (PooledConnection) m_pool.elementAt(x);

      if ( pcon.inUse() == false )
      {
        pcon.setInUse(true);
        return pcon.getConnection();
      }
    }


    Connection con = createConnection();

    pcon = new PooledConnection(con);

    pcon.setInUse(true);

    m_pool.addElement(pcon);

    return pcon.getConnection();
  }

  /**
   * @param con
   * @return
   * @throws SQLException
   */
  public synchronized void releaseConnection( Connection con )throws SQLException
  {

    for ( int x = 0; x < m_pool.size(); x++ )
    {

      PooledConnection pcon =
        (PooledConnection) m_pool.elementAt(x);

      if ( pcon.getConnection() == con )
      {
        if (DEBUG)
        {
          System.out.println("Releasing Connection " + x);
        }

        if (! isEnabled())
        {
          con.close();
          m_pool.removeElementAt(x);
          if (DEBUG)
          {
            System.out.println("-->Inactive Pool, Closing connection");
          }

        }
        else
        {
          pcon.setInUse(false);
        }

        break;
      }
    }
  }


  /**
   * @param con
   * @return
   * @throws SQLException
   */
  public synchronized void releaseConnectionOnError( Connection con )throws SQLException
  {

    for ( int x = 0; x < m_pool.size(); x++ )
    {

      PooledConnection pcon =
        (PooledConnection) m_pool.elementAt(x);

      if ( pcon.getConnection() == con )
      {
        if (DEBUG)
        {
          System.out.println("Releasing Connection On Error" + x);
        }

        con.close();
        m_pool.removeElementAt(x);
        if (DEBUG)
        {
          System.out.println("-->Inactive Pool, Closing connection");
        }
        break;
      }
    }
  }


  /**
   * @return
   * @throws SQLException
   */
  private Connection createConnection( )throws SQLException
  {
    Connection con = null;

    con = DriverManager.getConnection( m_url, m_ConnectionProtocol );

    return con;
  }

  /**
   * @return
   * @throws IllegalArgumentException
   * @throws SQLException
   */
  public synchronized void initializePool( )throws IllegalArgumentException, SQLException
  {

     if ( m_driver == null )
     {
       throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_DRIVER_NAME_SPECIFIED, null));
     }

     if ( m_url == null )
     {
       throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_URL_SPECIFIED, null));
     }

     if ( m_PoolMinSize < 1 )
     {
       throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_POOLSIZE_LESS_THAN_ONE, null));
     }


     try
     {
       Class.forName( m_driver );
     }
     catch(ClassNotFoundException e)
     {
       throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_DRIVER_NAME, null));
     }

     if ( !m_IsActive) return;

    do
    {

      Connection con = createConnection();

      if ( con != null )
      {

        PooledConnection pcon = new PooledConnection(con);

        addConnection(pcon);

        if (DEBUG) System.out.println("Adding DB Connection to the Pool");
      }
    }
    while (m_pool.size() < m_PoolMinSize);
  }

  /**
   * @param value
   * @return
   */
  private void addConnection( PooledConnection value )
  {
    m_pool.addElement(value);
  }


  /**
   * @return
   * @throws Throwable
   */
  protected void finalize( )throws Throwable
  {
    if (DEBUG)
    {
      System.out.println("In Default Connection Pool, Finalize");
    }

    for ( int x = 0; x < m_pool.size(); x++ )
    {

      if (DEBUG)
      {
        System.out.println("Closing JDBC Connection " + x);
      }

      PooledConnection pcon =
        (PooledConnection) m_pool.elementAt(x);

      if ( pcon.inUse() == false ) { pcon.close();  }
      else
      {
        if (DEBUG)
        {
          System.out.println("--> Force close");
        }

        try
        {
          java.lang.Thread.sleep(30000);
          pcon.close();
        }
        catch (InterruptedException ie)
        {
          if (DEBUG) System.err.println(ie.getMessage());
        }
      }
    }

    if (DEBUG)
    {
      System.out.println("Exit Default Connection Pool, Finalize");
    }

    super.finalize();
  }

  /**
   * The Pool can be Enabled and Disabled. Disabling the pool
   * closes all the outstanding Unused connections and any new
   * connections will be closed upon release.
   * @param flag Control the Connection Pool. If it is enabled then Connections will actuall be held
   * around. If disabled then all unused connections will be instantly closed and as
   * connections are released they are closed and removed from the pool.
   * @return
   */
  public void setPoolEnabled( final boolean flag )
  {

  }


}
