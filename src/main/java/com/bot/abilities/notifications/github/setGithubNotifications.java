package com.bot.abilities.notifications.github;

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

public class setGithubNotifications extends Command {
    String[] ch_id;
    long channelid;
    String repo;
    @Override
    public String[] call() {
        return new String[]{"setGithubNotis", "setGithubNotifications", "sGN"};
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
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException { //!sGN tr3xxx/PabooBot <#979405505820770314>
                                                                                            // 0    1               2
        if (event.getChannelType().isGuild()) {
            if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) {
                if (args.length == 3) {
                    try {
                        String[] trimmed = args[2].trim().split("#");
                        ch_id = trimmed[1].trim().split(">");
                        channelid = Long.parseLong(ch_id[0]);
                        event.getGuild().getTextChannelById(channelid);
                        if(!githubCore.testRepo(args[1])) {
                            throw new RuntimeException("Invalid repo");

                        }
                        repo = args[1];
                    } catch (Exception err) {
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

                    try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                         final PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO githubNotifications(channelid,repo,lastsha) VALUES(?,?,?)")) {
                            insertStatement.setLong(1,channelid);
                            insertStatement.setString(2,repo);
                            insertStatement.setString(3,"null");
                            insertStatement.execute();

                            log.logger.info("New GithubNotifications Channel has been set  (Server: " + event.getGuild().getName() + ", Repository: " + args[1] + ", User: " + event.getAuthor().getAsTag());

                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.green);
                            e.setTitle("Github Notifications were successfully set", null);
                            e.setFooter("presented by " + config.get("bot_name"));
                            event.getChannel().sendMessageEmbeds(e.build()).queue();

                        } catch (SQLException e) {
                            log.logger.warning(getClass()+": "+e.toString());
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
                } else {
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.red);
                e.setTitle("Something went wrong...", null);
                e.setDescription("You don't have enough permissions :( " +
                        "\n" +
                        "In order to be able to set up Github Notifications, you need the permission to manage channels on this " +
                        "Server");
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).queue();

                    return false;

                }

                return false;
            } else {
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("Something went wrong...", null);
            e.setDescription("Github Notifications can not be set through DM's :( " +
                    "\n" +
                    "Please use a Server-TextChannel to set up Github Notifications");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue();

            return false;
            }


        }


    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesGN", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noGN", "No"));

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
                case "help_yesGN" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How to set up Github Notifications", null);
                    eb.setDescription("To set up Github Notifications you need to execute: \n" +
                            "'" + prefix + "setGithubNotifications _repository_ #channel' " +
                            "\n \n" +
                            "Replace the _repository_ with your Repository in the following structure: Githubusername/Repositoryname (eg. DV8FromTheWorld/JDA) \n"+
                            "Replace the _channel_ with channel you wish to get your Notifications"
                    );
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue();
                }
                case "help_noGN" -> {
                    try{
                        e.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                }
                default -> {
                }
            }

        }


        public void getPrefix() throws SQLException{
            String temp = null;

            try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                 final PreparedStatement preparedStatement = connection.prepareStatement("SELECT prefix FROM prefix WHERE guildid = ?")) {
                preparedStatement.setLong(1, e.getGuild().getIdLong());
                try(final ResultSet resultSet = preparedStatement.executeQuery()){
                    if(resultSet.next()){
                        //return resultSet.getString("prefix");
                        temp = resultSet.getString("prefix");
                        this.prefix = temp;

                    }
                }
            } catch (SQLException e) {
                log.logger.warning(getClass()+": "+e.toString());
            }

        }
    }
}

