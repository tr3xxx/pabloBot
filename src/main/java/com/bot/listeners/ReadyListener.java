package com.bot.listeners;

import com.bot.core.bot;
import com.bot.events.updateStats;
import com.bot.log.log;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;

public class ReadyListener implements EventListener
{

    @Override
    public void onEvent(GenericEvent event)
    {
        if (event instanceof ReadyEvent) {
            InetAddress IP= null;

            log.logger.info("API is ready");
            log.logger.info("---------------------");
            log.logger.info("OS: "+System.getProperty("os.name")+"-"+System.getProperty("os.version"));
            log.logger.info("JAVA: "+System.getProperty("java.runtime.version")+" ("+System.getProperty("java.home")+")");
            try {
                IP = InetAddress.getLocalHost();
                log.logger.info("IP: "+IP.getHostAddress());
            } catch (Exception ignored) {}
            log.logger.info("---------------------");
            log.logger.info("BOT NAME: "+bot.jda.getSelfUser().getAsTag());
            log.logger.info("BOT PING: "+String.valueOf(bot.jda.getGatewayPing())+"ms");
            log.logger.info("---------------------");
            log.logger.info("BOT ONLINE");

        }

    }

}