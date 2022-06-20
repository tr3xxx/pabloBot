package com.bot.core.sql;

import com.bot.log.log;

import java.sql.*;


public class SQLDataSource {

     public static Connection connection;
    static{
        String url = "jdbc:sqlserver://pablobot.database.windows.net:1433;databaseName=database";
        String username = "sqlroot";
        String passwort = "sdfjlkSLJKDg§%§%35§T";
        try {
            log.logger.info("Try connection to database...");
            connection = DriverManager.getConnection(url, username, passwort);
            log.logger.info("Connected to database");
            

        } catch (Exception e) {
            log.logger.warning("Connection to database failed");
            log.logger.warning(e.toString());

        }

    }

    public static Connection getConnection() {

        return connection;
    }
}