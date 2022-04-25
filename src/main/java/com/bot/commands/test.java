package com.bot.commands;

import com.bot.config;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class test extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {

        if(e.getMessage().getContentRaw().equalsIgnoreCase(config.get("prefix")+"test")){
            e.getChannel().sendMessage("Hallo "+e.getAuthor().getAsTag()).queue();
        }


        // DAS IST KEIN FERTIGER COMMAND STOP HIER NICHT WEITER MACHEN !!!
    }
}

