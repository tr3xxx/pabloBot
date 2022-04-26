package com.bot.commands.core;

import com.bot.commands.basic.testCommand;
import net.dv8tion.jda.api.JDA;

public class CommandLoad {
    public CommandLoad(JDA jda){
        CommandManager.addCommand(new testCommand());
    }
}
