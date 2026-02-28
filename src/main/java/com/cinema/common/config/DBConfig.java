package com.cinema.common.config;

public final class DBConfig {

    public static final String HOST = "localhost";
    public static final String PORT = "3306";
    public static final String DB_NAME = "cinema1";

    public static final String USERNAME = "root";
    public static final String PASSWORD = "";

    public static final String JDBC_URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME +
            "?useSSL=false" +
            "&allowPublicKeyRetrieval=true" +
            "&serverTimezone=UTC";

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private DBConfig() {}
}
