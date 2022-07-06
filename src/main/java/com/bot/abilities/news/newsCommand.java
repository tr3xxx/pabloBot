package com.bot.abilities.news;

import com.bot.core.bot;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class newsCommand implements Runnable {

    protected void command(){

        newsRSSReader rssReader = new newsRSSReader();
        String[] replacements = {"&quot;","<![CDATA[","2022]]>","![CDATA[","]]"};
        int i = 1;

        //replace Placeholders
        for (String rssFeed : getRssFeeds()) {
            String[] feed = rssReader.readRSS(rssFeed).split("\n");
            for(String replace : replacements){
                String temp = feed[1].replace(replace,"");
                feed[1] = temp;
                temp = feed[0].replace(replace,"");
                feed[0] = temp;
            }

            if(checkIfLatest(feed[0], rssFeed)){
                for(long j : getChannelIDs()) {
                    try {

                        EmbedBuilder news = new EmbedBuilder();
                        news.setTitle("**" + feed[0] + "**");
                        news.setDescription(feed[1]);
                        news.setFooter("Source: " + rssFeed);
                        Objects.requireNonNull(bot.jda.getTextChannelById(j)).sendMessageEmbeds(news.build()).queue();
                    } catch (Exception ignored) {}
                }
            }
            i++;
        }

        //Sleep because I don't want server to crash
        try {
            Thread.sleep(600000);
        }catch (Exception e){
            log.logger.warning(e.toString());
        }
        command();
    }

    //this puts a feed in the Database
    protected void putInDatabase(String feed){
        //System.out.println("In Method");
        String sql;
        try {
            final Connection conn = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));
            Statement st = conn.createStatement();
            sql = "SELECT url FROM rssfeeds";
            ResultSet rs = st.executeQuery(sql);

            boolean isInDatabase = false;

            while (rs.next()){
                 System.out.println("In Loop");
                 if(feed.equals(rs.getString("url"))){
                     //System.out.println("Is in Database");
                     isInDatabase = true;
                 }
            }


            if(!isInDatabase){
                sql = "INSERT INTO rssfeeds VALUES ('"+feed+"',null)";
                st.executeUpdate(sql);
                log.logger.info(feed+" added to RSS feeds");
            }

        }catch (Exception e){
            log.logger.warning(e.toString());
        }

    }

    //this checks if a feed is the latest of the assigned rss feed
    protected boolean checkIfLatest(String title,String url){

        //System.out.println("title in checkIfLatest "+title );

        try{
            final Connection conn = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT lastPost FROM rssfeeds WHERE url = ?");
            preparedStatement.setString(1,url);
            final ResultSet set = preparedStatement.executeQuery();

            if(set.next()){
                //System.out.println("Output title from Database "+set.getString("lastPost"));
                if(title.equals(set.getString("lastPost"))){
                    //System.out.println("title equals title in Database");
                    return false;
                }
                else{
                   // System.out.println("Updates");
                    final PreparedStatement updateQuery = conn.prepareStatement("UPDATE rssfeeds SET lastPost = ? WHERE url = ?");
                    updateQuery.setString(1,title);
                    updateQuery.setString(2,url);
                    updateQuery.executeUpdate();
                    return true;
                }

            }
            else {
                //System.out.println("first change");
                final PreparedStatement insertFirstPost = conn.prepareStatement("UPDATE rssfeeds SET lastPost = ? WHERE  url = ?");
                insertFirstPost.setString(1,title);
                insertFirstPost.setString(2,url);
                insertFirstPost.executeUpdate();
            }

        }catch (SQLException e){
            log.logger.warning(e.toString());
        }

        return true;
    }

    //this runs the command (first method)
    @Override
    public void run() {

        command();
    }

    //this gets the rss feeds from the database
    private String[] getRssFeeds(){
        ArrayList<String> a = new ArrayList<>();
        try {
            final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT url FROM rssfeeds");
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String temp = resultSet.getString("url");
                //System.out.println(temp+" temp");
                a.add(temp);
            }

        }catch (SQLException|NullPointerException e){
            log.logger.warning(e.toString());
        }

        return a.toArray(new String[0]);
    }

    //this gets the id's from channel that are assigned to be news channel
    private Long[] getChannelIDs(){

        ArrayList<Long> a = new ArrayList<>();
        try {
            final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT ChannelID FROM newsChannel");
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Long temp = resultSet.getLong("ChannelID");
                //System.out.println(temp+" channelID");
                a.add(temp);
            }

        }catch (SQLException|NullPointerException i){
            log.logger.warning(i.toString());
        }

        return a.toArray(new Long[0]);

    }


}
