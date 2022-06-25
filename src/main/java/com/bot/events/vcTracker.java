package com.bot.events;

import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class vcTracker extends ListenerAdapter{
       private boolean first = true;
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event){
        if(event.getMember().getUser().getIdLong() == event.getJDA().getSelfUser().getIdLong()){
            new Timer().schedule(new TimerTask() {
                public void run() {
                    if (Objects.requireNonNull(event.getMember().getVoiceState()).inAudioChannel()) {
                        if(!first) {
                            try {
                                Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                                Statement statement = connection.createStatement();
                                statement.executeUpdate("UPDATE livetime SET minutesVC = minutesVC + 1 ");
                                statement.close();
                                connection.close();
                            } catch (SQLException e) {
                                log.logger.warning(getClass()+": "+e.toString());
                            }
                        }else{
                            first = false;
                        }
                    } else {
                        cancel();
                        }
                    }

            }, 0, 60000);
        }
    }

}
