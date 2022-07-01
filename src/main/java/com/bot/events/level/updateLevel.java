package com.bot.events.level;

import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class updateLevel extends ListenerAdapter {

    private static double multiplier;
    private static int lvl1_xp, msgXP,vcXP,vcTime,levelAmount,oldXP;

    public static void messageLevelUpdate(MessageReceivedEvent event){
        if(!event.getAuthor().isBot() && event.getChannelType().isGuild()){
            getCalculationPrefix(event.getGuild().getIdLong());
            int oldXP = getXP(event.getGuild().getIdLong(),event.getAuthor().getIdLong());
            CalculateNewXP(oldXP,event);
            int newXP = getXP(event.getGuild().getIdLong(),event.getAuthor().getIdLong());
            if(isLevelUp(oldXP,newXP)){
                LevelUp(event,newXP);
            }

        }
    }

    boolean first = true;
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event){
        if (!event.getMember().getUser().isBot()) {

            getCalculationPrefix(event.getGuild().getIdLong());
            int oldXP = getXP(event.getGuild().getIdLong(),event.getMember().getIdLong());
            new Timer().schedule(new TimerTask() {
                public void run() {
                    if (Objects.requireNonNull(event.getMember().getVoiceState()).inAudioChannel()) {
                        if(!first){
                            CalculateNewXP(getXP(event.getGuild().getIdLong(), Objects.requireNonNull(event.getMember()).getIdLong()), event);
                        }
                        else{
                            first = false;
                        }

                    } else {
                        int newXP = getXP(event.getGuild().getIdLong(),event.getMember().getIdLong());
                        if(isLevelUp(oldXP,newXP)){
                            LevelUp(event,newXP);
                        }
                        cancel();
                    }
                }
            }, 0, vcTime * 100000L);
        }
    }

    private static void LevelUp(MessageReceivedEvent event,int newXP) {
        int level = getLevel(newXP);
        EmbedBuilder e = new EmbedBuilder();
        e.setColor(Color.decode(config.get("color")));
        e.setTitle("You have reached level "+level+" on "+event.getGuild().getName()+"!" ,null);
        e.setThumbnail(Objects.requireNonNull(event.getGuild().getIconUrl()));
        e.setFooter("presented by " + config.get("bot_name"));
        event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(e.build()).queue());
    }
    private static void LevelUp(GuildVoiceJoinEvent event,int newXP) {
        int level = getLevel(newXP);
        EmbedBuilder e = new EmbedBuilder();
        e.setColor(Color.decode(config.get("color")));
        e.setTitle("You have reached level "+level+" on "+event.getGuild().getName()+"!" ,null);
        e.setThumbnail(Objects.requireNonNull(event.getGuild().getIconUrl()));
        e.setFooter("presented by " + config.get("bot_name"));
        event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(e.build()).queue());
    }

    private static boolean isLevelUp(int oldXP, int newXP){
        return getLevel(oldXP) != getLevel(newXP);
    }

    private static int getLevel(int xp){
        int tempXP = lvl1_xp;
        for (int i = 1; i < levelAmount; i++) {
            if(tempXP > xp){
                return i;
            }
            tempXP = (int) (tempXP * multiplier);
        }
        return 0;
    }

    private static void CalculateNewXP(int oldXP, MessageReceivedEvent event){
        setNewXP(oldXP+msgXP,event);
    }
    private static void CalculateNewXP(int oldXP, GuildVoiceJoinEvent event){
        setNewXP(oldXP+vcXP,event);
    }

    private static void setNewXP(int newXP, MessageReceivedEvent event){
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE level SET xp = ? WHERE guildid = ? AND userid = ?")) {
            preparedStatement.setInt(1, newXP);
            preparedStatement.setLong(2, event.getGuild().getIdLong());
            preparedStatement.setLong(3, event.getMember().getIdLong());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.logger.warning(updateLevel.class +": "+e.toString());
        }
    }
    private static void setNewXP(int newXP, GuildVoiceJoinEvent event){
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE level SET xp = ? WHERE guildid = ? AND userid = ?")) {
            preparedStatement.setInt(1, newXP);
            preparedStatement.setLong(2, event.getGuild().getIdLong());
            preparedStatement.setLong(3, event.getMember().getIdLong());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.logger.warning(updateLevel.class +": "+e.toString());
        }
    }
    private static int getXP(long guildid,long userid){
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT xp FROM level WHERE guildid = ? AND userid = ?")) {
            preparedStatement.setLong(1, guildid);
            preparedStatement.setLong(2, userid);
            try(final ResultSet resultSet = preparedStatement.executeQuery()){
                // do nothing
                if(resultSet.next()){
                    oldXP = resultSet.getInt("xp");
                }
                else{
                    try (final PreparedStatement preparedStatement2 = connection.prepareStatement("INSERT INTO level (userid,guildid,xp) VALUES (?,?,?)")) {
                        preparedStatement2.setLong(1, userid);
                        preparedStatement2.setLong(2, guildid);
                        preparedStatement2.setInt(3, 0);
                        preparedStatement2.executeUpdate();
                    }
                    oldXP = 0;
                }

            }
        }
            catch (SQLException e){
                        log.logger.warning(updateLevel.class +": "+e.toString());
            }
        return oldXP;
    }
    private static void getCalculationPrefix(long guildid){
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT amount,multiplier,lvl1_xp,msgXP,vcXP,vc_time FROM levelCalcPrefix WHERE guildid = ?")) {
            preparedStatement.setLong(1, guildid);
            try(final ResultSet resultSet = preparedStatement.executeQuery()){
                // do nothing
                if(resultSet.next()) {
                    levelAmount = resultSet.getInt("amount");
                    multiplier = resultSet.getDouble("multiplier");
                    lvl1_xp = resultSet.getInt("lvl1_xp");
                    msgXP = resultSet.getInt("msgXP");
                    vcXP = resultSet.getInt("vcXP");
                    vcTime = resultSet.getInt("vc_time");
                }
                else{throw new SQLException("No Result");}
            }
        }
        catch (SQLException e) {
            log.logger.warning(updateLevel.class +": "+e.toString());
        }
    }





}
