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
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while(true) {
                String shutdown = sc.nextLine();
                if (shutdown.equalsIgnoreCase(config.get("shutdown"))) {
                    jda.getPresence().setStatus(OnlineStatus.OFFLINE);
                    SQLite.disconnect();
                    log.logger.info("Bot Shutdown");
                    jda.shutdown();
                    System.exit(0);
                } else {
                    System.out.println("Invalid Input: '" + config.get("shutdown") + "' to shutdown");
                }
            }
        }).start();

    }


}
