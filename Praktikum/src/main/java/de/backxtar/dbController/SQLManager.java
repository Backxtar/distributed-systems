package de.backxtar.dbController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class SQLManager {
    private final Logger logger = LoggerFactory.getLogger(SQLManager.class);
    private Connection connection;
    private final String dbIp, dbName, dbUser, dbPass;

    public SQLManager(String dbIp, String dbName, String dbUser, String dbPass) {
        this.dbIp = dbIp;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }

    public void connect() {
        if (connection != null) return;

        try {
            final String jdbcDriver = "com.mysql.cj.jdbc.Driver";
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection("jdbc:mysql://" + dbIp + "/" + dbName, dbUser, dbPass);
            this.logger.info("Connected to database.");
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            this.logger.error("Connection to database failed!");
        }
    }

    public void disconnect() {
        if (connection == null) return;

        try {
            connection.close();
            connection = null;
            this.logger.info("Disconnected from database.");
        } catch (SQLException ex) {
            this.logger.error("Disconnect from database failed!");
        }
    }

    public void insert(String table, String[] fields, Object[] values) {
        StringBuilder stmtString = new StringBuilder("INSERT INTO " + table + Arrays.toString(fields).replace("[", "(").replace("]", ")") + " VALUES(");
        for (Object ignored : values) stmtString.append("?,");
        stmtString = new StringBuilder(stmtString.substring(0, stmtString.length() - 1) + ") ");

        try {
            PreparedStatement stmt = preparedStatement(stmtString.toString());

            int i = 1;
            for (Object value : values) {
                if (value.getClass().getName().equals("java.lang.String"))
                    stmt.setString(i++, (String) value);
                else stmt.setLong(i++, (Long) value);
            }
            stmt.execute();
        } catch (SQLException ex) {
            this.logger.error("Insert into db failed!");
        }
    }

    private PreparedStatement preparedStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }
}
