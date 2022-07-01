package com.bot.abilities.manage.welcomeMessages;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class setWelcomeMessage extends Command {
    @Override
    public String[] call() {
        return new String[] {"setWelcomeMessage"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        if (args.length == 2) {
            try {
                String[] trimmed = args[1].trim().split("#");
                String[] ch_id = trimmed[1].trim().split(">");
                long channelID = Long.parseLong(ch_id[0]);
                event.getGuild().getTextChannelById(channelID);
                String preMessage = "Welcome to {server}, {member}!";

                try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));
                     final PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO welcomeChannel(channelID,message) VALUES(?,?)")) {
                    insertStatement.setLong(1, channelID);
                    insertStatement.setString(2, preMessage);
                    insertStatement.execute();

                    EmbedBuilder e = new EmbedBuilder();
                    e.setColor(Color.green);
                    e.setTitle("Welcome-Channel has been set successfully", null);
                    e.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(e.build()).setActionRow(yes_noBT()).queue();
                    return false;

                }catch(SQLException e){
                    log.logger.warning(getClass().getName() + ": " + e.getMessage());
                }
            }
            catch (Exception err) {
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
        }
        else{
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
        return false;}



    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesWC", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noWC", "No"));

        return buttons;
    }


    public static class ButtonClick extends ListenerAdapter {
        ButtonInteractionEvent e;
        String prefix;

        public void onButtonInteraction(ButtonInteractionEvent e) {
            e.deferEdit().queue();
            this.e = e;
            try {
                prefix = Prefix.getPrefix(e);
            } catch (SQLException ex) {
                log.logger.warning(ex.toString());
            }
            switch (Objects.requireNonNull(e.getButton().getId())) {
                case "help_yesWC" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How to set a Welcome-Channel", null);
                    eb.setDescription("To set a Welcome-Channel you need to execute: \n" +
                            "'" + prefix + "setWelcomeChannel <#channelid>' " +
                            "\n \n" +
                            "Replace _channelID_ with the id of the channel which you want Welcome Messages to be sent to.");

                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(more_helpBT()).queue();
                }
                case "help_noWC" -> {
                    try{
                        e.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                }
                default -> {
                }
            }

        }

        private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> more_helpBT() {
            java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
            buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.link("https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-",
                    "How to get the Channel-ID"));

            return buttons;
        }
    }


}

