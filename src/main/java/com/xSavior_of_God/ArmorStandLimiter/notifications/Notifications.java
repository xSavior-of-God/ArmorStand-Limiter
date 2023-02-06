package com.xSavior_of_God.ArmorStandLimiter.notifications;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;

import com.xSavior_of_God.ArmorStandLimiter.Main;

public class Notifications {
    private static boolean notificationsEnabled, telegramEnabled, discordEnabled, consoleEnabled, onlinePlayerEnabled;
    private static String telegramAPI, telegramToken, telegramMessage, telegramChatID, discordColor, discordWebhook,
            discordTitle, discordDescription, discordMessage, consoleMessage, onlinePlayerMessage, onlinePlayerPermission;

    public Notifications() {
        notificationsEnabled = Main.instance.getConfig().getBoolean("Notifications.Enabled");
        telegramEnabled = Main.instance.getConfig().getBoolean("Notifications.Telegram.Enabled");
        telegramAPI = Main.instance.getConfig().getString("Notifications.Telegram.API");
        telegramToken = Main.instance.getConfig().getString("Notifications.Telegram.Token");
        telegramMessage = Main.instance.getConfig().getString("Notifications.Telegram.Message");
        telegramChatID = Main.instance.getConfig().getString("Notifications.Telegram.ChatID");
        if (telegramEnabled)
            new Telegram(telegramAPI, telegramToken, telegramMessage, telegramChatID);
        discordColor = Main.instance.getConfig().getString("Notifications.Discord.Color");
        discordEnabled = Main.instance.getConfig().getBoolean("Notifications.Discord.Enabled");
        discordWebhook = Main.instance.getConfig().getString("Notifications.Discord.Webhook");
        discordTitle = Main.instance.getConfig().getString("Notifications.Discord.Title");
        discordDescription = Main.instance.getConfig().getString("Notifications.Discord.Description");
        discordMessage = Main.instance.getConfig().getString("Notifications.Discord.Message");
        if (discordEnabled)
            new Discord(discordWebhook, discordTitle, discordDescription, discordMessage, discordColor);
        consoleEnabled = Main.instance.getConfig().getBoolean("Notifications.Console.Enabled");
        consoleMessage = Main.instance.getConfig().getString("Notifications.Console.Message");
        onlinePlayerEnabled = Main.instance.getConfig().getBoolean("Notifications.OnlinePlayer.Enabled");
        onlinePlayerPermission = Main.instance.getConfig().getString("Notifications.OnlinePlayer.Permission");
        onlinePlayerMessage = Main.instance.getConfig().getString("Notifications.OnlinePlayer.Message");
    }

    public static void send(final Chunk CHUNK, final int COUNTER) {
        if (notificationsEnabled) {

            if (consoleEnabled)
                Bukkit.getConsoleSender()
                        .sendMessage(ChatColor.translateAlternateColorCodes('&',
                                consoleMessage.replace("{x}", CHUNK.getX() + "").replace("{z}", CHUNK.getZ() + "")
                                        .replace("{world}", CHUNK.getWorld().getName()).replace("{counter}", COUNTER + "")
                                        .replace("{max}", Main.armorStandLimitChunkTrigger + "").replace("{type}", "chunk")));

            if (onlinePlayerEnabled) {
                Bukkit.getScheduler().runTask(Main.instance, new Runnable() {

                    @Override
                    public void run() {
                        Main.instance.getServer().getOnlinePlayers().forEach(p -> {
                            if (p.isOp() || p.hasPermission(onlinePlayerPermission))
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        onlinePlayerMessage.replace("{x}", CHUNK.getX() + "").replace("{z}", CHUNK.getZ() + "")
                                                .replace("{world}", CHUNK.getWorld().getName()).replace("{counter}", COUNTER + "")
                                                .replace("{max}", Main.armorStandLimitChunkTrigger + "").replace("{type}", "chunk")));
                        });
                    }

                });
            }

            if (telegramEnabled) {
                Location Loc = new Location(CHUNK.getWorld(), CHUNK.getX(), 0, CHUNK.getZ());
                Telegram.messageBuilder(Loc, COUNTER, Main.armorStandLimitChunkTrigger, true);
            }

            if (discordEnabled) {
                Location Loc = new Location(CHUNK.getWorld(), CHUNK.getX(), 0, CHUNK.getZ());
                Discord.messageBuilder(Loc, COUNTER, Main.armorStandLimitChunkTrigger, true);
            }
        }
    }

    public static void send(final Location LOC, final int COUNTER) {
        if (notificationsEnabled) {

            if (consoleEnabled)
                Bukkit.getConsoleSender()
                        .sendMessage(ChatColor.translateAlternateColorCodes('&',
                                consoleMessage.replace("{x}", LOC.getX() + "").replace("{z}", LOC.getZ() + "")
                                        .replace("{world}", LOC.getWorld().getName()).replace("{counter}", COUNTER + "")
                                        .replace("{max}", Main.armorStandLimitBlockTrigger + "").replace("{type}", "xyz")));

            if (onlinePlayerEnabled) {
                Bukkit.getScheduler().runTask(Main.instance, new Runnable() {

                    @Override
                    public void run() {
                        Main.instance.getServer().getOnlinePlayers().forEach(p -> {
                            if (p.isOp() || p.hasPermission(onlinePlayerPermission))
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        onlinePlayerMessage.replace("{x}", LOC.getX() + "").replace("{z}", LOC.getZ() + "")
                                                .replace("{world}", LOC.getWorld().getName()).replace("{counter}", COUNTER + "")
                                                .replace("{max}", Main.armorStandLimitBlockTrigger + "").replace("{type}", "xyz")));
                        });
                    }

                });
            }

            if (telegramEnabled) {
                Telegram.messageBuilder(LOC, COUNTER, Main.armorStandLimitBlockTrigger);
            }

            if (discordEnabled) {
                Discord.messageBuilder(LOC, COUNTER, Main.armorStandLimitBlockTrigger);
            }
        }
    }

}
