package com.bot.core;

import com.bot.commands.core.CommandLoad;
import com.bot.commands.core.CommandManager;
import com.bot.commands.voice.voicehub.setVoicehub;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.events.GuildJoin;
import com.bot.listeners.VoiceHub;
import com.bot.log.log;
import com.bot.listeners.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class bot {
    public static JDA jda;
    public static void main(String[] args) throws LoginException, IOException, SQLException {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd_HHmmss");
        new log("./logs/"+"log_"+ formatter.format(new Date(System.currentTimeMillis())) +".log");

        SQLiteDataSource.getConnection();
        try {
            jda = JDABuilder.createDefault(config.get("token"))
                    .addEventListeners(new ReadyListener())
                    .addEventListeners(new setVoicehub.ButtonClick())
                    .addEventListeners(new VoiceHub())
                    .addEventListeners(new GuildJoin())
                    .build();

            new CommandManager().load(jda);
            new CommandLoad(jda);
            new com.bot.events.Activity(jda);
            new console(jda); // hier 'drunter' keine obj erstellen, da console --> == while(true) <fix: thread()...>



        } catch (LoginException e) {
            com.bot.log.log.logger.warning(e.toString());
        }




    }

}