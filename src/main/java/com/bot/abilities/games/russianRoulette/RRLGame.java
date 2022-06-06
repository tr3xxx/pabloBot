package com.bot.abilities.games.russianRoulette;

import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.ThreadLocalRandom;

public class RRLGame implements Runnable {

    final RRLRevolver revolver;
    final int rounds;
    private long messageID;
    final MessageReceivedEvent event;

    public RRLGame(int size,int shots,int rounds,MessageReceivedEvent event){

        revolver = new RRLRevolver(shots,size);
        this.rounds = rounds;
        this.event = event;

    }


    @Override
    public void run() {
        int p; //Probability
        int getShot;
        EmbedBuilder inGame = new EmbedBuilder();
        inGame.setTitle("Russian Roulette");
        inGame.setFooter("presented by " + config.get("bot_name"));

        for(int i = 0; i < rounds;i++){

            p = revolver.getShots();
            getShot = ThreadLocalRandom.current().nextInt(revolver.getSize()-i)+1;

             inGame.setDescription("Round " + (i+1));

             event.getChannel().editMessageEmbedsById(messageID,inGame.build()).queue();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.logger.warning(getClass()+": "+e.toString());
            }

            if(getShot<=p){

                inGame.setDescription("You died"+" "+":skull: :skeleton:");
                event.getChannel().editMessageEmbedsById(messageID,inGame.build()).queue();
                return;
            }

            inGame.setDescription("You did not die");
            event.getChannel().editMessageEmbedsById(messageID,inGame.build()).queue();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.logger.warning(getClass()+": "+e.toString());
            }
        }
        inGame.setDescription("You Won");
        event.getChannel().editMessageEmbedsById(messageID,inGame.build()).queue();

    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }
}
