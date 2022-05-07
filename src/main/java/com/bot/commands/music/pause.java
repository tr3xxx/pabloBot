package com.bot.commands.music;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.lavaplayer.GuildMusicManager;
import com.bot.lavaplayer.PlayerManager;
import com.bot.log.log;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.bot.lavaplayer.PlayerManager.*;

public class pause extends Command {
    @Override
    public String call() {
        return "pause";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        if(!Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).inAudioChannel()){
            EmbedBuilder eb= new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("You have to be in a VoiceChannel to do this", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
             return false;
        }
        else if(!Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState()).inAudioChannel() || !Objects.equals(event.getGuild().getSelfMember().getVoiceState().getChannel(), event.getMember().getVoiceState().getChannel())){
            EmbedBuilder eb= new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("I have to be in your VoiceChannel to do this", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return false;
        }

        PlayerManager.getINSTANCE();
        final GuildMusicManager musicManager = getMusicManager(event.getGuild());
        final AudioManager audioManager = event.getGuild().getAudioManager();
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        musicManager.scheduler.audioPlayer.setPaused(true);
        event.getMessage().delete().queue();
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

            if (musicManager.scheduler.repeating) {
                event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstopLOOP()).queue(message -> {
                    message.getChannel().getHistory().retrievePast(30).queue(messages -> {
                        messages.forEach(message1 -> {
                            try {
                                if (!message1.getId().equals(message.getId())) {
                                    java.util.List<MessageEmbed> embeds = message1.getEmbeds();
                                    embeds.forEach(messageEmbed -> {
                                        if (messageEmbed.getDescription().contains(title)) {
                                            message.delete().queue();
                                        }
                                    });
                                }
                            } catch (Exception ignored) {
                            }
                        });
                    });
                });
            }

            else {
                event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstop()).queue(message -> {
                    message.getChannel().getHistory().retrievePast(30).queue(messages -> {
                        messages.forEach(message1 -> {
                            try {
                                if (!message1.getId().equals(message.getId())) {
                                    List<MessageEmbed> embeds = message1.getEmbeds();
                                    embeds.forEach(messageEmbed -> {
                                        if (messageEmbed.getDescription().contains(title)) {
                                            System.out.println(message1.getId());
                                            System.out.println(message.getId());
                                            message1.delete().queue();
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
        else{
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.decode(config.get("color")));
            eb.setTitle(":pause_button: **PAUSED**", null);
            eb.setDescription("**"+title+"** \n by **"+author+"** \n\n "+track.getInfo().uri);
            eb.setFooter("presented by " + config.get("bot_name"));

            event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(resumeORskipORstop()).queue();
        }
        log.logger.info("Pausing Song ("+track.getInfo().uri+") on ("+event.getGuild().getName()+")");

        return false;
    }
}


