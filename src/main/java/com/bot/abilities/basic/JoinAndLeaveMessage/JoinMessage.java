package com.bot.abilities.basic.JoinAndLeaveMessage;

import com.bot.core.bot;
import com.bot.core.config;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.*;
import java.util.Objects;

public class JoinMessage extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event){


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
                bot.jda.getTextChannelById(id).sendMessage("Welcome "+Objects.requireNonNull(event.getMember()).getEffectiveName()+"!").queue();
            }catch (NullPointerException e){}

        }catch (SQLException e){}

    }
}
