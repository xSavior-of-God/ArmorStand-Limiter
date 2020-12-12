package com.xSavior_of_God.ArmorStandLimiter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandReload implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (sender instanceof ConsoleCommandSender || sender.hasPermission("armostandlimiter.reload")) {
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
