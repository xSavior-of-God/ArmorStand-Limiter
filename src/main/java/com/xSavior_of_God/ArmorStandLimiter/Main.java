package com.xSavior_of_God.ArmorStandLimiter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.xSavior_of_God.ArmorStandLimiter.externals.HolographicDisplays;
import com.xSavior_of_God.ArmorStandLimiter.externals.ModelEngine;
import com.xSavior_of_God.ArmorStandLimiter.notifications.Notifications;

public class Main extends JavaPlugin {
    public static Main instance;
    public static int armorStandLimitBlockTrigger, armorStandLimitChunkTrigger, armorStandLimitBlockTaskRefersh,
            armorStandLimitChunkTaskRefersh, TPSMeterTrigger;
    public static boolean armorStandLimitBlockTaskEnabled, armorStandLimitChunkTaskEnabled, TPSMeterEnabled,
            LimitArmorStandPlaceForChunk, DisableDispenserSpawningArmorstand, EventsDisableArmorStandMovingWater,
            EventsDisableArmorStandMovingPiston, ChecksDisableIfNamed, ChecksDisableIfIsInvulnerable,
            ChecksDisableIfIsInvisible, ChecksDisableIfHasArms, ChecksDisableIfIsSmall, ChecksDisableIfHasNotBasePlate,
            ChecksDisableIfHasHelmet, LEGACY, ChecksDisableIfHolographicDisplaysEntityPart, ChecksDisableIfIsModelEngineEntity;
    public static Map<Location, Integer> counterBlock = new HashMap<Location, Integer>();
    public static Map<Chunk, Integer> counterChunk = new HashMap<Chunk, Integer>();
    public static String noPerms, tooManyArmorStand;
    public static List<String> ChecksDisabledWorlds, ChecksDisableIfNameContains = new ArrayList<String>();
    public File configFile = null;

    public void onEnable() {
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "\r\n" + "\r\n" + "&e /\\   _  _   _   _ &e(_  |_  _   _   _|   &f|   .  _  . |_  _  _\r\n"
                                + "&e/--\\ |  ||| (_) |  &e__) |_ (_| | ) (_|   &f|__ | ||| | |_ (- | \r\n" + "&7v"
                                + getDescription().getVersion() + "\r\n" + "&cCreated by xSavior_of_God \r\n" + "\r\n "));

        instance = this;
        loadConfig();

        if (ChecksDisableIfHolographicDisplaysEntityPart && !Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            Bukkit.getConsoleSender()
                    .sendMessage(ChatColor.translateAlternateColorCodes('&', "&cHolographicDisplays is not installed or is disabled."));
            ChecksDisableIfHolographicDisplaysEntityPart = false;
        }
        if (ChecksDisableIfIsModelEngineEntity && !Bukkit.getPluginManager().isPluginEnabled("ModelEngine")) {
            Bukkit.getConsoleSender()
                    .sendMessage(ChatColor.translateAlternateColorCodes('&', "&cModelEngine is not installed or is disabled."));
            ChecksDisableIfIsModelEngineEntity = false;
        }
        if (ChecksDisableIfHolographicDisplaysEntityPart)
            Bukkit.getServer().getPluginManager().registerEvents((Listener) new HolographicDisplays(), (Plugin) this);
        if (ChecksDisableIfIsModelEngineEntity)
            Bukkit.getServer().getPluginManager().registerEvents((Listener) new ModelEngine(), (Plugin) this);

        new Notifications();
        Checker check = new Checker();
        check.timerTask();
        getCommand("asl").setExecutor(new Commands());
        Bukkit.getServer().getPluginManager().registerEvents((Listener) new Events(), (Plugin) this);
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&eArmorStand &fLimiter &aLoaded!"));
        LEGACY = (Bukkit.getVersion().contains("1.8")) ? true : false;
    }

    public void loadConfig() {
        if (configFile == null)
            configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }

        CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(configFile);
        try {
            cfg.syncWithConfig(configFile, getResource("config.yml"));
        } catch (Exception e) {
            //e.printStackTrace();
        }

        armorStandLimitBlockTrigger = getConfig().getInt("ArmorStandLimit.Block.Trigger");
        armorStandLimitChunkTrigger = getConfig().getInt("ArmorStandLimit.Chunk.Trigger");
        armorStandLimitBlockTaskRefersh = getConfig().getInt("ArmorStandLimit.Block.Task.Refresh");
        armorStandLimitChunkTaskRefersh = getConfig().getInt("ArmorStandLimit.Chunk.Task.Refresh");
        armorStandLimitBlockTaskEnabled = getConfig().getBoolean("ArmorStandLimit.Block.Task.Enabled");
        armorStandLimitChunkTaskEnabled = getConfig().getBoolean("ArmorStandLimit.Chunk.Task.Enabled");
        TPSMeterTrigger = getConfig().getInt("TPSMeter.Trigger");
        TPSMeterEnabled = getConfig().getBoolean("TPSMeter.Enabled");
        LimitArmorStandPlaceForChunk = getConfig().getBoolean("Events.LimitArmorStandPlaceForChunk");
        DisableDispenserSpawningArmorstand = getConfig().getBoolean("Events.DisableDispenserSpawningArmorstand");
        EventsDisableArmorStandMovingWater = getConfig().getBoolean("Events.DisableArmorStandMoving.Water");
        EventsDisableArmorStandMovingPiston = getConfig().getBoolean("Events.DisableArmorStandMoving.Piston");
        noPerms = getConfig().getString("noPerms");
        tooManyArmorStand = getConfig().getString("tooManyArmorStand");
        ChecksDisabledWorlds = getConfig().getStringList("ArmorStandLimit.Checks.DisabledWorlds");
        ChecksDisableIfNameContains = getConfig().getStringList("ArmorStandLimit.Checks.DisableIfNameContains");
        ChecksDisableIfNamed = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfNamed");
        ChecksDisableIfIsInvulnerable = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfIsInvulnerable");
        ChecksDisableIfIsInvisible = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfIsInvisible");
        ChecksDisableIfHasArms = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfHasArms");
        ChecksDisableIfHasNotBasePlate = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfHasNotBasePlate");
        ChecksDisableIfHasHelmet = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfHasHelmet");
        ChecksDisableIfIsSmall = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfIsSmall");
        ChecksDisableIfHolographicDisplaysEntityPart = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfHolographicDisplaysEntityPart");
        ChecksDisableIfIsModelEngineEntity = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfIsModelEngineEntity");
    }

    public void onDisable() {
        Main.instance = null;
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&cArmorStand Limiter &cUnLoaded   Bye! Bye!"));
    }

}
