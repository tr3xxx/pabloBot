package com.bot.commands.core;


import com.bot.commands.basic.testCommand;
import com.bot.commands.fun.automatCommand;
import com.bot.commands.games.blackjack.BjMain;
import com.bot.commands.games.russianRoulette.RussianRouletteMain;
import com.bot.commands.manage.stats.createStats;
import com.bot.commands.manage.stats.setStatsNames;
import com.bot.commands.music.*;
import com.bot.commands.notifications.github.setGithubNotifications;
import com.bot.commands.prefix.deletePrefix;
import com.bot.commands.prefix.getPrefix;
import com.bot.commands.prefix.setPrefix;
import com.bot.commands.reddit.redditiwantevent;
import com.bot.commands.reddit.redditcommand;
import com.bot.commands.serverManage.clear;
import com.bot.commands.voice.voicehub.setGeneratedNames;
import com.bot.commands.voice.voicehub.setVoiceUserLimit;
import com.bot.commands.voice.voicehub.setVoicehub;
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

        // Always with getPrefix callable on any server
        CommandManager.addAlwaysCommand(new deletePrefix());
        CommandManager.addAlwaysCommand(new getPrefix());
    }
}