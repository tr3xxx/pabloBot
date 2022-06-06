package com.bot.core;

import com.bot.abilities.core.CommandLoad;
import com.bot.abilities.core.CommandManager;
import com.bot.abilities.notifications.github.GithubCommitNotifications;
import com.bot.abilities.notifications.github.githubCore;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.events.Activity;
import com.bot.events.updateStats;
import com.bot.listeners.Boot;
import com.bot.log.log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class bot {
    public static JDA jda;
    public static void main(String[] args) throws LoginException, IOException, SQLException {

        new log("./logs/" + "log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis())) + ".log");
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
            jda.addEventListener(new Boot());



        } catch (Exception  e) {
            log.logger.warning(bot.class +": "+e.toString());
        }


    }
    public static void loadComponents() throws SQLException, IOException {
        new EventListenersLoad().load(jda);
        new CommandManager().load(jda);
        new CommandLoad(jda);
        new updateStats();
        new console(jda);
        new Activity(jda);
        new githubCore();
        new GithubCommitNotifications();
        new Redditcore();
        new githubCore();
    }

}