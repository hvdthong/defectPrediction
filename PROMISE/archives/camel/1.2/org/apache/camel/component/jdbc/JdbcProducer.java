package org.apache.camel.component.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.IntrospectionSupport;

/**
 * @version $Revision: 533076 $
 */
public class JdbcProducer extends DefaultProducer<DefaultExchange> {

    private DataSource source;

    /** The maximum size for reading a result set <code>readSize</code> */
    private int readSize = 2000;

    public JdbcProducer(JdbcEndpoint endpoint, String remaining, int readSize) throws Exception {
        super(endpoint);
        this.readSize = readSize;
        source = (DataSource) getEndpoint().getContext().getRegistry().lookup(remaining);
    }

    /**
     * Execute sql of exchange and set results on output
     * 
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    public void process(Exchange exchange) throws Exception {
        String sql = exchange.getIn().getBody(String.class);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = source.getConnection();
            stmt = conn.createStatement();
            if (stmt.execute(sql)) {
                ResultSet rs = stmt.getResultSet();
                setResultSet(exchange, rs);
                rs.close();
            } else {
                int updateCount = stmt.getUpdateCount();
                exchange.getOut().setHeader("jdbc.updateCount", updateCount);
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public int getReadSize() {
        return this.readSize;
    }

    public void setReadSize(int readSize) {
        this.readSize = readSize;
    }

    public void setResultSet(Exchange exchange, ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        HashMap<String, Object> props = new HashMap<String, Object>();
        IntrospectionSupport.getProperties(meta, props, "jdbc.");
        exchange.getOut().setHeaders(props);
        int count = meta.getColumnCount();
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int rowNumber = 0;
        while (rs.next() && rowNumber < readSize) {
            HashMap<String, Object> row = new HashMap<String, Object>();
            for (int i = 0; i < count; i++) {
                int columnNumber = i + 1;
                String columnName = meta.getColumnName(columnNumber);
                row.put(columnName, rs.getObject(columnName));
            }
            data.add(row);
            rowNumber++;
        }
        exchange.getOut().setBody(data);
    }

}