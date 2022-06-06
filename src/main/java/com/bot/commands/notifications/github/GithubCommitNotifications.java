package com.bot.commands.notifications.github;

import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class GithubCommitNotifications {
    Long channelid;
    String repo;
    String lastsha;
    public GithubCommitNotifications() {
        new Timer().schedule(new TimerTask() {
            public void run() {
                try (final Connection connection = SQLiteDataSource.getConnection();
                     final PreparedStatement preparedStatement = connection.prepareStatement("SELECT channelid,repo,lastsha FROM githubNotifications")) {
                    try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                        while(resultSet.next()) {
                            repo = resultSet.getString("repo");
                            channelid  = resultSet.getLong("channelid");
                            lastsha = resultSet.getString("lastsha");

                            if(githubCore.getLastCommit(repo,channelid,lastsha)){
                                String newsha = githubCore.newsha;
                                try (final PreparedStatement insertStatement = connection.prepareStatement("UPDATE githubNotifications SET lastsha = ? WHERE channelid = ? AND repo = ?")) {
                                    insertStatement.setString(1, newsha);
                                    insertStatement.setLong(2, channelid);
                                    insertStatement.setString(3, repo);
                                    insertStatement.execute();
                                }
                            }

                            githubCore.newsha = null;
                            channelid = null;
                            repo = null;
                            lastsha = null;
                        }
                    }
                } catch (Exception e) {
                    log.logger.warning(e.toString());
                }
            }
        }, 0, 60000);
    }
}
