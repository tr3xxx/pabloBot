package com.bot.events;
import com.bot.core.config;
import net.dv8tion.jda.api.JDA;
import java.util.Timer;
import java.util.TimerTask;


public class Activity {
    String[] activities ={config.get("status1"), config.get("status2")};
    int index=0;

    public Activity(JDA jda){

        new Timer().schedule(new TimerTask(){
            public void run(){

                switch (index) {
                    case 0 -> {
                        jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.competing(activities[1]));
                        index = 1;
                    }
                    case 1 -> {
                        jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.listening(activities[0]));
                        index = 0;
                    }

                }
                // log.logger.info("Activity got changed");  Wären 14.000+ changes daily --> Log spam

            }},0,10_000);

    }
    }

