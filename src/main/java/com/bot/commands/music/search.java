package com.bot.commands.music;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.lavaplayer.GuildMusicManager;
import com.bot.lavaplayer.PlayerManager;
import com.bot.log.log;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.commons.collections4.map.HashedMap;

import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.bot.lavaplayer.PlayerManager.audioPlayerManager;
import static com.bot.lavaplayer.PlayerManager.getMusicManager;

public class search extends Command {
    public static HashMap<Integer, AudioTrack> trackMap = new HashMap<Integer,AudioTrack>();
    @Override
    public String[] call() {
        return new String[] {"search","searchfor"};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        String input = args[1];
        for (int i = 2; i < args.length ; i++) {
            input = input +" "+args[i];
        }
        String finalInput = input;
        if(isURL(input)){
            EmbedBuilder eb= new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("You cannot search for a link, please use keywords", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });
            try{
                event.getMessage().delete().queue();
            }catch(NullPointerException ignored){}
            return false;
        }
        input = "ytsearch:" + input + " audio";

        if(PlayerManager.musicManagers == null){
            new PlayerManager();
        }
        final GuildMusicManager musicManager = getMusicManager(event.getGuild());
        final AudioManager audioManager = event.getGuild().getAudioManager();
        AudioPlayer audioPlayer = musicManager.audioPlayer;


        audioPlayerManager.loadItemOrdered(musicManager,input,new AudioLoadResultHandler() {

                    @Override
                    public void trackLoaded(AudioTrack audioTrack) {}

                    @Override
                    public void playlistLoaded(AudioPlaylist audioPlaylist) {
                        final List<AudioTrack> tracks = audioPlaylist.getTracks();

                        EmbedBuilder eb= new EmbedBuilder();
                        eb.setColor(Color.decode(config.get("color")));
                        eb.setTitle("Top 5 Results for '"+ finalInput+"'", null);
                        eb.addField("1. "+tracks.get(0).getInfo().title, "**by:** "+tracks.get(0).getInfo().author, false);
                        eb.addField("2. "+tracks.get(1).getInfo().title, "**by:** "+tracks.get(1).getInfo().author, false);
                        eb.addField("3. "+tracks.get(2).getInfo().title, "**by:** "+tracks.get(2).getInfo().author, false);
                        eb.addField("4. "+tracks.get(3).getInfo().title, "**by:** "+tracks.get(3).getInfo().author, false);
                        eb.addField("5. "+tracks.get(4).getInfo().title, "**by:** "+tracks.get(4).getInfo().author, false);
                        eb.setFooter("presented by " + config.get("bot_name"));
                        trackMap.put(1,tracks.get(0));
                        trackMap.put(2,tracks.get(1));
                        trackMap.put(3,tracks.get(2));
                        trackMap.put(4,tracks.get(3));
                        trackMap.put(5,tracks.get(4));
                        event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(choose()).queue(m -> m.delete().queueAfter(60, TimeUnit.SECONDS));
                        event.getMessage().delete().queue();

                    }

                    @Override
                    public void noMatches() {
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.red);
                        e.setTitle("**NO MATCHES**", null);
                        e.setDescription("Nothing found for"+finalInput);
                        e.setFooter("presented by " + config.get("bot_name"));

                        event.getChannel().sendMessageEmbeds(e.build()).queue(m -> m.delete().queueAfter(20, TimeUnit.SECONDS));
                        log.logger.info("No Search-matches  for ("+ finalInput +") on ("+event.getGuild().getName()+")");
                    }

                    @Override
                    public void loadFailed(FriendlyException e) {
                        log.logger.info("Unexpected ("+e.toString()+") on ("+event.getGuild().getName()+")");
                    }
                });

        event.getMessage().delete().queue();
        return false;
    }

    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> choose() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("1choose","1"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("2choose","2"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("3choose","3"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("4choose","4"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("5choose","5"));
        return buttons;
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

    public static class SearchChoose extends ListenerAdapter {


        public void onButtonInteraction(ButtonInteractionEvent event) {


            switch (Objects.requireNonNull(event.getButton().getId())) {
                case "1choose" -> {
                    if (!event.getMember().getVoiceState().inAudioChannel()) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.red);
                        eb.setTitle("You have to be in a VoiceChannel to do this", null);
                        eb.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }
                    if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                        final AudioManager audioManager = event.getGuild().getAudioManager();
                        final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                        audioManager.openAudioConnection(memberChannel);
                    }
                    PlayerManager.getINSTANCE().loadAndPlay(event.getTextChannel(), trackMap.get(1).getInfo().uri, null);
                    try {
                        event.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                }
                case "2choose" -> {
                    if (!event.getMember().getVoiceState().inAudioChannel()) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.red);
                        eb.setTitle("You have to be in a VoiceChannel to do this", null);
                        eb.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }
                    if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                        final AudioManager audioManager = event.getGuild().getAudioManager();
                        final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                        audioManager.openAudioConnection(memberChannel);
                    }
                    PlayerManager.getINSTANCE().loadAndPlay(event.getTextChannel(), trackMap.get(2).getInfo().uri, null);
                    try {
                        event.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                }
                case "3choose" -> {
                    if (!event.getMember().getVoiceState().inAudioChannel()) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.red);
                        eb.setTitle("You have to be in a VoiceChannel to do this", null);
                        eb.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }
                    if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                        final AudioManager audioManager = event.getGuild().getAudioManager();
                        final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                        audioManager.openAudioConnection(memberChannel);
                    }
                    PlayerManager.getINSTANCE().loadAndPlay(event.getTextChannel(), trackMap.get(3).getInfo().uri, null);
                    try {
                        event.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                }
                case "4choose" -> {
                    if (!event.getMember().getVoiceState().inAudioChannel()) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.red);
                        eb.setTitle("You have to be in a VoiceChannel to do this", null);
                        eb.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }
                    if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                        final AudioManager audioManager = event.getGuild().getAudioManager();
                        final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                        audioManager.openAudioConnection(memberChannel);
                    }
                    PlayerManager.getINSTANCE().loadAndPlay(event.getTextChannel(), trackMap.get(4).getInfo().uri, null);
                    try {
                        event.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                }
                case "5choose" -> {
                    if (!event.getMember().getVoiceState().inAudioChannel()) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.red);
                        eb.setTitle("You have to be in a VoiceChannel to do this", null);
                        eb.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        return;
                    }
                    if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                        final AudioManager audioManager = event.getGuild().getAudioManager();
                        final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                        audioManager.openAudioConnection(memberChannel);
                    }
                    PlayerManager.getINSTANCE().loadAndPlay(event.getTextChannel(), trackMap.get(5).getInfo().uri, null);
                    try {
                        event.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                }
            }

        }
        }
}
