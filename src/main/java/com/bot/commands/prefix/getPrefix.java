package com.bot.commands.prefix;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class getPrefix extends Command {
    @Override
    public String call() {
        return "getPrefix";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        String prefix = null;
        if (event.getChannelType().isGuild()) {
            try (final Connection connection = SQLiteDataSource.getConnection();
                 final PreparedStatement preparedStatement = connection.prepareStatement("SELECT prefix FROM prefix WHERE guildid = ?")) {
                preparedStatement.setLong(1, event.getGuild().getIdLong());
                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        //return resultSet.getString("prefix");
                        prefix = resultSet.getString("prefix");

                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.green);
                        e.setTitle("Prefix on this server is'" + prefix + "'", null);
                        e.setDescription("You will always be able to call " + config.get("prefix") + "deletePrefix to" +
                                " reset the Prefix to " + config.get("prefix"));
                        e.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(e.build()).queue();

                    }
                }
            } catch (SQLException e) {
                log.logger.warning(e.toString());
            }
        }EmbedBuilder e = new EmbedBuilder();
        e.setColor(Color.red);
        e.setTitle("Something went wrong...", null);
        e.setDescription("You can't get the Prefix through a DM :( " +
                "\n" +
                "Please use a Server-TextChannel to get the Servers-Prefix");
        e.setFooter("presented by " + config.get("bot_name"));
        event.getChannel().sendMessageEmbeds(e.build()).queue();



        return false;
    }
}
