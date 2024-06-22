package org.battleplugins.virtualplayers.internal;

import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import javax.annotation.Nullable;

public class VirtualServerGamePacketListenerImpl extends ServerGamePacketListenerImpl {

    public VirtualServerGamePacketListenerImpl(MinecraftServer server, VirtualConnection connection, ServerPlayer player) {
        super(server, connection, player, CommonListenerCookie.createInitial(player.gameProfile, false));
    }

    @Override
    public void send(Packet<?> packet) {
        ((VirtualConnection) this.connection).processPacket(packet);
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener listener) {
        ((VirtualConnection) this.connection).processPacket(packet);
    }
}
