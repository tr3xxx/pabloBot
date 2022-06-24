package com.bot.abilities.games.blackjack;

import com.bot.abilities.core.Command;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class BjMain extends Command {


    @Override
    public String[] call() {
        return new String[] {"blackjack","bj"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[] {};
    }

    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        event.getMessage().delete().queue();
        log.logger.info(getClass().getName()+" was executed");
        if(!BjDraw.gameInProgress){
            EmbedBuilder start = new EmbedBuilder();
            start.setColor(Color.decode(config.get("color")));
            start.setTitle("Blackjack", null);
            start.setFooter("presented by " + config.get("bot_name"));

            event.getChannel().sendMessageEmbeds(start.build()).queue(message -> {
                BjGame game = new BjGame(message.getIdLong(), event);
                Thread gameThread = new Thread(game);
                BjDraw.gameInProgress = true;
                gameThread.run();
                BjDraw.gameInProgress = false;
                try{
                    message.delete().queueAfter(3, TimeUnit.MINUTES);
                }catch(NullPointerException ignored){}
            });
        }
        else{
            EmbedBuilder start = new EmbedBuilder();
            start.setColor(Color.decode(config.get("color")));
            start.setTitle("Blackjack still in Progress", null);
            start.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(start.build()).queue(message -> {

                try {
                    message.delete().queueAfter(10,TimeUnit.SECONDS);
                }
                catch (NullPointerException ignored){}

            });
        }


        return false;


    }

}
