package com.bot.core.sql;

import com.bot.log.log;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.bot.core.config;

public class SQLiteDataSource {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        try {
            final File dbFile = new File("database.db");

            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    log.logger.info("Created database file");
                } else {
                    log.logger.info("Could not create database file");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        config.setJdbcUrl("jdbc:sqlite:database.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

        try (final Statement statement = getConnection().createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS voicehub (voicehubid INTEGER, categoryid INTEGER, guildid INTEGER,name STRING, userlimit INTEGER)");
            statement.execute("CREATE TABLE IF NOT EXISTS prefix (prefix STRING, guildid INTEGER)");
            statement.execute("CREATE TABLE IF NOT EXISTS stats(namemember STRING,namebooster STRING,nameonline STRING, categoryid INTEGER, memberid INTEGER,onlineid INTEGER, boosterid INTEGER)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private SQLiteDataSource() { }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}