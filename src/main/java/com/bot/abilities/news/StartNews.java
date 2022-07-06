package com.bot.abilities.news;

import com.bot.log.log;

public class StartNews {

    public static void startNews(){
        newsCommand newsCommand = new newsCommand();
        Thread run = new Thread(newsCommand);
        run.start();
        log.logger.info("NEWS STATUS: RUNNING");
    }


}
