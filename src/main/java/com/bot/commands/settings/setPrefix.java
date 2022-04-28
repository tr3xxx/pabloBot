package com.bot.commands.settings;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class setPrefix extends Command {
    @Override
    public String call() {
        return "setPrefix";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        System.out.println(args[0]);
        if (event.getChannelType().isGuild()) {
            if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) {
                if (args.length == 2) {
                    if (args[1].length() < 6) {
                        for(int i=0; i<args[1].length();i++){
                            if(Character.isLetter(args[1].charAt(i))){
                                EmbedBuilder e = new EmbedBuilder();
                                e.setColor(Color.red);
                                e.setTitle("Something went wrong...", null);
                                e.setDescription("Prefix contains an illegal character :( " +
                                        "\n \n" +
                                        "Please try another one/");
                                e.setFooter("presented by " + config.get("bot_name"));
                                event.getChannel().sendMessageEmbeds(e.build()).queue();
                                return false;
                            }
                        }
                        try (final Connection connection = SQLiteDataSource.getConnection();
                             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE prefix SET prefix = ? WHERE guildid = ?")) {
                            preparedStatement.setString(1, args[1]);
                            preparedStatement.setLong(2, event.getGuild().getIdLong());
                            preparedStatement.executeUpdate();


                            log.logger.info("New Server-Prefix has been set  (Server: " + event.getGuild().getName() + ", Prefix: " + args[1] + ", User: " + event.getAuthor().getAsTag());

                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.green);
                            e.setTitle("Prefix successfully set to '" + args[1] + "'", null);
                            e.setDescription("You will always be able to call "+config.get("prefix")+"deletePrefix to" +
                                    " reset the Prefix to "+config.get("prefix"));
                            e.setFooter("presented by " + config.get("bot_name"));
                            event.getChannel().sendMessageEmbeds(e.build()).queue();

                        } catch (SQLException e) {
                            log.logger.warning(e.toString());
                        }
                    } else {
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.red);
                        e.setTitle("Something went wrong...", null);
                        e.setDescription("Prefix may not be longer than 5 character:( " +
                                "\n \n" +
                                "Please choose a shorter Prefix/");
                        e.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(e.build()).queue();
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
            } else {
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


            return false;
        } else {
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

    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yes", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_no", "No"));

        return buttons;
    }



    public static class ButtonClick extends ListenerAdapter {

        public void onButtonInteraction(ButtonInteractionEvent e) {
            e.deferEdit().queue();

            switch (Objects.requireNonNull(e.getButton().getId())) {
                case "help_yes" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.green);
                    eb.setTitle("How to set a Prefix", null);
                    eb.setDescription("To set a Prefix you need to execute: \n" +
                            "'" + config.get("prefix") + "setPrefix ABC' " +
                            "\n \n" +
                            "Replace the 'ABC' with whatever you wish to be your new Prefix"
                    );
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue();
                }
                case "help_no" -> e.getMessage().delete().queue();
                default -> {
                }
            }

        }
    }
}
