package com.bot.commands.basic;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class testCommand extends Command {
    @Override
    public String call() {
        return "test";
    }

    @Override
    public String help() {
        return config.get("prefix")+"test";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent e) {

        e.getChannel().sendMessage("Hallo "+e.getAuthor().getAsMention()).queue();
        log.logger.info("["+getClass().getName()+"] was executed by "+e.getAuthor().getAsTag()+" on '"+e.getGuild().getName()+"'");

        return false;
    }
}