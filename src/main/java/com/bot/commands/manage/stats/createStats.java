package com.bot.commands.manage.stats;

import com.bot.commands.core.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

public class createStats extends Command {
    @Override
    public String call() {
        return "createStats";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        return false;
    }
}
