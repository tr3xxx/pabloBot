package com.bot.abilities.basic.reactionroles;

import com.bot.abilities.core.Command;
import com.bot.core.config;
import com.bot.log.log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class reactionRole extends Command {
    @Override
    public String[] call() {
        return new String[] {"reactionRole", "rr"};
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[] {Permission.MESSAGE_MANAGE};
    }
    @Override
    public boolean usableInDM() {
        return false;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException { // !rr <@roleid> <messageid> <emoji>
        if (args.length == 4) {
            try {
                args[1] = args[1].replaceAll("<@&", "");
                args[1] = args[1].replaceAll(">", "");
                long roleID = Long.parseLong(args[1]);
                event.getGuild().getRoleById(roleID);
                long messageID = Long.parseLong(args[2]);
                Message msg =  event.getChannel().retrieveMessageById(messageID).complete();
                String emoji = args[3];
                event.getJDA().getEmotesByName(emoji, true).forEach(emote -> {
                    msg.addReaction(emote).queue();
                });
                //msg.addReaction(emoji).queue();
                try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
                     final PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO reactionroles(msgid,role,emoji) VALUES(?,?,?)")) {
                    insertStatement.setLong(1,messageID);
                    insertStatement.setLong(2,roleID);
                    insertStatement.setString(3,emoji);
                    insertStatement.execute();}

                log.logger.info("Added reaction role for message " + messageID + " with role " + roleID + " and emoji " + emoji + " on server " + event.getGuild().getName());

                EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.green);
                e.setTitle("Reaction Role has been set successfully", null);
                e.setFooter("presented by " + config.get("bot_name"));
                event.getChannel().sendMessageEmbeds(e.build()).queue();
                return false;
            }
            catch (Exception e){
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
        }
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

    private static java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> yes_noBT() {
        java.util.List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.success("help_yesRR", "Yes"));
        buttons.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("help_noRR", "No"));

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
                case "help_yesRR" -> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.decode(config.get("color")));
                    eb.setTitle("How to create Reaction Roles", null);
                    eb.setDescription("To set up a Reation Role you need to execute: \n" +
                            "'" + prefix + "reactionRole @_role_ _messageid_ _emoji_' "
                    );
                    eb.setFooter("presented by " + config.get("bot_name"));
                    e.getChannel().sendMessageEmbeds(eb.build()).queue();
                }
                case "help_noRR" -> {
                    try{
                        e.getMessage().delete().queue();
                    }catch(NullPointerException ignored){}
                }
                default -> {
                }
            }

        }


        public void getPrefix() throws SQLException{
            String temp = null;

            try (final Connection connection = DriverManager.getConnection(config.get("DATABASE_URL"),config.get("DATABASE_USERNAME"),config.get("DATABASE_PASSWORD"));
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
