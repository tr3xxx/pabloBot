package com.bot.abilities.voice.voicehub;

import com.bot.abilities.core.Command;
import com.bot.abilities.core.Prefix;
import com.bot.core.config;

import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;


public class setGeneratedNames extends Command{
    public static long id = 0;
    @Override
    public String[] call() {
        return new String[] {"setVoiceNames","sVN"};
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

        if (args.length == 2) {
                    try {
                        String[] trimmed = args[1].trim().split("#");
                        String[] ch_id = trimmed[1].trim().split(">");
                        id = Long.parseLong(ch_id[0]);
                    } catch (NumberFormatException e) {
                        log.logger.warning(getClass()+": "+e.toString());
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.red);
                        eb.setTitle("Something went wrong...", null);
                        eb.setDescription("You did not run this command correctly :( " +
                                "\n" +
                                "Do you want to learn how to do it correctly?");
                        eb.setFooter("presented by " + config.get("bot_name"));
                        event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(yes_noBT()).queue();
                    }

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("Change Voicehub-Name for "+Objects.requireNonNull(event.getGuild().getVoiceChannelById(id)).getName(), null);
                    eb.setDescription("\n\n");
                    eb.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(selectName()).queue();
                    return false;

                } else {
                    EmbedBuilder e = new EmbedBuilder();
                    e.setColor(Color.red);
                    e.setTitle("Something went wrong...", null);
                    e.setDescription("You don't have enough permissions :( " +
                            "\n" +
                            "In order to be able to edit Voicehubs, you need the permission to manage channels on " +
                            "this " +
                            "Server");
                    e.setFooter("presented by " + config.get("bot_name"));
                    event.getChannel().sendMessageEmbeds(e.build()).queue();
                    }
            return false;
    }
    private static SelectMenu selectName(){
        return SelectMenu.create("names")
                .setPlaceholder("Select a Name")
                .addOption("Talk + {index}", "talki", "Example: Talk #1", Emoji.fromMarkdown("▶️"))
                .addOption("{index} + Talk ", "italk", "Example: #1 Talk ", Emoji.fromMarkdown("▶️"))
                .addOption("Voice + {index}", "voicei", "Example: Voice #1",Emoji.fromMarkdown("▶️"))
                .addOption("{index} + Voice ", "ivoice", "Example: #1 Voice ",Emoji.fromMarkdown("▶️"))
                .addOption("Channel + {index}", "channeli", "Example: Channel #1",Emoji.fromMarkdown("▶️"))
                .addOption("{index} + Channel ", "ichannel", "Example:#1 Channel",Emoji.fromMarkdown("▶️"))
                .setRequiredRange(1, 1)
                .build();
    }
    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesNames", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noNames", "No"));

        return buttons;
    }


public static class MakeSelection extends ListenerAdapter {
    ButtonInteractionEvent event;
    String prefix;

    public void onButtonInteraction(ButtonInteractionEvent e) {
        //e.deferEdit().queue();
        this.event = e;
        try {
            prefix = Prefix.getPrefix(e);
        } catch (SQLException ex) {
            log.logger.warning(getClass()+": "+e.toString());
        }

        switch (Objects.requireNonNull(e.getButton().getId())) {
            case "help_yesNames" -> {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.decode(config.get("color")));
                eb.setTitle("How to change Voicehub Names", null);
                eb.setDescription("To change a Voicehub Name you need to execute: \n" +
                        "'" + prefix + "setVoiceNames <#channelid> " +
                        "\n \n" +
                        "Replace the 'channelid' with the ID of the desired channel");
                eb.setFooter("presented by " + config.get("bot_name"));
                e.getChannel().sendMessageEmbeds(eb.build()).setActionRow(more_helpBT()).queue();
            }
            case "help_noNames" -> {
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


    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {

        try {
            casesMenu(event);
        } catch (SQLException e) {
            log.logger.warning(getClass()+": "+e.toString());
        }

    }

    public void casesMenu(SelectMenuInteraction event) throws SQLException {
        if (Objects.equals(event.getComponent().getId(), "names")) {
            for (int i = 0; i < event.getValues().size(); i++) {
                switch (event.getValues().get(i)) {

                    case "talki":

                        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE voicehub SET name = ? WHERE voicehubid = ?")) {
                            preparedStatement.setString(1, "Talk #{index}");
                            preparedStatement.setLong(2, setGeneratedNames.id);
                            preparedStatement.executeUpdate();
                        }catch (SQLException e) {
                            log.logger.warning(getClass()+": "+e.toString());
                        }

                        break;

                    case "italk":
                        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE voicehub SET name = ? WHERE voicehubid = ?")) {
                            preparedStatement.setString(1, "#{index} Talk");
                            preparedStatement.setLong(2, setGeneratedNames.id);
                            preparedStatement.executeUpdate();
                        }catch (SQLException e) {
                            log.logger.warning(getClass()+": "+e.toString());
                        }
                        break;

                    case "voicei":
                        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE voicehub SET name = ? WHERE voicehubid = ?")) {
                            preparedStatement.setString(1, "Voice #{index}");
                            preparedStatement.setLong(2, setGeneratedNames.id);
                            preparedStatement.executeUpdate();
                        }catch (SQLException e) {
                            log.logger.warning(getClass()+": "+e.toString());
                        }
                        break;

                    case "ivoice":
                        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE voicehub SET name = ? WHERE voicehubid = ?")) {
                            preparedStatement.setString(1, "#{index} Voice");
                            preparedStatement.setLong(2, setGeneratedNames.id);
                            preparedStatement.executeUpdate();
                        }catch (SQLException e) {
                            log.logger.warning(getClass()+": "+e.toString());
                        }
                        break;

                    case "channeli":
                        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE voicehub SET name = ? WHERE voicehubid = ?")) {
                            preparedStatement.setString(1, "Channel #{index}");
                            preparedStatement.setLong(2, setGeneratedNames.id);
                            preparedStatement.executeUpdate();
                        }catch (SQLException e) {
                            log.logger.warning(getClass()+": "+e.toString());
                        }
                        break;

                    case "ichannel":
                        try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE voicehub SET name = ? WHERE voicehubid = ?")) {
                            preparedStatement.setString(1, "#{index} Channel");
                            preparedStatement.setLong(2, setGeneratedNames.id);
                            preparedStatement.executeUpdate();
                        }catch (SQLException e) {
                            log.logger.warning(getClass()+": "+e.toString());
                        }
                        break;
                    default:
                        break;
                }

            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.decode(config.get("color")));
            eb.setTitle("Successfully Changed Voicehub-Name", null);
            eb.setDescription("\n\n");
            eb.setFooter("presented by " + config.get("bot_name"));
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            log.logger.info("A Voicehub-Name has been changed (Server:"+ Objects.requireNonNull(event.getGuild()).getName()+")");


        }
    }
}
}
