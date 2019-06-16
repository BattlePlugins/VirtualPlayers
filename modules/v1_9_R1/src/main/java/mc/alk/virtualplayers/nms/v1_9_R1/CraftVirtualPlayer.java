package mc.alk.virtualplayers.nms.v1_9_R1;

import com.mojang.authlib.GameProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mc.alk.virtualplayers.api.VirtualPlayer;
import mc.alk.virtualplayers.util.Util;

import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.MinecraftServer;
import net.minecraft.server.v1_9_R1.PlayerInteractManager;
import net.minecraft.server.v1_9_R1.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MainHand;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.NumberConversions;

public class CraftVirtualPlayer extends CraftPlayer implements VirtualPlayer {

    Player keepInformed; // / who to send the messages to
    boolean online = true;
    double health = 20;
    boolean isop = true;
    boolean showMessages = true;
    boolean showTeleports = true;
    GameMode gamemode = GameMode.SURVIVAL;
    Location location;
    CraftScoreboard scoreboard;
    
    public CraftVirtualPlayer(CraftServer cserver, MinecraftServer mcserver, WorldServer worldServer,
            GameProfile gameProfile, PlayerInteractManager pim, Location loc) {
        super(cserver, new EntityPlayer(mcserver, worldServer, gameProfile, pim));
        this.location = loc;
    }
    
    public CraftVirtualPlayer(CraftServer cserver, MinecraftServer mcserver, WorldServer worldServer,
            GameProfile gameProfile, PlayerInteractManager pim) {
        super(cserver, new EntityPlayer(mcserver, worldServer, gameProfile, pim));
        this.location = this.getLocation();
    }
    
    public CraftVirtualPlayer(CraftServer cserver, EntityPlayer ep) {
        super(cserver, ep);
        this.location = this.getLocation();
    }
    
    @Override
    public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z) {
        List<net.minecraft.server.v1_9_R1.Entity> notchEntityList = this.entity.world.getEntities(this.entity, this.entity.getBoundingBox().grow(x, y, z));
        List<org.bukkit.entity.Entity> bukkitEntityList = new ArrayList(notchEntityList.size());
        for (net.minecraft.server.v1_9_R1.Entity e : notchEntityList) {
            bukkitEntityList.add(e.getBukkitEntity());
        }
        return bukkitEntityList;
    }

    @Override
    public MainHand getMainHand() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public InventoryView openInventory(Inventory inv) {
        return null;
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect) {
        return false;
    }
    
    @Override
    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        return false;
    }
    
    @Override
    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        return false;
    }

    @Override
    public void removePotionEffect(PotionEffectType effect) {
        /// do nothing
    }

    @Override
    public boolean isGliding() {
        return false;
    }

    @Override
    public void setGliding(boolean b) {
        // do nothing
    }

    @Override
    public void setAI(boolean b) {
        // do nothing
    }

    @Override
    public boolean hasAI() {
        return false;
    }

    @Override
    public void setCollidable(boolean b) {
        // do nothing
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public void closeInventory() {
        /// do nothing
    }

    @Override
    public void updateInventory() {
        /// Do nothing
    }

    @Override
    public void setGameMode(GameMode gamemode) {
        try {
            super.setGameMode(gamemode);
        } catch (Exception e) {
            /* say nothing*/
        }
        this.gamemode = gamemode;
    }

    @Override
    public GameMode getGameMode() {
        return gamemode;
    }

    @Override
    public double getHealth() {
        return health;
    }

    @Override
    public void setHealth(double h) {
        if (h < 0) {
            h = 0;
        }
        this.health = h;
        try {
            super.setHealth(h);
        } catch (Exception e) {
        }
        try {
            this.getHandle().setHealth((float) h);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isDead() {
        return super.isDead() || health <= 0;
    }

    @Override
    public void setInvulnerable(boolean b) {
        // do nothing
    }

    @Override
    public boolean isInvulnerable() {
        return false;
    }

    @Override
    public void sendMessage(String s) {
        if (showMessages) {
            Util.sendMessage(this, (!isOnline() ? "&4(Offline)&b" : "")
                    + getName() + " gettingMessage= " + s);
        }
    }

    @Override
    public void moveTo(Location loc) {
        entity.move(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public boolean teleport(Location l, boolean respawn) {
        if (isDead()) {
            return false;
        }
        try {
            boolean changedWorlds = !this.location.getWorld().getName()
                    .equals(l.getWorld().getName());
            final String teleporting = respawn ? "respawning" : "teleporting";
            if (showTeleports && showMessages) {
                String fromWorld = "";
                String toWorld = "";
                if (changedWorlds) {
                    fromWorld = "&5" + location.getWorld().getName() + "&4,";
                    toWorld = "&5" + l.getWorld().getName() + "&4,";
                }
                Util.sendMessage(this, getName() + "&e " + teleporting + " from &4"
                        + fromWorld + Util.getLocString(location) + " &e-> &4" + toWorld
                        + Util.getLocString(l));
            }
            this.location = l.clone();
            if (changedWorlds) {
                PlayerChangedWorldEvent pcwe = new PlayerChangedWorldEvent(this,
                        l.getWorld());
                CraftServer cserver = (CraftServer) Bukkit.getServer();
                cserver.getPluginManager().callEvent(pcwe);
                /// For some reason, world doesnt get changed, so lets explicitly set it
                this.entity.world = ((CraftWorld) location.getWorld()).getHandle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        if (isDead()) {
            return false;
        }
        super.teleport(location, cause);
        teleport(location, false);
        return true;
    }

    @Override
    public boolean teleport(Location l) {
        return teleport(l, PlayerTeleportEvent.TeleportCause.UNKNOWN);
    }

    @Override
    public void respawn(Location loc) {
        this.health = 20;
        boolean changedWorlds = !this.location.getWorld().getName()
                .equals(loc.getWorld().getName());
        CraftServer cserver = (CraftServer) Bukkit.getServer();
        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this, loc,
                false);
        cserver.getPluginManager().callEvent(respawnEvent);
        if (changedWorlds) {
            PlayerChangedWorldEvent pcwe = new PlayerChangedWorldEvent(this,
                    loc.getWorld());
            cserver.getPluginManager().callEvent(pcwe);
        }
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public void setOnline(boolean b) {
        if (showMessages) {
            Util.sendMessage(this, getName() + " is "
                    + (b ? "connecting" : "disconnecting"));
        }
        online = b;
    }

    @Override
    public boolean isOp() {
        return isop;
    }

    @Override
    public void setOp(boolean b) {
        isop = b;
    }

    @Override
    public String toString() {
        String world = "&5" + this.location.getWorld().getName() + ",";
        return getName() + "&e h=&2" + getHealth() + "&e o=&5" + isOnline()
                + "&e d=&7" + isDead() + "&e loc=&4" + world + "&4"
                + Util.getLocString(location) + "&e gm=&8" + getGameMode();
    }
    
    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        Object s = null;
        this.scoreboard = (CraftScoreboard) scoreboard;
        if (scoreboard != null) {
            if (Bukkit.getScoreboardManager().getMainScoreboard() != null
                    && scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                s = "BukkitMainScoreboard";
            } else if (scoreboard.getObjective(DisplaySlot.SIDEBAR) != null) {
                s = scoreboard.getObjective(DisplaySlot.SIDEBAR).getName();
            } else if (scoreboard.getObjective(DisplaySlot.PLAYER_LIST) != null) {
                s = scoreboard.getObjective(DisplaySlot.PLAYER_LIST).getName();
            }
        }
        if (showMessages) {
            Util.sendMessage(this, getName() + " setting scoreboard " + s);
        }
    }

    @Override
    public CraftScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void setLocation(Location l) {
        location = l;
    }

    @Override
    public Player getInformed() {
        return keepInformed;
    }

    @Override
    public void setShowMessages(boolean visibility) {
        showMessages = visibility;
    }

    @Override
    public int _INVALID_getLastDamage() {
        return NumberConversions.ceil(getLastDamage());
    }

    @Override
    public void _INVALID_setLastDamage(int damage) {
        setLastDamage(damage);
    }

    @Override
    public void _INVALID_damage(int amount) {
        damage(amount);
    }

    @Override
    public void _INVALID_damage(int amount, Entity source) {
        damage(amount, source);
    }

    @Override
    public int _INVALID_getHealth() {
        return NumberConversions.ceil(getHealth());
    }

    @Override
    public void _INVALID_setHealth(int hp) {
        setHealth(hp);
    }

    @Override
    public int _INVALID_getMaxHealth() {
        return NumberConversions.ceil(getMaxHealth());
    }

    @Override
    public void _INVALID_setMaxHealth(int max) {
        setMaxHealth(max);
    }

}
