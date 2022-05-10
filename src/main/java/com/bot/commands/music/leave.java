package com.bot.commands.music;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.lavaplayer.GuildMusicManager;
import com.bot.lavaplayer.PlayerManager;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static com.bot.lavaplayer.PlayerManager.getMusicManager;

public class leave extends Command {
    @Override
    public String[] call() {
        return new String[]{"leave", "l"};
    }


    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("You have to be in a VoiceChannel to do this", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                    event.getMessage().delete().queue();
                }catch(NullPointerException ignored){}

            });
            return false;
        }

        if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }
        PlayerManager.getINSTANCE();
        final GuildMusicManager musicManager = getMusicManager(event.getGuild());
        final AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            event.getMessage().delete().queue();
            log.logger.info("Left VoiceChannel "+event.getGuild().getSelfMember().getVoiceState().getChannel()+" on "+event.getGuild().getName());
            audioManager.closeAudioConnection();
            if( musicManager.scheduler.audioPlayer.isPaused()) {
                musicManager.scheduler.audioPlayer.setPaused(false);
            }
            musicManager.scheduler.audioPlayer.stopTrack();
            musicManager.scheduler.queue.clear();


        } else {

            event.getMessage().delete().queue();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("I am already in a VoiceChannel", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                    event.getMessage().delete().queue();
                }catch(NullPointerException ignored){}

            });
            return false;
        }

        return false;
    }
}