package com.bot.abilities.core;

import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.*;

public class Prefix {

    public static String getPrefix(MessageReceivedEvent event) throws SQLException {
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT prefix FROM prefix WHERE guildid = ?")) {
            preparedStatement.setLong(1, event.getGuild().getIdLong());
            try(final ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    //return resultSet.getString("prefix");
                    return resultSet.getString("prefix");


                }
            }
        } catch (SQLException e) {
            log.logger.warning(Prefix.class +": "+e.toString());
        }
        return null;
    }

    public static String getPrefix(ButtonInteractionEvent event) throws SQLException {
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT prefix FROM prefix WHERE guildid = ?")) {
            preparedStatement.setLong(1, event.getGuild().getIdLong());
            try(final ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    //return resultSet.getString("prefix");
                    return resultSet.getString("prefix");


                }
            }
        } catch (SQLException e) {
            log.logger.warning(Prefix.class +": "+e.toString());
        }
        return null;
    }

    public static void registerPrefix(MessageReceivedEvent event){
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE prefix SET prefix = ? WHERE guildid = ?")) {
            preparedStatement.setString(1, config.get("prefix"));
            preparedStatement.setLong(2, event.getGuild().getIdLong());
            preparedStatement.executeUpdate();

            log.logger.info("New Server-Prefix has been set  (Server: " + event.getGuild().getName() + ", Prefix: " + config.get("prefix") + ", User: Bot");

        } catch (SQLException e) {
            log.logger.warning(Prefix.class +": "+e.toString());
        }
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO prefix(prefix,guildid) VALUES(?,?)")) {
            preparedStatement.setString(1, config.get("prefix"));
            preparedStatement.setLong(2, event.getGuild().getIdLong());
            preparedStatement.executeUpdate();

            log.logger.info("New Server-Prefix has been set  (Server: " + event.getGuild().getName() + ", Prefix: " + config.get("prefix") + ", User: Bot");

        } catch (SQLException e) {
            log.logger.warning(Prefix.class +": "+e.toString());
        }
    }

}
