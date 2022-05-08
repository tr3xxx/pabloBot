package com.bot.core;

import com.bot.commands.core.CommandLoad;
import com.bot.commands.core.CommandManager;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.events.Activity;
import com.bot.events.updateStats;
import com.bot.log.log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class bot {
    public static JDA jda;
    public static void main(String[] args) throws LoginException, IOException, SQLException {

        try {
            new log("./logs/" + "log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis())) + ".log");
        }catch(Exception e){
            throw e;
        }
        SQLiteDataSource.getConnection();

        try {
            jda = JDABuilder.createDefault(config.get("token"))
                    .enableIntents(GatewayIntent.GUILD_PRESENCES)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableCache(CacheFlag.ONLINE_STATUS)
                    .enableCache(CacheFlag.ACTIVITY)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build();

            new EventListenersLoad().load(jda);
            new CommandManager().load(jda);
            new CommandLoad(jda);
            new updateStats();
            new console(jda);
            new Activity(jda);


        } catch (LoginException  e) {
            log.logger.warning(e.toString());
        }


    }

}