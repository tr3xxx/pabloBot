package com.bot.commands.basic.help.helpfun;

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

public class helpFun extends Command {

    private String prefix;

    @Override
    public String call() {
        return "hFun";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        log.logger.info("["+getClass().getName()+"] was executed by "+event.getAuthor().getAsTag()+" on '"+event.getGuild().getName()+"'");

        try (final Connection connection = SQLiteDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT prefix FROM prefix WHERE guildid = ?")) {
            preparedStatement.setLong(1, event.getGuild().getIdLong());
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    //return resultSet.getString("prefix");
                    prefix = resultSet.getString("prefix");
                }
            }
        } catch (SQLException e) {
            log.logger.warning(e.toString());
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode(config.get("color")));
        eb.setTitle("Fun help Commands", null);
        eb.setDescription(prefix + "**hAutomat**"
        );
        eb.setFooter("presented by " + config.get("bot_name"));
        event.getChannel().sendMessageEmbeds(eb.build()).queue();



        return false;
    }
}
