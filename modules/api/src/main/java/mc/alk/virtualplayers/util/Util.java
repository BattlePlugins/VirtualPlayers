package mc.alk.virtualplayers.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mc.alk.virtualplayers.api.VirtualPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * 
 * @author Alkarin
 */
public class Util {

    public interface VPMessageListener {

        void gettingMessage(Player player, String msg);
    }

    static final List<VPMessageListener> listeners = new ArrayList<>();

    static public String getLocString(Location l) {
        if (l == null) {
            return "null";
        }
        DecimalFormat format = new DecimalFormat("0.##");
        StringBuilder sb = new StringBuilder();
        sb.append(format.format(l.getX())).append(",");
        sb.append(format.format(l.getY())).append(",");
        sb.append(format.format(l.getZ()));
        if (l.getYaw() != 0f) {
            sb.append(",").append(format.format(l.getYaw()));
        }
        if (l.getPitch() != 0f) {
            sb.append(",").append(format.format(l.getPitch()));
        }
        return sb.toString();
    }

    public static void sendMessage(VirtualPlayer p, String msg) {
        final String finalMsg = colorChat("&5[" + p.getDisplayName() + "] &b'" + msg + "'");
        Bukkit.getConsoleSender().sendMessage(finalMsg);
        if (!listeners.isEmpty()) {
            for (VPMessageListener l : listeners) {
                l.gettingMessage(p, msg);
            }
        }
        if (p.getInformed() != null) {
            p.getInformed().sendMessage(finalMsg);
        }
    }

    public static String colorChat(String msg) {
        return msg.replace('&', (char) 167);
    }

    public static boolean isInt(double f) {
        return Math.floor(f) == f;
    }

    public static boolean addListener(VPMessageListener listener) {
        return listeners.add(listener);
    }

    public static boolean removeListener(VPMessageListener listener) {
        return listeners.remove(listener);
    }
    
    /**
     * Uses the location, chunk, and distance calculations.
     * 
     * @param vp the virtual player
     * @param radius the radius
     * @return the nearby entities
     */
    public static Collection<Entity> getNearbyEntities(VirtualPlayer vp, int radius) {
        Map<Integer, Entity> emap = new HashMap<>();
        int numChunks = (int) Math.floor(radius / 16) + 1;
        int blocks = numChunks * 16;
        int diameter = blocks * 2;
        Location start = vp.getLocation().clone().subtract(blocks, 0, blocks);
        // Location end = start.clone().add(diameter, 0, diameter);
        
        for (int dx = 0 ; dx <= diameter ; dx = dx + 16) {
            for (int dz = 0 ; dz <= diameter ; dz = dz +16) {
                Entity[] entities = start.clone().add(dx, 0, dz).getChunk().getEntities();
                for (Entity entity : entities) {
                    int entityID = entity.getEntityId();
                    if (!emap.containsKey(entityID) ) {
                        double distance = vp.getLocation().distance(entity.getLocation());
                        if (distance <= radius) {
                            emap.put(entityID, entity);
                        }
                    }
                }
            }
        }
        
        return emap.values();
    }
}
