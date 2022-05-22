package com.bot.commands.reddit;

import com.bot.commands.core.Command;
import com.bot.core.Redditcore;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dean.jraw.models.EmbeddedMedia;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.SubredditReference;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class reddittest extends Command {
    MessageReceivedEvent e;
    String prefix;
    String redditUsed;
    String [] reddits = new String[]{
            "waifusfortr3x/waifu/false",
            "memes/meme/false",
            "videos/vid/false",
            "EGirls/egirls/true",
            "porn/porn/true",
    };

    @Override
    public String[] call() {
        return new String[]{
                "waifu",
                "meme",
                "egirls",
                "vid"
        };
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        e = event;
        try {
            getPrefix();
            String call = args[0].replace(prefix, "");

            for (String reddit : reddits) {
                String [] cut = reddit.trim().split("/");
                String used_reddit = cut[0];
                String used_cmd = cut[1];
                Boolean isNSFW = Boolean.parseBoolean(cut[2]);
                if(call.equalsIgnoreCase(used_cmd)){
                    redditUsed = used_reddit;
                    if(isNSFW){
                        if(!event.getTextChannel().isNSFW()){
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(Color.red);
                            eb.setTitle("**UNAUTHORIZED CHANNEL**", null);
                            eb.setDescription("You tried to use an NSFW-Command ("+used_cmd+") in an not valid NSFW" +
                                    " Channel, please " +
                                    "use " +
                                    "an NSFW-Channel for NSFW-Commands");
                            eb.setFooter("presented by " + config.get("bot_name"));
                            event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                                try{
                                    m.delete().queueAfter(20, TimeUnit.SECONDS);
                                    event.getMessage().delete().queue();
                                }catch(NullPointerException ignored){}
                            });
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.logger.warning(e.toString());
            return false;
        }


        String url = "";
        String title = "";
        EmbeddedMedia embeddedMedia;
        String type;

        DefaultPaginator<Submission> paginator = Redditcore.reddit.subreddit(redditUsed).posts().limit(50).build();

        int pos = ThreadLocalRandom.current().nextInt(50);

        try {
            paginator.next();
            url = paginator.getCurrent().get(pos).getUrl();
            title = paginator.getCurrent().get(pos).getTitle();
            embeddedMedia =paginator.getCurrent().get(pos).getEmbeddedMedia();

            try {
                if(embeddedMedia.getOEmbed().getType().equals("video")){
                    while (embeddedMedia.getOEmbed().getType().equals("video")){
                        pos = ThreadLocalRandom.current().nextInt(50);
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
            event.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
                try{
                    m.delete().queueAfter(20, TimeUnit.SECONDS);
                    event.getMessage().delete().queue();
                }catch(NullPointerException ignored){}
            });
            return false;
        }


        EmbedBuilder e = new EmbedBuilder();
        e.setTitle(title);
        e.setImage(url);
        event.getChannel().sendMessageEmbeds(e.build()).queue();

        return false;
    }

    public void getPrefix() throws SQLException{
        String temp = null;

        try (final Connection connection = SQLiteDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT prefix FROM prefix WHERE guildid = ?")) {
            preparedStatement.setLong(1, e.getGuild().getIdLong());
            try(final ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    //return resultSet.getString("prefix");
                    temp = resultSet.getString("prefix");
                    this.prefix = temp;

                }
            }
        } catch (SQLException e) {
            log.logger.warning(e.toString());
        }

    }
}
