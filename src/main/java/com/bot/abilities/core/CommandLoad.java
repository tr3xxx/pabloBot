package com.bot.abilities.core;


import com.bot.abilities.basic.testCommand;
import com.bot.abilities.fun.automatCommand;
import com.bot.abilities.games.blackjack.BjMain;
import com.bot.abilities.games.russianRoulette.RussianRouletteMain;
import com.bot.abilities.manage.stats.createStats;
import com.bot.abilities.manage.stats.setStatsNames;
import com.bot.abilities.music.*;
import com.bot.abilities.notifications.github.setGithubNotifications;
import com.bot.abilities.prefix.deletePrefix;
import com.bot.abilities.prefix.getPrefix;
import com.bot.abilities.prefix.setPrefix;
import com.bot.abilities.reddit.redditiwantevent;
import com.bot.abilities.reddit.redditcommand;
import com.bot.abilities.serverManage.ban;
import com.bot.abilities.serverManage.clear;
import com.bot.abilities.serverManage.kick;
import com.bot.abilities.serverManage.timeout;
import com.bot.abilities.voice.voicehub.setGeneratedNames;
import com.bot.abilities.voice.voicehub.setVoiceUserLimit;
import com.bot.abilities.voice.voicehub.setVoicehub;
import net.dv8tion.jda.api.JDA;


public class CommandLoad {
    public CommandLoad(JDA jda) {


        // Normal Commands, call dependents on the servers prefix
        CommandManager.addCommand(new testCommand());
        CommandManager.addCommand(new automatCommand());
        CommandManager.addCommand(new setVoicehub());
        CommandManager.addCommand(new setPrefix());
        CommandManager.addCommand(new createStats());
        CommandManager.addCommand(new setStatsNames());
        CommandManager.addCommand(new setGeneratedNames());
        CommandManager.addCommand(new setVoiceUserLimit());
        CommandManager.addCommand(new play());
        CommandManager.addCommand(new stop());
        CommandManager.addCommand(new pause());
        CommandManager.addCommand(new resume());
        CommandManager.addCommand(new skip());
        CommandManager.addCommand(new loop());
        CommandManager.addCommand(new search());
        CommandManager.addCommand(new BjMain());
        CommandManager.addCommand(new leave());
        CommandManager.addCommand(new join());
        CommandManager.addCommand(new queue());
        CommandManager.addCommand(new redditcommand());
        CommandManager.addCommand(new redditiwantevent());
        CommandManager.addCommand(new clear());
        CommandManager.addCommand(new RussianRouletteMain());
        CommandManager.addCommand(new setGithubNotifications());
        CommandManager.addCommand(new ban());
        CommandManager.addCommand(new kick());
        CommandManager.addCommand(new timeout());

        // Always with getPrefix callable on any server
        CommandManager.addAlwaysCommand(new deletePrefix());
        CommandManager.addAlwaysCommand(new getPrefix());
    }
}