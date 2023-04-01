package com.xSavior_of_God.ArmorStandLimiter.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Reload {
    public static PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        if (file == null)
            throw new InvalidDescriptionException("File cannot be null");

        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("plugin.yml");

            if (entry == null) {
                throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain plugin.yml"));
            }

            stream = jar.getInputStream(entry);

            return new PluginDescriptionFile(stream);

        } catch (IOException ex) {
            throw new InvalidDescriptionException(ex);
        } catch (YAMLException ex) {
            throw new InvalidDescriptionException(ex);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Loads and enables a plugin.
     *
     * @param name plugin's name
     */
    public static void load(String name) {
        Plugin target = null;
        boolean paperLoaded = false;

        File pluginDir = new File("plugins");
        if (!pluginDir.isDirectory()) return;
        File pluginFile = new File(pluginDir, name + ".jar");
        if (!pluginFile.isFile()) for (File f : pluginDir.listFiles())
            if (f.getName().endsWith(".jar")) try {
                PluginDescriptionFile desc = getPluginDescription(f);
                if (desc.getName().equalsIgnoreCase(name)) {
                    pluginFile = f;
                    break;
                }
            } catch (InvalidDescriptionException e) {
                return;
            }
        try {
            Class paper = Class.forName("io.papermc.paper.plugin.manager.PaperPluginManagerImpl");
            Object paperPluginManagerImpl = paper.getMethod("getInstance").invoke(null);

            Field instanceManagerF = paperPluginManagerImpl.getClass().getDeclaredField("instanceManager");
            instanceManagerF.setAccessible(true);
            Object instanceManager = instanceManagerF.get(paperPluginManagerImpl);

            Method loadMethod = instanceManager.getClass().getMethod("loadPlugin", Path.class);
            loadMethod.setAccessible(true);
            target = (Plugin) loadMethod.invoke(instanceManager, pluginFile.toPath());

            Method enableMethod = instanceManager.getClass().getMethod("enablePlugin", Plugin.class);
            enableMethod.setAccessible(true);
            enableMethod.invoke(instanceManager, target);

            paperLoaded = true;
        } catch (Exception ignore) {
        } // Paper most likely not loaded

        if (!paperLoaded) {
            try {
                target = Bukkit.getPluginManager().loadPlugin(pluginFile);
            } catch (InvalidDescriptionException e) {
                e.printStackTrace();
            } catch (InvalidPluginException e) {
                e.printStackTrace();
            }

            target.onLoad();
            Bukkit.getPluginManager().enablePlugin(target);
        }
    }

    public static void reload(String pluginName) {
        unload(pluginName);
        load(pluginName);
    }

    public static void unload(String plName) {
        boolean reloadlisteners = true;
        Plugin plugin = Bukkit.getPluginManager().getPlugin(plName);
        String name = plugin.getName();
        PluginManager pluginManager = Bukkit.getPluginManager();
        SimpleCommandMap commandMap = null;
        List<Plugin> plugins = null;
        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;

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
                return;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
        }
        pluginManager.disablePlugin(plugin);
        if (plugins != null && plugins.contains(plugin)) plugins.remove(plugin);
        if (names != null && names.containsKey(name)) names.remove(name);
        if (listeners != null && reloadlisteners) for (SortedSet<RegisteredListener> set : listeners.values())
            set.removeIf(value -> value.getPlugin() == plugin);
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

        try {

            Class paper = Class.forName("io.papermc.paper.plugin.manager.PaperPluginManagerImpl");
            Object paperPluginManagerImpl = paper.getMethod("getInstance").invoke(null);

            Field instanceManagerF = paperPluginManagerImpl.getClass().getDeclaredField("instanceManager");
            instanceManagerF.setAccessible(true);
            Object instanceManager = instanceManagerF.get(paperPluginManagerImpl);

            Field lookupNamesF = instanceManager.getClass().getDeclaredField("lookupNames");
            lookupNamesF.setAccessible(true);
            Map<String, Object> lookupNames = (Map<String, Object>) lookupNamesF.get(instanceManager);

            Method disableMethod = instanceManager.getClass().getMethod("disablePlugin", Plugin.class);
            disableMethod.setAccessible(true);
            disableMethod.invoke(instanceManager, plugin);

            lookupNames.remove(plugin.getName().toLowerCase());

            Field pluginListF = instanceManager.getClass().getDeclaredField("plugins");
            pluginListF.setAccessible(true);
            List<Plugin> pluginList = (List<Plugin>) pluginListF.get(instanceManager);
            pluginList.remove(plugin);

        } catch (Exception ignore) {
        } // Paper most likely not loaded

        System.gc();
    }
}
