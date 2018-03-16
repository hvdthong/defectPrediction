package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import java.io.*;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Properties;
import java.util.zip.*;
import java.sql.*;

/**
 * Reads in a text file containing SQL statements seperated with semicolons
 * and executes it in a given db.
 * 
 * @author <a href="mailto:jeff@custommonkey.org">Jeff Martin</a>
 */
public class SQLExec extends Task {
    
    private int goodSql = 0, totalSql = 0;

    private Path classpath;

    private AntClassLoader loader;

    /**
     * Database connection
     */
    private Connection conn = null;
    
    /**
     * Autocommit flag. Default value is false
     */
    private boolean autocommit=false;
    
    /**
     * SQL statement
     */
    private Statement statement = null;

    /**
     * DB driver.
     */
    private String driver = null;

    /**
     * DB url.
     */
    private String url = null;

    /**
     * User name.
     */
    private String userId = null;

    /**
     * Password
     */
    private String password = null;

    /**
     * SQL input file
     */
    private File srcFile = null;

    /**
     * SQL input command
     */
    private String sqlCommand = "";

    /**
     * SQL transactions to perform
     */
    private Vector transactions = new Vector();

    /**
     * Print SQL results.
     */
    private boolean print = false;

    /**
     * Print header columns.
     */
    private boolean showheaders = true;

    /**
     * Results Output file.
     */
    private File output = null;

    /**
     * RDBMS Product needed for this SQL.
     **/
    private String rdbms = null;

    /**
     * RDBMS Version needed for this SQL.
     **/
    private String version = null;

    /**
     * Action to perform if an error is found
     **/
    private String onError = "abort";

    /**
     * Set the classpath for loading the driver.
     */
    public void setClasspath(Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    /**
     * Create the classpath for loading the driver.
     */
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(project);
        }
        return this.classpath.createPath();
    }

    /**
     * Set the classpath for loading the driver using the classpath reference.
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }
    
    /**
     * Set the name of the sql file to be run.
     */
    public void setSrc(File srcFile) {
        this.srcFile = srcFile;
    }
    
    /**
     * Set the sql command to execute
     */
    public void addText(String sql) {
        this.sqlCommand += sql;
    }
    
    /**
     * Set the sql command to execute
     */
    public Transaction createTransaction() {
      Transaction t = new Transaction();
      transactions.addElement(t);
      return t;
    }
    
    /**
     * Set the JDBC driver to be used.
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }
    
    /**
     * Set the DB connection url.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * Set the user name for the DB connection.
     */
    public void setUserid(String userId) {
        this.userId = userId;
    }
    
    /**
     * Set the password for the DB connection.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Set the autocommit flag for the DB connection.
     */
    public void setAutocommit(boolean autocommit) {
        this.autocommit = autocommit;
    }

    /**
     * Set the print flag.
     */
    public void setPrint(boolean print) {
        this.print = print;
    }
    
    /**
     * Set the showheaders flag.
     */
    public void setShowheaders(boolean showheaders) {
        this.showheaders = showheaders;
    }

    /**
     * Set the output file.
     */
    public void setOutput(File output) {
        this.output = output;
    }

    /**
     * Set the rdbms required
     */
    public void setRdbms(String vendor) {
        this.rdbms = vendor.toLowerCase();
    }

    /**
     * Set the version required
     */
    public void setVersion(String version) {
        this.version = version.toLowerCase();
    }

    /**
     * Set the action to perform onerror
     */
    public void setOnerror(OnError action) {
        this.onError = action.getValue();
    }

    /**
     * Load the sql file and then execute it
     */
    public void execute() throws BuildException {
        sqlCommand = sqlCommand.trim();

        if (srcFile == null && sqlCommand.length() == 0) { 
            if (transactions.size() == 0) {
                throw new BuildException("Source file, transactions or sql statement must be set!", location);
            }
        } else {
            Transaction t = createTransaction();
            t.setSrc(srcFile);
            t.addText(sqlCommand);
        }

        if (driver == null) {
            throw new BuildException("Driver attribute must be set!", location);
        }
        if (userId == null) {
            throw new BuildException("User Id attribute must be set!", location);
        }
        if (password == null) {
            throw new BuildException("Password attribute must be set!", location);
        }
        if (url == null) {
            throw new BuildException("Url attribute must be set!", location);
        }
        if (srcFile != null && !srcFile.exists()) {
            throw new BuildException("Source file does not exist!", location);
        }
        Driver driverInstance = null;
        try {
            Class dc;
            if (classpath != null) {
                log( "Loading " + driver + " using AntClassLoader with classpath " + classpath, 
                     Project.MSG_VERBOSE );

                loader = new AntClassLoader(project, classpath, false);
                dc = loader.loadClass(driver);
            }
            else {
                log("Loading " + driver + " using system loader.", Project.MSG_VERBOSE);
                dc = Class.forName(driver);
            }
            driverInstance = (Driver) dc.newInstance();
        }catch(ClassNotFoundException e){
            throw new BuildException("Class Not Found: JDBC driver " + driver + " could not be loaded", location);
        }catch(IllegalAccessException e){
            throw new BuildException("Illegal Access: JDBC driver " + driver + " could not be loaded", location);
        }catch(InstantiationException e) {
            throw new BuildException("Instantiation Exception: JDBC driver " + driver + " could not be loaded", location);
        }

        try{
            log("connecting to " + url, Project.MSG_VERBOSE );
            Properties info = new Properties();
            info.put("user", userId);
            info.put("password", password);
            conn = driverInstance.connect(url, info);

            if (conn == null) {
                throw new SQLException("No suitable Driver for "+url);
            }

            if (!isValidRdbms(conn)) return;

            conn.setAutoCommit(autocommit);

            statement = conn.createStatement();

            
            PrintStream out = System.out;
            try {
                if (output != null) {
                    log("Opening PrintStream to output file " + output, Project.MSG_VERBOSE);
                    out = new PrintStream(new BufferedOutputStream(new FileOutputStream(output)));
                }
                        
                for (Enumeration e = transactions.elements(); 
                     e.hasMoreElements();) {
                       
                    ((Transaction) e.nextElement()).runTransaction(out);
                    if (!autocommit) {
                        log("Commiting transaction", Project.MSG_VERBOSE);
                        conn.commit();
                    }
                }
            }
            finally {
                if (out != null && out != System.out) {
                    out.close();
                }
            }
        } catch(IOException e){
            if (!autocommit && conn != null && onError.equals("abort")) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            throw new BuildException(e, location);
        } catch(SQLException e){
            if (!autocommit && conn != null && onError.equals("abort")) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            throw new BuildException(e, location);
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {}
        }
          
        log(goodSql + " of " + totalSql + 
            " SQL statements executed successfully");
    }

    protected void runStatements(Reader reader, PrintStream out) throws SQLException, IOException {
        String sql = "";
        String line = "";
 
        BufferedReader in = new BufferedReader(reader);
 
        try{
            while ((line=in.readLine()) != null){
                if (line.trim().startsWith("--")) continue;

                sql += " " + line;
                sql = sql.trim();

                if (line.indexOf("--") >= 0) sql += "\n";

                if (sql.endsWith(";")){
                    log("SQL: " + sql, Project.MSG_VERBOSE);
                    execSQL(sql.substring(0, sql.length()-1), out);
                    sql = "";
                }
            }
 
            if(!sql.equals("")){
                execSQL(sql, out);
            }
        }catch(SQLException e){
            throw e;
        }

    }
 
    /**
     * Verify if connected to the correct RDBMS
     **/
    protected boolean isValidRdbms(Connection conn) {
        if (rdbms == null && version == null)
            return true;
        
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            
            if (rdbms != null) {
                String theVendor = dmd.getDatabaseProductName().toLowerCase();
                
                log("RDBMS = " + theVendor, Project.MSG_VERBOSE);
                if (theVendor == null || theVendor.indexOf(rdbms) < 0) {
                    log("Not the required RDBMS: "+rdbms, Project.MSG_VERBOSE);
                    return false;
                }
            }
            
            if (version != null) {
                String theVersion = dmd.getDatabaseProductVersion().toLowerCase();
                
                log("Version = " + theVersion, Project.MSG_VERBOSE);
                if (theVersion == null || 
                    !(theVersion.startsWith(version) || 
                      theVersion.indexOf(" " + version) >= 0)) {
                    log("Not the required version: \""+ version +"\"", Project.MSG_VERBOSE);
                    return false;
                }
            }
        }
        catch (SQLException e) {
            log("Failed to obtain required RDBMS information", Project.MSG_ERR);
            return false;
        }
        
        return true;
    }
    
    /**
     * Exec the sql statement.
     */
    protected void execSQL(String sql, PrintStream out) throws SQLException {
        if ("".equals(sql.trim())) return;
        
        try {  
            totalSql++;
            if (!statement.execute(sql)) {
                log(statement.getUpdateCount()+" rows affected", 
                    Project.MSG_VERBOSE);
            }
            
            if (print) {
                printResults(out);
            }
            
            SQLWarning warning = conn.getWarnings();
            while(warning!=null){
                log(warning + " sql warning", Project.MSG_VERBOSE);
                warning=warning.getNextWarning();
            }
            conn.clearWarnings();
            goodSql++;
        }
        catch (SQLException e) {
            log("Failed to execute: " + sql, Project.MSG_ERR);
            if (!onError.equals("continue")) throw e;
            log(e.toString(), Project.MSG_ERR);
        }
    }
    
    /**
     * print any results in the statement.
     */
    protected void printResults(PrintStream out) throws java.sql.SQLException {
        ResultSet rs = null;
        do {
            rs = statement.getResultSet();
            if (rs != null) {
      	        log("Processing new result set.", Project.MSG_VERBOSE);
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();
                StringBuffer line = new StringBuffer();
                if (showheaders) {
                    for (int col = 1; col < columnCount; col++) {
                         line.append(md.getColumnName(col));
                         line.append(",");
                    }
                    line.append(md.getColumnName(columnCount));
                    out.println(line);
                    line.setLength(0);
                }
                while (rs.next()) {
                    boolean first = true;
                    for (int col = 1; col <= columnCount; col++) {
                        String columnValue = rs.getString(col);
                        if (columnValue != null) {
                            columnValue = columnValue.trim();
                        }
                         
                        if (first) {
                            first = false;
                        }
                        else {
                            line.append(",");
                        }
                        line.append(columnValue);
                    }
                    out.println(line);
                    line.setLength(0);
                }
            }
        }
        while (statement.getMoreResults());
        out.println();
    }

    /**
     * Enumerated attribute with the values "continue", "stop" and "abort"
     * for the onerror attribute.  
     */
    public static class OnError extends EnumeratedAttribute {
        public String[] getValues() {
            return new String[] {"continue", "stop", "abort"};
        }
    }

    /**
     * Contains the definition of a new transaction element.
     * Transactions allow several files or blocks of statements
     * to be executed using the same JDBC connection and commit
     * operation in between.
     */
    public class Transaction {
        private File tSrcFile = null;
        private String tSqlCommand = "";

        public void setSrc(File src) {
            this.tSrcFile = src;
        }

        public void addText(String sql) {
            this.tSqlCommand += sql;
        }

        private void runTransaction(PrintStream out) throws IOException, SQLException {
            if (tSqlCommand.length() != 0) {
                log("Executing commands", Project.MSG_INFO);
                runStatements(new StringReader(tSqlCommand), out);
            }
      
            if (tSrcFile != null) {
                log("Executing file: " + tSrcFile.getAbsolutePath(), 
                    Project.MSG_INFO);
                FileReader reader = new FileReader(tSrcFile);
                runStatements(reader, out);
                reader.close();
            }
        }
    }

}
