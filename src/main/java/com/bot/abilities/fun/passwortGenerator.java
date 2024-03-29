package com.bot.abilities.fun;

import com.bot.abilities.core.Command;
import com.bot.abilities.core.Prefix;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class passwortGenerator extends Command {
    @Override
    public String[] call() {
        return new String[] {"generatePassword","gPW"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[0];
    }

    @Override
    public boolean usableInDM() {
        return true;
    }

    private String prefix;
    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException { // !gPW <length>
        event.getMessage().delete().queue();
        if(args.length == 2){
            try{
                int length = Integer.parseInt(args[1]);
                String password = "";
                for(int i = 0; i < length; i++){
                    int random = (int) (Math.random() * 3);
                    if(random == 0){
                        password += (char) ((int) (Math.random() * 26) + 97);
                    }else if(random == 1){
                        password += (char) ((int) (Math.random() * 26) + 65);
                    }else{
                        password += (char) ((int) (Math.random() * 10) + 48);
                    }
                }
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.green);
                e.setTitle("Your Password has been generated", null);
                e.setDescription(password);
                e.setFooter("presented by " + config.get("bot_name"));
                event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(e.build()).queue());
                return false;
            }catch (NumberFormatException er){
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.red);
                e.setTitle("Something went wrong...", null);
                e.setDescription("You need to specify the exact length" +
                        "\n" +
                        "Please use the following command: " + prefix + "gPW <length>");
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).queue(m -> {
                    try{
                        m.delete().queueAfter(30, TimeUnit.SECONDS);
                    }catch(NullPointerException ignored){}

                });
                return false;
            }
        }
        else{
            try{
                prefix = Prefix.getPrefix(event);
            }catch (Exception e){
                log.logger.warning(getClass().getName() + ": " + e.getMessage());
            }

            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("Something went wrong...", null);
            e.setDescription("You need to specify the exact length" +
                    "\n" +
                    "Please use the following command: " + prefix + "gPW <length>");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue();
            return false;
        }
    }
}
