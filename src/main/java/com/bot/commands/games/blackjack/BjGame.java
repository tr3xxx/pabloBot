package com.bot.commands.games.blackjack;

import com.bot.core.config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import java.awt.*;
import java.io.ObjectInputFilter;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


public class BjGame implements Runnable {

    private BjPerson dealer = new BjPerson();
    private BjPerson player = new BjPerson();
    private BjCardDeck cards = new BjCardDeck();
    private MessageReceivedEvent e;
    public long messageID;

    BjGame(long messageID,MessageReceivedEvent event){

        this.messageID = messageID;
        e = event;
    }

    public void run() {

        int pos = 0;

        EmbedBuilder start = new EmbedBuilder();
        start.setTitle("Blackjack");
        start.setColor(Color.decode(config.get("color")));
        start.setFooter("presented by " + config.get("bot_name"));


       player.setCard(cards.drawCard(),pos);
       dealer.setCard(cards.drawCard(),pos);

        pos++;


        player.setCard(cards.drawCard(),pos);
        dealer.setCard(cards.drawCard(),pos);


        start.setDescription("You have " + player.getScore() + "\n" + "Dealer has " + dealer.getHandAt(0));

        e.getChannel().editMessageEmbedsById(messageID,start.build()).setActionRow(BjListener.sendButtons()).queue();

        try {
            pos++;
            turn(pos);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }


    public void turn(int pos) throws InterruptedException {



            BjDraw.buttonPushed = false;

            try {
                while(!BjDraw.buttonPushed){
                    Thread.sleep(200);

                    if(BjDraw.buttonPushed){break;}
                }

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            BjDraw.buttonPushed = false;


            if(BjDraw.draw) player.setCard(cards.drawCard(),pos);
            if(busting(player)){
                e.getChannel().editMessageEmbedsById(messageID,getWoLEmbed(2).build()).setActionRow(BjListener.sendOKButton()).queue();
                BjDraw.gameInProgress=false;
                return;
            }

            if(dealer.getScore()<17)dealer.setCard(cards.drawCard(),pos);


            if (busting(dealer)) {
                e.getChannel().editMessageEmbedsById(messageID, getWoLEmbed(1).build()).setActionRow(BjListener.sendOKButton()).queue();
                BjDraw.gameInProgress = false;
                return;
            }


            if(!BjDraw.draw) {
                e.getChannel().editMessageEmbedsById(messageID, getWoLEmbed(winOrLose()).build()).setActionRow(BjListener.sendOKButton()).queue();
                BjDraw.gameInProgress = false;
                return;
            }



                EmbedBuilder em = new EmbedBuilder();
                em.setTitle("Blackjack");
                em.setColor(Color.decode(config.get("Color")));
                em.setDescription("You have " + player.getScore() + "\n" + "Dealer has " + (dealer.getScore()-dealer.getHandAt(pos)));

                e.getChannel().editMessageEmbedsById(messageID,em.build()).queue();

                turn(pos++);

    }



    public int winOrLose(){ // 0 = nothing ; 1 = won ; 2 = lost ; 3 = draw

        if(dealer.getScore()> player.getScore()){
            return 2;
        }
        else if(player.getScore() > dealer.getScore()){
            return 1;
        }
        else if(player.getScore() == dealer.getScore()){
            return 3;
        }

        return 0;
    }

    public boolean busting(BjPerson p){
        return p.getScore() > 21;
    }

    public EmbedBuilder getWoLEmbed(int e){
        if(e == 1){
            EmbedBuilder s = new EmbedBuilder();
            s.setTitle("You won");
            s.setColor(Color.decode(config.get("color")));
            s.setDescription("You have " + player.getScore() + "\n" + "Dealer has " + dealer.getScore());
            s.setFooter("presented by " + config.get("bot_name"));
            return s;
        }
        else if(e==2){
            EmbedBuilder s = new EmbedBuilder();
            s.setTitle("You lost");
            s.setColor(Color.decode(config.get("color")));
            s.setDescription("You have " + player.getScore() + "\n" + "Dealer has " + dealer.getScore());
            s.setFooter("presented by " + config.get("bot_name"));
            return s;
        }
        else if(e==3){
            EmbedBuilder s = new EmbedBuilder();
            s.setTitle("It's a draw");
            s.setColor(Color.decode(config.get("color")));
            s.setDescription( "You have " + player.getScore() + "\n" + "Dealer has " + dealer.getScore());
            s.setFooter("presented by " + config.get("bot_name"));
            return s;
        }
        return null;
    }




    public static class BjListener extends ListenerAdapter{



        public void onButtonInteraction(ButtonInteractionEvent e){

            switch ((Objects.requireNonNull(e.getButton().getId()))){

                case "bjDraw" -> {

                    BjDraw.buttonPushed = true;
                    BjDraw.draw = true;

                }
                case "bjDrawNot" ->{
                    BjDraw.buttonPushed = true;
                    BjDraw.draw = false;
                }
                case "bjOver" ->{

                    e.getMessage().delete().queue();

                }


            }



        }

        public static List<Button> sendButtons(){


            List<Button> buttons = new ArrayList<>();
            buttons.add(Button.success("bjDraw","Draw"));
            buttons.add(Button.danger("bjDrawNot","Don't draw"));
            return buttons;

        }
        public static List<Button> sendOKButton(){
            List<Button> buttons = new ArrayList<>();
            buttons.add(Button.success("bjOver","Game is over"));
            return  buttons;
        }

   }


}
