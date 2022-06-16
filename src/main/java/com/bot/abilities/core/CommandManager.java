package com.bot.abilities.core;

import com.bot.core.config;
import com.bot.core.sql.SQLDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.*;
import java.util.ArrayList;

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
            getPrefix();
            if(prefix == null){
                registerPrefix();
                getPrefix();
            }
        } catch (SQLException e) {
            log.logger.warning(getClass()+": "+e.toString());
        }

        String call = invoke.replace(prefix, "");

        commands.forEach(cmd -> {
            String [] aliases = cmd.call();
            for (String alias : aliases) {
                if(invoke.startsWith(prefix) && alias.equalsIgnoreCase(call) && !event.getAuthor().isBot()) {
                    try {
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
                        cmd.execute(msg, event);
                    } catch (SQLException e) {
                        log.logger.warning(getClass()+": "+e.toString());
                    }
                }
            }});

    }

    public void getPrefix() throws SQLException{
        String temp = null;

        try (final Connection connection = SQLDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT prefix FROM prefix WHERE guildid = ?")) {
            preparedStatement.setLong(1, event.getGuild().getIdLong());
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

    public void registerPrefix(){
        try (final Connection connection = SQLDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE prefix SET prefix = ? WHERE guildid = ?")) {
            preparedStatement.setString(1, config.get("prefix"));
            preparedStatement.setLong(2, event.getGuild().getIdLong());
            preparedStatement.executeUpdate();

            log.logger.info("New Server-Prefix has been set  (Server: " + event.getGuild().getName() + ", Prefix: " + config.get("prefix") + ", User: Bot");

        } catch (SQLException e) {
            log.logger.warning(getClass()+": "+e.toString());
        }
        try (final Connection connection = SQLDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO prefix(prefix,guildid) VALUES(?,?)")) {
            preparedStatement.setString(1, config.get("prefix"));
            preparedStatement.setLong(2, event.getGuild().getIdLong());
            preparedStatement.executeUpdate();

            log.logger.info("New Server-Prefix has been set  (Server: " + event.getGuild().getName() + ", Prefix: " + config.get("prefix") + ", User: Bot");

        } catch (SQLException e) {
            log.logger.warning(getClass()+": "+e.toString());
        }
    }



}


