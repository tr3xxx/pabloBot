package com.bot.abilities.core;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;

public abstract class Command extends ListenerAdapter {
    public abstract String[] call();
    public abstract Permission[] getPermissions();
    public abstract boolean usableInDM();

    public abstract boolean execute(String[] args, MessageReceivedEvent event) throws SQLException;
}
