package com.bot.listeners;

import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


public class VoiceHub extends ListenerAdapter {

    private boolean isVoiceHub(long id) throws SQLException {
        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("SELECT voicehubid FROM voicehub WHERE voicehubid = ?")) {
            preparedStatement.setLong(1, id);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLDataException e) {
            log.logger.warning(e.toString());
        }
        return false;
    }

    private boolean isValidCategory(long id) throws SQLException{
        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("SELECT categoryid FROM voicehub WHERE categoryid = ?")) {
            preparedStatement.setLong(1, id);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLDataException e) {
            log.logger.warning(e.toString());
        }
        return false;

    }

    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {

        try {
            if (isVoiceHub(Long.parseLong(e.getChannelJoined().getId()))) {

                Guild guild = e.getGuild();

                List<GuildChannel> cat = Objects.requireNonNull(Objects.requireNonNull(e.getGuild().getVoiceChannelById(e.getChannelJoined().getId())).getParentCategory()).getChannels();
                String name = "Voice #" + cat.size();

                for (GuildChannel ch : cat) {
                    if (ch.getName().equals(name)) {
                        name = "Voice #" + cat.size() + 1;
                    }
                }
                guild.createVoiceChannel(name)
                        .setUserlimit(69)
                        .setParent(Objects.requireNonNull(e.getGuild().getVoiceChannelById(e.getChannelJoined().getId())).getParentCategory())
                        .setBitrate(e.getGuild().getMaxBitrate())
                        .syncPermissionOverrides()
                        .queue(voiceChannel ->
                                guild.moveVoiceMember(e.getMember(), guild.getVoiceChannelById(voiceChannel.getIdLong())).queue());
                                log.logger.info("VoiceChannel got created through an Voicehub ("+
                                        e.getGuild().getName()+","+
                                        e.getMember().getUser().getAsTag()+
                                        ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
        VoiceChannel channel = e.getGuild().getVoiceChannelById(Long.parseLong(e.getChannelLeft().getId()));
        try {
            assert channel != null;
            if (!isVoiceHub(channel.getIdLong())) {
                try{
                if(isValidCategory(channel.getParentCategoryIdLong())){
                    if(channel.getMembers().size() == 0){
                        channel.delete().queue();
                        log.logger.info("Voice Channel got deleted ("+
                                e.getGuild().getName()+")");
                        }
                    }
                }
                catch (Exception err){
                    log.logger.warning(err.toString());
                }
            }
        } catch (SQLException ex) {
            log.logger.warning(e.toString());
        }
    }
}
