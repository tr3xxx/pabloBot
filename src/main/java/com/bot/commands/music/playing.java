package com.bot.commands.music;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.lavaplayer.GuildMusicManager;
import com.bot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.sql.SQLException;

public class playing extends Command {
    @Override
    public String call() {
        return "playing";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        if(!event.getMember().getVoiceState().inAudioChannel()){

            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("You have to be in a VoiceChannel to do this", null);
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue();
            return false;
        }

        final GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        final AudioManager audioManager = event.getGuild().getAudioManager();

        String title = musicManager.scheduler.audioPlayer.getPlayingTrack().getInfo().title;

        EmbedBuilder e = new EmbedBuilder();
        e.setColor(Color.decode(config.get("color")));
        e.setTitle("Currently playing: "+title, null);
        e.setFooter("presented by " + config.get("bot_name"));
        event.getChannel().sendMessageEmbeds(e.build()).queue();
        return false;
    }
}
