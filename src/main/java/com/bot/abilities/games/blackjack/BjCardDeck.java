package com.bot.abilities.games.blackjack;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class BjCardDeck {

    private Integer[] cards = new Integer[52];
    private int cardsGiven;


    BjCardDeck(){

        int value = 2; //gibt Karten ihren Wert
        int cCount = 0; //Zählt Karten zum auffüllen

        for (int i = 0; i < 52; i++) {

            if (cCount == 4 && value != 10 || value == 10 && cCount == 16) {  //Befüllt Kartendeck
                value++;
                cCount = 0;
            }

            cards[i] = value; //Arrayfeld wird eine Karte zugewiesen
            cCount++;

        }


    }

    public int drawCard(){

        int pos = ThreadLocalRandom.current().nextInt(52-cardsGiven);
        int newCard = cards[pos];
        cards[pos]=0;
        Arrays.sort(cards, Collections.reverseOrder());
        cardsGiven++;
        return newCard;
    }

    public void soutCards(){

        for(int i : cards){

            System.out.println(i);

        }

    }


}
