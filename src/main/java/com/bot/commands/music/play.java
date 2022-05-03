package com.bot.commands.music;

import com.bot.commands.core.Command;
import com.bot.commands.lavaplayer.PlayerManager;
import com.bot.core.config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.print.URIException;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class play extends Command {
    @Override
    public String call() {
        return "play";
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
        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }
        String link = null;

        for (String arg : args) {
            link = arg + " ";
        }
        if(!isUrl(link)){
            link = "ytsearch:" + link + " audio";
        }
        PlayerManager.getINSTANCE().loadAndPlay(event.getTextChannel(), link);

        return false;
    }

    public boolean isUrl(String url){
        try{
            new URI(url);
            return true;
        }
        catch(URISyntaxException e){
            return false;
        }
    }
}
