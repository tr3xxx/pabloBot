package com.bot.abilities.reddit;

import com.bot.core.Redditcore;
import com.bot.log.log;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RedditNews implements Runnable {
    public MessageReceivedEvent e;

    RedditNews(MessageReceivedEvent e){
        this.e = e;
    }
    @Override
    public void run() {


        String url = "";
        String controll = "";


        while (true){
            DefaultPaginator<Submission> paginator = Redditcore.reddit.subreddit("Nachrichten").posts().limit(Paginator.RECOMMENDED_MAX_LIMIT).build();
            try {

                paginator.next();
                controll = paginator.getCurrent().get(0).getTitle();
            }catch (Exception ignored){}

            System.out.println("I do stuff");

            if(!controll.equals(url)){
               url = controll;
                EmbedBuilder s = new EmbedBuilder();
                try {

                    s.setTitle(paginator.getCurrent().get(0).getTitle());
                    s.setDescription(paginator.getCurrent().get(0).getSelfText() + "\n \n" + paginator.getCurrent().get(0).getUrl());
                    s.setAuthor(paginator.getCurrent().get(0).getAuthor());
                    s.setImage(paginator.getCurrent().get(0).getThumbnail());
                }catch (Exception e){
                    log.logger.warning(getClass()+": "+e.toString());
                }
                this.e.getChannel().sendMessageEmbeds(s.build()).queue();
            }


            try {
                Thread.sleep(600000);
            }
            catch (InterruptedException e){
                log.logger.warning(getClass()+": "+e.toString());
            }

        }





    }
}
