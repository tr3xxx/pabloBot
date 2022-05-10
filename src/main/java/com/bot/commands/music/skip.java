package com.bot.commands.music;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.lavaplayer.GuildMusicManager;
import com.bot.lavaplayer.PlayerManager;
import com.bot.log.log;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.bot.lavaplayer.PlayerManager.*;

public class skip extends Command {
    @Override
    public String[] call() {
        return new String[] {"skip","next"};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        final GuildMusicManager musicManager = getMusicManager(event.getGuild());
        if (!Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).inAudioChannel()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("You have to be in a VoiceChannel to do this", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            try{
                event.getMessage().delete().queue();
            }catch(NullPointerException ignored){}
            event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });
            return false;
        } else if (!Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState()).inAudioChannel() || !Objects.equals(event.getGuild().getSelfMember().getVoiceState().getChannel(), event.getMember().getVoiceState().getChannel())) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("I have to be in your VoiceChannel to do this", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            try {
                event.getMessage().delete().queue();
            }catch(NullPointerException ignored){}
            event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });

            return false;
        }
        PlayerManager.getINSTANCE();
        final AudioManager audioManager = event.getGuild().getAudioManager();
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        String title = track.getInfo().title;
        String author = track.getInfo().author;
        Boolean isStream = track.getInfo().isStream;
        Long length = track.getDuration();
        if( musicManager.scheduler.audioPlayer.isPaused()) {
            musicManager.scheduler.audioPlayer.setPaused(false);
        }
        try {
            if(musicManager.scheduler.queue.size()!=0) {

                event.getChannel().getHistory().retrievePast(30).queue(messages -> {
                    messages.forEach(message1 -> {
                        try {
                                java.util.List<MessageEmbed> embeds = message1.getEmbeds();
                                embeds.forEach(messageEmbed -> {
                                    if (messageEmbed.getDescription().contains(title)) {
                                        try{
                                            message1.delete().queue();
                                        }catch(NullPointerException ignored){}

                                    }
                                });
                            } catch (Exception ignored) {
                        }
                    });
                });
                musicManager.scheduler.nextTrack();
                log.logger.info("Skipped Song on ("+event.getGuild().getName()+")");
            }
            else{

                EmbedBuilder eb= new EmbedBuilder();
                eb.setColor(Color.red);
                eb.setTitle("There are no Songs in the Queue currently", null);
                eb.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                    try{
                        m.delete().queueAfter(20, TimeUnit.SECONDS);
                    }catch(NullPointerException ignored){}
                });
                log.logger.info("Tried to skip, but queue is empty on ("+event.getGuild().getName()+")");
                return false;
            }
        }
        catch(Exception err ){
            log.logger.warning(err.toString());
            return false;
        }
        log.logger.info("Skipped Song on ("+event.getGuild().getName()+")");
        return false;

    }
}
