package com.bot.commands.basic;

import com.bot.commands.core.Command;
import com.bot.core.bot;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class testCommand extends Command {
    @Override
    public String call() {
        return "test";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent e) {

        e.getChannel().sendMessage("Hallo "+e.getAuthor().getAsMention()).queue();
        log.logger.info("["+getClass().getName()+"] was executed by "+e.getAuthor().getAsTag()+" on '"+e.getGuild().getName()+"'");
        return false;
    }
}
