package com.bot.commands.music;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.lavaplayer.GuildMusicManager;
import com.bot.lavaplayer.PlayerManager;
import com.bot.log.log;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bot.lavaplayer.PlayerManager.getMusicManager;

public class queue extends Command {
    @Override
    public String[] call() {
        return new String[]{"queue", "q"};
    }


    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        PlayerManager.getINSTANCE();
        final GuildMusicManager musicManager = getMusicManager(event.getGuild());
        final AudioManager audioManager = event.getGuild().getAudioManager();
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("You have to be in a VoiceChannel to do this", null);
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return false;
        }

        if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }
        if(musicManager.scheduler.queue.size() != 0){
            BlockingQueue<AudioTrack> tracks = musicManager.scheduler.queue;
            EmbedBuilder eb= new EmbedBuilder();
            eb.setColor(Color.decode(config.get("color")));
            eb.setTitle(":notes: QUEUE", null);
            AtomicInteger i = new AtomicInteger(1);
            tracks.forEach(track -> {
                    eb.addField( i+"."+track.getInfo().title, " **by** "+track.getInfo().author,false);
                    i.set(i.get() + 1);

                });
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getMessage().delete().queue();
            event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });

        }else{

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



        return false;
    }
}