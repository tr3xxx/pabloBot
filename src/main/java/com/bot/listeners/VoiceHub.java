package com.bot.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class VoiceHub implements EventListener {


    @Override
    public void onEvent(@NotNull GenericEvent event) {

        String voicehubID = "968575485019824169";

        ///
            // hier wird später die .db die id raussuchen
        ///


        if(event instanceof GuildVoiceJoinEvent e){
            if(e.getChannelJoined().getId().equalsIgnoreCase(voicehubID)){

                Guild guild =  e.getGuild();
                guild.createVoiceChannel("Voice")
                       .setUserlimit(10)
                       .setParent(Objects.requireNonNull(e.getGuild().getVoiceChannelById(voicehubID)).getParentCategory())
                       .setBitrate(e.getGuild().getMaxBitrate())
                       .syncPermissionOverrides()
                       .queue(voiceChannel ->{
                           // move


                         });

                //


            }
        }
    }
}
