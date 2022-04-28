package com.bot.commands.core;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        } catch (SQLException e) {
            log.logger.warning(e.toString());
        }
        String call = invoke.replace(prefix, "");


        commands.forEach(cmd -> {
            if(invoke.startsWith(prefix) && cmd.call().equalsIgnoreCase(call) && !event.getAuthor().isBot()) {
                try {
                    cmd.execute(msg, event);
                } catch (SQLException e) {
                    log.logger.warning(e.toString());
                }
            }
        });
        always.forEach(cmd ->{
            if(invoke.startsWith(config.get("prefix")) && cmd.call().equalsIgnoreCase(invoke.replace(config.get("prefix"), "")) && !event.getAuthor().isBot()){
                try {
                    cmd.execute(msg, event);
                } catch (SQLException e) {
                    log.logger.warning(e.toString());
                }

            }});

    }

    public void getPrefix() throws SQLException{
        String temp = null;

        try (final Connection connection = SQLiteDataSource.getConnection();
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
            log.logger.warning(e.toString());
        }

    }



}


