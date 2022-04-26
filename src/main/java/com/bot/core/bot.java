package com.bot.core;

import com.bot.commands.basic.testCommand;
import com.bot.commands.core.CommandLoad;
import com.bot.commands.core.CommandManager;
import com.bot.log.log;
import com.bot.listeners.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class bot {
    public static void main(String[] args) throws LoginException, IOException {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd_HHmmss");
        new log("./logs/"+"log_"+ formatter.format(new Date(System.currentTimeMillis())) +".log");
        SQLite.connect();
        try {
            JDA jda = JDABuilder.createDefault(config.get("token"))
                    .addEventListeners(new ReadyListener())
                    .build();

            new CommandManager().load(jda);
            new CommandLoad(jda);
            new console(jda);
            new com.bot.events.Activity(jda);


        } catch (LoginException e) {
            com.bot.log.log.logger.warning(e.toString());
        }




    }

}