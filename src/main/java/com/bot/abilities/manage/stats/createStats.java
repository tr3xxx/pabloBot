package com.bot.abilities.manage.stats;

import com.bot.abilities.core.Command;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class createStats extends Command {
    long members;
    long boosters;
    long online = 0;
    long categoryid;
    long onlineid;
    long memberid;
    long boostid;

    @Override
    public String[] call() {
        return new String[] {"createStats","cS"};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        if (event.getChannelType().isGuild()) {
            if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_CHANNEL)) {

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
                    try (final Connection connection = SQLiteDataSource.getConnection();
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
                                try (final Connection connection = SQLiteDataSource.getConnection();
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
                                try (final Connection connection = SQLiteDataSource.getConnection();
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
                                try (final Connection connection = SQLiteDataSource.getConnection();
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
            } else {
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.red);
                e.setTitle("Something went wrong...", null);
                e.setDescription("You don't have enough permissions :( " +
                        "\n" +
                        "In order to create Stats-Channel, you need the permission to " +
                        "manage Channel on this Server");
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).queue();

            }
        } else {
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("Something went wrong...", null);
            e.setDescription("You can't create Stats-Channel through a DM :( " +
                    "\n" +
                    "Please use a Server-TextChannel to create Stats-Channel");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue();
        }
        return false;
    }
}
