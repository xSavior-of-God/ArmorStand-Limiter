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

public class Commands implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (sender instanceof Player && sender.hasPermission("armorstandlimiter.check")
        && args.length > 0 && args[0].equalsIgnoreCase("check")) {
      boolean isChunk = false;
      String message = "&cThere are &f{c}&c Armor Stands &7(x{x}, z{z} - {type})";
      int c = 0;

      if (args.length > 1 && args[1] != null && args[1].equalsIgnoreCase("chunk")) {
        isChunk = true;
      }

      final Chunk chunk = ((Player) sender).getLocation().getChunk();
      if (isChunk) {
        for (Entity ent : chunk.getEntities()) {
          if (ent instanceof ArmorStand)
            c++;
        }
      } else {
        Location loc = ((Player) sender).getLocation();
        int X = (int) loc.getX();
        int Z = (int) loc.getZ();

        for (Entity ent : chunk.getEntities()) {
          if (ent instanceof ArmorStand && (X == (int) ent.getLocation().getX() && Z == (int) ent.getLocation().getZ()))
            c++;
        }

      }

      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', message.replace("{c}", c + "").replace("{x}", chunk.getX() + "")
              .replace("{z}", chunk.getZ() + "").replace("{type}", isChunk ? "chunk" : "xyz")));
      return true;
    } else if (sender instanceof ConsoleCommandSender || sender.hasPermission("armostandlimiter.reload")) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Reloading..."));
      Bukkit.getServer().getPluginManager()
          .disablePlugin(Bukkit.getServer().getPluginManager().getPlugin(Main.instance.getName()));
      Main.instance.reloadConfig();
      Bukkit.getServer().getPluginManager()
          .enablePlugin(Bukkit.getServer().getPluginManager().getPlugin(Main.instance.getName()));
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aReloaded!"));
      return true;
    } else {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.noPerms));
      return true;
    }
  }

}
