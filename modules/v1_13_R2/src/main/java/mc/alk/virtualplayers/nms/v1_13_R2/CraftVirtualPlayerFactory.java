package mc.alk.virtualplayers.nms.v1_13_R2;

import com.mojang.authlib.GameProfile;

import java.util.List;
import java.util.UUID;

import mc.alk.virtualplayers.api.VirtualPlayer;
import mc.alk.virtualplayers.api.VirtualPlayerFactory;
import mc.alk.virtualplayers.util.Util;

import net.minecraft.server.v1_13_R2.DimensionManager;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import net.minecraft.server.v1_13_R2.PlayerInteractManager;
import net.minecraft.server.v1_13_R2.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;

/**
 * Make, track, & delete VirtualPlayers.
 */
public class CraftVirtualPlayerFactory extends VirtualPlayerFactory {

    @Override
    public VirtualPlayer makeVirtualPlayer(String name) throws Exception {
        VirtualPlayer vp = newCraftVirtualPlayer(name);
        vps.put(vp.getUniqueId(), vp);
        names.put(vp.getName(), vp);
        return vp;
    }
    
    private CraftVirtualPlayer newCraftVirtualPlayer(String name) throws Exception {
        CraftServer cserver = (CraftServer) Bukkit.getServer();
        List<World> worlds = cserver.getWorlds();
        if (worlds.isEmpty()) {
            throw new Exception("There must be at least one world");
        }
        CraftWorld w = (CraftWorld) worlds.get(0);
        Location location = new Location(w, 0, 0, 0);
        MinecraftServer mcserver = cserver.getServer();
        DimensionManager dm = DimensionManager.OVERWORLD;
        WorldServer worldServer = mcserver.getWorldServer(dm);
        PlayerInteractManager pim = new PlayerInteractManager(worldServer);
        if (name == null) {
            name = "p" + (vps.size() + 1);
        }
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), Util.colorChat(name));
        return new CraftVirtualPlayer(cserver, mcserver, worldServer, gameProfile, pim, location);
    }

    @Override
    public void deleteVirtualPlayer(String name) {
        deleteVirtualPlayer(names.get(name));
    }

    @Override
    public void deleteVirtualPlayer(VirtualPlayer vp) {
        CraftVirtualPlayer cvp = (CraftVirtualPlayer) vp;
        WorldServer world = ((CraftWorld) cvp.getLocation().getWorld()).getHandle();
        world.removeEntity(cvp.getHandle());
        cvp.remove();
        vps.remove(cvp.getUniqueId());
        names.remove(cvp.getName());
    }

}
