package com.xSavior_of_God.ArmorStandLimiter.notifications;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;

import com.xSavior_of_God.ArmorStandLimiter.Utils;

public class Telegram {
    private static String message;
    private static String chatID;
    private static URL url;


    public Telegram(final String API, final String TOKEN, final String MESSAGE, final String CHAT_ID) {
        Telegram.message = MESSAGE;
        Telegram.chatID = CHAT_ID;
        try {
            url = new URL(API + TOKEN + "/sendMessage");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void messageBuilder(final Location LOC, final int COUNTER, final int LIMIT, Boolean... TYPES) {
        final long Time_now = new java.sql.Timestamp(System.currentTimeMillis()).getTime();
        DateFormat date_format = new SimpleDateFormat("yyyy");
        final String Year = date_format.format(new Date(Time_now));
        final double X = LOC.getX();
        final double Z = LOC.getZ();
        boolean TYPE = false;
        if (TYPES.length > 0) TYPE = TYPES[0];
        final String WORLD = LOC.getWorld().getName();
        final String MESS = message.replaceAll("%empty%", "\u200b").replace("{x}", X + "").replace("{z}", Z + "").replace("{world}", WORLD).replace("{counter}", COUNTER + "").replace("{max}", LIMIT + "").replace("{type}", TYPE ? "chunk" : "xyz");
        sendMessage(MESS + "\n \n<code>Created by xSavior_of_God @ 2020/" + Year + " HeroxPlugins</code>");
    }

    public static void sendMessage(String text) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("chat_id", chatID);
        parameters.put("text", text);
        parameters.put("parse_mode", "HTML");
        try {
            Utils.apiRequest(parameters, url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
