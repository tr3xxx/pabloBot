package com.bot.commands.games.russianRoulette;

import com.bot.commands.core.Command;
import com.bot.core.config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class RussianRouletteMain extends Command {

    @Override
    public String[] call() {
        return new String[]{"russianRoulette","rrl"};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        event.getMessage().delete().queue();

        boolean failed = false;
        int size = 0;
        int shots = 0;
        int rounds = 0;

        if(args.length == 4){

            try {
                size = Integer.parseInt(args[1]);
            }
            catch (Exception e){failed = true;}

            if(size < 5 || size > 10){
                failed = true;
            }
            try {
                shots = Integer.parseInt(args[2]);
            }catch (Exception e){failed = true;}

            if(shots > size || shots < 0){
                failed = true;
            }
            try {
                rounds = Integer.parseInt(args[3]);
            }catch (Exception e){failed = true;}

            if(rounds > size || rounds < 0){
                failed = true;
            }
        }
        else {
            failed = true;
        }

        if(failed){
            EmbedBuilder eFailed = new EmbedBuilder();
            eFailed.setTitle("How To Play RRL");
            eFailed.setDescription(config.get("Prefix") + "rrl"+ " size shots rounds"+"\n"+" size between 5 and 10, shots and rounds < size");
            eFailed.setFooter("presented by " + config.get("bot_name"));

            event.getChannel().sendMessageEmbeds(eFailed.build()).queue(message -> {
                message.delete().queueAfter(3, TimeUnit.MINUTES);
            });

            return false;
        }

        EmbedBuilder start = new EmbedBuilder();
        start.setTitle("Russian Roulette");
        start.setFooter("presented by " + config.get("bot_name"));

        RRLGame game = new RRLGame(size,shots,rounds,event);

        event.getChannel().sendMessageEmbeds(start.build()).queue(message -> {
            game.setMessageID(message.getIdLong());
            Thread runnableGame = new Thread(game);
            runnableGame.run();

            try{
                message.delete().queueAfter(3, TimeUnit.MINUTES);
            }catch(NullPointerException ignored){}

        });


        return false;
    }
}
