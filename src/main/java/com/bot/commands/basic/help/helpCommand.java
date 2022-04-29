package com.bot.commands.basic.help;

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

public class helpCommand extends Command {

    private String prefix;

    @Override
    public String call() {return "help";}

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {





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




        EmbedBuilder e = new EmbedBuilder();
        e.setColor(Color.decode(config.get("color")));
        e.setTitle("Here is help",null);
        e.setDescription(
                prefix + "**hFun**"
                + "\n"
                + prefix + "**hPrefix**"
                + "\n"
                + prefix + "**hVoicehub**"

        );
        e.setFooter("presented by " + config.get("bot_name"));
        event.getChannel().sendMessageEmbeds(e.build()).queue();

        return false;
    }
}
