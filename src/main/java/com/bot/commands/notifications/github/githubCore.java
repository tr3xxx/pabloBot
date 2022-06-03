package com.bot.commands.notifications.github;

import com.bot.core.bot;
import com.bot.core.config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class githubCore {
    public static GHCommit lastCommit;
    public githubCore() throws IOException {
        GitHub github = GitHubBuilder.fromPropertyFile(".github/github.properties").build();
        GHRepository repo = github.getRepository("tr3xxx/pabloBot");
        List<GHCommit> commits = repo.listCommits().toList();
        lastCommit = commits.get(commits.size()-1);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.black);
        eb.setTitle("Github Commit-Notification ("+repo.getName()+")", null);
        eb.setThumbnail("https://cdn-icons-png.flaticon.com/512/25/25231.png");
        eb.addField("**Details** ",
                "CommitID: "+lastCommit.getCommitShortInfo().getMessage()+"\n"+
                "Commiter: "+ lastCommit.getAuthor().getName()+"\n"+
                "Time: "+lastCommit.getCommitDate()
        ,false);
        eb.setFooter("presented by " + config.get("bot_name"));

        bot.jda.getTextChannelById("979405429442478080").sendMessageEmbeds(eb.build()).queue();
    }
}
