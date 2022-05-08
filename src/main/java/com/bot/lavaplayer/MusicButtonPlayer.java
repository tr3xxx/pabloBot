package com.bot.lavaplayer;

import com.bot.core.config;
import com.bot.log.log;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.bot.lavaplayer.PlayerManager.*;


public class MusicButtonPlayer extends ListenerAdapter {
    ButtonInteractionEvent e;
    String prefix;

    public void onButtonInteraction(ButtonInteractionEvent e) {
        //e.deferEdit().queue();
        this.e = e;
        switch (Objects.requireNonNull(e.getButton().getId())) {
            case "pause" -> {
                if(!Objects.requireNonNull(Objects.requireNonNull(e.getMember()).getVoiceState()).inAudioChannel()){
                    EmbedBuilder eb= new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("You have to be in a VoiceChannel to do this", null);
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}

                    });
                    return;
                }
                else if(!Objects.requireNonNull(Objects.requireNonNull(e.getGuild()).getSelfMember().getVoiceState()).inAudioChannel() || !Objects.equals(e.getGuild().getSelfMember().getVoiceState().getChannel(), e.getMember().getVoiceState().getChannel())){
                    EmbedBuilder eb= new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("I have to be in your VoiceChannel to do this", null);
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}

                    });
                    return;
                }

                PlayerManager.getINSTANCE();
                final GuildMusicManager musicManager = getMusicManager(e.getGuild());
                final AudioManager audioManager = e.getGuild().getAudioManager();
                AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
                musicManager.scheduler.audioPlayer.setPaused(true);
                try{
                    e.getMessage().delete().queue();
                }catch(NullPointerException ignored){}
                String title = track.getInfo().title;
                String author = track.getInfo().author;
                Boolean isStream = track.getInfo().isStream;
                Long length = track.getDuration();

                if(track.getSourceManager().getSourceName().equals("youtube")) {
                    String[] id = track.getInfo().uri.trim().split("=");
                    String thumb = "https://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg".replace("<insert-youtube-video-id-here>",id[1]);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle(":pause_button: **PAUSED**", null);
                    eb.setImage(thumb);
                    eb.setDescription("**"+title+"** \n("+(length/1000)/60+" min) \n by **"+author+"** \n\n "+track.getInfo().uri);
                    eb.setFooter("presented by " + config.get("bot_name"));

                    if(musicManager.scheduler.repeating){
                        e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstopLOOP()).queue();
                    }else{
                        e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstop()).queue();
                    }
                    log.logger.info("Pausing Song ("+track.getInfo().uri+") on ("+e.getGuild().getName()+")");
                }
                else{
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle(":pause_button: **PAUSED**", null);
                    eb.setDescription("**"+title+"** \n by **"+author+"** \n\n "+track.getInfo().uri);
                    eb.setFooter("presented by " + config.get("bot_name"));

                    if(musicManager.scheduler.repeating){
                        e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstopLOOP()).queue();
                    }else{
                        e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstop()).queue();
                    }
                    log.logger.info("Pausing Song ("+track.getInfo().uri+") on ("+e.getGuild().getName()+")");
                }
            }
            case "stop" -> {
                if(!Objects.requireNonNull(Objects.requireNonNull(e.getMember()).getVoiceState()).inAudioChannel()){
                    EmbedBuilder eb= new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("You have to be in a VoiceChannel to do this", null);
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}

                    });
                    return;
                }
                else if(!Objects.requireNonNull(Objects.requireNonNull(e.getGuild()).getSelfMember().getVoiceState()).inAudioChannel() || !Objects.equals(e.getGuild().getSelfMember().getVoiceState().getChannel(), e.getMember().getVoiceState().getChannel())){
                    EmbedBuilder eb= new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("I have to be in your VoiceChannel to do this", null);
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}

                    });
                    return;
                }

                PlayerManager.getINSTANCE();
                final GuildMusicManager musicManager = getMusicManager(e.getGuild());
                final AudioManager audioManager = e.getGuild().getAudioManager();

                musicManager.scheduler.audioPlayer.stopTrack();
                musicManager.scheduler.queue.clear();
                try{
                    e.getMessage().delete().queue();
                }catch(NullPointerException ignored){}
                if(audioManager.isConnected()) {
                    audioManager.closeAudioConnection();
                }
                if( musicManager.scheduler.audioPlayer.isPaused()) {
                    musicManager.scheduler.audioPlayer.setPaused(false);
                }

                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.decode(config.get("color")));
                eb.setTitle(":stop_button:   **MUSIK STOPPED**", null);
                eb.setFooter("presented by " + config.get("bot_name"));
                e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                    try{
                        m.delete().queueAfter(20, TimeUnit.SECONDS);
                    }catch(NullPointerException ignored){}

                });
                log.logger.info("Stopping Song on ("+e.getGuild().getName()+")");
            }
            case "skip" -> {
                final GuildMusicManager musicManager = getMusicManager(e.getGuild());
                if(!Objects.requireNonNull(Objects.requireNonNull(e.getMember()).getVoiceState()).inAudioChannel()){
                    EmbedBuilder eb= new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("You have to be in a VoiceChannel to do this", null);
                    eb.setFooter("presented by " + config.get("bot_name"));
                    try{
                        e.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}

                    });
                    return;
                }
                else if(!Objects.requireNonNull(Objects.requireNonNull(e.getGuild()).getSelfMember().getVoiceState()).inAudioChannel() || !Objects.equals(e.getGuild().getSelfMember().getVoiceState().getChannel(), e.getMember().getVoiceState().getChannel())){
                    EmbedBuilder eb= new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("I have to be in your VoiceChannel to do this", null);
                    eb.setFooter("presented by " + config.get("bot_name"));
                    try{
                        e.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}

                    });
                    return;
                }
                PlayerManager.getINSTANCE();
                if( musicManager.scheduler.audioPlayer.isPaused()) {
                    musicManager.scheduler.audioPlayer.setPaused(false);
                }
                try {
                    if(musicManager.scheduler.queue.size()!=0) {
                        musicManager.scheduler.nextTrack();
                        log.logger.info("Skipped Song on ("+e.getGuild().getName()+")");
                    }
                    else{
                        EmbedBuilder eb= new EmbedBuilder();
                        eb.setColor(Color.decode(config.get("color")));
                        eb.setTitle("There are no Songs in the Queue currently", null);
                        eb.setFooter("presented by " + config.get("bot_name"));
                        e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                            try{
                                m.delete().queueAfter(20, TimeUnit.SECONDS);
                            }catch(NullPointerException ignored){}

                        });
                        log.logger.info("Tried to skip, but queue is empty on ("+e.getGuild().getName()+")");
                        return;
                    }
                }
                catch(Exception err ){
                    log.logger.warning(err.toString());
                    return;
                }
                try{
                    e.getMessage().delete().queue();
                }catch(NullPointerException ignored){}
            }
            case "resume" -> {
                if(!Objects.requireNonNull(Objects.requireNonNull(e.getMember()).getVoiceState()).inAudioChannel()){
                    EmbedBuilder eb= new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("You have to be in a VoiceChannel to do this", null);
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}

                    });
                    return;
                }
                else if(!Objects.requireNonNull(Objects.requireNonNull(e.getGuild()).getSelfMember().getVoiceState()).inAudioChannel() || !Objects.equals(e.getGuild().getSelfMember().getVoiceState().getChannel(), e.getMember().getVoiceState().getChannel())){
                    EmbedBuilder eb= new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("I have to be in your VoiceChannel to do this", null);
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}

                    });
                    return;
                }

                PlayerManager.getINSTANCE();
                final GuildMusicManager musicManager = getMusicManager(e.getGuild());
                final AudioManager audioManager = e.getGuild().getAudioManager();
                AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
                String title = track.getInfo().title;
                String author = track.getInfo().author;
                Boolean isStream = track.getInfo().isStream;
                Long length = track.getDuration();

                if( musicManager.scheduler.audioPlayer.isPaused()) {
                    musicManager.scheduler.audioPlayer.setPaused(false);
                    if(track.getSourceManager().getSourceName().equals("youtube")) {
                        String[] id = track.getInfo().uri.trim().split("=");
                        String thumb = "https://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg".replace("<insert-youtube-video-id-here>",id[1]);
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.decode(config.get("color")));
                        eb.setTitle(":arrow_forward: **NOW PLAYING**", null);
                        eb.setImage(thumb);
                        eb.setDescription("**"+title+"** \n("+(length/1000)/60+" min) \n by **"+author+"** \n\n "+track.getInfo().uri);
                        eb.setFooter("presented by " + config.get("bot_name"));

                        try{
                            e.getMessage().delete().queue();
                        }catch(NullPointerException ignored){}
                        if(musicManager.scheduler.repeating){
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0RstopLOOP()).queue(m -> {
                                try{
                                    m.delete().queueAfter(length, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                            });
                        }else{
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0Rstop()).queue(m -> {
                                try{
                                    m.delete().queueAfter(length, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                            });
                        }
                        log.logger.info("Resuming "+track.getInfo().uri+" on ("+e.getGuild().getName()+")");

                    }
                    else{
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.decode(config.get("color")));
                        eb.setTitle(":arrow_forward: **NOW PLAYING**", null);
                        eb.setDescription("**"+title+"** \n by **"+author+"** \n\n "+track.getInfo().uri);
                        eb.setFooter("presented by " + config.get("bot_name"));

                        try{
                            e.getMessage().delete().queue();
                        }catch(NullPointerException ignored){}
                        if(musicManager.scheduler.repeating){
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0RstopLOOP()).queue(m -> {
                                try{
                                    m.delete().queueAfter(length, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                            });
                        }else{
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0Rstop()).queue(m -> {
                                try{
                                    m.delete().queueAfter(length, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                            });
                        }
                        log.logger.info("Resuming "+track.getInfo().uri+" on ("+e.getGuild().getName()+")");
                    }
                }
            }
            case "loop" -> {
                if(!Objects.requireNonNull(Objects.requireNonNull(e.getMember()).getVoiceState()).inAudioChannel()){
                    EmbedBuilder eb= new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("You have to be in a VoiceChannel to do this", null);
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}
                    });
                    return;
                }
                else if(!Objects.requireNonNull(Objects.requireNonNull(e.getGuild()).getSelfMember().getVoiceState()).inAudioChannel() || !Objects.equals(e.getGuild().getSelfMember().getVoiceState().getChannel(), e.getMember().getVoiceState().getChannel())){
                    EmbedBuilder eb= new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("I have to be in your VoiceChannel to do this", null);
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                        try{
                            m.delete().queueAfter(20, TimeUnit.SECONDS);
                        }catch(NullPointerException ignored){}
                    });
                    return;
                }
                PlayerManager.getINSTANCE();
                final GuildMusicManager musicManager = getMusicManager(e.getGuild());
                musicManager.scheduler.repeating = !musicManager.scheduler.repeating;
                PlayerManager.getINSTANCE();
                final AudioManager audioManager = e.getGuild().getAudioManager();
                AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
                String title = track.getInfo().title;
                String author = track.getInfo().author;
                Boolean isStream = track.getInfo().isStream;
                Long length = track.getDuration();
                try{
                    e.getMessage().delete().queue();
                }catch(NullPointerException ignored){}
                if(musicManager.audioPlayer.isPaused()){
                    if(track.getSourceManager().getSourceName().equals("youtube")) {
                        String[] id = track.getInfo().uri.trim().split("=");
                        String thumb = "https://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg".replace("<insert-youtube-video-id-here>",id[1]);
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.decode(config.get("color")));
                        eb.setTitle(":pause_button: **PAUSED**", null);
                        eb.setImage(thumb);
                        eb.setDescription("**"+title+"** \n("+(length/1000)/60+" min) \n by **"+author+"** \n\n "+track.getInfo().uri);
                        eb.setFooter("presented by " + config.get("bot_name"));

                        if(musicManager.scheduler.repeating){
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstopLOOP()).queue();
                        }else{
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstop()).queue();
                        }
                        log.logger.info("Started Loop for "+track.getInfo().uri+" on ("+e.getGuild().getName()+")");
                    }
                    else{
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.decode(config.get("color")));
                        eb.setTitle(":pause_button: **PAUSED**", null);
                        eb.setDescription("**"+title+"** \n by **"+author+"** \n\n "+track.getInfo().uri);
                        eb.setFooter("presented by " + config.get("bot_name"));

                        if(musicManager.scheduler.repeating){
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstopLOOP()).queue();
                        }else{
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstop()).queue();
                        }
                        log.logger.info("Started Loop for "+track.getInfo().uri+" on ("+e.getGuild().getName()+")");
                    }
                }else {

                    if (track.getSourceManager().getSourceName().equals("youtube")) {
                        String[] id = track.getInfo().uri.trim().split("=");
                        String thumb = "https://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg".replace("<insert-youtube-video-id-here>", id[1]);
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.decode(config.get("color")));
                        eb.setTitle(":arrow_forward: **NOW PLAYING**", null);
                        eb.setImage(thumb);
                        eb.setDescription("**" + title + "** \n(" + (length / 1000) / 60 + " min) \n by **" + author + "** \n\n " + track.getInfo().uri);
                        eb.setFooter("presented by " + config.get("bot_name"));

                        if (musicManager.scheduler.repeating) {
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0RstopLOOP()).queue(m -> {
                                try{
                                    m.delete().queueAfter(length, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                            });
                        } else {
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0Rstop()).queue(m -> {
                                try{
                                    m.delete().queueAfter(length, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                            });
                        }
                        log.logger.info("Started Loop for "+track.getInfo().uri+" on ("+e.getGuild().getName()+")");
                    } else {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.decode(config.get("color")));
                        eb.setTitle(":arrow_forward: **NOW PLAYING**", null);
                        eb.setDescription("**" + title + "** \n by **" + author + "** \n\n " + track.getInfo().uri);
                        eb.setFooter("presented by " + config.get("bot_name"));
                        if (musicManager.scheduler.repeating) {
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0RstopLOOP()).queue(m -> {
                                try{
                                    m.delete().queueAfter(length, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                            });
                        } else {
                            e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0Rstop()).queue(m -> {
                                try{
                                    m.delete().queueAfter(length, TimeUnit.SECONDS);
                                }catch(NullPointerException ignored){}
                                }
                            );
                        }
                        log.logger.info("Started Loop for "+track.getInfo().uri+" on ("+e.getGuild().getName()+")");
                    }
                }
            }
        }
    }
}