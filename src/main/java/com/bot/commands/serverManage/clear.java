package com.bot.commands.serverManage;

import com.bot.commands.core.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

public class clear extends Command {
    int value;
    @Override
    public String[] call() {
        return new String[]{"clear","deleteLast"};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) throws SQLException {
        if(args.length > 2){
            // too long
            return false;
        }
        try{
           value =  Integer.parseInt(args[1]);
        }
        catch(Exception e){
            // error wrong input
            return false;
        }
        for (int i = 0; i <= value ; i++) {
            event.getChannel().delete();
        }
        // x msg deleted
        return false;
    }
}
