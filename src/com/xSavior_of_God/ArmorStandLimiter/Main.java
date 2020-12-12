package com.xSavior_of_God.ArmorStandLimiter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.xSavior_of_God.ArmorStandLimiter.notifications.Notifications;

public class Main extends JavaPlugin {
  public static Main instance;
  public static int armorStandLimitBlockTrigger, armorStandLimitChunkTrigger, armorStandLimitBlockTaskRefersh,
      armorStandLimitChunkTaskRefersh, TPSMeterTrigger;
  public static boolean armorStandLimitBlockTaskEnabled, armorStandLimitChunkTaskEnabled, TPSMeterEnabled;
  public static Map<Location, Integer> counterBlock = new HashMap<Location, Integer>();
  public static Map<Chunk, Integer> counterChunk = new HashMap<Chunk, Integer>();
  public static String noPerms;

  public void onEnable() {
    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
        "\n\r \n\r                           &cArmorStand Limiter\n\r                       &fCreated by xSavior_of_God \n\r \n\r "));
    final File configFile = new File(this.getDataFolder(), "config.yml");
    if (!configFile.exists()) {
      saveResource("config.yml", false);
    }
    CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(configFile);
    try {
      cfg.syncWithConfig(configFile, this.getResource("config.yml"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    instance = this;
    loadConfig();
    new Notifications();
    Checker check = new Checker();
    check.timerTask();
    getCommand("asl").setExecutor(new CommandReload());
    Bukkit.getConsoleSender()
        .sendMessage(ChatColor.translateAlternateColorCodes('&', "&cArmorStand Limiter &aLoaded!"));
  }

  private void loadConfig() {
    armorStandLimitBlockTrigger = getConfig().getInt("ArmorStandLimit.Block.Trigger");
    armorStandLimitChunkTrigger = getConfig().getInt("ArmorStandLimit.Chunk.Trigger");
    armorStandLimitBlockTaskRefersh = getConfig().getInt("ArmorStandLimit.Block.Task.Refresh");
    armorStandLimitChunkTaskRefersh = getConfig().getInt("ArmorStandLimit.Chunk.Task.Refresh");
    armorStandLimitBlockTaskEnabled = getConfig().getBoolean("ArmorStandLimit.Block.Task.Enabled");
    armorStandLimitChunkTaskEnabled = getConfig().getBoolean("ArmorStandLimit.Chunk.Task.Enabled");
    TPSMeterTrigger = getConfig().getInt("TPSMeter.Trigger");
    TPSMeterEnabled = getConfig().getBoolean("TPSMeter.Enabled");
    noPerms = getConfig().getString("noPerms");
  }

  public void onDisable() {
    Bukkit.getConsoleSender()
        .sendMessage(ChatColor.translateAlternateColorCodes('&', "&cArmorStand Limiter &cUnLoaded   Bye! Bye!"));
  }

}
