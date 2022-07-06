package com.bot.abilities.news;

import com.bot.log.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class newsRSSReader {

    protected String readRSS(String urlAddress){

            try {
                URL rssUrl = new URL(urlAddress);
                BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
                String sourceCode = "";
                String line;
                int titleDelete = 0;
                int i=0;
                while ((line = in.readLine()) != null && i<4) {
                    if (line.contains("<title>")&& i<3) {
                        int firstPos = line.indexOf("<title>");
                        String temp = line.substring(firstPos);
                        temp = temp.replace("<title>", "");
                        int lastPos = temp.indexOf("</title>");
                        temp = temp.substring(0, lastPos);
                        titleDelete++;
                        i++;
                        if(titleDelete>2) {
                            sourceCode += temp + "\n";
                        }

                    }
                    if (line.contains("<link>")) {
                        int firstPos = line.indexOf("<link>");
                        String temp = line.substring(firstPos);
                        temp = temp.replace("<link>", "");
                        int lastPos = temp.indexOf("</link>");
                        temp = temp.substring(0, lastPos);
                        if(titleDelete>2) {
                            sourceCode += temp + "\n";
                            if(i==3)i++;
                        }

                    }

                }
                in.close();
                return sourceCode;
            }catch (Exception e){
                log.logger.warning(e.toString());
            }


        return null;
    }



}
