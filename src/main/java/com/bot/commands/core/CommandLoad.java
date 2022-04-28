package com.bot.commands.core;

import com.bot.commands.basic.testCommand;
import com.bot.commands.fun.automatCommand;
import com.bot.commands.voice.voicehub.setVoicehub;
import net.dv8tion.jda.api.JDA;

public class CommandLoad {
    public CommandLoad(JDA jda){
        CommandManager.addCommand(new testCommand());
        CommandManager.addCommand(new setVoicehub());
        CommandManager.addCommand(new automatCommand());
    }
}
