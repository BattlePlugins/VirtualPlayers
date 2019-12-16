package mc.alk.virtualplayers.api;

import mc.alk.virtualplayers.util.Util;

import org.bukkit.entity.Player;

import java.util.Optional;

public class VirtualPlayerDataHolder {

    private VirtualPlayer virtualPlayer;
    private Player informed;
    private boolean showMessages = true;
    private boolean showTeleports = true;
    private boolean online = true;

    public VirtualPlayerDataHolder(VirtualPlayer player, Player informed) {
        this.virtualPlayer = player;
        this.informed = informed;
    }

    public void setOnline(boolean online) {
        if (showMessages) {
            Util.sendMessage(virtualPlayer, virtualPlayer.getName() + " is "
                    + (online ? "connecting" : "disconnecting"));
        }
        this.online = online;
    }

    public VirtualPlayer getVirtualPlayer() {
        return virtualPlayer;
    }

    public Optional<Player> getInformed() {
        return Optional.ofNullable(informed);
    }

    public void setInformed(Player informed) {
        this.informed = informed;
    }

    public void setShowMessages(boolean showMessages) {
        this.showMessages = showMessages;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isShowMessages() {
        return showMessages;
    }

    public boolean isShowTeleports() {
        return showTeleports;
    }

    public void setShowTeleports(boolean showTeleports) {
        this.showTeleports = showTeleports;
    }
}
