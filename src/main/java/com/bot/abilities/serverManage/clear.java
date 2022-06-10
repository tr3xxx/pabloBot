package com.bot.abilities.serverManage;

import com.bot.abilities.core.Command;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class clear extends Command {
    int value;

    @Override
    public String[] call() {
        return new String[]{"clear", "deleteLast"};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        if(!(Objects.requireNonNull(event.getMember())).hasPermission(Permission.MESSAGE_MANAGE)){
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("Something went wrong...", null);
            e.setDescription("You don't have enough permissions :( " +
                    "\n" +
                    "In order to be able to delete Messages, you need the permission to " +
                    "manage Messages on this Server");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue(m -> {
                try{
                    m.delete().queueAfter(10, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });
        }

        if (args.length != 2) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("Something went wrong...", null);
            eb.setDescription("You did not run this command correctly :( " +
                    "\n" +
                    "Do you want to learn how to do it correctly?");
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });
            return false;
        }
        try {
            value = Integer.parseInt(args[1]) + 1;
        } catch (Exception e) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("Something went wrong...", null);
            eb.setDescription("You did not run this command correctly :( " +
                    "\n" +
                    "Do you want to learn how to do it correctly?");
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });
            return false;
        }
        MessageHistory history = event.getChannel().getHistory();

        List<Message> retrievedHistory = history.retrievePast(value).complete();
        if(history.getRetrievedHistory().size()<value){
            value = history.getRetrievedHistory().size();
        }
        for (int i = 0; i < value; i++) {
            retrievedHistory.get(i).delete().queue();
        }
        EmbedBuilder e = new EmbedBuilder();
        e.setColor(Color.green);
        e.setTitle(value-1 + " Messages got deleted", null);
        e.setFooter("presented by " + config.get("bot_name"));
        event.getChannel().sendMessageEmbeds(e.build()).queue(m -> {
            try{
                m.delete().queueAfter(10, TimeUnit.SECONDS);
            }catch(NullPointerException ignored){}

        });
        return false;
    }

    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesClear", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noClear", "No"));

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
                case "help_yesClear" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How to delete Messages", null);
                    eb.setDescription("To delete Messages you need to execute: \n" +
                            "'" + prefix + "clear _amount_' " +
                            "\n \n" +
                            "Replace _amount_ with the amount of Messages you want to delete"
                    );
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(60, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}

                    });
                }
                case "help_noClear" -> {
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

            try (final Connection connection = SQLiteDataSource.getConnection();
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
