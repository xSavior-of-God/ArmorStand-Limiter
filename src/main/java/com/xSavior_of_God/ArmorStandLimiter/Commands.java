package com.xSavior_of_God.ArmorStandLimiter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.xSavior_of_God.ArmorStandLimiter.notifications.Notifications;

public class Commands implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if((sender instanceof ConsoleCommandSender || sender.isOp() || sender.hasPermission("armorstandlimiter.help")) && (args.length < 1 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) ) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eArmorStand&fLimiter &6Created by xSavior_of_God"));
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCorrect Usage: &f/asl reload&a,&f /asl check [chunk/xyz]"));
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
      return true;
    }

    if ((sender instanceof ConsoleCommandSender || sender.isOp()
        || sender.hasPermission("armorstandlimiter.test")) && args.length > 0 && args[0].equalsIgnoreCase("test") ) {
      Location loc = new Location(Bukkit.getWorlds().get(0), 1, 2, 3);
      Notifications.send(loc, 9999);
      Notifications.send(loc.getChunk(), 9999);
      return true;
    }
    if (sender instanceof Player && sender.hasPermission("armorstandlimiter.check") && args.length > 0
        && args[0].equalsIgnoreCase("check")) {
      boolean isChunk = false;
      String message = "&cThere are &f{c}&c Armor Stands of which only &f&n{i}&c are detected &7(x{x}, z{z} - {type})";
      int c = 0;
      int i = 0;

      if (args.length > 1 && args[1] != null && args[1].equalsIgnoreCase("chunk")) {
        isChunk = true;
      }

      final Chunk chunk = ((Player) sender).getLocation().getChunk();
      if (isChunk) {
        for (Entity ent : chunk.getEntities()) {
          if (ent instanceof ArmorStand) {
            c++;
            if (Utilis.checkArmorStand((ArmorStand) ent)) i++;
          }

        }
      } else {
        Location loc = ((Player) sender).getLocation();
        int X = (int) loc.getX();
        int Z = (int) loc.getZ();

        for (Entity ent : chunk.getEntities()) {
          if (ent instanceof ArmorStand && (X == (int) ent.getLocation().getX() && Z == (int) ent.getLocation().getZ())) {
            c++;
            if (Utilis.checkArmorStand((ArmorStand) ent)) i++;
          }
        }

      }

      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', message.replace("{c}", c + "").replace("{i}", (c - i) + "").replace("{x}", chunk.getX() + "")
              .replace("{z}", chunk.getZ() + "").replace("{type}", isChunk ? "chunk" : "xyz")));
      return true;
    } else if (sender instanceof ConsoleCommandSender || sender.hasPermission("armostandlimiter.reload") && args[0].equalsIgnoreCase("reload")) {

      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Config Reloading..."));

      Main.instance.reloadConfig();
      Main.instance.loadConfig();

      String pluginName = Main.instance.getName().toString();
      Bukkit.getServer().getPluginManager().disablePlugin(Bukkit.getServer().getPluginManager().getPlugin(pluginName));

      Bukkit.getServer().getPluginManager().enablePlugin(Bukkit.getServer().getPluginManager().getPlugin(pluginName));
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aReloaded!"));
      return true;
    } else {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.noPerms));
      return true;
    }
  }

}
