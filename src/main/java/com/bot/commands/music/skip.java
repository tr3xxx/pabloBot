package com.bot.commands.music;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.lavaplayer.GuildMusicManager;
import com.bot.lavaplayer.PlayerManager;
import com.bot.log.log;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;

import static com.bot.lavaplayer.PlayerManager.*;

public class skip extends Command {

    @Override
    public String call() {
        return "skip";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        final GuildMusicManager musicManager = getMusicManager(event.getGuild());
        if (!Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).inAudioChannel()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("You have to be in a VoiceChannel to do this", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getMessage().delete().queue();
            if (musicManager.scheduler.repeating) {
                event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0RstopLOOP()).queue();
            } else {
                event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0Rstop()).queue();
            }
            return false;
        } else if (!Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState()).inAudioChannel() || !Objects.equals(event.getGuild().getSelfMember().getVoiceState().getChannel(), event.getMember().getVoiceState().getChannel())) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("I have to be in your VoiceChannel to do this", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getMessage().delete().queue();
            if (musicManager.scheduler.repeating) {
                event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0RstopLOOP()).queue();
            } else {
                event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(pause0Rstop()).queue();
            }
            return false;
        }
        PlayerManager.getINSTANCE();
        if (musicManager.scheduler.audioPlayer.isPaused()) {
            musicManager.scheduler.audioPlayer.setPaused(false);
        }
        try {
            musicManager.scheduler.nextTrack();
        } catch (Exception err) {
            log.logger.warning(err.toString());
            return false;
        }
        event.getMessage().delete().queue();
        return false;

    }
}
