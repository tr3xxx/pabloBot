package com.bot.listeners;

import com.bot.commands.voice.voicehub.setGeneratedNames;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.*;
import java.util.List;
import java.util.Objects;


public class VoiceHub extends ListenerAdapter {
    String name;
    Long userlimit = 69L;

    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {

        try {
            if (isVoiceHub(Long.parseLong(e.getChannelJoined().getId()))) {

                Guild guild = e.getGuild();

                List<GuildChannel> cat = Objects.requireNonNull(Objects.requireNonNull(e.getGuild().getVoiceChannelById(e.getChannelJoined().getId())).getParentCategory()).getChannels();
                if(getName(e.getChannelJoined().getIdLong())){
                    int index = cat.size();
                    name = name.replace("{index}",String.valueOf(index));
                    for (GuildChannel ch : cat) {
                        if (ch.getName().equals(name)) {
                            name = name.replace("{index}",String.valueOf(index+1));
                        }
                    }
                }
                else{
                    this.name = "das sollte nicht so sein";
                }
                getUserlimit(e.getChannelJoined().getIdLong());
                guild.createVoiceChannel(name)
                        .setUserlimit(Math.toIntExact(userlimit))
                        .setParent(Objects.requireNonNull(e.getGuild().getVoiceChannelById(e.getChannelJoined().getId())).getParentCategory())
                        .setBitrate(e.getGuild().getMaxBitrate())
                        .syncPermissionOverrides()
                        .queue(voiceChannel -> {
                            guild.moveVoiceMember(e.getMember(),
                                    guild.getVoiceChannelById(voiceChannel.getIdLong())).queue();
                            log.logger.info("VoiceChannel got created through an Voicehub ("+
                                    e.getGuild().getName()+","+
                                    e.getMember().getUser().getAsTag()+
                                    ")");
                            try {
                                setValidCategory(voiceChannel.getParentCategoryIdLong(),e.getChannelJoined().getIdLong());
                            } catch (SQLException ex) {
                                log.logger.warning(ex.toString());
                            }
                        });
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
                            try{
                                channel.delete().queue();
                            }catch(NullPointerException ignored){}
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

    private boolean getUserlimit(long id) throws SQLException {
        try (final Connection connection = SQLiteDataSource.getConnection();
             final PreparedStatement preparedStatement =
                     connection.prepareStatement("SELECT userlimit FROM voicehub WHERE voicehubid = ?")){
            preparedStatement.setLong(1, id);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.userlimit = resultSet.getLong("userlimit");
                    return true;
                }
            }
        } catch (SQLDataException e) {
            log.logger.warning(e.toString());
        }
        return false;
    }


    private boolean isVoiceHub(long id) throws SQLException {
        try (final Connection connection = SQLiteDataSource.getConnection();
             final PreparedStatement preparedStatement =
                     connection.prepareStatement("SELECT voicehubid FROM voicehub WHERE voicehubid = ?")) {
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

    private void setValidCategory(long cat_id,long ch_id)throws SQLException {
        try (final Connection connection = SQLiteDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE voicehub SET categoryid = ? WHERE voicehubid = ?")) {
            preparedStatement.setLong(1, cat_id);
            preparedStatement.setLong(2, ch_id);
            preparedStatement.executeUpdate();
        }
    }

    private boolean isValidCategory(long id) throws SQLException{
        try (final Connection connection = SQLiteDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT categoryid FROM voicehub WHERE categoryid = ?")) {
            preparedStatement.setLong(1, id);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("categoryid") == id;
                }
            }
        } catch (SQLDataException e) {
            log.logger.warning(e.toString());
        }
        return false;


    }
    private boolean getName(long id){
        try (final Connection connection = SQLiteDataSource.getConnection();
             final PreparedStatement preparedStatement =
                     connection.prepareStatement("SELECT name FROM voicehub WHERE voicehubid = ?")) {
            preparedStatement.setLong(1, id);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.name = resultSet.getString("name");
                    return true;
                }
            }
        } catch (SQLException e) {
            log.logger.warning(e.toString());
        }
        return false;
    }

}