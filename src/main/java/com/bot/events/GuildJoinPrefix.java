package com.bot.events;

import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GuildJoinPrefix extends ListenerAdapter {

    public void onGuildJoin(GuildJoinEvent event){
        action(event);
    }

    public boolean action(GuildJoinEvent event){
        try (final Connection connection = SQLiteDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE prefix SET prefix = ? WHERE guildid = ?")) {
            preparedStatement.setString(1, config.get("prefix"));
            preparedStatement.setLong(2, event.getGuild().getIdLong());
            preparedStatement.executeUpdate();

            log.logger.info("New Server-Prefix has been set  (Server: " + event.getGuild().getName() + ", Prefix: " + config.get("prefix") + ", User: Bot");
            return false;

        } catch (SQLException e) {
            log.logger.warning(e.toString());
        }
        try (final Connection connection = SQLiteDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO prefix(prefix,guildid) VALUES(?,?)")) {
            preparedStatement.setString(1, config.get("prefix"));
            preparedStatement.setLong(2, event.getGuild().getIdLong());
            preparedStatement.executeUpdate();

            log.logger.info("New Server-Prefix has been set  (Server: " + event.getGuild().getName() + ", Prefix: " + config.get("prefix") + ", User: Bot");
            return false;

        } catch (SQLException e) {
            log.logger.warning(e.toString());
        }
        return false;
    }

}