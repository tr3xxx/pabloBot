package com.bot.core;

import com.bot.commands.prefix.setPrefix;
import com.bot.commands.voice.voicehub.setVoicehub;
import com.bot.events.GuildJoin;
import com.bot.listeners.ReadyListener;
import com.bot.listeners.VoiceHub;
import net.dv8tion.jda.api.JDA;

public class EventListenersLoad {

    public void load(JDA jda){
                jda.addEventListener(new ReadyListener());
                jda.addEventListener(new setVoicehub.ButtonClick());
                jda.addEventListener(new setPrefix.ButtonClick());
                jda.addEventListener(new VoiceHub());
                jda.addEventListener(new GuildJoin());

    }
}
