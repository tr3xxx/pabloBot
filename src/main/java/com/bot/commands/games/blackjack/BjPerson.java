package com.bot.commands.games.blackjack;

import java.util.Arrays;

public class BjPerson {

    private Integer[] hand = new Integer[10];


    public BjPerson(){

        Arrays.fill(hand,0);

    }

    public void setCard(int val,int pos){

        //System.out.println("Person setCard() pos " + pos);
        if(val == 11 && (getScore()+val)>21){
            val = 1;
        }

        hand[pos]= val;

        //System.out.println("Person setCard() val " + val );
        //System.out.println("Person setCard() hand[pos] " + hand[pos]);

    }

    public Integer[] getHand(){

        return this.hand;

    }

    public int getHandAt(int pos){


        //System.out.println(pos +" pos in getHand before if");
            //System.out.println(hand[pos] + " hand[pos] getHandAt if()");
            return hand[pos];


    }

    public int getScore(){
        int score =0;
        for(int i : hand){
            score = score + i;
        }
        return score;

    }

    public void soutScore(){
        for(int i : hand){
            System.out.println(i);
        }
    }

}
