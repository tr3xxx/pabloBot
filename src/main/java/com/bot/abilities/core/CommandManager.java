package com.bot.abilities.core;

import com.bot.core.config;
import com.bot.events.level.updateLevel;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;
import java.util.*;

public class CommandManager extends ListenerAdapter {
    MessageReceivedEvent event;
    String prefix;

   private static final ArrayList<Command> commands = new ArrayList<Command>();
   private static final ArrayList<Command> always = new ArrayList<Command>();

    public void load(JDA jda){
        jda.addEventListener(this);
        commands.forEach(jda::addEventListener);
    }

    public static void addCommand(Object obj){
        commands.add((Command) obj);
    }

    public static void addAlwaysCommand(Object obj){

        always.add((Command) obj);
    }
    
    public void onMessageReceived(MessageReceivedEvent event) {

        this.event = event;
        String[] msg = event.getMessage().getContentRaw().trim().split(" ");
        String invoke = msg[0];
        try {
            prefix = Prefix.getPrefix(event);
            // BETA --> prefix = config.get("prefix");
            if(prefix == null){
                Prefix.registerPrefix(event);
                Prefix.getPrefix(event);
            }
        } catch (SQLException e) {
            log.logger.warning(getClass()+": "+e.toString());
        }

        String call = invoke.replace(prefix, "");
        Permission userPerms[] = event.getMember().getPermissions().toArray(new Permission[0]);
        Channel channel = event.getChannel();

        commands.forEach(cmd -> {
            String [] aliases = cmd.call();
            Permission cmdPerms[] = cmd.getPermissions();
            boolean usableInDM = cmd.usableInDM();
            for (String alias : aliases) {
                if(invoke.startsWith(prefix) && alias.equalsIgnoreCase(call) && !event.getAuthor().isBot()) {
                    for (Permission permission : cmdPerms) {
                        if (!Arrays.asList(userPerms).contains(permission)) {
                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.red);
                            e.setTitle("You don't have enough Permissions to use this Command", null);
                            e.setDescription("You need the following Permissions: " + Arrays.toString(cmdPerms));
                            e.setFooter("presented by " + config.get("bot_name"));
                            event.getChannel().sendMessageEmbeds(e.build()).queue();
                            return;
                        }
                    }
                    if(!usableInDM && !channel.getType().isGuild()){
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.red);
                        e.setTitle("This Command cannot be used in Direct Messages", null);
                        e.setDescription("Please use this Command in a Server");
                        e.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(e.build()).queue();
                        return;
                    }
                    try {
                        event.getChannel().sendTyping().queue();
                        cmd.execute(msg, event);
                    } catch (SQLException e) {
                        log.logger.warning(getClass()+": "+e.toString());
                    }
                }
            }

        });
        always.forEach(cmd ->{
            String [] aliases = cmd.call();
            for (String alias : aliases) {
                if(invoke.startsWith(config.get("prefix")) && alias.equalsIgnoreCase(invoke.replace(config.get("prefix"), "")) && !event.getAuthor().isBot()){
                    try {
                        event.getChannel().sendTyping().queue();
                        cmd.execute(msg, event);
                    } catch (SQLException e) {
                        log.logger.warning(getClass()+": "+e.toString());
                    }
                }
            }});

            if(event.getMessage().getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()){
                toLivetimeStats();
            }


            updateLevel.messageLevelUpdate(event);
    }

    private void toLivetimeStats(){
        try {
            Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE livetime SET messages = messages + 1 ");
            statement.close();
            connection.close();
        } catch (SQLException e) {
            log.logger.warning(getClass()+": "+e.toString());
        }
    }



}


