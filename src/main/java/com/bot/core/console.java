package com.bot.core;

import com.bot.log.log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;

import java.util.Scanner;

public class console {
    private static JDA jda = null;

    public console(JDA jda){
        console.jda = jda;
        shutdown();
    }

    public void shutdown(){
        Scanner sc = new Scanner(System.in);

        //System.out.println("1");
        String shutdown = sc.nextLine();
        //System.out.println("2");
        if(shutdown.equalsIgnoreCase(config.get("shutdown"))){
            jda.getPresence().setStatus(OnlineStatus.OFFLINE);
            SQLite.disconnect();
            log.logger.info("Bot Shutdown");
            jda.shutdown();
            System.exit(0);
        }

    }


}
