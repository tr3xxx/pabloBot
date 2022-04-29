package com.bot.commands.basic.help.helpfun;

import com.bot.commands.core.Command;
import com.bot.core.config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.SQLException;

public class helpAutomat extends Command {
    @Override
    public String call() {
        return "hAutomat";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode(config.get("color")));
        eb.setTitle("Why you even want to use this", null);
        eb.setDescription( "A = {a,b} \n V = q0,q1 \n S = q0 \n \n q0 = 'a' q0 \n q0 = 'b' q1 " +
                "\n q1 = 'b' q1 \n q1 = 'a' q0 \n q1 = ε \n \n put the wort behind the command"
        );
        eb.setFooter("presented by " + config.get("bot_name"));
        event.getChannel().sendMessageEmbeds(eb.build()).queue();


        return false;
    }
}
