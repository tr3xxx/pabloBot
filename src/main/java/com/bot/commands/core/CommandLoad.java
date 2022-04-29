package com.bot.commands.core;

import com.bot.commands.basic.help.helpCommand;
import com.bot.commands.basic.help.helpPrefix;
import com.bot.commands.basic.help.helpVoicehub;
import com.bot.commands.basic.help.helpfun.helpAutomat;
import com.bot.commands.basic.help.helpfun.helpFun;
import com.bot.commands.basic.testCommand;
import com.bot.commands.fun.automatCommand;
import com.bot.commands.prefix.deletePrefix;
import com.bot.commands.prefix.getPrefix;
import com.bot.commands.prefix.setPrefix;
import com.bot.commands.voice.voicehub.setVoicehub;
import net.dv8tion.jda.api.JDA;

public class CommandLoad {
    public CommandLoad(JDA jda) {
        CommandManager.addCommand(new testCommand());
        CommandManager.addCommand(new automatCommand());
        CommandManager.addCommand(new setVoicehub());
        CommandManager.addCommand(new setPrefix());
        CommandManager.addAlwaysCommand(new deletePrefix());
        CommandManager.addAlwaysCommand(new getPrefix());
        CommandManager.addCommand(new helpCommand());
        CommandManager.addCommand(new helpPrefix());
        CommandManager.addCommand(new helpVoicehub());
        CommandManager.addCommand(new helpFun());
        CommandManager.addCommand(new helpAutomat());
    }
}