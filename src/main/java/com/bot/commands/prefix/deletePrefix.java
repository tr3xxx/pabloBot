package com.bot.commands.prefix;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class deletePrefix extends Command
{
    @Override
    public String call() {
        return "deletePrefix";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        if (event.getChannelType().isGuild()) {
            if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) {

                try (final Connection connection = SQLiteDataSource.getConnection();
                     final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE prefix SET prefix = ? WHERE guildid = ?")) {
                    preparedStatement.setString(1, config.get("prefix"));
                    preparedStatement.setLong(2, event.getGuild().getIdLong());
                    preparedStatement.executeUpdate();


                    log.logger.info("New Server-Prefix has been set  (Server: " + event.getGuild().getName() + ", Prefix: " + config.get("prefix") + ", User: " + event.getAuthor().getAsTag());

                    EmbedBuilder e = new EmbedBuilder();
                    e.setColor(Color.green);
                    e.setTitle("Prefix successfully reseted to '" + config.get("prefix") + "'", null);
                    e.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(e.build()).queue();

                } catch (SQLException e) {
                    log.logger.warning(e.toString());
                }
            }else {
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.red);
                e.setTitle("Something went wrong...", null);
                e.setDescription("You don't have enough permissions :( " +
                        "\n" +
                        "In order to be able to change the Server-Prefix, you need the permission to " +
                        "manage this Server");
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).queue();
            }
        }else {
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("Something went wrong...", null);
            e.setDescription("Voicehubs can not be set through DM's :( " +
                    "\n" +
                    "Please use a Server-TextChannel to set a Voicehub");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue();
        }

        return false;
    }
}
