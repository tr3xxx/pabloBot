package com.bot.abilities.prefix;

import com.bot.abilities.core.Command;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.*;

public class getPrefix extends Command {
    @Override
    public String[] call() {
        return new String[] {"getPrefix"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[] {};
    }

    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        String prefix = null;
            try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                 final PreparedStatement preparedStatement = connection.prepareStatement("SELECT prefix FROM prefix WHERE guildid = ?")) {
                preparedStatement.setLong(1, event.getGuild().getIdLong());
                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        //return resultSet.getString("prefix");
                        prefix = resultSet.getString("prefix");

                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.decode(config.get("color")));
                        e.setTitle("The prefix of this server is '" + prefix + "'", null);
                        e.setDescription("If you have enough permissions you can always call " + config.get("prefix") + "deletePrefix  to set the prefix back to '" + config.get("prefix")+"'");
                        e.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(e.build()).queue();
                        return false;

                    }
                }
            } catch (SQLException e) {
                log.logger.warning(getClass()+": "+e.toString());
            }
        return false;
    }
}
