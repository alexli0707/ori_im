package com.walker.learning.store.core;

import com.walker.learning.utils.LoggerHelper;
import org.slf4j.Logger;

import java.sql.*;

/**
 * SQLConnInstance
 *
 * @author walker lee
 * @date 2019/4/4
 */
public class SQLConnInstance {


    private static SQLConnInstance sSqlConnInstance;
    private static Logger sLOGGER = LoggerHelper.getLogger(SQLConnInstance.class);
    private static Connection sSqlConn;


    private SQLConnInstance() {
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            sLOGGER.info("Connecting to a selected database...");
            sSqlConn = DriverManager.getConnection(StoreConfig.IM_MYSQL_URL, StoreConfig.IM_MYSQL_USERNAME, StoreConfig.IM_MYSQL_PASSWORD);
            sLOGGER.info("Connected database successfully...");
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
        }//end try
    }


    public static SQLConnInstance getInstance() {
        if (null == sSqlConnInstance) {
            synchronized (SQLConnInstance.class) {
                if (null == sSqlConnInstance) {
                    sSqlConnInstance = new SQLConnInstance();
                }
            }
        }
        return sSqlConnInstance;
    }


    public ResultSet executeQueryRawSql(String rawSql, String... args) throws SQLException {
        PreparedStatement stmt = sSqlConn.prepareStatement(rawSql);
        for (int i = 1; i < args.length + 1; i++) {
            stmt.setString(i, args[i - 1]);
        }
        return stmt.executeQuery();
    }


    public Boolean executeRawSql(String rawSql) throws SQLException {
        Statement stmt = sSqlConn.createStatement();
        return stmt.execute(rawSql);
    }

    public int executeUpdateRawSql(String rawSql) throws SQLException {
        Statement stmt = sSqlConn.createStatement();
        return stmt.executeUpdate(rawSql);
    }


}
