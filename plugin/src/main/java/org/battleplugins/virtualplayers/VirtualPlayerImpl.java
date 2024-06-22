package org.battleplugins.virtualplayers;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import org.battleplugins.virtualplayers.api.Observer;
import org.battleplugins.virtualplayers.api.VirtualPlayer;
import org.battleplugins.virtualplayers.internal.VirtualServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VirtualPlayerImpl extends CraftPlayer implements VirtualPlayer {
    private final List<Observer> observers = new ArrayList<>();

    public VirtualPlayerImpl(CraftServer server, ServerPlayer entity) {
        super(server, entity);
    }

    @Override
    public List<Observer> getObservers() {
        return List.copyOf(this.observers);
    }

    @Override
    public void addObserver(CommandSender observer) {
        this.addObserver(new Observer(observer));
    }

    @Override
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    public static VirtualPlayer createVirtualPlayer(String name) {
        CraftServer server = (CraftServer) Bukkit.getServer();
        VirtualServerPlayer entity = new VirtualServerPlayer(server.getServer(), server.getServer().overworld(), new GameProfile(UUID.randomUUID(), name));
        VirtualPlayerImpl player = new VirtualPlayerImpl(server, entity);
        entity.setPlayer(player);
        return player;
    }
}
