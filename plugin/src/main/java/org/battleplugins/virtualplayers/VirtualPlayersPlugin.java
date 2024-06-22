package org.battleplugins.virtualplayers;

import org.battleplugins.virtualplayers.api.VirtualPlayer;
import org.battleplugins.virtualplayers.api.VirtualPlayers;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualPlayersPlugin extends JavaPlugin implements VirtualPlayers {
    private final Map<String, VirtualPlayer> virtualPlayers = new HashMap<>();

    private VirtualPlayersExecutor executor;

    @Override
    public void onEnable() {
        CommandInjector.inject(this, "virtualplayers", "The main VirtualPlayers command.", command -> {
            command.setAliases(List.of("vp"));
            command.setExecutor(this.executor = new VirtualPlayersExecutor(this));
            command.setPermission("virtualplayers.command.help");
        });
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<VirtualPlayer> getVirtualPlayers() {
        return List.copyOf(this.virtualPlayers.values());
    }

    @Override
    public VirtualPlayer createVirtualPlayer(String name) throws IllegalArgumentException {
        if (this.virtualPlayers.containsKey(name)) {
            throw new IllegalArgumentException("Virtual player with name " + name + " already exists!");
        }

        if (Bukkit.getPlayer(name) != null) {
            throw new IllegalArgumentException("Cannot create virtual player " + name + " as they are a real player on the server!");
        }

        VirtualPlayer virtualPlayer = VirtualPlayerImpl.createVirtualPlayer(name);
        this.virtualPlayers.put(name, virtualPlayer);
        return virtualPlayer;
    }

    @Override
    public void removeVirtualPlayer(VirtualPlayer virtualPlayer) {
        this.virtualPlayers.remove(virtualPlayer.getName());

        // Dereference from command executor
        this.executor.getSelectedPlayers().entrySet().removeIf(entry -> entry.getValue().equals(virtualPlayer));
    }

    @Override
    public void removeAllVirtualPlayers() {
        this.virtualPlayers.clear();
    }

    @Nullable
    @Override
    public VirtualPlayer getVirtualPlayer(String name) {
        return this.virtualPlayers.get(name);
    }
}
