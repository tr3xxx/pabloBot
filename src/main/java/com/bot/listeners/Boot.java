package com.bot.listeners;

import com.bot.core.bot;
import com.bot.log.log;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class Boot implements EventListener
{
    public static JDA.Status Status;
    public static long Ping;
    public static long lastUpdate;
    public static boolean bootLogged = false;


    @Override
    public void onEvent(@NotNull GenericEvent event)
    {

        if (event instanceof ReadyEvent) {
            boolean CONN = false;
            String HOSTNAME = null;
            String IP=null;
            ApplicationInfo info = bot.jda.retrieveApplicationInfo().complete();
            List<String> devteam = new ArrayList<>();
            MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
            OperatingSystemMXBean os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            String cpu = String.valueOf((os.getProcessCpuLoad()*100));
            String[] cpuload = cpu.trim().split(String.valueOf(cpu.charAt(4)), 4);

            try {
                InetAddress IA = InetAddress.getLocalHost();
                HOSTNAME = IA.getHostName();
                CONN = IA.isReachable(5000);
                IP = IA.getHostAddress();

                Objects.requireNonNull(info.getTeam()).getMembers().forEach(Member->{
                    devteam.add(Member.getUser().getAsTag());

                });

            } catch (Exception ignored) {}

            log.logger.info("API STATUS: READY");
            log.logger.info("APPLICATION STATUS: "+bot.jda.getStatus());
            log.logger.info("---------------------");
            log.logger.info("SYSTEM: "+HOSTNAME);
            log.logger.info("OS: "+System.getProperty("os.name")+" (Version "+System.getProperty("os.version")+") "+System.getProperty("os.arch"));
            log.logger.info("DIRECTORY: "+System.getProperty("user.dir"));
            log.logger.info("MEMORY-USAGE ALL(JAVA): "+((os.getTotalMemorySize()/1000000)-(os.getFreeMemorySize()/1000000))+ "MB ("+(mem.getHeapMemoryUsage().getUsed()/10000)+"MB) / "+(os.getTotalMemorySize()/1000000)+"MB");
            log.logger.info("CPU-USAGE JAVA: "+cpuload[0]+"%");
            log.logger.info("JAVA: "+System.getProperty("java.runtime.version")+" ("+System.getProperty("java.home")+")");
            log.logger.info("JDK: "+System.getProperty("java.vm.name"));
            log.logger.info("NETWORK: "+CONN);
            log.logger.info("IP: "+IP);
            log.logger.info("---------------------");
            log.logger.info("NAME: "+bot.jda.getSelfUser().getAsTag());
            log.logger.info("INVITE: "+ bot.jda.getInviteUrl(Permission.ADMINISTRATOR));
            log.logger.info("GATEWAY PING: "+String.valueOf(bot.jda.getGatewayPing())+"ms");
            log.logger.info("CACHE-FLAGS: "+bot.jda.getCacheFlags());
            log.logger.info("INTENTS: "+bot.jda.getGatewayIntents());
            log.logger.info("ACC-ID: "+bot.jda.getSelfUser().getIdLong());
            log.logger.info("CREATED: "+bot.jda.getSelfUser().getTimeCreated());
            log.logger.info("OWNER: "+ Objects.requireNonNull(Objects.requireNonNull(info.getTeam()).getOwner()).getUser().getAsTag());
            log.logger.info("DEV-TEAM: "+devteam.toString());
            log.logger.info("PUBLIC: "+info.isBotPublic());
            log.logger.info("VERIFIED: "+bot.jda.getSelfUser().isVerified());
            log.logger.info("---------------------");
            bootLogged = true;


        }
        if(event instanceof GatewayPingEvent){
            if((((GatewayPingEvent) event).getNewPing()+40)<((GatewayPingEvent) event).getOldPing() || (((GatewayPingEvent) event).getNewPing()-40)>((GatewayPingEvent) event).getOldPing()){
                log.logger.info("PING SPIKE DETECTED FROM "+((GatewayPingEvent) event).getOldPing()+"ms TO "+((GatewayPingEvent) event).getNewPing()+" ms");
            }
            Ping = ((GatewayPingEvent) event).getNewPing();
            lastUpdate = System.currentTimeMillis();
        }


        if(event instanceof StatusChangeEvent){

            if(((StatusChangeEvent) event).getNewStatus().equals(JDA.Status.CONNECTING_TO_WEBSOCKET)){log.logger.info("CONNECTING TO WEBSOCKET...");}
            else if(((StatusChangeEvent) event).getNewStatus().equals(JDA.Status.IDENTIFYING_SESSION)){}
            else if(((StatusChangeEvent) event).getNewStatus().equals(JDA.Status.AWAITING_LOGIN_CONFIRMATION)){log.logger.info("LOGGING INTO DISCORD...");}
            else if(((StatusChangeEvent) event).getNewStatus().equals(JDA.Status.LOGGING_IN)){log.logger.info("LOGGED IN DISCORD SUCCESSFULLY");}
            else if(((StatusChangeEvent) event).getNewStatus().equals(JDA.Status.LOADING_SUBSYSTEMS)){log.logger.info("LOADING...");}
            else if(((StatusChangeEvent) event).getNewStatus().equals(JDA.Status.CONNECTED)){
                if(!Boot.bootLogged) {
                    log.logger.warning("BOOT FAILED");
                    bot.jda.shutdownNow();
                    System.exit(0);
                }else{
                    log.logger.info("CONFIRM ONLINE");
                }
            }
            else if(((StatusChangeEvent) event).getNewStatus().equals(JDA.Status.SHUTTING_DOWN)){log.logger.info("SHUTTING DOWN...");}
            else if(((StatusChangeEvent) event).getNewStatus().equals(JDA.Status.SHUTDOWN)){log.logger.info("CONFIRM OFFLINE");}
            else if(((StatusChangeEvent) event).getNewStatus().equals(JDA.Status.DISCONNECTED)){log.logger.info("DISCONNECTED FROM WEBSOCKET");}
            else if(((StatusChangeEvent) event).getNewStatus().equals(JDA.Status.ATTEMPTING_TO_RECONNECT)){log.logger.info("RECONNECTING...");}
            else{
                log.logger.info("STATUS CHANGE ["+ ((StatusChangeEvent) event).getOldStatus()+" --> "+ ((StatusChangeEvent) event).getNewStatus()+"]");
            }
            Status = ((StatusChangeEvent) event).getNewStatus();
        }


    }

}