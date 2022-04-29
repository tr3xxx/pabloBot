package com.bot.commands.games;

import com.bot.commands.core.Command;
import com.bot.core.config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class Blackjack extends Command {

    private Integer cards[] = new Integer[52];
    long id = 0;
    public static MessageReceivedEvent event;
    EmbedBuilder game = new EmbedBuilder();

    @Override
    public String call() {
        return "blackjack";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        this.event = event;

        boolean gameInProgress = true;
        int value = 2; //gibt Karten ihren Wert
        int count = 0; //Zählt Karten zum Auffüllen

        int dealer[] = new int[10]; //Hand des Dealers
        int player[] = new int[10]; //Hand des Spielers
        Arrays.fill(dealer,0);
        Arrays.fill(player,0);


        for(int i = 0; i<52; i++){

            if(count == 4 && value != 10 || value ==10 && count ==16){  //Befüllt Kartendeck
                value++;
                count = 0;
            }

            cards[i]= value; //Arrayfeld wird eine Karte zugewiesen
            count++;

        }
        blackjackGame t = new blackjackGame();

        EmbedBuilder e = new EmbedBuilder();
        EmbedBuilder s = new EmbedBuilder();
        s.setColor(Color.decode(config.get("color")));
        s.setTitle("Blackjack",null);
        s.setFooter("presented by " + config.get("bot_name"));

            event.getChannel().sendMessageEmbeds(s.build()).queue(message -> {
                id = message.getIdLong();
                event.getChannel().editMessageEmbedsById(id,e.build()).queue();
                t.game();
            });

        int cardsGiven = 0;
        int runs=0;
        int newCard;

        while(gameInProgress){


           if(runs <=1){ //verteilt die ersten 4 Karten

               newCard = ThreadLocalRandom.current().nextInt(52-cardsGiven); // Karte ziehen
               dealer[runs] = cards[newCard].intValue(); //gibt dem Dealer die Karte an einer zufälligen Stelle
               cards[newCard] = 0; //entfernt gezogene Karte aus dem Spiel
               Arrays.sort(cards,Collections.reverseOrder()); //Sortiert den Array so dass 0 immer hinten ist
               cardsGiven++;

               newCard = ThreadLocalRandom.current().nextInt(52-cardsGiven);
               player[runs] = cards[newCard].intValue();
               cards[newCard] = 0;
               Arrays.sort(cards,Collections.reverseOrder());
               cardsGiven++;
           }
           else{

               if(runs ==2){


                   e.setColor(Color.decode(config.get("color")));
                   e.setTitle("Blackjack",null);
                   e.setDescription("Dealer hat " + dealer[1] + "\n " +
                           "Du hast " + t.countCards(player));

                   e.setFooter("presented by " + config.get("bot_name"));

                gameInProgress = false;
               }
           }
            runs++;
        }
        return false;
    }

    private class blackjackGame{

        public int countCards(int c[]){

            int value=0;

            for(int i = 0; i<c.length;i++){

                value = value + c[i];

            }

            return value;
        }

        public void game(){


            game.setColor(Color.decode(config.get("color")));
            game.setTitle("Blackjack",null);
            game.setFooter("presented by " + config.get("bot_name"));

            event.getChannel().editMessageEmbedsById(id,game.build()).queue();


        }

    }

}
