package com.bot.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class log {
    public static Logger logger;
    FileHandler fh;

    public log(String file_name) throws SecurityException, IOException{
        File f = new File(file_name);
        File f_lck = new File(file_name+".lck");
        if(!f.exists()){
           f.createNewFile();
        }

        fh = new FileHandler(file_name, true);
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        logger = Logger.getLogger("Logger");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

        if(f_lck.exists()){
            f_lck.delete();
        }
        else{
            logger.warning(".LCK FILE COULD NOT BE FOUND ("+f_lck.getName()+")");
        }
    }
}
