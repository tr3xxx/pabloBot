package com.bot.core;

import com.bot.abilities.basic.reactionroles.reactionRole;
import com.bot.abilities.games.blackjack.BjGame;
import com.bot.abilities.manage.stats.setStatsNames;
import com.bot.abilities.music.search;
import com.bot.abilities.notifications.github.setGithubNotifications;
import com.bot.abilities.notifications.twitch.setTwitchNotifications;
import com.bot.abilities.prefix.setPrefix;
import com.bot.abilities.voice.voicehub.setGeneratedNames;
import com.bot.abilities.voice.voicehub.setVoiceUserLimit;
import com.bot.abilities.voice.voicehub.setVoicehub;
import com.bot.events.prefix.GuildJoinPrefix;
import com.bot.events.level.updateLevel;
import com.bot.events.reactionroles.onReact;
import com.bot.events.vcTracker;
import com.bot.lavaplayer.MusicButtonPlayer;
import com.bot.events.VoiceHub;
import net.dv8tion.jda.api.JDA;

public class EventListenersLoad {

    public void load(JDA jda){

        jda.addEventListener(new setVoicehub.ButtonClick());
        jda.addEventListener(new setPrefix.ButtonClick());
        jda.addEventListener(new VoiceHub());
        jda.addEventListener(new updateLevel());
        jda.addEventListener(new GuildJoinPrefix());
        jda.addEventListener(new setGeneratedNames.MakeSelection());
        jda.addEventListener(new setStatsNames.ButtonClick());
        jda.addEventListener(new setVoiceUserLimit.ButtonClick());
        jda.addEventListener(new MusicButtonPlayer());
        jda.addEventListener(new search.SearchChoose());
        jda.addEventListener(new BjGame.BjListener());
        jda.addEventListener(new reactionRole.ButtonClick());
        jda.addEventListener(new onReact());
        jda.addEventListener(new setTwitchNotifications.ButtonClick());
        jda.addEventListener(new vcTracker());
        jda.addEventListener(new setGithubNotifications.ButtonClick());

    }
}