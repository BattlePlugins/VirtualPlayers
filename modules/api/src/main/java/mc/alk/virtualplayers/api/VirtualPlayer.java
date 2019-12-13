package mc.alk.virtualplayers.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * 
 * @author Nikolai
 */
public interface VirtualPlayer extends Player {
    
    void setOnline(boolean b);
    
    void respawn(Location loc);
    void moveTo(Location loc);
    boolean teleport(Location location, boolean respawn);
    
    Player getInformed();
    void setShowMessages(boolean visibility);

}
