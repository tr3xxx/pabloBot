package com.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager{
    public final AudioPlayer audioPlayer;
    public final TrackScheduler scheduler;
    private final AudioPlaySendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager){
        this.audioPlayer = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.scheduler);
        this.sendHandler = new AudioPlaySendHandler(this.audioPlayer);

    }

    public AudioPlaySendHandler getSendHandler(){
        return this.sendHandler;
    }
}
