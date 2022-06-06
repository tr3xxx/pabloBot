package com.bot.abilities.games.russianRoulette;

public class RRLRevolver {

    private int shots;
    private int size;

    public RRLRevolver(int shots,int size){

        this.shots = shots;
        this.size = size;

    }

    public int getShots(){
        return shots;
    }
    public int getSize(){
        return size;
    }

}
