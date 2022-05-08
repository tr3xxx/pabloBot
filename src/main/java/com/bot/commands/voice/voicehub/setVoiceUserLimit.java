package com.bot.commands.voice.voicehub;

import com.bot.commands.core.Command;
import com.bot.core.config;
import com.bot.core.sql.SQLiteDataSource;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class setVoiceUserLimit extends Command {
    long channelid;
    int userlimit;
    @Override
    public String call() {
        return "setUserlimit";
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        if (event.getChannelType().isGuild()) {
            if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_CHANNEL)) {
                if (args.length == 3) {
                    try {
                        String[] trimmed = args[1].trim().split("#");
                        String[] ch_id = trimmed[1].trim().split(">");
                        channelid = Long.parseLong(ch_id[0]);
                        userlimit = Integer.parseInt(String.valueOf(args[2]));
                    }catch (Exception e){
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.red);
                        eb.setTitle("Something went wrong...", null);
                        eb.setDescription("You did not run this command correctly :( " +
                                "\n" +
                                "Do you want to learn how to do it correctly?");
                        eb.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue();
                        return false;
                    }
                    if(isVoiceHub(channelid)) {
                        try (final Connection connection = SQLiteDataSource.getConnection();
                             final PreparedStatement insertStatement = connection.prepareStatement("UPDATE voicehub SET userlimit = ? WHERE voicehubid = ?")) {
                            insertStatement.setLong(1, userlimit);
                            insertStatement.setLong(2, channelid);
                            insertStatement.executeUpdate();
                        } catch (SQLException e) {
                            log.logger.warning(e.toString());
                            return false;
                        }
                        log.logger.info("New Voicehub-Userlimit has been set. (Server: " + event.getGuild().getName() + ", Voicehub: " + event.getGuild().getVoiceChannelById(channelid).getName()  + "Limit: " + userlimit + ")");
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.green);
                        e.setTitle("Voicehub-Limit successfully set to '" + userlimit + "'", null);
                        e.setDescription(" ");
                        e.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(e.build()).queue();
                    }else{
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.red);
                        eb.setTitle("Something went wrong...", null);
                        eb.setDescription("Tagged channel is no Voicehub :( " +
                                "\n" +
                                "Do you want to learn how to do it correctly?");
                        eb.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue();
                        return false;
                    }
                }
                else {
                    EmbedBuilder e = new EmbedBuilder();
                    e.setColor(Color.red);
                    e.setTitle("Something went wrong...", null);
                    e.setDescription("You did not run this command correctly :( " +
                            "\n" +
                            "Do you want to learn how to do it correctly?");
                    e.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(e.build()).setActionRow(yes_noBT()).queue();

                    return false;
                }
            }else {
                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.red);
                e.setTitle("Something went wrong...", null);
                e.setDescription("You don't have enough permissions :( " +
                        "\n" +
                        "In order to be able to edit Voicehubs, you need the permission to manage channels on this " +
                        "Server");
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).queue();
            }

        }else {
            EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.red);
            e.setTitle("Something went wrong...", null);
            e.setDescription("Voicehubs can not be set through DM's :( " +
                    "\n" +
                    "Please use a Server-TextChannel to edit a Voicehub");
            e.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(e.build()).queue();
        }
        return false;
    }
    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT () {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesUserlimit", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noUserlimit", "No"));

        return buttons;
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
                case "help_yesUserlimit" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How to set a VoiceHub", null);
                    eb.setDescription("To set a Userlimit for created Voice-Channels you need to execute: \n" +
                            "'" + prefix + "setUserlimit <#channelid> 'userlimit'' " +
                            "\n \n" +
                            "Replace 'channelid' with the ID of the desired channel and 'userlimit with your wished userlimit'"
                    );
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(more_helpBT()).queue();
                }
                case "help_noUserlimit" -> {
                    try{
                        e.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
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
                log.logger.warning(e.toString());
            }

        }

    }
}
