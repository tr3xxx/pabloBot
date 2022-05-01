package com.bot.events;

import com.bot.core.bot;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class updateStats {
    static boolean firstRun = true;
    long memberid;
    long onlineid;
    long boosterid;
    int online = 0;
    String nameonline;
    String namemember;
    String namebooster;

    public updateStats() throws SQLException {
        Timer timer = new Timer();
        timer.schedule( new TimerTask() {
            public void run() {
                if(firstRun){
                    firstRun = false;
                }
                else{
                    update();
                }
            }
        }, 0, 30000); // 30000 = 5min, < 2x Request in 10min would end in being rate limited
    }
    public boolean update(){
        try (final Connection connection = SQLiteDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT memberid,onlineid,boosterid FROM stats")) {
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()){
                    memberid = resultSet.getLong("memberid");
                    onlineid = resultSet.getLong("onlineid");
                    boosterid = resultSet.getLong("boosterid");
                    try (final Connection connection1 = SQLiteDataSource.getConnection();
                         final PreparedStatement preparedStatement1 =connection1.prepareStatement("SELECT nameonline,namemember,namebooster FROM stats WHERE memberid = ? OR onlineid = ? OR boosterid = ? ")) {
                        preparedStatement1.setLong(1,memberid);
                        preparedStatement1.setLong(2,onlineid);
                        preparedStatement1.setLong(3,boosterid);
                        try (final ResultSet resultSet1 = preparedStatement1.executeQuery()) {
                            if (resultSet1.next()) {
                                namemember = resultSet1.getString("namemember");
                                nameonline = resultSet1.getString("nameonline");
                                namebooster = resultSet1.getString("namebooster");
                            }
                        }};
                    try {
                        VoiceChannel memberchannel = bot.jda.getVoiceChannelById(memberid);
                        assert memberchannel != null;
                        String nameMember = namemember.replace("{counter}",Integer.toString(memberchannel.getGuild().getMemberCount()));
                        memberchannel.getManager().setName(nameMember).queue();
                    } catch (Exception ignored) {}
                    try {
                        VoiceChannel onlinechannel = bot.jda.getVoiceChannelById(onlineid);
                        assert onlinechannel != null;
                        List<Member> allMember = onlinechannel.getGuild().getMembers();
                        allMember.forEach(member -> {
                            if(!member.getOnlineStatus().equals(OnlineStatus.OFFLINE)){
                                online++;
                            }
                        });
                        String nameOnline = nameonline.replace("{counter}",String.valueOf(online));
                        onlinechannel.getManager().setName(nameOnline).queue();
                        online = 0;
                    } catch (Exception ignored) {}
                    try {
                        VoiceChannel boosterchannel = bot.jda.getVoiceChannelById(boosterid);
                        assert boosterchannel != null;
                        String nameBooster = namebooster.replace("{counter}",Integer.toString(boosterchannel.getGuild().getBoostCount()));
                        boosterchannel.getManager().setName(nameBooster).queue();
                    } catch (Exception ignored) {}

                    memberid = 0;
                    onlineid= 0;
                    boosterid = 0;
                    nameonline = null;
                    namemember= null;
                    namebooster= null;
                }
            }
        } catch (SQLException e) {
            log.logger.warning(e.toString());
        }
        return false;
    }
}
