package com.bot.abilities.ticketSystem;

import com.bot.abilities.core.Command;
import com.bot.abilities.core.Prefix;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.w3c.dom.Text;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class createTicketSystem extends Command {
    @Override
    public String[] call() {
        return new String[] {"createTicketSystem", "cTS"};
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
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException { // !createTicketSystem
        event.getMessage().delete().queue();
        try{
            Category category = event.getGuild().createCategory("Support").complete();
            TextChannel channel = category.createTextChannel("Support-Ticket").complete();
            channel.createPermissionOverride(event.getGuild().getPublicRole()).setDeny(EnumSet.of(Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION)).queue();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.decode(config.get("color")));
            eb.setTitle("Support-Tickets", null);
            eb.setDescription("Here you can create a ticket for your questions or problems.\n Create a ticket by clicking the button below.");
            eb.setFooter("presented by " + config.get("bot_name"));
            Message message =  channel.sendMessageEmbeds(eb.build()).setActionRow(createTicket()).complete();

            long messageID = message.getIdLong();
            long channelID = channel.getIdLong();
            long categoryID = category.getIdLong();

            try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                 final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ticketsystem (channelid, categoryid, messageid) VALUES (?,?,?)")) {
                preparedStatement.setLong(1, channelID);
                preparedStatement.setLong(2, categoryID);
                preparedStatement.setLong(3, messageID);
                preparedStatement.executeUpdate();
            }

            log.logger.info("TicketSystem created for " + event.getGuild().getName());
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.green);
            e.setTitle("Ticket System has been created ", null);
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue(m -> {
                try{
                    m.delete().queueAfter(10, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });
            return false;

        }catch(Exception ex){
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("Something went wrong...", null);
            e.setDescription("""
                    Couldn't create Ticket-System :(\s
                    \s
                    Please try again""");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue(m -> {
                try{
                    m.delete().queueAfter(10, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });
            return false;
        }
    }

    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> createTicket() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("createTicket", "Create Ticket"));
        return buttons;
    }
    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> closeTicket() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("closeTicket", "Close Ticket"));
        return buttons;
    }

    public static class ButtonClick extends ListenerAdapter {
        ButtonInteractionEvent e;
        String prefix;
        long channelID;
        long categoryID;
        long messageID;
        public void onButtonInteraction(ButtonInteractionEvent e) {
            Random ran = new Random();
            try {
                prefix = Prefix.getPrefix(e);
            } catch (SQLException ex) {
                log.logger.warning(ex.toString());
            }
            if(Objects.equals(e.getButton().getId(), "closeTicket")){
                TextChannel channel = (TextChannel) e.getChannel();
                channel.createPermissionOverride(Objects.requireNonNull(e.getMember())).setDeny(EnumSet.of(Permission.MESSAGE_SEND)).queue();
                channel.sendMessage("Ticket closed. Channel will be deleted soon.").queue();
                channel.delete().queueAfter(30, TimeUnit.SECONDS);

            }
            if(Objects.equals(e.getButton().getId(), "createTicket")){
                try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                     final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ticketsystem WHERE channelid = ?")) {
                    preparedStatement.setLong(1, e.getChannel().getIdLong());
                    preparedStatement.executeQuery();
                    java.sql.ResultSet resultSet = preparedStatement.getResultSet();
                    if(resultSet.next()){
                        channelID = e.getChannel().getIdLong();
                        categoryID = resultSet.getLong("categoryid");
                        messageID = resultSet.getLong("messageid");
                    }
                    else{
                        throw new SQLException("No Ticket-System found for this channel");
                    }
                } catch (SQLException ex) {
                    log.logger.warning(ex.toString());
                }
                    int ticketNumber = ran.nextInt(1000,10000);
                    TextChannel ticketChannel = e.getJDA().getTextChannelById(channelID);
                    Category category = e.getJDA().getCategoryById(categoryID);

                    TextChannel channel = category.createTextChannel("ticket-"+ticketNumber).complete();


                    channel.createPermissionOverride(Objects.requireNonNull(e.getMember())).setAllow(EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_HISTORY, Permission.MESSAGE_EMBED_LINKS)).queue();
                    channel.createPermissionOverride(e.getGuild().getPublicRole()).setDeny(EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_HISTORY, Permission.MESSAGE_EMBED_LINKS)).queue();

                    Message mentionMessage = channel.sendMessage(e.getMember().getAsMention()).complete();
                    mentionMessage.delete().queue();


                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("Ticket #"+ticketNumber, null);
                    eb.setDescription("Please describe your problem in detail and precisely, if necessary, indicate reproduction steps\n" +
                            "A moderator will deal with your problem soon\n" +
                            "Please do not use this ticket for other purposes than to solve your problem\n" +
                            "If your Problem has been solved or you accidentally created an ticket click the button below to close your ticket"
                    );
                    eb.setFooter("presented by " + config.get("bot_name"));
                    channel.sendMessageEmbeds(eb.build()).setActionRow(closeTicket()).queue();

                }
            }
        }
    }



