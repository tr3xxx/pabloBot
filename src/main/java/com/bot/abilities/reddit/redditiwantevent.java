package com.bot.abilities.reddit;

import com.bot.abilities.core.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

public class redditiwantevent extends Command {
    @Override
    public String[] call() {
        return new String[]{"rnewstest"};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

        RedditNews redditNews = new RedditNews(event);
        Thread r = new Thread(redditNews);
        r.run();


        return false;
    }
}
