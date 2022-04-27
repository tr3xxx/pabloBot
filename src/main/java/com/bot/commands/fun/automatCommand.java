package com.bot.commands.fun;

import com.bot.commands.core.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class automatCommand extends Command {
    @Override
    public String call() {
        return "Automat";
    }


    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) {

        return false;
    }

}
