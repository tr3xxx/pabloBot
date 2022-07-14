package com.bot.abilities.basic.JoinAndLeaveMessage;

import com.bot.core.bot;
import com.bot.core.config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Member;
import java.sql.*;
import java.util.Objects;

public class LeaveMessage extends ListenerAdapter {


    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        long id=0;
        try {
            final Connection conn = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT channelid FROM WelcomeLeaveMessage WHERE guildid = ?");
            preparedStatement.setString(1, event.getGuild().toString());
            final ResultSet set = preparedStatement.executeQuery();

            if(set.next()){
                id = set.getLong("channelid");
            }
            try {
                EmbedBuilder e = new EmbedBuilder();
                e.setTitle("**"+Objects.requireNonNull(event.getMember()).getEffectiveName()+" left the server!**");
                bot.jda.getTextChannelById(id).sendMessageEmbeds(e.build()).queue();

            }catch (NullPointerException e){}

        }catch (SQLException e){}



    }

}
