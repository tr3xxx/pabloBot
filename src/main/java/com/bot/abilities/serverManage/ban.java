package com.bot.abilities.serverManage;

import com.bot.abilities.core.Command;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.PrivateChannel;
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

public class ban extends Command {
    Member member;
    String [] id;
    @Override
    public String[] call() {
        return new String[] {"ban"};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        if(!(Objects.requireNonNull(event.getMember())).hasPermission(Permission.BAN_MEMBERS)){
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("Something went wrong...", null);
            e.setDescription("You don't have enough permissions :( " +
                    "\n" +
                    "In order to be able to ban Members, you need the permission to " +
                    "ban Members on this Server");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue(m -> {
                try{
                    m.delete().queueAfter(10, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });
        }
        if (args.length > 3) {
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
            String[] trimmed = args[1].trim().split("@");
            id = trimmed[1].trim().split(">");
            long memberid = Long.parseLong(id[0]);
            member = event.getGuild().getMemberById(memberid);

            if(args.length==3){
                member.ban(0,args[2]).queue();
            }
            else{
                member.ban(0).queue();
            }

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

        EmbedBuilder e = new EmbedBuilder();
        e.setColor(Color.red);
        e.setTitle(member.getUser().getAsTag()+" got banned", null);
        e.setFooter("presented by " + config.get("bot_name"));
        event.getChannel().sendMessageEmbeds(e.build()).queue(m -> {
            try{
                m.delete().queueAfter(10, TimeUnit.SECONDS);
            }catch(NullPointerException ignored){}

        });


        EmbedBuilder ep = new EmbedBuilder();
        ep.setColor(Color.red);
        ep.setTitle("You got banned from "+event.getGuild().getName()+" by "+event.getMessage().getAuthor().getAsTag(), null);
        if(args.length==3){
            ep.setDescription("Ban-Reason: "+args[3]);
        }
        ep.setFooter("presented by " + config.get("bot_name"));
        PrivateChannel channel = member.getUser().openPrivateChannel().complete();
        try{
            channel.sendMessageEmbeds(ep.build()).queue();
        }catch(Exception ignored){}
        event.getMessage().delete().queue();
        return false;

    }

    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesban", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noban", "No"));

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
                case "help_yesban" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How to ban Member", null);
                    eb.setDescription("To ban Member you need to execute: \n" +
                            "'" + prefix + "ban _@member_' " +
                            "\n \n" +
                            "Replace _@member_ with the Member you want to ban\n"+
                            "You can optionally add a reason after mention the user"
                    );
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(60, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}

                    });
                }
                case "help_noban" -> {
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
