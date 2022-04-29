package com.bot.commands.basic.help;

import com.bot.commands.core.Command;
import com.bot.core.config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.SQLException;

public class helpPrefix extends Command {
    @Override
    public String call() {
        return "hPrefix";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode(config.get("color")));
        eb.setTitle("How to set a Prefix", null);
        eb.setDescription(
                "**set Prefix** \n \n"+
                        "To set a Prefix you need to execute: \n" +
                        "'" + config.get("prefix") + "setPrefix _character_' " +
                        "\n \n" +
                        "Replace the _character_ with whatever you wish to be your new Prefix"
                        +"\n \n"
                        +"**Reset** \n \n"
                        +"You will always be able to call " + config.get("prefix") + "deletePrefix to" +
                        " reset the Prefix to " + config.get("prefix")
        );
        eb.setFooter("presented by " + config.get("bot_name"));
        event.getChannel().sendMessageEmbeds(eb.build()).queue();

        return false;
    }
}
