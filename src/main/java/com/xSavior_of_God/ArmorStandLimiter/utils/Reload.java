package com.xSavior_of_God.ArmorStandLimiter.utils;

import com.xSavior_of_God.ArmorStandLimiter.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class Reload {


    /**
     * Loads and enables a plugin.
     *
     * @param name plugin's name
     * @return status message
     */
    public static String load(String name) {

        Plugin target = null;

        File pluginDir = new File("plugins");

        if (!pluginDir.isDirectory())
            return "";

        File pluginFile = new File(pluginDir, name + ".jar");

        if (!pluginFile.isFile()) for (File f : pluginDir.listFiles())
            if (f.getName().endsWith(".jar")) try {
                PluginDescriptionFile desc = Bukkit.getPluginManager().getPlugins()[0].getPluginLoader().getPluginDescription(f);
                if (desc.getName().equalsIgnoreCase(name)) {
                    pluginFile = f;
                    break;
                }
            } catch (InvalidDescriptionException e) {
                return "";
            }

        try {
            target = Bukkit.getPluginManager().loadPlugin(pluginFile);
        } catch (InvalidDescriptionException | InvalidPluginException e) {
            e.printStackTrace();
            return "";
        }

        target.onLoad();
        Bukkit.getPluginManager().enablePlugin(target);

        return "";

    }


    public static void reload(String pluginName) {
        unload(pluginName);
        load(pluginName);
    }

    public static String unload(String plName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(plName);

        String name = plugin.getName();

        PluginManager pluginManager = Bukkit.getPluginManager();

        SimpleCommandMap commandMap = null;

        List<Plugin> plugins = null;

        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;

        boolean reloadlisteners = true;

        if (pluginManager != null) {

            pluginManager.disablePlugin(plugin);

            try {

                Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                plugins = (List<Plugin>) pluginsField.get(pluginManager);

                Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
                lookupNamesField.setAccessible(true);
                names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);

                try {
                    Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
                    listenersField.setAccessible(true);
                    listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
                } catch (Exception e) {
                    reloadlisteners = false;
                }

                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                commands = (Map<String, Command>) knownCommandsField.get(commandMap);

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return "";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "";
            }

        }

        pluginManager.disablePlugin(plugin);

        if (plugins != null && plugins.contains(plugin))
            plugins.remove(plugin);

        if (names != null && names.containsKey(name))
            names.remove(name);

        if (listeners != null && reloadlisteners) for (SortedSet<RegisteredListener> set : listeners.values())
            for (Iterator<RegisteredListener> it = set.iterator(); it.hasNext(); ) {
                RegisteredListener value = it.next();
                if (value.getPlugin() == plugin) it.remove();
            }

        if (commandMap != null)
            for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Command> entry = it.next();
                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin() == plugin) {
                        c.unregister(commandMap);
                        it.remove();
                    }
                }
            }

        ClassLoader cl = plugin.getClass().getClassLoader();

        if (cl instanceof URLClassLoader) {

            try {

                Field pluginField = cl.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                pluginField.set(cl, null);

                Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
                pluginInitField.setAccessible(true);
                pluginInitField.set(cl, null);

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                ex.printStackTrace();
            }

            try {
                ((URLClassLoader) cl).close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        System.gc();

        return "";

    }

}