package mc.alk.virtualplayers.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import mc.euro.bukkitinterface.BukkitInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Nikolai
 */
public interface VirtualPlayersAPI {
    
    /**
     * Returns all online players, including api.VirtualPlayers.
     * <pre>
     * VirtualPlayers.getOnlinePlayers() :
     * Using this method has an added bonus: It is backwards compatible.
     * v1.7.9 - Bukkit.getOnlinePlayers - returns Player[]
     * v1.7.10 - Bukkit.getOnlinePlayers - returns {@code Collection<Player> }
     * It doesn't matter what the return type for Bukkit.getOnlinePlayers() is,
     * this method will work.
     * </pre>
     *
     * @return an Array of bukkit.entity.Player + api.VirtualPlayer.
     */
    public default Collection<? extends Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        VirtualPlayerFactory.getVirtualPlayers().stream().filter((p) -> (p.isOnline())).forEachOrdered((p) -> {
            players.add(p);
        });
        players.addAll(BukkitInterface.getOnlinePlayers());
        return players;
    };

    public Player makeVirtualPlayer(String name) throws Exception;
    public void setEventMessages(boolean visibility);
    public void deleteVirtualPlayer(VirtualPlayer vp);
    public void deleteVirtualPlayers();
    
    public default void setGlobalMessages(boolean visibility) {
        VirtualPlayerFactory.getVirtualPlayers().forEach((vp) -> {
            vp.setShowMessages(visibility);
        });
    }

     public default Player[] getOnlinePlayersArray() {
        return getOnlinePlayers().toArray(new Player[0]);
    };
     
    public default Collection<? extends VirtualPlayer> getVirtualPlayers() {
        return VirtualPlayerFactory.getVirtualPlayers();
    };
    
    public default List<VirtualPlayer> getVirtualPlayersList() {
        return VirtualPlayerFactory.getNewPlayerList();
    }
    
    public default void setPlayerMessages(boolean visibility) {
        getVirtualPlayers().forEach((vp) -> {
            vp.setShowMessages(visibility);
        });
    }

    public default Map<UUID, VirtualPlayer> getVps() {
        return VirtualPlayerFactory.getVps();
    }

    public default Map<String, VirtualPlayer> getNames() {
        return VirtualPlayerFactory.getNames();
    }

    public default Player getPlayer(String pname) {
        Player vp = Bukkit.getPlayer(pname);
        if (vp == null) {
            vp = getNames().get(pname);
        }
        return vp;
    }

    public default Player getPlayer(UUID id) {
        Player vp = Bukkit.getPlayer(id);
        if (vp == null) {
            vp = getVps().get(id);
        }
        return vp;
    }

    public default Player getPlayerExact(String pname) {
        Player vp = Bukkit.getPlayerExact(pname);
        if (vp == null) {
            vp = getNames().get(pname);
        }
        return vp;
    }

    public default Player getOrMakePlayer(String pname) {
        Player vp = Bukkit.getPlayer(pname);
        if (vp == null) {
            vp = getNames().get(pname);
        }
        if (vp == null) {
            try {
                return makeVirtualPlayer(pname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vp;
    }

    public default VirtualPlayer getOrCreate(String name) {
        Player vp = VirtualPlayerFactory.getNames().get(name);
        if (vp == null) {
            try {
                vp = makeVirtualPlayer(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (VirtualPlayer) vp;
    }

    public default Player makeVirtualPlayer() throws Exception {
        return makeVirtualPlayer(null);
    }

    public default void deleteVirtualPlayer(String name) {
        VirtualPlayer vp = getNames().get(name);
        deleteVirtualPlayer(vp);
    }

}
