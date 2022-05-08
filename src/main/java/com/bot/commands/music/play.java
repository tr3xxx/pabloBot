package com.bot.commands.music;

import com.bot.commands.core.Command;
import com.bot.lavaplayer.PlayerManager;
import com.bot.core.config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.net.URL;
import java.sql.SQLException;

public class play extends Command {
    @Override
    public String[] call() {
        return new String[] {"play","p"};
    }


    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        if(!event.getMember().getVoiceState().inAudioChannel()){
            EmbedBuilder eb= new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("You have to be in a VoiceChannel to do this", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return false;
        }

        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }
        String link = null;

        for (String arg : args) {
            link = arg+" " ;
        }
        StringBuilder sb = new StringBuilder(link);
        sb.deleteCharAt(link.length() - 1);
        link = sb.toString();
        String input = link;
        System.out.println(input);
        if(!isURL(link)){
            link = "ytsearch:" + link + " audio";
        }
        try{
            event.getMessage().delete().queue();
        }catch(NullPointerException ignored){}

        PlayerManager.getINSTANCE().loadAndPlay(event.getTextChannel(), link,input);
        return false;
    }

    public boolean isURL(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
