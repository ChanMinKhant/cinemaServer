package com.cinema.common.util;

import com.cinema.common.config.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {

    static {
        try {
            Class.forName(DBConfig.DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }

    private DBUtil() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DBConfig.JDBC_URL,
                DBConfig.USERNAME,
                DBConfig.PASSWORD
        );
    }
}
