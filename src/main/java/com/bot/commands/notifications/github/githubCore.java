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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class githubCore {
    public static GHCommit lastCommit;
    public static GitHub github;
    String affectedFiles=null;
    public githubCore() throws IOException {github = GitHubBuilder.fromPropertyFile(".github/github.properties").build();getLastCommit();}

    public void getLastCommit() throws IOException {
        GHRepository repo = github.getRepository("tr3xxx/pabloBot");
        List<GHCommit> commits = repo.listCommits().toList();
        lastCommit = commits.get(0);
        String CommiterURL = String.valueOf(lastCommit.getAuthor().getUrl());
        EmbedBuilder eb = new EmbedBuilder();
        List<GHCommit.File> files = lastCommit.getFiles();
        String sha =  lastCommit.getSHA1().substring(0,7);
        String addedOrDeleted;
        int linesChanged = lastCommit.getLinesAdded()-lastCommit.getLinesDeleted();
        if(linesChanged>0){
            addedOrDeleted  = "added";
        }
        else{
            addedOrDeleted = "deleted";
        }

        files.forEach(file -> {
            String[] fileParts = file.getFileName().trim().split("/");
            String fileName = fileParts[fileParts.length-1];
            affectedFiles = affectedFiles + "\n" + fileName;
            if(affectedFiles.length()>500){
                affectedFiles = affectedFiles + "\n" + "...";
            }
        });
        eb.setColor(Color.black);
        eb.setTitle("Commit "+sha+" ("+repo.getFullName()+")", null);
        eb.setThumbnail("https://logos-world.net/wp-content/uploads/2020/11/GitHub-Logo-700x394.png");


        eb.addField("",
                linesChanged+" lines "+addedOrDeleted+" (+"+lastCommit.getLinesAdded()+"/-"+lastCommit.getLinesDeleted()+")\n"+
                        "**Comment** \n_"+lastCommit.getCommitShortInfo().getMessage()+"_ \n"+
                        "**Author** \n"+ CommiterURL.replace("api.","").replace("/users","")+"\n\n"

                ,false);
        eb.addField("**Affected files**","```"+affectedFiles.replace("null","")+"```",false);
        eb.setFooter("presented by " + config.get("bot_name"));

        String repoUrl = "https://github.com/"+repo.getFullName();
        String commitUrl = repoUrl+"/commit/"+lastCommit.getSHA1();

        bot.jda.getTextChannelById("979405429442478080").sendMessageEmbeds(eb.build()).setActionRow(repo_commit(repoUrl,commitUrl)).queue();
    }
    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> repo_commit (String repo,String commit) {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.link(repo, "Repository"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.link(commit, "Details"));

        return buttons;
    }
}
