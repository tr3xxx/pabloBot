package com.bot.core;

import com.bot.commands.core.CommandLoad;
import com.bot.commands.core.CommandManager;
import com.bot.commands.prefix.setPrefix;
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
    public static void main(String[] args) throws LoginException, IOException, SQLException {

        new log("./logs/"+"log_"+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis())) +".log");
        SQLiteDataSource.getConnection();

        try {
            JDA jda = JDABuilder.createDefault(config.get("token")).build();

            new EventListenersLoad().load(jda);
            new CommandManager().load(jda);
            new CommandLoad(jda);
            new console(jda);
            new com.bot.events.Activity(jda);


        } catch (LoginException e) {
            com.bot.log.log.logger.warning(e.toString());
        }




    }

}