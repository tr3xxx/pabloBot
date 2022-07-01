package com.bot.abilities.notifications.twitch;

import com.bot.core.config;
import com.bot.log.log;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.checkerframework.checker.units.qual.A;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class twitchCore {
    static private TwitchClient twitchClient;
    static private SimpleEventHandler eventHandler;

    public twitchCore() {
       /* twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withDefaultEventHandler(SimpleEventHandler.class)
                .build();

        System.out.println(1);
        eventHandler = new SimpleEventHandler();
        System.out.println(2);
        twitchClient.getEventManager().registerEventHandler(eventHandler);
        System.out.println(3);
        new Timer().schedule(new TimerTask() {
            public void run() {
                try {
                    System.out.println(4);
                    getStreamer();
                    System.out.println(5);
                    EventWaiter();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }}, 0, 60000);

        }

    private void getStreamer() throws SQLException {
        try{
            Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT streamer FROM twitchChannels");
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
            if(resultSet.next()){
                createListener(resultSet.getString("streamer"));
            }
            }
        }catch (SQLException e){
            log.logger.warning(getClass()+": "+e.toString());
        }

    }

    public static void createListener(String name){
        twitchClient.getClientHelper().enableStreamEventListener(name);
    }

    private ArrayList<Long> getChannels(String streamer){
        ArrayList<Long> channels = new ArrayList<>();
        try{
            Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT channelid FROM twitchChannels WHERE streamer = ?");
            preparedStatement.setString(1, streamer);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()){
                    channels.add(resultSet.getLong("channelid"));
                }
            }
        }catch (SQLException e){
            log.logger.warning(getClass()+": "+e.toString());
        }
        return channels;
    }

    private void EventWaiter(){
        System.out.println(twitchClient.getEventManager().getEventHandlers().toString());

        eventHandler.onEvent(ChannelGoLiveEvent.class, event -> {
            ArrayList <Long> channels = getChannels(event.getChannel().getName());
            channels.forEach(ch -> {
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.getColor("#800080"));
                e.setTitle(event.getChannel().getName()+ " went LIVE with "+event.getStream().getGameName() ,null);
                e.setThumbnail(event.getStream().getThumbnailUrl());
                e.setFooter("presented by " + config.get("bot_name"));
            });

        });
    }*/

    }

}
