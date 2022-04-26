package com.bot.commands.core;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter {
    public abstract String call();
    public abstract String help();
    public abstract boolean execute(String[] args, MessageReceivedEvent event);
}
