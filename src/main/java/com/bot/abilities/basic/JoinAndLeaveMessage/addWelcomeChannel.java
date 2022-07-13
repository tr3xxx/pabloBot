package com.bot.abilities.basic.JoinAndLeaveMessage;

import com.bot.abilities.core.Command;
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

public class addWelcomeChannel extends Command {
    @Override
    public String[] call() {
        return new String[]{"addWelcomeChannel"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[0];
    }

    @Override
    public boolean usableInDM() {
        return false;
    }

    String[] ch_id;
    long channelid;
    String guildid;


    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        if (args.length == 2) {
            try {
                String[] trimmed = args[1].trim().split("#");
                ch_id = trimmed[1].trim().split(">");
                channelid = Long.parseLong(ch_id[0]);
                event.getGuild().getTextChannelById(channelid);

            } catch (Exception err) {
                System.out.println(err.toString());
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.red);
                e.setTitle("Something went wrong...", null);
                e.setDescription("You did not run this command correctly :( " +
                        "\n" +
                        "Do you want to learn how to do it correctly?");
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).setActionRow(yes_noBT()).queue();
                return false;
            }

            try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));) {

                final PreparedStatement getIds = connection.prepareStatement("SELECT channelid from WelcomeLeaveMessage");
                final ResultSet rs = getIds.executeQuery();

                boolean isInDatabase = false;

                while (rs.next()) {
                    //System.out.println("In Loop");
                    if (channelid == rs.getLong("channelid")) {
                        System.out.println("Is in Database");
                        isInDatabase = true;
                    }
                }
                if (!isInDatabase) {
                    final PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO WelcomeLeaveMessage(guildid) VALUES(?)");
                    insertStatement.setString(1,event.getGuild().toString());
                    insertStatement.execute();

                    final PreparedStatement updateQuery = connection.prepareStatement("UPDATE WelcomeLeaveMessage SET channelid = ? WHERE guildid = ?");
                    updateQuery.setLong(1,channelid);
                    updateQuery.setString(2,event.getGuild().toString());
                    updateQuery.executeUpdate();

                    log.logger.info("New Welcome-Channel has been set  (Server: " + event.getGuild().getName() + ", Repository: " + args[1] + ", User: " + event.getAuthor().getAsTag());

                    EmbedBuilder e = new EmbedBuilder();
                    e.setColor(Color.green);
                    e.setTitle("Welcome-Channel was successfully set", null);
                    e.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(e.build()).queue();
                } else {
                    EmbedBuilder e = new EmbedBuilder();
                    e.setColor(Color.green);
                    e.setTitle("Channel is already assigned to be a Welcome-Channel", null);
                    e.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(e.build()).queue();

                }
            } catch (SQLException e) {
                log.logger.warning(getClass() + ": " + e.toString());
            }
        } else {
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("Something went wrong...", null);
            e.setDescription("You did not run this command correctly :( " +
                    "\n" +
                    "Do you want to learn how to do it correctly?");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).setActionRow(yes_noBT()).queue();
            return false;
        }

        return false;
    }


    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesNC", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noNC", "No"));

        return buttons;
    }


    public static class ButtonClick extends ListenerAdapter {
        ButtonInteractionEvent e;
        String prefix;

        public void onButtonInteraction(ButtonInteractionEvent e) {
            //e.deferEdit().queue();
            this.e = e;
            try {
                getPrefix();
            } catch (SQLException ex) {
                log.logger.warning(ex.toString());
            }
            switch (Objects.requireNonNull(e.getButton().getId())) {
                case "help_yesNC" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How to set up Welcome-channel", null);
                    eb.setDescription("To set up Welcome-channel you need to execute: \n" +
                            "'" + prefix + "setWelcomeChannel #channel' "
                    );
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue();
                }
                case "help_noNC" -> {
                    try {
                        e.getMessage().delete().queue();
                    } catch (NullPointerException ignored) {
                    }
                }
                default -> {
                }
            }

        }


        public void getPrefix() throws SQLException {
            String temp = null;

            try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));
                 final PreparedStatement preparedStatement = connection.prepareStatement("SELECT prefix FROM prefix WHERE guildid = ?")) {
                preparedStatement.setLong(1, e.getGuild().getIdLong());
                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        //return resultSet.getString("prefix");
                        temp = resultSet.getString("prefix");
                        this.prefix = temp;

                    }
                }
            } catch (SQLException e) {
                log.logger.warning(getClass() + ": " + e.toString());
            }

        }
    }
}












