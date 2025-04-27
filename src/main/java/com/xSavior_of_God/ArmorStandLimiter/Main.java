package com.xSavior_of_God.ArmorStandLimiter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xSavior_of_God.ArmorStandLimiter.metrics.Metrics;
import com.xSavior_of_God.ArmorStandLimiter.scheduler.Scheduler;
import com.xSavior_of_God.ArmorStandLimiter.utils.CommentedConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.xSavior_of_God.ArmorStandLimiter.externals.HolographicDisplays;
import com.xSavior_of_God.ArmorStandLimiter.externals.ModelEngine;
import com.xSavior_of_God.ArmorStandLimiter.notifications.Notifications;

public class Main extends JavaPlugin {
    public static Main instance;
    public static Map<Entity, Double> trackFallEntity = new HashMap<>();
    public static int
            armorStandLimitBlockTrigger,
            armorStandLimitChunkTrigger,
            armorStandLimitBlockTaskRefresh,
            armorStandLimitChunkTaskRefresh,
            TPSMeterTrigger,
            EventsDisableArmorStandMovingGravityRequired,
            EventsDisableArmorStandMovingGravityFallBlocks,
            EventsDisableArmorStandMovingPistonRequired;
    public static boolean
            armorStandLimitBlockTaskEnabled,
            armorStandLimitChunkTaskEnabled,
            TPSMeterEnabled,
            LimitArmorStandPlaceForChunk,
            DisableDispenserSpawningArmorStand,
            EventsDisableArmorStandMovingGravityEnabled,
            EventsDisableArmorStandMovingPistonEnabled,
            ChecksDisableIfNamed,
            ChecksDisableIfIsInvulnerable,
            ChecksDisableIfIsInvisible,
            ChecksDisableIfHasArms,
            ChecksDisableIfIsSmall,
            ChecksDisableIfHasNotBasePlate,
            ChecksDisableIfHasHelmet,
            ChecksDisableIfHasChestPlate,
            ChecksDisableIfHasLeggings,
            ChecksDisableIfHasBoots,
            ChecksDisableIfHolographicDisplaysEntityPart,
            ChecksDisableIfIsModelEngineEntity,
            LEGACY,
            isFolia;
    public static String noPerms, tooManyArmorStand;
    public static List<String> ChecksDisabledWorlds, ChecksDisableIfNameContains = new ArrayList<String>();
    public File configFile = null;
    public static Scheduler scheduler;

    public void onEnable() {
        new Metrics(this, 17051);
        isFolia = isFolia();

        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "\r\n" + "\r\n" + "&e /\\   _  _   _   _ &e(_  |_  _   _   _|   &f|   .  _  . |_  _  _&r\r\n"
                                + "&e/--\\ |  ||| (_) |  &e__) |_ (_| | ) (_|   &f|__ | ||| | |_ (- | &r\r\n" + "&7v"
                                + getDescription().getVersion() + "&r\r\n" + "&cCreated by xSavior_of_God &r\r\n" + "&r\r\n "));
        if (isFolia) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&fFolia&e support &aEnabled&r"));
            scheduler = new com.xSavior_of_God.ArmorStandLimiter.scheduler.SchedulerFolia(this);
        } else {
            scheduler = new com.xSavior_of_God.ArmorStandLimiter.scheduler.SchedulerBukkit();
        }

        instance = this;
        loadConfig();

        if (ChecksDisableIfHolographicDisplaysEntityPart && !Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            Bukkit.getConsoleSender()
                    .sendMessage(ChatColor.translateAlternateColorCodes('&', "&cHolographicDisplays is not installed or is disabled.&r"));
            ChecksDisableIfHolographicDisplaysEntityPart = false;
        }
        if (ChecksDisableIfIsModelEngineEntity && !Bukkit.getPluginManager().isPluginEnabled("ModelEngine")) {
            Bukkit.getConsoleSender()
                    .sendMessage(ChatColor.translateAlternateColorCodes('&', "&cModelEngine is not installed or is disabled.&r"));
            ChecksDisableIfIsModelEngineEntity = false;
        }

        if (ChecksDisableIfHolographicDisplaysEntityPart)
            Bukkit.getServer().getPluginManager().registerEvents((Listener) new HolographicDisplays(), (Plugin) this);
        if (ChecksDisableIfIsModelEngineEntity)
            Bukkit.getServer().getPluginManager().registerEvents((Listener) new ModelEngine(), (Plugin) this);

        new Notifications();
        if(isFolia) {
            new CheckerFolia();
        } else {
            new Checker();
        }
        getCommand("asl").setExecutor(new Commands());
        Bukkit.getServer().getPluginManager().registerEvents((Listener) new Events(), (Plugin) this);
        try {
            Class.forName("io.papermc.paper.event.entity.EntityMoveEvent");
            Bukkit.getServer().getPluginManager().registerEvents((Listener) new EventsPaper(), (Plugin) this);
        } catch (ClassNotFoundException ignore) {
        }
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', "&eArmorStand &fLimiter &aLoaded!"));
        LEGACY = Bukkit.getVersion().contains("1.8");
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServerInitEvent");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
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
        armorStandLimitBlockTaskRefresh = Math.max(getConfig().getInt("ArmorStandLimit.Block.Task.Refresh"), 10);
        armorStandLimitChunkTaskRefresh = Math.max(getConfig().getInt("ArmorStandLimit.Chunk.Task.Refresh"), 10);
        armorStandLimitBlockTaskEnabled = getConfig().getBoolean("ArmorStandLimit.Block.Task.Enabled");
        armorStandLimitChunkTaskEnabled = getConfig().getBoolean("ArmorStandLimit.Chunk.Task.Enabled");
        TPSMeterTrigger = getConfig().getInt("TPSMeter.Trigger");
        TPSMeterEnabled = getConfig().getBoolean("TPSMeter.Enabled");
        LimitArmorStandPlaceForChunk = getConfig().getBoolean("Events.LimitArmorStandPlaceForChunk");
        DisableDispenserSpawningArmorStand = getConfig().getBoolean("Events.DisableDispenserSpawningArmorStand");
        EventsDisableArmorStandMovingGravityEnabled = getConfig().getBoolean("Events.DisableArmorStandMoving.Gravity.Enabled");
        EventsDisableArmorStandMovingGravityRequired = getConfig().getInt("Events.DisableArmorStandMoving.Gravity.RequiredArmorStand");
        EventsDisableArmorStandMovingGravityFallBlocks = getConfig().getInt("Events.DisableArmorStandMoving.Gravity.RequiredFallBlocksBeforeBlocked");
        EventsDisableArmorStandMovingPistonEnabled = getConfig().getBoolean("Events.DisableArmorStandMoving.Piston.Enabled");
        EventsDisableArmorStandMovingPistonRequired = getConfig().getInt("Events.DisableArmorStandMoving.Piston.RequiredArmorStand");
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
        ChecksDisableIfHasChestPlate = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfHasChestPlate");
        ChecksDisableIfHasLeggings = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfHasLeggings");
        ChecksDisableIfHasBoots = getConfig().getBoolean("ArmorStandLimit.Checks.DisableIfHasBoots");
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
