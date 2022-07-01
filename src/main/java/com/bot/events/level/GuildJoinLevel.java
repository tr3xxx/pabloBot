package com.bot.events.level;

import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.*;
import java.util.List;

public class GuildJoinLevel extends ListenerAdapter {
    static int count = 0;

    public void onGuildJoin(MessageReceivedEvent event){ action(event);}


    public void action(MessageReceivedEvent event){
        List<Member> members = event.getGuild().getMembers();
        members.forEach(member -> {
            try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                 final PreparedStatement preparedStatement = connection.prepareStatement("SELECT xp FROM level WHERE guildid = ? AND userid = ?")) {
                preparedStatement.setLong(1, event.getGuild().getIdLong());
                preparedStatement.setLong(2, member.getIdLong());
                try(final ResultSet resultSet = preparedStatement.executeQuery()){
                    // do nothing
                    if(resultSet.next()) return;
                    else{throw new SQLException("No Result");}

                }
            }

            catch (SQLException e) {
                try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                     final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO level(guildid,userid,xp) VALUES(?,?,?)")) {
                    preparedStatement.setLong(1, event.getGuild().getIdLong());
                    preparedStatement.setLong(2, member.getIdLong());
                    preparedStatement.setInt(3, 0);
                    preparedStatement.executeUpdate();
                    count++;

                } catch (SQLException er) {
                    log.logger.warning(GuildJoinLevel.class +": "+er.toString());
                }

            }

        });
        if (count != 0) {
            log.logger.info("New Server has been added to the Level-System (Server: " + event.getGuild().getName() + ", Users: " + count + ", User: Bot");
        }
        if(!xpCalculationPrefix(event.getGuild().getIdLong())){
            createCalculationPrefix(event.getGuild().getIdLong());
            log.logger.info("New CalculationPrefix has been set (Server: " + event.getGuild().getName() + ")");
        }



    }

    public boolean xpCalculationPrefix(long guildid){
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT amount,multiplier,lvl1_xp FROM levelCalcPrefix WHERE guildid = ?")) {
            preparedStatement.setLong(1, guildid);
            try(final ResultSet resultSet = preparedStatement.executeQuery()){
                // do nothing
                if(resultSet.next()) return true;
                else{throw new SQLException("No Result");}
            }
        }
        catch (SQLException e) {
            return false;
        }
    }

    public void createCalculationPrefix(long guildid){
        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO levelCalcPrefix(guildid,amount,multiplier,lvl1_xp,msgXP,vcXP,vc_time) VALUES(?,?,?,?,?,?,?)")) {
            preparedStatement.setLong(1, guildid);
            preparedStatement.setInt(2, 10);
            preparedStatement.setDouble(3, 2.0);
            preparedStatement.setInt(4, 100);
            preparedStatement.setInt(5, 1);
            preparedStatement.setInt(6, 5);
            preparedStatement.setInt(7, 10); // time in minutes
            preparedStatement.executeUpdate();
        } catch (SQLException er) {
            log.logger.warning(GuildJoinLevel.class +": "+er.toString());
        }
    }

}
