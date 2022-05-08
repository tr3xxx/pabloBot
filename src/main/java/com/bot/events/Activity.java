package com.bot.events;
import com.bot.core.config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Timer;
import java.util.TimerTask;


public class Activity {
    int index = 0;
    int members;

    public Activity(JDA jda) {

        new Timer().schedule(new TimerTask() {
            public void run() {
                if(members==0){
                    jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.playing("booting..."));
                    jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);

                        jda.getGuilds().forEach(guild ->{
                            members = members + guild.getMemberCount();
                        });

                }
                else {
                    switch (index) {
                        case 0 -> {
                            jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.streaming("for " + members+ " people", "https://www.twitch.tv/."));
                            index = 1;
                        }
                        case 1 -> {
                            jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.streaming("on " + jda.getGuilds().size() + " Servers", "https://www.twitch.tv/."));
                            index = 0;
                        }
                    }
                }
                // log.logger.info("Activity got changed");  Wären 14.000+ changes daily --> Log spam

            }
        }, 0, 10_000);
    }
}

