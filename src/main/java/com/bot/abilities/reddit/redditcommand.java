package com.bot.abilities.reddit;

import com.bot.abilities.core.Command;
import com.bot.core.Redditcore;
import com.bot.core.config;
import com.bot.core.sql.SQLDataSource;
import com.bot.log.log;
import net.dean.jraw.models.EmbeddedMedia;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class redditcommand extends Command {
    MessageReceivedEvent e;
    String prefix;
    String redditUsed;
    String [] reddits = new String[]{
            "waifusfortr3x/waifu/false",
            "memes/meme/false",
            "vid/vid/false",

    };

    @Override
    public String[] call() {
        return new String[]{
                "waifu",
                "meme",
                "vid",
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
            log.logger.warning(getClass()+": "+e.toString());
            return false;
        }


        String url = "";
        String title = "";
        EmbeddedMedia embeddedMedia ;
        String type;
        boolean notNull = false;
        int limit = 0;
       // if (redditUsed.equals("nsfw"))
        DefaultPaginator<Submission> paginator = Redditcore.reddit.subreddit(redditUsed).posts().limit(100).build();

        int pos = ThreadLocalRandom.current().nextInt(100);
        paginator.next();
        try {
            while(!notNull) {
                try {

                    url = paginator.getCurrent().get(pos).getUrl();

                    title = paginator.getCurrent().get(pos).getTitle();
                    notNull = true;
                }catch (Exception e){
                    pos = ThreadLocalRandom.current().nextInt(100);
                    log.logger.warning(getClass()+": "+e.toString());
                }
            }
            embeddedMedia = paginator.getCurrent().get(pos).getEmbeddedMedia();
            try {
                embeddedMedia.getOEmbed().getType();


                event.getChannel().sendMessage(paginator.getCurrent().get(0).getUrl()).queue();
                return false;

            }catch (NullPointerException e){}
        }catch (Exception e){
            log.logger.warning(getClass()+": "+e.toString());
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

        try (final Connection connection = SQLDataSource.getConnection();
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
            log.logger.warning(getClass()+": "+e.toString());
        }

    }
}
