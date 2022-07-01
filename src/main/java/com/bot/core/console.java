package com.bot.core;

import com.bot.listeners.Boot;
import com.bot.log.log;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
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
                MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
                OperatingSystemMXBean os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
                String cpu = String.valueOf((os.getProcessCpuLoad()*100));
                String[] cpuload = cpu.trim().split(String.valueOf(cpu.charAt(4)), 4);
                final long duration = ManagementFactory.getRuntimeMXBean().getUptime();
                final long years = duration / 31104000000L;
                final long months = duration / 2592000000L % 12;
                final long days = duration / 86400000L % 30;
                final long hours = duration / 3600000L % 24;
                final long minutes = duration / 60000L % 60;
                final long seconds = duration / 1000L % 60;
                String uptime = (years == 0 ? "" : + years + "y ") + (months == 0 ? "" :  + months + " " +
                        "m ") + (days == 0 ? "" : "" + days + "d ") + (hours == 0 ? "" : "" + hours +
                        "h ")
                        + (minutes == 0 ? "" : "" + minutes + "m ") + (seconds == 0 ? "" :
                        "" + seconds + "s ");
                if (shutdown.equalsIgnoreCase(config.get("shutdown"))) {
                    jda.getPresence().setActivity(Activity.playing("shutting down..."));
                    jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                    log.logger.info("BOT SHUTDOWN REQUESTED DUE CONSOLE");
                    log.logger.info("LAST PING: "+Boot.Ping+"ms");
                    log.logger.info("MEMORY-USAGE ALL(JAVA): "+((os.getTotalMemorySize()/1000000)-(os.getFreeMemorySize()/1000000))+ "MB ("+(mem.getHeapMemoryUsage().getUsed()/10000)+"MB) / "+(os.getTotalMemorySize()/1000000)+"MB");
                    log.logger.info("CPU-USAGE JAVA: "+cpuload[0]+"%");
                    log.logger.info("UPTIME: "+uptime);
                    jda.shutdownNow();
                    System.exit(0);
                }
                else if(shutdown.equalsIgnoreCase("status") || shutdown.equalsIgnoreCase("info") ){

                    log.logger.info("CURRENT BOT STATUS: "+Boot.Status.toString());
                    log.logger.info("LATEST PING: "+Boot.Ping+"ms  (Updated "+(System.currentTimeMillis()-Boot.lastUpdate)/1000+"s ago)");
                    log.logger.info("MEMORY-USAGE ALL: "+((os.getTotalMemorySize()/1000000)-(os.getFreeMemorySize()/1000000))+ "MB / "+(os.getTotalMemorySize()/1000000)+"MB");
                    log.logger.info("MEMORY-USAGE ALL(JAVA): "+((os.getTotalMemorySize()/1000000)-(os.getFreeMemorySize()/1000000))+ "MB ("+(mem.getHeapMemoryUsage().getUsed()/10000)+"MB) / "+(os.getTotalMemorySize()/1000000)+"MB");
                    log.logger.info("CPU-USAGE JAVA: "+cpuload[0]+"%");
                    log.logger.info("UPTIME: "+uptime);
                }
                else if(shutdown.equalsIgnoreCase("list") ){
                    log.logger.info(bot.jda.getRegisteredListeners().toString());
                    log.logger.info(String.valueOf(bot.jda.getRegisteredListeners().size()));
                }

                else {
                    System.out.println("Invalid Input");
                    System.out.println("'" + config.get("shutdown") + "' to shutdown");
                    System.out.println("'status'/'info' for status");
                }
            }

        }).start();

    }


}
