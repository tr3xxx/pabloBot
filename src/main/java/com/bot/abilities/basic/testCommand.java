package com.bot.abilities.basic;

import com.bot.abilities.core.Command;
import com.bot.log.log;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class testCommand extends Command {
    @Override
    public String[] call() {
        return new String[] {"test","testanomi"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[] {};
    }

    @Override
    public boolean usableInDM() {
        return true;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent e) {

        e.getChannel().sendMessage("Hallo "+e.getAuthor().getAsMention()).queue();
        log.logger.info("["+getClass().getName()+"] was executed by "+e.getAuthor().getAsTag()+" on '"+e.getGuild().getName()+"'");
        return false;
    }
}
