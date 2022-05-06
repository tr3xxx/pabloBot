package com.bot.core;

import com.bot.commands.manage.stats.setStatsNames;
import com.bot.commands.prefix.setPrefix;
import com.bot.commands.voice.voicehub.setGeneratedNames;
import com.bot.commands.voice.voicehub.setVoiceUserLimit;
import com.bot.commands.voice.voicehub.setVoicehub;
import com.bot.events.GuildJoinPrefix;
import com.bot.lavaplayer.MusicButtonPlayer;
import com.bot.lavaplayer.PlayerManager;
import com.bot.listeners.ReadyListener;
import com.bot.listeners.VoiceHub;
import net.dv8tion.jda.api.JDA;

public class EventListenersLoad {

    public void load(JDA jda){
                jda.addEventListener(new ReadyListener());
                jda.addEventListener(new setVoicehub.ButtonClick());
                jda.addEventListener(new setPrefix.ButtonClick());
                jda.addEventListener(new VoiceHub());
                jda.addEventListener(new GuildJoinPrefix());
                jda.addEventListener(new setGeneratedNames.MakeSelection());
                jda.addEventListener(new setStatsNames.ButtonClick());
                jda.addEventListener(new setVoiceUserLimit.ButtonClick());
                jda.addEventListener(new MusicButtonPlayer());

    }
}
