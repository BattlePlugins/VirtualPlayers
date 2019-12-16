package mc.alk.virtualplayers.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * 
 * @author Nikolai
 */
public interface VirtualPlayer extends Player {

    VirtualPlayerDataHolder getDataHolder();

    void setOnline(boolean online);
    
    void respawn(Location loc);
    boolean teleport(Location location, boolean respawn);

    Player getInformed();
    void setShowMessages(boolean visibility);

}
