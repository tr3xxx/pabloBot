package com.bot.commands.music;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.lavaplayer.GuildMusicManager;
import com.bot.lavaplayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;

import static com.bot.lavaplayer.PlayerManager.*;

public class stop extends Command {
    @Override
    public String call() {
        return "stop";
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

        musicManager.scheduler.audioPlayer.stopTrack();
        musicManager.scheduler.queue.clear();
        event.getMessage().delete().queue();
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
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
        return false;
   }

}
