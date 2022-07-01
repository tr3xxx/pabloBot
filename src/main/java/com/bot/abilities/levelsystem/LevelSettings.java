package com.bot.abilities.levelsystem;

import com.bot.abilities.core.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

public class LevelSettings extends Command {
    @Override
    public String[] call() {
        return new String[] {"LevelSettings"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        return false;
    }
}
