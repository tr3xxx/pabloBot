package com.bot;

import com.bot.log.log;
import com.bot.commands.test;
import com.bot.listeners.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class bot {
    public static void main(String[] args) throws LoginException, IOException {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd_HHmmss");
        log log= new log("./logs/"+"log_"+ formatter.format(new Date(System.currentTimeMillis())) +".log");
        SQLite.connect();
        try {
            JDA jda = JDABuilder.createDefault(config.get("token"))
                    .addEventListeners(new test())
                    .addEventListeners(new ReadyListener())
                    .build();

            new com.bot.events.Activity(jda);
            new console(jda);

        } catch (LoginException e) {
            com.bot.log.log.logger.warning(e.toString());
        }





    }

}