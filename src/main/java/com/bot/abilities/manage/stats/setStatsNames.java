package com.bot.abilities.manage.stats;

import com.bot.abilities.core.Command;
import com.bot.abilities.core.Prefix;
import com.bot.core.bot;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class setStatsNames extends Command {
    private long channelid,online;
    @Override
    public String[] call() {
        return new String[] {"modifyStats"};
    }
    @Override
    public Permission[] getPermissions() {
        return new Permission[] {Permission.MANAGE_SERVER};
    }
    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        try {
            String[] trimmed = args[1].trim().split("#");
            String[] channel_id = trimmed[1].trim().split(">");
            channelid = Long.parseLong(channel_id[0]);
            VoiceChannel channel = event.getGuild().getVoiceChannelById(channelid);
        } catch (Exception ignored) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("Something went wrong...", null);
            eb.setDescription("You have not mentioned the channel correctly!" +
                    "\nDo you want to learn how to do it right?");
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue();
            return false;
        }
        if (args[2].toLowerCase().equals("m") || args[2].toLowerCase().equals("o") || args[2].toLowerCase().equals("b")) {

        } else {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("Something went wrong...", null);
            eb.setDescription("You have not specified which channel you want to modify!" +
                    "\n" +
                    "Do you want to learn how to do it right?");
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue();
            return false;
        }
        String channelname = "";
        for (int i = 3; i <= args.length - 1; i++) {
            channelname = channelname + " " + args[i];
        }
        if (channelname.equals("")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.red);
            eb.setTitle("Something went wrong...", null);
            eb.setDescription("You have not given a name for the channel \n" +
                    "Do you want to learn how to do it right?");
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue();
            return false;
        }
        if (channelname.toLowerCase().contains("{counter}")) {
            channelname = channelname.replace("{Counter}", "{counter}");
            switch (args[2].toLowerCase()) {
                case "m":
                    try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));
                         final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE stats SET namemember = ? WHERE memberid = ?")) {
                        preparedStatement.setString(1, channelname);
                        preparedStatement.setLong(2, channelid);
                        preparedStatement.executeUpdate();
                        VoiceChannel memberchannel = bot.jda.getVoiceChannelById(channelid);
                        String nameMember = channelname.replace("{counter}", Integer.toString(memberchannel.getGuild().getMemberCount()));
                        memberchannel.getManager().setName(nameMember).queue();

                        if (!memberchannel.getName().equals(nameMember)) {
                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.red);
                            e.setTitle("Something went wrong...", null);
                            e.setDescription("The channel name was not changed due to rate limitation implemented by discord,\n" +
                                    " Please wait a few minutes and try again");
                            e.setFooter("presented by " + config.get("bot_name"));
                            event.getChannel().sendMessageEmbeds(e.build()).queue();
                            return false;
                        }

                    } catch (Exception e) {
                        log.logger.warning(getClass() + ": " + e.toString());
                    }
                    break;
                case "o":
                    try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));
                         final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE stats SET nameonline = ? WHERE memberid = ?")) {
                        preparedStatement.setString(1, channelname);
                        preparedStatement.setLong(2, channelid);
                        preparedStatement.executeUpdate();

                        VoiceChannel onlinechannel = bot.jda.getVoiceChannelById(channelid);
                        List<Member> allMember = onlinechannel.getGuild().getMembers();
                        allMember.forEach(member -> {
                            if (!member.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
                                online++;
                            }
                        });
                        String nameOnline = channelname.replace("{counter}", String.valueOf(online));
                        onlinechannel.getManager().setName(nameOnline).queue();

                        if (!onlinechannel.getName().equals(nameOnline)) {
                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.red);
                            e.setTitle("Something went wrong...", null);
                            e.setDescription("The channel name was not changed due to rate limitation implemented by discord,\n" +
                                    " Please wait a few minutes and try again");
                            e.setFooter("presented by " + config.get("bot_name"));
                            event.getChannel().sendMessageEmbeds(e.build()).queue();
                            return false;
                        }
                    } catch (Exception e) {
                        log.logger.warning(getClass() + ": " + e.toString());
                    }
                    break;
                case "b":
                    try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"), config.get("DATABASE_USERNAME"), config.get("DATABASE_PASSWORD"));
                         final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE stats SET namebooster = ? WHERE memberid = ?")) {
                        preparedStatement.setString(1, channelname);
                        preparedStatement.setLong(2, channelid);
                        preparedStatement.executeUpdate();
                        VoiceChannel boosterchannel = bot.jda.getVoiceChannelById(channelid);
                        String nameBooster = channelname.replace("{counter}", Integer.toString(boosterchannel.getGuild().getBoostCount()));
                        boosterchannel.getManager().setName(nameBooster).queue();

                        if (!boosterchannel.getName().equals(nameBooster)) {
                            EmbedBuilder e = new EmbedBuilder();
                            e.setColor(Color.red);
                            e.setTitle("Something went wrong...", null);
                            e.setDescription("The channel name was not changed due to rate limitation implemented by discord,\n" +
                                    " Please wait a few minutes and try again");
                            e.setFooter("presented by " + config.get("bot_name"));
                            event.getChannel().sendMessageEmbeds(e.build()).queue();
                            return false;
                        }
                    } catch (Exception e) {
                        log.logger.warning(getClass() + ": " + e.toString());
                    }
                    break;
                default:
                    break;
            }

            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.green);
            e.setTitle("Stats were successfully modified", null);
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue();

        } else {
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("Something went wrong...", null);
            e.setDescription("You have not specified the necessary variable _{counter}_, please use it where the number of counted persons should be placed" +
                    "\n" +
                    "Do you want to learn how to do it right?");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).setActionRow(yes_noBT()).queue();
            return false;
        }
        return false;
    }

    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesCustomStatNames", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noCustomStatNames", "No"));

        return buttons;
    }

    public static class ButtonClick extends ListenerAdapter {
        ButtonInteractionEvent e;
        String prefix;

        public void onButtonInteraction(ButtonInteractionEvent e) {
            //e.deferEdit().queue();
            this.e = e;
            try {
                prefix = Prefix.getPrefix(e);
            } catch (SQLException ex) {
                log.logger.warning(ex.toString());
            }
            switch (Objects.requireNonNull(e.getButton().getId())) {
                case "help_yesCustomStatNames" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How to modify the names of the individual stats counter", null);
                    eb.setDescription("To modify the channel names you need to execute: \n" +
                            "'" + prefix+ "modifyStats <#_channelID_> _M/O/B_  _name_' " +
                            "\n \n" +
                            "Replace _channelID_ with the id of the channel you want to edit\n"+
                            "Select if it is a (_M_)ember-/(_O_)nline- or (_B_)oostercounter channel \n"+
                            "Replace _name_ with your desired name\n\n"+
                            "**ATTENTION**\n"+
                            "_name_ has to include {counter}, it will later be replaced the number of counted persons, place it where ever you want to in _name_");

                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(more_helpBT()).queue();
                }
                case "help_noCustomStatNames" -> {
                    try{
                        e.getMessage().delete().queue();
                    }
                    catch(NullPointerException ignored){}
                }

                default -> {
                }
            }

        }
        private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> more_helpBT() {
            java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
            buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.link("https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-",
                    "How to get the Channel-ID"));

            return buttons;
        }
    }
}
