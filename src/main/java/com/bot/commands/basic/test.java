package com.bot.commands.basic;

import com.bot.commands.core.Command;
import com.bot.core.config;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class test extends Command {
    @Override
    public String call() {
        return "test";
    }

    @Override
    public String help() {
        return config.get("prefix")+"test";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) {

        event.getChannel().sendMessage("Hallo "+event.getAuthor().getAsMention()).queue();

        return false;
    }
}
