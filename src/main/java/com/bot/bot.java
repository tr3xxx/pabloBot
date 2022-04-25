package com.bot;

import com.bot.commands.test;
import com.bot.events.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class bot {
    public static void main(String[] args) throws LoginException {

        try {
            JDA jda = JDABuilder.createDefault("OTY4MTgxNjQ0MTIzMDQ1OTk4.YmbHaw.SX-6v3cNdA-R7LcMEZwFZfnZcxE")
                    .setActivity(Activity.playing(" JDA-API"))
                    .addEventListeners(new test())
                    .addEventListeners(new ReadyListener())

                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }


    }

}