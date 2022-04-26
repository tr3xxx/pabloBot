package com.bot.core;

import com.bot.log.log;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLite {
    private static Connection conn;
    private static Statement stmt;

    public static void connect() {
        conn = null;

        try {
            File f = new File("database.db");
            if (!f.exists()) {
                f.createNewFile();
            }
            String url = "jdbc:sqlite:" + f.getPath();
            conn = DriverManager.getConnection(url);

            log.logger.info("Connected to database");

            Statement stmt = conn.createStatement();
        } catch (SQLException | IOException e) {
            log.logger.warning(e.toString());
        }
    }

    public static void disconnect() {
        try {
            if (conn != null) {
                conn.close();
                log.logger.info("Disconnected from database");
            }
        } catch (SQLException e) {
            log.logger.warning(e.toString());
        }
    }

    public static void onUpdate(String sql){
        try{

            stmt.execute(sql);
        } catch (SQLException e) {
            log.logger.warning(e.toString());
        }
    }

    public static ResultSet onQuery(String sql){

        try{
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            log.logger.warning(e.toString());
        }

        return null;
    }
}