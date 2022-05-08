package com.bot.lavaplayer;

import com.bot.core.bot;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.commons.collections4.map.HashedMap;

import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PlayerManager {

    public static PlayerManager INSTANCE;
    public static Map<Long, GuildMusicManager> musicManagers;
    public static AudioPlayerManager audioPlayerManager;
    private static final LinkedBlockingQueue<TextChannel> channels = new LinkedBlockingQueue<TextChannel>();;

    public PlayerManager() {
        musicManagers = new HashedMap<>();
        audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);

    }

    public static GuildMusicManager getMusicManager(Guild guild){
        return PlayerManager.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(PlayerManager.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public static void nextInQueue(AudioTrack track){
        if (track != null|| channels.peek() != null){
            TextChannel textChannel = channels.peek();
            final GuildMusicManager musicManager = PlayerManager.getMusicManager(textChannel.getGuild());
            final AudioManager audioManager = textChannel.getGuild().getAudioManager();
            AudioPlayer audioPlayer = musicManager.audioPlayer;
            String title = track.getInfo().title;
            String author = track.getInfo().author;
            Boolean isStream = track.getInfo().isStream;
            Long length = track.getDuration();
            if (track.getSourceManager().getSourceName().equals("youtube")) {
                String[] id = track.getInfo().uri.trim().split("=");
                String thumb = "https://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg".replace("<insert-youtube-video-id-here>", id[1]);
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.decode(config.get("color")));
                e.setTitle(":arrow_forward: **NOW PLAYING**", null);
                e.setImage(thumb);
                e.setDescription("**" + title + "** \n(" + (length / 1000) / 60 + " min) \n by **" + author + "** \n\n " + track.getInfo().uri);
                e.setFooter("presented by " + config.get("bot_name"));

                textChannel.sendMessageEmbeds(e.build()).setActionRow(pause0Rstop()).queue();
            } else {
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.decode(config.get("color")));
                e.setTitle(":arrow_forward: **NOW PLAYING**", null);
                e.setDescription("**" + title + "** \n by **" + author + "** \n\n " + track.getInfo().uri);
                e.setFooter("presented by " + config.get("bot_name"));

                textChannel.sendMessageEmbeds(e.build()).setActionRow(pause0Rstop()).queue();
            }
            log.logger.info("Playing Song ("+track.getInfo().uri+") on ("+textChannel.getGuild().getName()+")");

        }
        else{
            if(channels.peek() == null){
                log.logger.warning("Channel is null in nextInQueue()");
            }
            else{
                log.logger.warning("Track is null in nextInQueue()");
            }
        }
    }

    public void loadAndPlay(TextChannel textChannel, String trackURL,String input){
        final GuildMusicManager musicManager = getMusicManager(textChannel.getGuild());
        final AudioManager audioManager = textChannel.getGuild().getAudioManager();
        AudioPlayer audioPlayer = musicManager.audioPlayer;

        audioPlayerManager.loadItemOrdered(musicManager, trackURL,new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);

                musicManager.scheduler.queue(audioTrack);
                String title = audioTrack.getInfo().title;
                String author = audioTrack.getInfo().author;
                long length = audioTrack.getDuration();
                if (musicManager.scheduler.audioPlayer.getPlayingTrack() != audioTrack) {
                    channels.add(textChannel);
                    if (audioTrack.getSourceManager().getSourceName().equals("youtube")) {
                        String[] id = audioTrack.getInfo().uri.trim().split("=");
                        String thumb = "https://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg".replace("<insert-youtube-video-id-here>", id[1]);
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.decode(config.get("color")));
                        e.setTitle(":white_check_mark:  **QUEUED**", null);
                        e.setDescription("**" + title + "** \n(" + (length / 1000) / 60 + " min) \n by **" + author + "** \n\n " + audioTrack.getInfo().uri);
                        e.setFooter("presented by " + config.get("bot_name"));

                        textChannel.sendMessageEmbeds(e.build()).setActionRow(playNow()).queue(m -> {
                                try{
                                    m.delete().queueAfter(10, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                        });
                    } else {
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.decode(config.get("color")));
                        e.setTitle(":white_check_mark:  **QUEUED**", null);
                        e.setDescription("**" + title + "** \n by **" + author + "** \n\n " + audioTrack.getInfo().uri);
                        e.setFooter("presented by " + config.get("bot_name"));

                        textChannel.sendMessageEmbeds(e.build()).setActionRow(playNow()).queue(m -> {
                            try{
                                m.delete().queueAfter(10, TimeUnit.SECONDS);
                            }catch(NullPointerException ignored){}
                        });
                    }
                    log.logger.info("Playing Song ("+audioTrack.getInfo().uri+") on ("+textChannel.getGuild().getName()+ ")");
                } else {
                    if (audioTrack.getSourceManager().getSourceName().equals("youtube")) {
                        String[] id = audioTrack.getInfo().uri.trim().split("=");
                        String thumb = "https://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg".replace("<insert-youtube-video-id-here>", id[1]);
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.decode(config.get("color")));
                        e.setTitle(":arrow_forward: **NOW PLAYING**", null);
                        e.setImage(thumb);
                        e.setDescription("**" + title + "** \n(" + (length / 1000) / 60 + " min) \n by **" + author + "** \n\n " + audioTrack.getInfo().uri);
                        e.setFooter("presented by " + config.get("bot_name"));

                        if(musicManager.scheduler.repeating){
                            textChannel.sendMessageEmbeds(e.build()).setActionRow(pause0RstopLOOP()).queue(m -> {
                                try{
                                    m.delete().queueAfter(1, TimeUnit.HOURS);
                                }catch(NullPointerException ignored){}
                            });
                        }else{
                            textChannel.sendMessageEmbeds(e.build()).setActionRow(pause0Rstop()).queue(m -> {
                                try{
                                    m.delete().queueAfter(1, TimeUnit.HOURS);
                                }catch(NullPointerException ignored){}
                            });
                        }
                    } else {
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.decode(config.get("color")));
                        e.setTitle(":arrow_forward: **NOW PLAYING**", null);
                        e.setDescription("**" + title + "** \n by **" + author + "** \n\n " + audioTrack.getInfo().uri);
                        e.setFooter("presented by " + config.get("bot_name"));

                        if(musicManager.scheduler.repeating){
                            textChannel.sendMessageEmbeds(e.build()).setActionRow(pause0RstopLOOP()).queue();
                        }else{
                            textChannel.sendMessageEmbeds(e.build()).setActionRow(pause0Rstop()).queue();
                        }
                    }
                    log.logger.info("Queued Song ("+audioTrack.getInfo().uri+") on ("+textChannel.getGuild().getName()+")");
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                if(!tracks.isEmpty()){
                    Random rand = new Random();

                    AudioTrack track = tracks.get(rand.nextInt(0,10));
                    musicManager.scheduler.queue(track);
                    String title = track.getInfo().title;
                    String author = track.getInfo().author;
                    Boolean isStream = track.getInfo().isStream;
                    Long length = track.getDuration();
                    if(musicManager.scheduler.audioPlayer.getPlayingTrack() != track){
                        channels.add(textChannel);
                        if(track.getSourceManager().getSourceName().equals("youtube")) {
                            String[] id = track.getInfo().uri.trim().split("=");
                            String thumb = "https://img.youtube.com/vi/<vidid>/mqdefault.jpg".replace("<vidid>",id[1]);
                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.decode(config.get("color")));
                            e.setTitle(":white_check_mark:  **QUEUED**", null);
                            e.setDescription("**"+title+"** \n("+(length/1000)/60+" min) \n by **"+author+"** \n\n "+track.getInfo().uri);
                            e.setFooter("presented by " + config.get("bot_name"));

                            textChannel.sendMessageEmbeds(e.build()).setActionRow(playNow()).queue(m -> {
                                try{
                                    m.delete().queueAfter(10, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                            });
                        }
                        else{
                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.decode(config.get("color")));
                            e.setTitle(":white_check_mark:  **QUEUED**", null);
                            e.setDescription("**"+title+"** \n by **"+author+"** \n\n "+track.getInfo().uri);
                            e.setFooter("presented by " + config.get("bot_name"));

                            textChannel.sendMessageEmbeds(e.build()).setActionRow(playNow()).queue(m -> {
                                try{
                                    m.delete().queueAfter(10, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                            });
                        }
                        log.logger.info("Queued Song ("+track.getInfo().uri+") on ("+textChannel.getGuild().getName()+")");
                    }
                    else{
                        if(track.getSourceManager().getSourceName().equals("youtube")) {
                            String[] id = track.getInfo().uri.trim().split("=");
                            String thumb = "https://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg".replace("<insert-youtube-video-id-here>",id[1]);
                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.decode(config.get("color")));
                            e.setTitle(":arrow_forward: **NOW PLAYING**", null);
                            e.setImage(thumb);
                            e.setDescription("**"+title+"** \n("+(length/1000)/60+" min) \n by **"+author+"** \n\n "+track.getInfo().uri);
                            e.setFooter("presented by " + config.get("bot_name"));

                            if(musicManager.scheduler.repeating){
                                textChannel.sendMessageEmbeds(e.build()).setActionRow(pause0RstopLOOP()).queue(m -> {
                                    try{
                                        m.delete().queueAfter(length, TimeUnit.SECONDS);
                                    }catch(NullPointerException ignored){}
                                });
                            }else{
                                textChannel.sendMessageEmbeds(e.build()).setActionRow(pause0Rstop()).queue(m -> {
                                    try{
                                        m.delete().queueAfter(length, TimeUnit.SECONDS);
                                    }catch(NullPointerException ignored){}
                                });
                            }
                        }
                        else{
                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.decode(config.get("color")));
                            e.setTitle(":arrow_forward: **NOW PLAYING**", null);
                            e.setDescription("**"+title+"** \n by **"+author+"** \n\n "+track.getInfo().uri);
                            e.setFooter("presented by " + config.get("bot_name"));

                            if(musicManager.scheduler.repeating){
                                textChannel.sendMessageEmbeds(e.build()).setActionRow(pause0RstopLOOP()).queue(m -> {
                                    try{
                                        m.delete().queueAfter(length, TimeUnit.SECONDS);
                                    }catch(NullPointerException ignored){}
                                });
                            }else{
                                textChannel.sendMessageEmbeds(e.build()).setActionRow(pause0Rstop()).queue(m -> {
                                    try{
                                        m.delete().queueAfter(length, TimeUnit.SECONDS);
                                    }catch(NullPointerException ignored){}
                                });
                            }
                        }
                        log.logger.info("Playing Song ("+track.getInfo().uri+") on ("+textChannel.getGuild().getName()+ ")");
                    }
                }
            }
           @Override
            public void noMatches() {

                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.red);
                e.setTitle("**NO MATCHES**", null);
                e.setDescription(input + " was not found");
                e.setFooter("presented by " + config.get("bot_name"));

                textChannel.sendMessageEmbeds(e.build()).queue(m -> {
                    try{
                        m.delete().queueAfter(20, TimeUnit.SECONDS);
                    }catch(NullPointerException ignored){}
                });
                log.logger.info("No matches ("+input+") ("+trackURL+") on ("+textChannel.getGuild().getName()+")");

            }

            @Override
            public void loadFailed(FriendlyException e) {

                if(trackURL.contains("https://open.spotify.com/")){
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("**SPOTIFY PLAYBACK IS NOT ALLOWED**", null);
                    eb.setDescription("Due to Spotifys Copyright regulations no Spotify Songs can be played");
                    eb.setFooter("presented by " + config.get("bot_name"));

                    textChannel.sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}
                    });
                    log.logger.info("Tried to play Spotify on ("+textChannel.getGuild().getName()+")");
                }
                else{
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("**AN UNEXPECTED ERROR OCCURRED**", null);
                    eb.setDescription("Error: "+e.toString());
                    eb.setFooter("presented by " + config.get("bot_name"));

                    textChannel.sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}
                    });
                    log.logger.info("Unexpected ("+e.toString()+") on ("+textChannel.getGuild().getName()+")");
                }
                if(audioManager.isConnected()) {
                    audioManager.closeAudioConnection();
                }
                System.out.println(e.toString());


            }
        });


    }
    public static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> pause0Rstop() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("pause","Pause"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("stop","Stop"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("skip","Next"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("loop","Repeat"));

        return buttons;
    }
    public static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> pause0RstopLOOP() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("pause","Pause"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("stop","Stop"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("skip","Next"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("loop","Repeat"));

        return buttons;
    }
    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> playNow() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("skip","Play Now"));
        return buttons;
    }
    public static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> resumeORskipORstop() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("resume","Pause"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("stop","Stop"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("skip","Next"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("loop","Repeat"));
        return buttons;
    }
    public static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> resumeORskipORstopLOOP() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("resume","Pause"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("stop","Stop"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("skip","Next"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("loop","Repeat"));
        return buttons;
    }

    public static PlayerManager getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }



}
