package com.bot.abilities.levelsystem;

import com.bot.abilities.core.Command;
import com.bot.core.config;
import com.bot.events.level.updateLevel;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class getLevel extends Command {
    private int lvl1_xp, msgXP,vcXP,vcTime,levelAmount,oldXP;
    private double multiplier;
    @Override
    public String[] call() {
        return new String[]{"lvl","level"};
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
        event.getMessage().delete().queue();
        getCalculationPrefix(event.getGuild().getIdLong());
        if(args.length==2){
            String[] trimmed = args[1].trim().split("@");
            String[] memberid = trimmed[1].trim().split(">");
            Member member = event.getGuild().getMemberById(memberid[0]);
            int level = getLevel(getXP(event.getGuild().getIdLong(),member.getIdLong()));

            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.decode(config.get("color")));
            e.setTitle(member.getUser().getName()+" is currently level "+level+" on "+event.getGuild().getName()+"!" ,null);
            e.setThumbnail(Objects.requireNonNull(event.getGuild().getIconUrl()));
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });

            return false;
        }
        else{
            getCalculationPrefix(event.getGuild().getIdLong());
            int level = getLevel(getXP(event.getGuild().getIdLong(),event.getAuthor().getIdLong()));

            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.decode(config.get("color")));
            e.setTitle(event.getAuthor().getName()+" is currently level "+level+" on "+event.getGuild().getName()+"!" ,null);
            e.setThumbnail(Objects.requireNonNull(event.getGuild().getIconUrl()));
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue(m -> {
                try{
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                }catch(NullPointerException ignored){}

            });

            return false;
        }
    }
    private int getXP(long guildID,long userID){
        int xp;
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT xp FROM level WHERE guildid = ? AND userid = ?")) {
            preparedStatement.setLong(1, guildID);
            preparedStatement.setLong(2, userID);
            try(final ResultSet resultSet = preparedStatement.executeQuery()){
                // do nothing
                if(resultSet.next()){
                    return resultSet.getInt("xp");
                }
                else{throw new SQLException("No Result");}
            }
        }
        catch (SQLException e){
            log.logger.warning(updateLevel.class +": "+e.toString());
        }
        return 0;
    }

    private void getCalculationPrefix(long guildid){
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
    private int getLevel(int xp){
        int tempXP = lvl1_xp;
        for (int i = 1; i < levelAmount; i++) {
            if(tempXP > xp){
                return i;
            }
            tempXP = (int) (tempXP * multiplier);
        }
        return 0;
    }
}
