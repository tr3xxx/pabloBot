package com.bot.abilities.manage.stats;

import com.bot.abilities.core.Command;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class createStats extends Command {
    private long members,boosters,online = 0,categoryid,onlineid,memberid,boostid;

    @Override
    public String[] call() {
        return new String[] {"createStats"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[] {Permission.MANAGE_SERVER};
    }

    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

                List<Member> allMember = event.getGuild().getMembers();
                allMember.forEach(member -> {
                   if(!member.getOnlineStatus().equals(OnlineStatus.OFFLINE)){
                        online++;
                    }
                });
                members = event.getGuild().getMemberCount();
                boosters = event.getGuild().getBoostCount();


                event.getGuild().createCategory("Stats").queue(category -> {
                    categoryid = category.getIdLong();
                    try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                         final PreparedStatement preparedStatement =
                                 connection.prepareStatement("INSERT INTO stats(categoryid,memberid,onlineid,boosterid,nameonline,namemember,namebooster) VALUES(?,?,?,?,?,?,?)")) {
                        preparedStatement.setLong(1,categoryid);
                        preparedStatement.setLong(2,0);
                        preparedStatement.setLong(3,0);
                        preparedStatement.setLong(4,0);
                        preparedStatement.setString(5,"Online: {counter}");
                        preparedStatement.setString(6,"Member: {counter}");
                        preparedStatement.setString(7,"Boosts: {counter}");
                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        log.logger.warning(getClass()+": "+e.toString());
                    }

                    event.getGuild().createVoiceChannel("Online: " + online)
                            .setParent(event.getGuild().getCategoryById(categoryid))
                            .queue(voiceChannel -> {
                                this.onlineid = voiceChannel.getIdLong();
                                try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                                     final PreparedStatement preparedStatement =
                                             connection.prepareStatement("UPDATE stats SET onlineid = ? WHERE categoryid = ?")) {
                                    preparedStatement.setLong(1,onlineid);
                                    preparedStatement.setLong(2,categoryid);
                                    preparedStatement.executeUpdate();
                                } catch (SQLException e) {
                                    log.logger.warning(getClass()+": "+e.toString());
                                }
                            });
                    event.getGuild().createVoiceChannel("Member: " + members)
                            .setParent(event.getGuild().getCategoryById(categoryid))
                            .queue(voiceChannel -> {
                                this.memberid = voiceChannel.getIdLong();
                                try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                                     final PreparedStatement preparedStatement =
                                             connection.prepareStatement("UPDATE stats SET memberid = ? WHERE categoryid = ?")) {
                                    preparedStatement.setLong(1,memberid);
                                    preparedStatement.setLong(2,categoryid);
                                    preparedStatement.executeUpdate();
                                } catch (SQLException e) {
                                    log.logger.warning(getClass()+": "+e.toString());
                                }
                            });
                    event.getGuild().createVoiceChannel("Booster: " + boosters)
                            .setParent(event.getGuild().getCategoryById(categoryid))
                            .queue(voiceChannel -> {
                                this.boostid = voiceChannel.getIdLong();
                                try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                                     final PreparedStatement preparedStatement =
                                             connection.prepareStatement("UPDATE stats SET boosterid = ? WHERE categoryid = ?")) {
                                    preparedStatement.setLong(1,boostid);
                                    preparedStatement.setLong(2,categoryid);
                                    preparedStatement.executeUpdate();
                                } catch (SQLException e) {
                                    log.logger.warning(getClass()+": "+e.toString());
                                }
                            });


                });


                return false;

    }
}
