package com.bot.commands.fun;
import com.bot.log.log;
import com.bot.commands.core.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class automatCommand extends Command {
    @Override
    public String[] call() {
        return new String[] {"automat"};
    }


    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) {

        Automat automat = new Automat();
        automat.wortPruefen((String) args[1]);
        int value = automat.getZustand();

        if(value == 1){
            event.getChannel().sendMessage("Wort in der Sprache").queue();
        }
        else if(value == 0){
            event.getChannel().sendMessage("Wort nicht in der Sprache").queue();
        }
        else{
            event.getChannel().sendMessage("Alphabet falsch was tust du").queue();
        }

        log.logger.info("["+getClass().getName()+"] was executed by "+event.getAuthor().getAsTag()+" on '"+event.getGuild().getName()+"'");

        return false;
    }


    private class Automat{

        private int zustand;

        private void zustandWechseln(char eingabe){


            switch(zustand){
                case 0 :

                    if(eingabe == 'a'){zustand = 0;}
                    else if(eingabe == 'b'){zustand = 1;}
                    else{zustand = 3;}
                    break;

                case 1 :

                   if(eingabe == 'a'){this.zustand = 0;}
                   else if(eingabe == 'b'){zustand = 1;}
                   else{this.zustand= 3;}
                   break;
            }

        }

        public int wortPruefen(String wort){

                char[] alph = wort.toCharArray();

                for(char c : alph){
                   zustandWechseln(c);
                    if(zustand==3){return 3;}
              }
                if(zustand == 1){return 1;}
                return 0;
        }

        public int getZustand(){return this.zustand;}
    }



}
