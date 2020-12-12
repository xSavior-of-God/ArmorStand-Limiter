package com.xSavior_of_God.ArmorStandLimiter.notifications;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.xSavior_of_God.ArmorStandLimiter.JSONObject;
import com.xSavior_of_God.ArmorStandLimiter.Utilis;

public class Discord {
  private static int color;
  private static String title, description, message;
  private static URL webhook;

  public Discord(final String WEBHOOK, final String TITLE, final String DESCRIPTION, final String MESSAGE,
      final String COLOR) {
    try {
      webhook = new URL(WEBHOOK);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    title = TITLE;
    description = DESCRIPTION;
    message = MESSAGE;
    color = Color.decode(COLOR).getRed();
    color = (color << 8) + Color.decode(COLOR).getGreen();
    color = (color << 8) + Color.decode(COLOR).getBlue();
  }

  public static void messageBuilder(final Location LOC, final int COUNTER, final int LIMIT) {
    final double X = LOC.getX();
    final double Z = LOC.getZ();
    final String WORLD = LOC.getWorld().getName();
    final String MESS = message.replace("{x}", X + "").replace("{z}", Z + "").replace("{world}", WORLD)
        .replace("{counter}", COUNTER + "").replace("{max}", LIMIT + "");
    sendMessage(MESS);
  }

  public static void sendMessage(String text) {
    final long Time_now = new java.sql.Timestamp(System.currentTimeMillis()).getTime();
    DateFormat date_format = new SimpleDateFormat("yyyy");
    final String Year = date_format.format(new Date(Time_now));
    List<JSONObject> jsonArray = new ArrayList<>();
    JSONObject json = new JSONObject();
    JSONObject JsonX;
    json.put("title", title);
    json.put("description", description);
    Bukkit.getConsoleSender().sendMessage("DIOPORCO " + color);
    json.put("color", color);
    JsonX = new JSONObject();
    JsonX.put("name", text);
    JsonX.put("value", "󠀀󠀀 󠀀󠀀");
    JsonX.put("inline", true);
    jsonArray.add(JsonX);
    json.put("fields", jsonArray.toArray());
    JsonX = new JSONObject();
    JsonX.put("url",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTeER8zwJX-WLGXeIuw94rBioFCUx254ypvcQ&usqp=CAU");
    json.put("thumbnail", JsonX);
    JsonX = new JSONObject();
    JsonX.put("text", "Created by xSavior_of_God   © 2020/" + Year + " HeroxPlugins");
    JsonX.put("icon_url",
        "https://cdn.discordapp.com/icons/577978634569252895/eae04392234904524a7124d07777371f.png?size=128");
    json.put("footer", JsonX);

    jsonArray = new ArrayList<>();
    jsonArray.add(json);
    json = new JSONObject();
    json.put("embeds", jsonArray);

    try {
      Utilis.apiRequestDiscord(webhook, json);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
