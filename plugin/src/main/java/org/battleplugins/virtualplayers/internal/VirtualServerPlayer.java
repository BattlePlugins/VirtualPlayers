package org.battleplugins.virtualplayers.internal;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.battleplugins.virtualplayers.VirtualPlayerImpl;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class VirtualServerPlayer extends ServerPlayer {
    private VirtualPlayerImpl player;

    public VirtualServerPlayer(MinecraftServer server, ServerLevel world, GameProfile profile) {
        super(server, world, profile, ClientInformation.createDefault());

        this.connection = new VirtualServerGamePacketListenerImpl(server, new VirtualConnection(PacketFlow.SERVERBOUND), this);
        this.joining = false;
    }

    public void setPlayer(VirtualPlayerImpl player) {
        this.player = player;
        ((VirtualConnection) this.connection.connection).setPlayer(player);
    }

    @Override
    public CraftPlayer getBukkitEntity() {
        return this.player;
    }

    @Override
    public CraftEntity getBukkitEntityRaw() {
        return this.player;
    }

    @Override
    public CraftLivingEntity getBukkitLivingEntity() {
        return this.player;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return null; // Should never show up
    }
}
