package com.bot.commands.music;

import com.bot.commands.core.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

public class stop extends Command {
    @Override
    public String call() {
        return "stop";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        return false;
    }
}
