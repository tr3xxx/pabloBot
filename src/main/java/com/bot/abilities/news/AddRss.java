package com.bot.abilities.news;

import com.bot.abilities.core.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

public class AddRss extends Command {


    @Override
    public String[] call() {
        return new String[]{"RssAdd"};
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
        newsCommand c = new newsCommand();
        c.putInDatabase("http://feeds.4players.de/PC-CDROM/articles/-/rss.xml");


        return false;
    }
}
