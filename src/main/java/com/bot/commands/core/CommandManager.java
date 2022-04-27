package com.bot.commands.core;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.util.ArrayList;

public class CommandManager extends ListenerAdapter {

   private static ArrayList<Command> commands = new ArrayList<Command>();

    public void load(JDA jda){
        jda.addEventListener(this);
        commands.forEach(obj -> {
            jda.addEventListener(obj);
        });
    }

    public static void addCommand(Object obj){

        commands.add((Command) obj);
    }

    public void onMessageReceived(MessageReceivedEvent event) {


        String[] msg = event.getMessage().getContentRaw().trim().split(" ");
        String invoke = msg[0];
        String call = invoke.replace(config.get("prefix"), "");
        String[] args = msg;


        commands.forEach(cmd -> {
            if(invoke.startsWith(config.get("prefix")) && cmd.call().equalsIgnoreCase(call) && !event.getAuthor().isBot()) {
                try {
                    cmd.execute(args, event);
                } catch (SQLException e) {
                    log.logger.warning(e.toString());
                }
            }
        });

    }

}


