package com.bot.events.reactionroles;

import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.awt.*;
import java.sql.*;
import java.util.Objects;

public class onReact extends ListenerAdapter {

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        long msgID = event.getMessageIdLong();
        String emoji = event.getReaction().getReactionEmote().getEmoji();

        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT role FROM reactionroles WHERE msgid = ? AND emoji = ?")) {
            preparedStatement.setLong(1, msgID);
            preparedStatement.setString(2,emoji);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    long roleID = resultSet.getLong("role");
                    Role role = event.getGuild().getRoleById(roleID);
                    if (role != null) {
                        if (!Objects.requireNonNull(event.getMember()).getRoles().contains(role)) {
                            event.getGuild().addRoleToMember(event.getMember(), role).queue();

                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.green);
                            e.setTitle("You got the Role: " + Objects.requireNonNull(event.getGuild().getRoleById(roleID)).getName() + " on " + event.getGuild().getName(), null);
                            e.setFooter("presented by " + config.get("bot_name"));
                            if(!event.getMember().getUser().isBot()) {
                                event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(e.build()).queue());
                            }
                            return;
                        } else {
                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.red);
                            e.setTitle("You already have the Role: " + Objects.requireNonNull(event.getGuild().getRoleById(roleID)).getName() + " on " + event.getGuild().getName(), null);
                            e.setFooter("presented by " + config.get("bot_name"));
                            if(!event.getMember().getUser().isBot()) {
                                event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(e.build()).queue());
                            }
                            return;
                        }
                    }
                }
            }
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        long msgID = event.getMessageIdLong();
        String emoji = event.getReaction().getReactionEmote().getName();

        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT role FROM reactionroles WHERE msgid = ? AND emoji = ?")) {
            preparedStatement.setLong(1, msgID);
            preparedStatement.setString(2,emoji);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    long roleID = resultSet.getLong("role");
                    Role role = event.getGuild().getRoleById(roleID);
                    if (role != null) {
                        if (Objects.requireNonNull(event.getMember()).getRoles().contains(role)) {
                            event.getGuild().removeRoleFromMember(event.getMember(), role).queue();

                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.red);
                            e.setTitle("You lost the Role: " + Objects.requireNonNull(event.getGuild().getRoleById(roleID)).getName() + " on " + event.getGuild().getName(), null);
                            e.setFooter("presented by " + config.get("bot_name"));
                            event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(e.build()).queue());
                            return;
                        }

                    }
                }
            }
        }
        catch(SQLException ignored) {}
    }
}