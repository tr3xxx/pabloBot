package com.bot.abilities.help;

import com.bot.abilities.core.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

public class helpCommand extends Command {
    @Override
    public String[] call() {
        return new String[]{"help"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[0];
    }

    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        event.getChannel().sendMessage("soon usable").queue();;
        return false;
    }
}
