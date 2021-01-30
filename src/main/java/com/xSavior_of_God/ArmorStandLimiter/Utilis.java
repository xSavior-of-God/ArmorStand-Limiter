package com.xSavior_of_God.ArmorStandLimiter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.json.JSONObject;

import com.xSavior_of_God.ArmorStandLimiter.api.events.onArmorStandRemove;

public class Utilis {

  public static void apiRequest(final Map<String, Object> PAR, final URL URL) throws IOException {
    StringBuilder postData = new StringBuilder();

    for (Map.Entry<String, Object> param : PAR.entrySet()) {
      if (postData.length() != 0)
        postData.append('&');

      postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
      postData.append('=');
      postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
    }

    byte[] postDataBytes = postData.toString().getBytes("UTF-8");
    HttpURLConnection conn = (HttpURLConnection) URL.openConnection();

    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
    conn.setDoOutput(true);
    conn.getOutputStream().write(postDataBytes);

    StringBuilder content = new StringBuilder();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
    String line;

    while ((line = bufferedReader.readLine()) != null) {
      content.append(line);
    }
    bufferedReader.close();
  }

  public static void apiRequestDiscord(URL url, JSONObject json) throws IOException {
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.addRequestProperty("Content-Type", "application/json");
    connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-by-xSavior_of_God");
    connection.setDoOutput(true);
    connection.setRequestMethod("POST");
    OutputStream stream = connection.getOutputStream();
    stream.write(json.toString().getBytes());
    stream.flush();
    stream.close();
    connection.getInputStream().close();
    connection.disconnect();
  }

  public static boolean checkArmorStand_for113PLUS(ArmorStand arm) {
    if(Main.ChecksDisableIfIsInvulnerable && arm.isInvulnerable())
      return true;
    return false;
  }
   
  public static boolean checkArmorStand(ArmorStand arm) {
    onArmorStandRemove event = new onArmorStandRemove(arm);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return true;
    }
    if(arm.getName()!= null && Main.ChecksDisableIfNameContains.contains(arm.getName()))
      return true;
    if(Main.ChecksDisableIfNamed && (arm.getName() != null && arm.getName() != ""))
      return true;
    if(Main.ChecksDisableIfHasArms && arm.hasArms())
      return true;
    if(Main.ChecksDisableIfHasNotBasePlate && !arm.hasBasePlate())
      return true;
    if(Main.ChecksDisableIfHasHelmet && arm.getHelmet() != null)
      return true;
    if(!Main.LEGACY && checkArmorStand_for113PLUS(arm))
      return true;
    if(Main.ChecksDisableIfIsSmall && arm.isSmall())
      return true;
    if(Main.ChecksDisableIfIsInvisible && !arm.isVisible())
      return true;
    
    return false;
  }
  
  public static Class<?> getNMSClass(final String NAME) {
    try {
      return Class.forName(
          "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + NAME);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
