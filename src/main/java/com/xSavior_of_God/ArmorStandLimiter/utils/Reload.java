package com.xSavior_of_God.ArmorStandLimiter.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.xSavior_of_God.ArmorStandLimiter.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class Reload {

    final static String packageName = Bukkit.getServer().getClass().getPackage().getName();
    final static String nms = (packageName.equals("org.bukkit.craftbukkit") ? "." : "." + packageName.split("\\.")[3] + ".");
    static boolean nmsVers = true;


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

        } catch (IOException | YAMLException ex) {
            throw new InvalidDescriptionException(ex);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException ignored) {
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Loads and enables a plugin.
     */
    public static void load(String name) {
        Plugin target = null;
        boolean paperLoaded = false;

        File pluginDir = new File("plugins");
        if (!pluginDir.isDirectory()) return;
        File pluginFile = new File(pluginDir, name + ".jar");
        if (!pluginFile.isFile()) for (File f : pluginDir.listFiles())
            if (f.getName().endsWith(".jar"))
                try {
                    PluginDescriptionFile desc = getPluginDescription(f);
                    if (desc.getName().equalsIgnoreCase(name)) {
                        pluginFile = f;
                        break;
                    }
                } catch (Exception e) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            target.onLoad();
            Bukkit.getPluginManager().enablePlugin(target);

        }

        Plugin finalTarget = target;
        Main.scheduler.runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
            loadCommands(finalTarget);
        }, 10L);
    }

    public static List<Map.Entry<String, Command>> getCommandsFromPlugin(Plugin plugin) {
        final Class<?> pluginClassLoader;
        final Field pluginClassLoaderPlugin;

        try {
            pluginClassLoader = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
            pluginClassLoaderPlugin = pluginClassLoader.getDeclaredField("plugin");
            pluginClassLoaderPlugin.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        Map<String, Command> knownCommands = getKnownCommands();
        return knownCommands.entrySet().stream()
                .filter(s -> {
                    if (s.getKey().contains(":")) return s.getKey().split(":")[0].equalsIgnoreCase(plugin.getName());
                    else {
                        ClassLoader cl = s.getValue().getClass().getClassLoader();
                        try {
                            return cl.getClass() == pluginClassLoader && pluginClassLoaderPlugin.get(cl) == plugin;
                        } catch (IllegalAccessException e) {
                            return false;
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    public static void loadCommands(Plugin plugin) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        List<Map.Entry<String, Command>> commands = getCommandsFromPlugin(plugin);


        for (Map.Entry<String, Command> entry : commands) {

            String alias = entry.getKey();
            Command command = entry.getValue();
            wrap(command, alias);
        }

        sync();
    }


    public static void reload() {
        try {
            Class.forName("net.minecraft.server.MinecraftServer");
            nmsVers = false;
        } catch (ClassNotFoundException ignore) {}

        final String pluginName = Main.instance.getName();

        unload(pluginName);
        System.out.println("Unloading '" + pluginName + "' plugin");
        load(pluginName);
    }

    public static void unload(String plName) {
        Map<String, Object> lookupNames = null;
        List<Plugin> pluginList = null;
        boolean reloadlisteners = true;
        Plugin plugin = Bukkit.getPluginManager().getPlugin(plName);
        String name = plugin.getName();
        PluginManager pluginManager = Bukkit.getPluginManager();
        SimpleCommandMap commandMap = null;
        List<Plugin> plugins = null;
        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;

        pluginManager.disablePlugin(plugin);

        try {
            // CHECK IF PAPER IS LOADED
            Class.forName("io.papermc.paper.plugin.manager.PaperPluginManagerImpl");
            String[] version = Bukkit.getBukkitVersion().split("-")[0].split("\\.");

            int paperVersion = Integer.parseInt(version[1]) * 100;
            if (version.length >= 3)
                paperVersion += Integer.parseInt(version[2]);

            if (paperVersion >= 2005) {
                // NEW PAPER PLUGIN MANAGER
                Class<?> paper = Class.forName("io.papermc.paper.plugin.manager.PaperPluginManagerImpl");
                Object paperPluginManagerImpl = paper.getMethod("getInstance").invoke(null);

                Field instanceManagerField = paperPluginManagerImpl.getClass().getDeclaredField("instanceManager");
                instanceManagerField.setAccessible(true);
                Object instanceManager = instanceManagerField.get(paperPluginManagerImpl);

                Field lookupNamesField = instanceManager.getClass().getDeclaredField("lookupNames");
                lookupNamesField.setAccessible(true);
                lookupNames = (Map<String, Object>) lookupNamesField.get(instanceManager);

                Field pluginsField = instanceManager.getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                pluginList = (List<Plugin>) pluginsField.get(instanceManager);

                pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                plugins = (List<Plugin>) pluginsField.get(pluginManager);

                lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
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


            } else {
                // OLD PAPER PLUGIN MANAGER
                Class paper = Class.forName("io.papermc.paper.plugin.manager.PaperPluginManagerImpl");
                Object paperPluginManagerImpl = paper.getMethod("getInstance").invoke(null);

                Field instanceManagerF = paperPluginManagerImpl.getClass().getDeclaredField("instanceManager");
                instanceManagerF.setAccessible(true);
                Object instanceManager = instanceManagerF.get(paperPluginManagerImpl);

                Field lookupNamesF = instanceManager.getClass().getDeclaredField("lookupNames");
                lookupNamesF.setAccessible(true);
                lookupNames = (Map<String, Object>) lookupNamesF.get(instanceManager);

                Method disableMethod = instanceManager.getClass().getMethod("disablePlugin", Plugin.class);
                disableMethod.setAccessible(true);
                disableMethod.invoke(instanceManager, plugin);
                Field pluginListF = instanceManager.getClass().getDeclaredField("plugins");
                pluginListF.setAccessible(true);
                pluginList = (List<Plugin>) pluginListF.get(instanceManager);
            }
        } catch (Exception ignored) {
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
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }


        if (listeners != null && reloadlisteners)
            for (SortedSet<RegisteredListener> set : listeners.values())
                set.removeIf(value -> value.getPlugin() == plugin);


        if (commandMap != null) {
            try {
                // CHECK IF PAPER IS LOADED
                Class.forName("io.papermc.paper.plugin.manager.PaperPluginManagerImpl");

                for (Map.Entry<String, Command> entry : new HashSet<>(commands.entrySet())) {
                    if (entry.getValue() instanceof PluginCommand) {
                        PluginCommand c = (PluginCommand) entry.getValue();
                        if (c.getPlugin().equals(plugin)) {
                            c.unregister(commandMap);
                            commands.remove(entry.getKey());
                        }
                        continue;
                    }

                    try {
                        Field pluginField = Arrays.stream(entry.getValue().getClass().getDeclaredFields())
                                .filter(field -> Plugin.class.isAssignableFrom(field.getType()))
                                .findFirst()
                                .orElse(null);
                        if (pluginField != null) {
                            Plugin owningPlugin;
                            try {
                                pluginField.setAccessible(true);
                                owningPlugin = (Plugin) pluginField.get(entry.getValue());
                                if (owningPlugin.getName().equalsIgnoreCase(plugin.getName())) {
                                    entry.getValue().unregister(commandMap);
                                    commands.remove(entry.getKey());
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IllegalStateException e) {
                        if (e.getMessage().equalsIgnoreCase("zip file closed")) {
                            entry.getValue().unregister(commandMap);
                            commands.remove(entry.getKey());
                        }
                    }
                }
            } catch (Exception ignored) {
                // ONLY SPIGOT
                Map<String, Command> modifiedKnownCommands = new HashMap<>(commands);

                for (Map.Entry<String, Command> entry : new HashMap<>(commands).entrySet()) {
                    if (entry.getValue() instanceof PluginCommand) {
                        PluginCommand c = (PluginCommand) entry.getValue();
                        if (c.getPlugin() == plugin) {
                            c.unregister(commandMap);
                            modifiedKnownCommands.remove(entry.getKey());
                        }
                        continue;
                    }

                    try {
                        Field pluginField = Arrays.stream(entry.getValue().getClass().getDeclaredFields())
                                .filter(field -> Plugin.class.isAssignableFrom(field.getType()))
                                .findFirst()
                                .orElse(null);
                        if (pluginField != null) {
                            Plugin owningPlugin;
                            try {
                                pluginField.setAccessible(true);
                                owningPlugin = (Plugin) pluginField.get(entry.getValue());
                                if (owningPlugin.getName().equalsIgnoreCase(plugin.getName())) {
                                    entry.getValue().unregister(commandMap);
                                    modifiedKnownCommands.remove(entry.getKey());
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        if (e.getMessage().equalsIgnoreCase("zip file closed")) {
                            entry.getValue().unregister(commandMap);
                            modifiedKnownCommands.remove(entry.getKey());
                        }
                    }
                }
                setKnownCommands(modifiedKnownCommands);
            }
        }

        try {
            // CHECK IF PAPER IS LOADED
            Class.forName("io.papermc.paper.plugin.manager.PaperPluginManagerImpl");

            lookupNames.remove(plugin.getName().toLowerCase());
            pluginList.remove(plugin);
        } catch (Exception ignored) {
        }

        if (plugins != null && plugins.contains(plugin)) plugins.remove(plugin);
        if (names != null && names.containsKey(name)) names.remove(name);

        // UNLOAD COMMANDS ---
        Map<String, Command> knownCommands = getKnownCommands();
        List<Map.Entry<String, Command>> _commands = getCommandsFromPlugin(plugin);

        for (Map.Entry<String, Command> entry : _commands) {
            String alias = entry.getKey();
            unwrap(alias);
        }

        for (Map.Entry<String, Command> entry : knownCommands.entrySet().stream().filter(stringCommandEntry -> Plugin.class.isAssignableFrom(stringCommandEntry.getValue().getClass())).filter(stringCommandEntry -> {
            Field pluginField = Arrays.stream(stringCommandEntry.getValue().getClass().getDeclaredFields()).filter(field -> Plugin.class.isAssignableFrom(field.getType())).findFirst().orElse(null);
            if (pluginField != null) {
                Plugin owningPlugin;
                try {
                    owningPlugin = (Plugin) pluginField.get(stringCommandEntry.getValue());
                    return owningPlugin.getName().equalsIgnoreCase(plugin.getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }).collect(Collectors.toList())) {
            String alias = entry.getKey();
            unwrap(alias);
        }
        sync();
        // UNLOAD COMMANDS END ---


        // Close CLass Loader...
        try {
            // CHECK IF PAPER IS LOADED
            Class.forName("io.papermc.paper.plugin.manager.PaperPluginManagerImpl");

            ClassLoader cl = plugin.getClass().getClassLoader();
            if (cl instanceof URLClassLoader) {
                try {
                    Field pluginField = cl.getClass().getDeclaredField("plugin");
                    pluginField.setAccessible(true);
                    pluginField.set(cl, null);
                    Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
                    pluginInitField.setAccessible(true);
                    pluginInitField.set(cl, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    ((URLClassLoader) cl).close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ignored) {
        }


        System.gc();
    }

    public static void setKnownCommands(Map<String, Command> knownCommands) {
        PluginManager pluginManager = Bukkit.getPluginManager();

        Field commandMapField;
        try {
            commandMapField = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer").getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        SimpleCommandMap commandMap;
        try {
            commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Field knownCommandsField;
        try {
            knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            knownCommandsField.set(commandMap, knownCommands);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Command> getKnownCommands() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        Field commandMapField;
        try {
            commandMapField = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer").getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        SimpleCommandMap commandMap;
        try {
            commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Field knownCommandsField;
        try {
            knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Map<String, Command> knownCommands;
        try {
            knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        return knownCommands;
    }

    public static void wrap(Command command, String alias) {
        try {
            Class.forName("com.mojang.brigadier.CommandDispatcher");
        } catch (Exception ignored) {
            // No need to wrap commands if brigadier is not present
            return;
        }

        Method getServerMethod;
        Class<?> minecraftServerClass;
        try {
            minecraftServerClass = Class.forName("net.minecraft.server" + (nmsVers? nms : ".") + "MinecraftServer");
            getServerMethod = minecraftServerClass.getMethod("getServer");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Object minecraftServer;
        try {
            minecraftServer = getServerMethod.invoke("net.minecraft.server" + (nmsVers? nms : ".") + "MinecraftServer");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Field vanillaCommandDispatcherField;
        try {
            vanillaCommandDispatcherField = minecraftServerClass.getDeclaredField("vanillaCommandDispatcher");
            vanillaCommandDispatcherField.setAccessible(true);
        } catch (Exception ignored) {
            return;
        }

        Object commandDispatcher;
        try {
            commandDispatcher = vanillaCommandDispatcherField.get(minecraftServer);
        } catch (Exception ignored) {
            return;
        }
        if (commandDispatcher == null) return;


        Constructor bukkitcommandWrapperConstructor;
        try {
            bukkitcommandWrapperConstructor = Class.forName(packageName + ".command.BukkitCommandWrapper").getDeclaredConstructor(Class.forName(packageName + ".CraftServer"), Command.class);
            bukkitcommandWrapperConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Object commandWrapper;
        try {
            commandWrapper = bukkitcommandWrapperConstructor.newInstance(Bukkit.getServer(), command);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Method aMethod;
        try {
            aMethod = commandDispatcher.getClass().getDeclaredMethod("a");
            aMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Object aInstance;
        try {
            aInstance = aMethod.invoke(commandDispatcher);
        } catch (Exception ignored) {
            return;
        }
        if (aInstance == null) return;

        Method registerMethod;
        try {
            registerMethod = Class.forName(packageName + ".command.BukkitCommandWrapper")
                    .getMethod("register", CommandDispatcher.class, String.class);
            registerMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        try {
            registerMethod.invoke(commandWrapper, aInstance, alias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unwrap(String command) {
        try {
            Class.forName("com.mojang.brigadier.CommandDispatcher");
        } catch (Exception ignored) {
            // No need to unwrap commands if brigadier is not present
            return;
        }

        Method getServerMethod;
        Class<?> minecraftServerClass;
        try {
            minecraftServerClass = Class.forName("net.minecraft.server" + (nmsVers? nms : ".") + "MinecraftServer");
            getServerMethod = minecraftServerClass.getMethod("getServer");
        } catch (Exception ignored) {
            return;
        }
        Object server;
        try {
            server = getServerMethod.invoke(minecraftServerClass);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        Field vanillaCommandDispatcherField;
        try {
            vanillaCommandDispatcherField = minecraftServerClass.getDeclaredField("vanillaCommandDispatcher");
            vanillaCommandDispatcherField.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
            return;
        }
        Object commandDispatcher;
        try {
            commandDispatcher = vanillaCommandDispatcherField.get(server);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        Field bField;
        try {
            bField = Class.forName("net.minecraft.server" + (nmsVers? nms : ".") + "CommandDispatcher").getDeclaredField("b");
            bField.setAccessible(true);
            ;
        } catch (Exception e) {
            try {
                Class<?> clazz = Class.forName("net.minecraft.commands.CommandDispatcher");
                Field gField = clazz.getDeclaredField("g");
                if (gField.getType() == CommandDispatcher.class)
                    bField = gField;
                else
                    bField = clazz.getDeclaredField("h");
                bField.setAccessible(true);
            } catch (Exception ex) {
                ex.addSuppressed(e);
                e.printStackTrace();
                return;
            }
        }

        CommandDispatcher b;
        try {
            b = (CommandDispatcher) bField.get(commandDispatcher);
        } catch (Exception ignored) {
            return;
        }
        if (b == null) return;

        Method removeCommandMethod;
        try {
            try {
                removeCommandMethod = RootCommandNode.class.getDeclaredMethod("removeCommand", String.class);
            } catch (Exception ex) {
                removeCommandMethod = CommandNode.class.getDeclaredMethod("removeCommand", String.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            removeCommandMethod.invoke(b.getRoot(), command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sync() {
        Method syncCommandsMethod;
        try {
            syncCommandsMethod = Class.forName(packageName + ".CraftServer").getDeclaredMethod("syncCommands");
            syncCommandsMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            syncCommandsMethod.invoke(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Bukkit.getOnlinePlayers().isEmpty()) return;

        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

}

