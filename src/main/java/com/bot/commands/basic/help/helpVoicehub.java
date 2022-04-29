package com.bot.commands.basic.help;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.SQLException;

public class helpVoicehub extends Command {
    @Override
    public String call() {
        return "hVoicehub";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        log.logger.info("["+getClass().getName()+"] was executed by "+event.getAuthor().getAsTag()+" on '"+event.getGuild().getName()+"'");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode(config.get("color")));
        eb.setTitle("How to set a VoiceHub", null);
        eb.setDescription("To set a VoiceHub you need to execute: \n" +
                "'" + config.get("prefix") + "setVoicehub <#channelid>' " +
                "\n \n" +
                "Replace the 'channelid' with the ID of the desired channel"
        );
        eb.setFooter("presented by " + config.get("bot_name"));
        event.getChannel().sendMessageEmbeds(eb.build()).queue();



        return false;
    }
}
