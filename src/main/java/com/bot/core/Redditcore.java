package com.bot.core;


import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.OAuthData;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

import java.net.http.WebSocket;
import java.rmi.server.UID;
import java.util.UUID;

public class Redditcore {

    public static UserAgent userAgent;
    public static Credentials credentials;
    public static NetworkAdapter adapter;
    public static RedditClient reddit;


    public Redditcore(){

         UUID x = UUID.fromString("5c9e9d30-d22b-11ec-9d64-0242ac120002");

        userAgent = new UserAgent("bot","com.bot.core.Redditcore","0.0","JavaBot187");
        credentials = Credentials.userless(config.get("CLIENTID"),config.get("REDDITCLIENTSECRET"),x);
        adapter = new OkHttpNetworkAdapter(userAgent);
        reddit = OAuthHelper.automatic(adapter,credentials);


    }

}
