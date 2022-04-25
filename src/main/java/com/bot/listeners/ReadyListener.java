package com.bot.listeners;

import com.bot.log.log;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class ReadyListener implements EventListener
{

    @Override
    public void onEvent(GenericEvent event)
    {
        if (event instanceof ReadyEvent) {
            log.logger.info("API is ready");

        }

    }

}