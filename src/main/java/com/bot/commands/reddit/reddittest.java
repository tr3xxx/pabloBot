package com.bot.commands.reddit;

import com.bot.commands.core.Command;
import com.bot.core.Redditcore;
import com.bot.core.config;
import com.bot.log.log;
import net.dean.jraw.models.EmbeddedMedia;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.SubredditReference;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class reddittest extends Command {
    @Override
    public String[] call() {
        return new String[]{"reddittest"};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {//25

        String url = "";
        String title = "";
        EmbeddedMedia embeddedMedia;
        String type;

        DefaultPaginator<Submission> paginator = Redditcore.reddit.subreddit("waifusfortr3x").posts().limit(68).build();

        int pos = ThreadLocalRandom.current().nextInt(68);

        try {
            paginator.next();
            url = paginator.getCurrent().get(pos).getUrl();
            title = paginator.getCurrent().get(pos).getTitle();
            embeddedMedia =paginator.getCurrent().get(pos).getEmbeddedMedia();

            try {
                type = embeddedMedia.getType();

                if(embeddedMedia.getType().equals("youtube.com")){
                    while (embeddedMedia.getType().equals("youtube.com")){
                        pos = ThreadLocalRandom.current().nextInt(68);
                        url = paginator.getCurrent().get(pos).getUrl();
                        title = paginator.getCurrent().get(pos).getTitle();
                        embeddedMedia =paginator.getCurrent().get(pos).getEmbeddedMedia();
                    }
                }
            }catch (NullPointerException ignored){}


        }catch (Exception e){
            log.logger.warning(e.toString());
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("**AN UNEXPECTED ERROR OCCURRED**", null);
            eb.setDescription("Error: "+e.toString());
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return false;
        }


        EmbedBuilder e = new EmbedBuilder();
        e.setTitle(title);
        e.setImage(url);
        event.getChannel().sendMessageEmbeds(e.build()).queue();

        return false;
    }
}
