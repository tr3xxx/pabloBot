package com.bot.abilities.voice.voicehub;

import com.bot.abilities.core.Command;
import com.bot.abilities.core.Prefix;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;


public class setVoicehub extends Command {
    String[] ch_id;
    long cat_id;
    long guild_id;

    @Override
    public String[] call() {
        return new String[] {"setVoicehub"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {

                if (args.length == 2) {
                    try {
                        String[] trimmed = args[1].trim().split("#");
                        ch_id = trimmed[1].trim().split(">");
                        cat_id = Objects.requireNonNull(event.getGuild().getVoiceChannelById(Long.parseLong(ch_id[0]))).getParentCategoryIdLong();
                        guild_id = event.getGuild().getIdLong();


                    } catch (Exception err) {
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.red);
                        e.setTitle("Something went wrong...", null);
                        e.setDescription("You have not executed this command correctly! " +
                                "\n" +
                                "Do you want to learn how to do it right?");
                        e.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(e.build()).setActionRow(yes_noBT()).queue();
                        return false;
                    }

                    try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                         final PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO voicehub(voicehubid,categoryid,guildid,name,userlimit) VALUES(?,?,?,?,?)")) {
                        insertStatement.setLong(1, Long.parseLong(ch_id[0]));
                        insertStatement.setLong(2,cat_id);
                        insertStatement.setLong(3,guild_id);
                        insertStatement.setString(4,"Talk #{index}");
                        insertStatement.setLong(5,69);
                        insertStatement.execute();

                        log.logger.info("New Voicehub has been set. (Server: " + event.getGuild().getName() + ", Channel: " + Objects.requireNonNull(event.getGuild().getGuildChannelById(Long.parseLong(ch_id[0]))).getName() + ")");
                        EmbedBuilder e = new EmbedBuilder();
                        e.setColor(Color.green);
                        e.setTitle("Voicehub successfully set to '" + Objects.requireNonNull(event.getGuild().getGuildChannelById(Long.parseLong(ch_id[0]))).getName() + "'", null);
                        e.setDescription(" ");
                        e.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(e.build()).queue();

                    } catch (SQLException e) {
                        log.logger.warning(getClass()+": "+e.toString());
                    }
                } else {
                    EmbedBuilder e = new EmbedBuilder();
                    e.setColor(Color.red);
                    e.setTitle("Something went wrong...", null);
                    e.setDescription("You have not executed this command correctly! " +
                            "\n" +
                            "Do you want to learn how to do it right?");
                    e.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(e.build()).setActionRow(yes_noBT()).queue();

                    return false;

                }

                return false;

    }

        private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT () {
            java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
            buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesVH", "Yes"));
            buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noVH", "No"));

            return buttons;
        }


    public static class ButtonClick extends ListenerAdapter {
        ButtonInteractionEvent e;
        String prefix;

        public void onButtonInteraction(ButtonInteractionEvent e) {
            e.deferEdit().queue();
            this.e = e;
            try {
                prefix = Prefix.getPrefix(e);
            } catch (SQLException ex) {
                log.logger.warning(ex.toString());
            }
            switch (Objects.requireNonNull(e.getButton().getId())) {
                case "help_yesVH" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How to set a voicehub", null);
                    eb.setDescription("To set a voicehub you need to execute: \n" +
                            "'" + prefix + "setVoicehub <#channelid>' " +
                            "\n \n" +
                            "Replace _channelID_ with the id of the channel which you want to make to a voicehub");

                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(more_helpBT()).queue();
                }
                case "help_noVH" -> {
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
    }


}

