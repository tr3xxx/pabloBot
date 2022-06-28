package com.bot.abilities.notifications.twitch;

import com.bot.abilities.core.Command;
import com.bot.abilities.core.Prefix;
import com.bot.core.config;

import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class setTwitchNotifications extends Command {
    String[] ch_id;
    long channelid;
    String name;
    String repo;
    @Override
    public String[] call() {
        return new String[]{"setTwitchNotis", "setTwitchNotifications", "sTN"};
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

        if (args.length == 3) {
            try {
                String[] trimmed = args[2].trim().split("#");
                ch_id = trimmed[1].trim().split(">");
                channelid = Long.parseLong(ch_id[0]);
                event.getGuild().getTextChannelById(channelid);
                name = args[1];
            } catch (Exception err) {
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.red);
                e.setTitle("Something went wrong...", null);
                e.setDescription("You have not executed this command correctly! " +
                        "\n" +
                        "Do you want to learn how to do it right?");
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).setActionRow(yes_noBT()).queue();
                return false;
            }

            try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                 final PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO twitchChannels(channelid,streamer) VALUES(?,?)")) {
                insertStatement.setLong(1,channelid);
                insertStatement.setString(2,name);
                insertStatement.execute();

                log.logger.info("New Twitch Notifications Channel has been set  (Server: " + event.getGuild().getName() + ", Streamer: " + args[1] + ", User: " + event.getAuthor().getAsTag());

                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.green);
                e.setTitle("Twitch Notifications were successfully set", null);
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).queue();

            } catch (SQLException e) {
                log.logger.warning(getClass()+": "+e.toString());
            }
        } else {
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.getColor("#800080"));
            e.setTitle("Something went wrong...", null);
            e.setDescription("You have not executed this command correctly! " +
                    "\n" +
                    "Do you want to learn how to do it right?");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).setActionRow(yes_noBT()).queue();
            return false;
        }
        return false;
    }


    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesTN", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noTN", "No"));

        return buttons;
    }



    public static class ButtonClick extends ListenerAdapter {
        ButtonInteractionEvent e;
        String prefix;
        public void onButtonInteraction(ButtonInteractionEvent e) {
            //e.deferEdit().queue();
            this.e = e;
            try {
                prefix = Prefix.getPrefix(e);
            } catch (SQLException ex) {
                log.logger.warning(ex.toString());
            }
            switch (Objects.requireNonNull(e.getButton().getId())) {
                case "help_yesTN" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.getColor("#800080"));
                    eb.setTitle("How to set up Twitch Notifications", null);
                    eb.setDescription("To set up Twitch Notifications you need to execute: \n" +
                            "'" + prefix + "setTwitchNotifications _streamer_ #channel' " +
                            "\n \n" +
                            "Replace the streamer with the Streamer you want to get Notifications from\n"+
                            "Replace the _channel_ with channel you wish to get your Notifications"
                    );
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue();
                }
                case "help_noTN" -> {
                    try{
                        e.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                }
                default -> {
                }
            }

        }
    }
}

