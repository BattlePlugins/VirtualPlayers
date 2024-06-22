package org.battleplugins.virtualplayers.internal;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.battleplugins.virtualplayers.Util;
import org.battleplugins.virtualplayers.api.Observer;
import org.battleplugins.virtualplayers.api.VirtualPlayer;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

public class VirtualConnection extends Connection {
    private VirtualPlayer player;

    public VirtualConnection(PacketFlow side) {
        super(side);
    }

    public void setPlayer(VirtualPlayer player) {
        this.player = player;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener callbacks, boolean flush) {
        this.processPacket(packet);
    }

    public void processPacket(Packet<?> packet) {
        if (packet instanceof ClientboundPlayerChatPacket playerChatPacket) {
            for (Observer observer : this.player.getObservers()) {
                if (observer.isVerbose()) {
                    CommandSender sender = observer.getSender();

                    Component adventure = PaperAdventure.asAdventure(playerChatPacket.unsignedContent());
                    sender.sendMessage(Util.playerPrefix(this.player, "Chat").append(adventure));
                }
            }
        } else if (packet instanceof ClientboundSystemChatPacket(net.minecraft.network.chat.Component content, boolean overlay)) {
            for (Observer observer : this.player.getObservers()) {
                if (observer.isVerbose()) {
                    CommandSender sender = observer.getSender();

                    Component adventure = PaperAdventure.asAdventure(content);
                    sender.sendMessage(Util.playerPrefix(this.player, "System").append(adventure));
                }
            }
        }
    }

    @Override
    public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> state, T packetListener) {
    }
}
