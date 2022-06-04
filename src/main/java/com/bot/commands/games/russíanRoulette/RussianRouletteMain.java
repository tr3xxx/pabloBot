package com.bot.commands.games.russíanRoulette;

import com.bot.commands.core.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

public class RussianRouletteMain extends Command {

    @Override
    public String[] call() {
        return new String[0];
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        return false;
    }
}
