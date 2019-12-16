package mc.alk.virtualplayers.nms.v1_5_R2;

import mc.alk.virtualplayers.api.VirtualPlayer;
import mc.alk.virtualplayers.api.VirtualPlayerDataHolder;
import mc.alk.virtualplayers.util.Util;

import net.minecraft.server.v1_5_R2.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class CraftVirtualPlayer extends CraftPlayer implements VirtualPlayer {

    private VirtualPlayerDataHolder dataHolder;

    public CraftVirtualPlayer(CraftServer cserver, MinecraftServer mcserver, WorldServer worldServer, String name, PlayerInteractManager pim, Location loc) {
        super(cserver, new EntityPlayer(mcserver, worldServer, name, pim));

        this.dataHolder = new VirtualPlayerDataHolder(this, null);
        this.setGameMode(GameMode.SURVIVAL);
        this.teleport(loc);
    }

    public CraftVirtualPlayer(CraftServer cserver, MinecraftServer mcserver, WorldServer worldServer, String name, PlayerInteractManager pim) {
        super(cserver, new EntityPlayer(mcserver, worldServer, name, pim));

        this.dataHolder = new VirtualPlayerDataHolder(this, null);
    }

    public CraftVirtualPlayer(CraftServer cserver, EntityPlayer ep) {
        super(cserver, ep);

        this.dataHolder = new VirtualPlayerDataHolder(this, null);
    }

    @Override
    public void sendMessage(String s) {
        if (dataHolder.isShowMessages()) {
            Util.sendMessage(this, (!isOnline() ? "&4(Offline)&b" : "")
                    + getName() + " gettingMessage= " + s);
        }
    }

    @Override
    public boolean teleport(Location location, boolean respawn) {
        if (isDead()) {
            return false;
        }
        try {
            boolean changedWorlds = !this.getLocation().getWorld().getName()
                    .equals(location.getWorld().getName());
            final String teleporting = respawn ? "respawning" : "teleporting";
            if (dataHolder.isShowTeleports() && dataHolder.isShowMessages()) {
                String fromWorld = "";
                String toWorld = "";
                if (changedWorlds) {
                    fromWorld = "&5" + location.getWorld().getName() + "&4,";
                    toWorld = "&5" + location.getWorld().getName() + "&4,";
                }
                Util.sendMessage(this, getName() + "&e " + teleporting + " from &4"
                        + fromWorld + Util.getLocString(location) + " &e-> &4" + toWorld
                        + Util.getLocString(location));
            }
            this.teleport(location.clone());
            if (changedWorlds) {
                PlayerChangedWorldEvent pcwe = new PlayerChangedWorldEvent(this,
                        location.getWorld());
                Bukkit.getServer().getPluginManager().callEvent(pcwe);
                /// For some reason, world doesnt get changed, so lets explicitly set it
                this.entity.world = ((CraftWorld) location.getWorld()).getHandle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void respawn(Location loc) {
        this.setHealth(20);
        boolean changedWorlds = !this.getLocation().getWorld().getName()
                .equals(loc.getWorld().getName());
        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this, loc,
                false);
        Bukkit.getServer().getPluginManager().callEvent(respawnEvent);
        if (changedWorlds) {
            PlayerChangedWorldEvent pcwe = new PlayerChangedWorldEvent(this,
                    loc.getWorld());
            Bukkit.getServer().getPluginManager().callEvent(pcwe);
        }
    }

    @Override
    public VirtualPlayerDataHolder getDataHolder() {
        return dataHolder;
    }

    @Override
    public boolean isOnline() {
        return dataHolder.isOnline();
    }

    public void setOnline(boolean online) {
        if (dataHolder.isShowMessages()) {
            Util.sendMessage(this, getName() + " is "
                    + (online ? "connecting" : "disconnecting"));
        }
        dataHolder.setOnline(online);
    }

    @Override
    public String toString() {
        return "&4" + this.getName() + "&eHealth: &c" + this.getHealth() +
                "&e, Online: &a" + this.isOnline()  +
                "&e, Alive: &a" + !this.isDead() +
                "&e, Operator: &6" + this.isOp() +
                "&e, Position: &6[" + this.getLocation().getWorld().getName() + Util.getLocString(getLocation()) +
                "]&e, Game Mode: &6" + this.getGameMode().name().toLowerCase();
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        Object s = null;
        super.setScoreboard(scoreboard);
        if (scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            s = "BukkitMainScoreboard";
        } else if (scoreboard.getObjective(DisplaySlot.SIDEBAR) != null) {
            s = scoreboard.getObjective(DisplaySlot.SIDEBAR).getName();
        } else if (scoreboard.getObjective(DisplaySlot.PLAYER_LIST) != null) {
            s = scoreboard.getObjective(DisplaySlot.PLAYER_LIST).getName();
        }
        if (dataHolder.isShowMessages()) {
            Util.sendMessage(this, getName() + " setting scoreboard " + s);
        }
    }

    @Override
    public Player getInformed() {
        return dataHolder.getInformed().orElse(null);
    }

    @Override
    public void setShowMessages(boolean visibility) {
        dataHolder.setShowMessages(visibility);
    }
}
