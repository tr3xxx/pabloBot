package com.bot.abilities.manage.stats;

import com.bot.abilities.core.Command;
import com.bot.core.bot;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
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
    long channelid;
    long online;
    @Override
    public String[] call() {
        return new String[] {"customizeStatsName","cSN"};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException { // setStatsName <#channelid> M/O/B channelname
        if (event.getChannelType().isGuild()) {
            if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_CHANNEL)) {
                try{
                    String[] trimmed = args[1].trim().split("#");
                    String[] channel_id = trimmed[1].trim().split(">");
                    channelid = Long.parseLong(channel_id[0]);
                    VoiceChannel channel = event.getGuild().getVoiceChannelById(channelid);
                } catch (Exception e) {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("Something went wrong...", null);
                    eb.setDescription("You did not tagged the Channel correctly :( " +
                            "\n" +
                            "Do you want to learn how to do it correctly?");
                    eb.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue();
                    return false;
                }
                if(args[2].toLowerCase().equals("m") || args[2].toLowerCase().equals("o") || args[2].toLowerCase().equals("b")){

                }
                else{
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("Something went wrong...", null);
                    eb.setDescription("You did not choosed what kind of Channel the tagged Channel is :( " +
                            "\n" +
                            "Do you want to learn how to do it correctly?");
                    eb.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue();
                    return false;
                }
                String channelname= "";
                for (int i=3;i<=args.length-1;i++){
                    channelname = channelname +" "+args[i];
                }
                if(channelname.equals("")){
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.red);
                    eb.setTitle("Something went wrong...", null);
                    eb.setDescription("You did not entered an Channelname:( \n" +
                            "Do you want to learn how to do it correctly?");
                    eb.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue();
                    return false;
                }
                if(channelname.toLowerCase().contains("{counter}")) {
                    channelname = channelname.replace("{Counter}", "{counter}");
                    switch (args[2].toLowerCase()) {
                        case "m":
                            try (final Connection connection = SQLiteDataSource.getConnection();
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
                                    e.setDescription("The channel name was not changed due to rate limitation implemented by Discord," +
                                            " please wait a few minutes and try again");
                                    e.setFooter("presented by " + config.get("bot_name"));
                                    event.getChannel().sendMessageEmbeds(e.build()).queue();
                                    return false;
                                }

                            } catch (Exception e) {
                                log.logger.warning(getClass()+": "+e.toString());
                            }
                            break;
                        case "o":
                            try (final Connection connection = SQLiteDataSource.getConnection();
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
                                String nameOnline = channelname.replace("{counter}",String.valueOf(online));
                                onlinechannel.getManager().setName(nameOnline).queue();

                                if (!onlinechannel.getName().equals(nameOnline)) {
                                    EmbedBuilder e = new EmbedBuilder();
                                    e.setColor(Color.red);
                                    e.setTitle("Something went wrong...", null);
                                    e.setDescription("The channel name was not changed due to rate limitation implemented by Discord," +
                                            " please wait a few minutes and try again");
                                    e.setFooter("presented by " + config.get("bot_name"));
                                    event.getChannel().sendMessageEmbeds(e.build()).queue();
                                    return false;
                                }
                            } catch (Exception e) {
                                log.logger.warning(getClass()+": "+e.toString());
                            }
                            break;
                        case "b":
                            try (final Connection connection = SQLiteDataSource.getConnection();
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
                                    e.setDescription("The channel name was not changed due to rate limitation implemented by Discord," +
                                            " please wait a few minutes and try again");
                                    e.setFooter("presented by " + config.get("bot_name"));
                                    event.getChannel().sendMessageEmbeds(e.build()).queue();
                                    return false;
                                }
                            } catch (Exception e) {
                                log.logger.warning(getClass()+": "+e.toString());
                            }
                            break;
                        default:
                            log.logger.warning("[DEFAULT CASE]");
                            break;
                    }

                    EmbedBuilder e = new EmbedBuilder();
                    e.setColor(Color.green);
                    e.setTitle("Stats-Name successfully set", null);
                    e.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(e.build()).queue();

                }
                else{
                    EmbedBuilder e = new EmbedBuilder();
                    e.setColor(Color.red);
                    e.setTitle("Something went wrong...", null);
                    e.setDescription("You did not include the necessary {counter} in the channel name :( " +
                            "\n" +
                            "Do you want to learn how to do it?");
                    e.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(e.build()).setActionRow(yes_noBT()).queue();
                    return false;
                }

            } else {
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.red);
                e.setTitle("Something went wrong...", null);
                e.setDescription("You don't have enough permissions :( " +
                        "\n" +
                        "In order to create Stats-Channel, you need the permission to " +
                        "manage Channel on this Server");
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).queue();
                return false;

            }
        }
        else {
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.red);
                e.setTitle("Something went wrong...", null);
                e.setDescription("You can't create Stats-Channel through a DM :( " +
                        "\n" +
                        "Please use a Server-TextChannel to create Stats-Channel");
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).queue();
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
                getPrefix();
            } catch (SQLException ex) {
                log.logger.warning(ex.toString());
            }
            switch (Objects.requireNonNull(e.getButton().getId())) {
                case "help_yesCustomStatNames" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How to customize Stats-Names", null);
                    eb.setDescription("To customize Stats-Names you need to execute: \n" +
                            "'" + prefix+ "customizeStatsNames <#channelid> M/O/B 'customName {counter}' " +
                            "\n \n" +
                            "1. Replace 'channelid' with the ID of the Channel you want to edit\n"+
                            "2. Choose if the tagged Channel is a (M)ember-/(O)nline- or (B)oostercounter-Channel \n"+
                            "3. Replace 'customName' with your own Custom Name\n\n"+
                            "**ATTENTION**\n"+
                            "'customName' has to include {counter}, it will later be replaced with the counter, place it " +
                            "where ever you want to"
                    );
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
        public void getPrefix() throws SQLException{
            String temp = null;

            try (final Connection connection = SQLiteDataSource.getConnection();
                 final PreparedStatement preparedStatement = connection.prepareStatement("SELECT prefix FROM prefix WHERE guildid = ?")) {
                preparedStatement.setLong(1, e.getGuild().getIdLong());
                try(final ResultSet resultSet = preparedStatement.executeQuery()){
                    if(resultSet.next()){
                        //return resultSet.getString("prefix");
                        temp = resultSet.getString("prefix");
                        this.prefix = temp;

                    }
                }
            } catch (SQLException e) {
                log.logger.warning(getClass()+": "+e.toString());
            }

        }
    }
}
