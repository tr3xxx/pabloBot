package com.bot.abilities.prefix;

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

public class setPrefix extends Command {
    @Override
    public String[] call() {
        return new String[] {"setPrefix"};
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
                    if (args[1].length() < 6) {
                        for(int i=0; i<args[1].length();i++){
                            if(Character.isLetter(args[1].charAt(i))){
                                EmbedBuilder e = new EmbedBuilder();
                                e.setColor(Color.red);
                                e.setTitle("Something went wrong...", null);
                                e.setDescription("The prefix contains an illegal character, please try another one ");
                                e.setFooter("presented by " + config.get("bot_name"));
                                event.getChannel().sendMessageEmbeds(e.build()).queue();
                                return false;
                            }
                        }
                        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE prefix SET prefix = ? WHERE guildid = ?")) {
                            preparedStatement.setString(1, args[1]);
                            preparedStatement.setLong(2, event.getGuild().getIdLong());
                            preparedStatement.executeUpdate();


                            log.logger.info("New Server-Prefix has been set  (Server: " + event.getGuild().getName() + ", Prefix: " + args[1] + ", User: " + event.getAuthor().getAsTag());

                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.green);
                            e.setTitle("The prefix successfully set to '" + args[1] + "'", null);
                            e.setDescription("If you have enough permissions you can always call " + config.get("prefix") + "deletePrefix  to set the prefix back to '" + config.get("prefix")+"'");
                            e.setFooter("presented by " + config.get("bot_name"));
                            event.getChannel().sendMessageEmbeds(e.build()).queue();

                        } catch (SQLException e) {
                            log.logger.warning(getClass()+": "+e.toString());
                        }
                    } else {
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.red);
                        e.setTitle("Something went wrong...", null);
                        e.setDescription("The prefix cannot be longer than 5 characters. \n Please choose a shorter one");
                        e.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(e.build()).queue();
                    }
                } else {
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

                return false;
    }

    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesPrefix", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noPrefix", "No"));

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
                case "help_yesPrefix" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How do I set a prefix?", null);
                    eb.setDescription("To set a prefix you need to execute: \n" +
                            "'" + prefix + "setPrefix _character_' " +
                            "\n \n" +
                            "Replace _character_ with your desired prefix"
                    );
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue();
                }
                case "help_noPrefix" -> {
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
