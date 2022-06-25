package com.bot.events;
import com.bot.core.bot;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;


public class Activity {
    private int index = 0;
    private int members;
    private int messages;
    private int voiceMinutes;
    private boolean first = true;

    public Activity() {

        new Timer().schedule(new TimerTask() {
            public void run() {
                if(first){
                    bot.jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.playing("booting..."));
                    bot.jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                    first = false;
                }
                    if(getDetails()) {
                        switch (index) {
                            case 0 -> {
                                bot.jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.streaming("to " + members + " people", "https://www.twitch.tv/."));
                                index = 1;
                            }
                            case 1 -> {
                                bot.jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.streaming(messages + " sent messages", "https://www.twitch.tv/."));
                                index = 2;
                            }
                            case 2 -> {
                                bot.jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.streaming("on " + bot.jda.getGuilds().size() + " Servers", "https://www.twitch.tv/."));

                                index = 3;
                            }
                            case 3 -> {
                                bot.jda.getPresence().setActivity(net.dv8tion.jda.api.entities.Activity.streaming(voiceMinutes + " min in voice chat", "https://www.twitch.tv/."));
                                index = 0;
                            }
                        }
                    }
                // log.logger.info("Activity got changed");  Wären 14.000+ changes daily --> Log spam

            }
        }, 0, 10_000);
    }

    private boolean getDetails(){
        members = 0;
        messages = 0;
        voiceMinutes = 0;

        bot.jda.getGuilds().forEach(guild ->{
            members = members + guild.getMemberCount();
        });

        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT messages FROM livetime")) {
            try(final ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    messages = resultSet.getInt("messages");
                }
                else{throw new SQLException("No Result");}

            }
            try(final PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT minutesVC FROM livetime")) {
                try(final ResultSet resultSet = preparedStatement1.executeQuery()){
                    if(resultSet.next()){
                        voiceMinutes = resultSet.getInt("minutesVC");
                    }
                    else{throw new SQLException("No Result");}

                }
            }

        } catch (SQLException e) {
            log.logger.warning(getClass()+": "+e.toString());
        }
        return true;
    }
}

