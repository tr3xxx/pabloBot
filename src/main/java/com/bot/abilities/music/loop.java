package com.bot.abilities.music;

import com.bot.abilities.core.Command;
import com.bot.core.config;
import com.bot.lavaplayer.GuildMusicManager;
import com.bot.lavaplayer.PlayerManager;
import com.bot.log.log;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.bot.lavaplayer.PlayerManager.*;

public class loop extends Command {
    @Override
    public String[] call() {
        return new String[] {"loop","repeat"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[] {};
    }

    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        if(!Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).inAudioChannel()){
            EmbedBuilder eb= new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("You must be in a voice channel to do this!", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });
            return false;
        }
        else if(!Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState()).inAudioChannel() || !Objects.equals(event.getGuild().getSelfMember().getVoiceState().getChannel(), event.getMember().getVoiceState().getChannel())){
            EmbedBuilder eb= new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("I need to be in your voice channel to do this!!", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                    event.getMessage().delete().queue();
                }catch(NullPointerException ignored){}

            });
            return false;
        }
        PlayerManager.getINSTANCE();
        final GuildMusicManager musicManager = getMusicManager(event.getGuild());
        musicManager.scheduler.repeating = !musicManager.scheduler.repeating;
        PlayerManager.getINSTANCE();
        final AudioManager audioManager = event.getGuild().getAudioManager();
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        String title = track.getInfo().title;
        String author = track.getInfo().author;
        Boolean isStream = track.getInfo().isStream;
        Long length = track.getDuration();
        try{
            event.getMessage().delete().queue();
        }catch(NullPointerException ignored){}

        if(musicManager.audioPlayer.isPaused()) {
            if (track.getSourceManager().getSourceName().equals("youtube")) {
                String[] id = track.getInfo().uri.trim().split("=");
                String thumb = "https://img.youtube.com/vi/<insert-youtube-video-id-here>/mqdefault.jpg".replace("<insert-youtube-video-id-here>", id[1]);
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.decode(config.get("color")));
                eb.setTitle(":pause_button: **PAUSED**", null);
                eb.setImage(thumb);
                eb.setDescription("**" + title + "** \n(" + (length / 1000) / 60 + " min) \n by **" + author + "** \n\n " + track.getInfo().uri);
                eb.setFooter("presented by " + config.get("bot_name"));

                if (musicManager.scheduler.repeating) {
                    event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstopLOOP()).queue(message -> {
                        message.getChannel().getHistory().retrievePast(30).queue(messages -> {
                            messages.forEach(message1 -> {
                                try {
                                    if (!message1.getId().equals(message.getId())) {
                                        java.util.List<MessageEmbed> embeds = message1.getEmbeds();
                                        embeds.forEach(messageEmbed -> {
                                            if (messageEmbed.getDescription().contains(title)) {
                                                try{
                                                    message.delete().queue();
                                                }catch(NullPointerException ignored){}
                                            }
                                        });
                                    }
                                } catch (Exception ignored) {
                                }
                            });
                        });
                    });
                } else {
                    event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstop()).queue(message -> {
                        message.getChannel().getHistory().retrievePast(30).queue(messages -> {
                            messages.forEach(message1 -> {
                                try {
                                    if (!message1.getId().equals(message.getId())) {

                                        java.util.List<MessageEmbed> embeds = message1.getEmbeds();
                                        embeds.forEach(messageEmbed -> {
                                            if (messageEmbed.getDescription().contains(title)) {
                                                try{
                                                    message.delete().queue();
                                                }catch(NullPointerException ignored){}
                                            }
                                        });
                                    }
                                } catch (Exception ignored) {
                                }
                            });
                        });
                    });
                }
            } else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.decode(config.get("color")));
                eb.setTitle(":pause_button: **PAUSED**", null);
                eb.setDescription("**" + title + "** \n by **" + author + "** \n\n " + track.getInfo().uri);
                eb.setFooter("presented by " + config.get("bot_name"));

                if (musicManager.scheduler.repeating) {
                    event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstopLOOP()).queue(message -> {
                        message.getChannel().getHistory().retrievePast(30).queue(messages -> {
                            messages.forEach(message1 -> {
                                try {
                                    if (!message1.getId().equals(message.getId())) {
                                        java.util.List<MessageEmbed> embeds = message1.getEmbeds();
                                        embeds.forEach(messageEmbed -> {
                                            if (messageEmbed.getDescription().contains(title)) {
                                                try{
                                                    message.delete().queue();
                                                }catch(NullPointerException ignored){}
                                            }
                                        });
                                    }
                                } catch (Exception ignored) {
                                }
                            });
                        });
                    });
                } else {
                    event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstop()).queue(message -> {
                        message.getChannel().getHistory().retrievePast(30).queue(messages -> {
                            messages.forEach(message1 -> {
                                try {
                                    if (!message1.getId().equals(message.getId())) {
                                        java.util.List<MessageEmbed> embeds = message1.getEmbeds();
                                        embeds.forEach(messageEmbed -> {
                                            if (messageEmbed.getDescription().contains(title)) {
                                                try{
                                                    message.delete().queue();
                                                }catch(NullPointerException ignored){}
                                            }
                                        });
                                    }
                                } catch (Exception ignored) {
                                }
                            });
                        });
                    });
                }
            }
        }
        log.logger.info("Started Loop for "+track.getInfo().uri+" on ("+event.getGuild().getName()+")");
        return false;
    }
}
