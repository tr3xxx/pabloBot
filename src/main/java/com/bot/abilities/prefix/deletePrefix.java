package com.bot.abilities.prefix;

import com.bot.abilities.core.Command;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class deletePrefix extends Command
{
    @Override
    public String[] call() {
        return new String[] {"deletePrefix"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[] {Permission.MANAGE_SERVER};
    }

    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

                try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                     final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE prefix SET prefix = ? WHERE guildid = ?")) {
                    preparedStatement.setString(1, config.get("prefix"));
                    preparedStatement.setLong(2, event.getGuild().getIdLong());
                    preparedStatement.executeUpdate();


                    log.logger.info("New Server-Prefix has been set  (Server: " + event.getGuild().getName() + ", Prefix: " + config.get("prefix") + ", User: " + event.getAuthor().getAsTag());

                    EmbedBuilder e = new EmbedBuilder();
                    e.setColor(Color.green);
                    e.setTitle("The prefix was successfully reset to '" + config.get("prefix") + "'", null);
                    e.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(e.build()).queue();

                } catch (SQLException e) {
                    log.logger.warning(getClass()+": "+e.toString());
                }


        return false;
    }
}
