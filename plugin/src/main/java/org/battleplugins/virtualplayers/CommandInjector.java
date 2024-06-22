package org.battleplugins.virtualplayers;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

class CommandInjector {

    public static void inject(VirtualPlayersPlugin plugin, String commandName, String description, Consumer<PluginCommand> commandConsumer) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand pluginCommand = constructor.newInstance(commandName, plugin);
            pluginCommand.setDescription(description);

            commandConsumer.accept(pluginCommand);

            Bukkit.getCommandMap().register(commandName, "virtualplayers", pluginCommand);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to construct PluginCommand " + commandName, e);
        }
    }
}
