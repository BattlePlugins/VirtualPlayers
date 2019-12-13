package mc.alk.virtualplayers.api;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mc.euro.version.VersionFactory;


/**
 * abstract VirtualPlayerFactory: handle the creation, tracking, and deletion of VirtualPlayers.
 * <pre>
 * Abstractions:
 *     makeVirtualPlayer();
 *     deleteVirtualPlayer();
 * Implementations:
 *     mc.alk.virtualplayers.nms.{version}.CraftVirtualPlayerFactory;
 * </pre>
 * @author Nikolai
 */
public abstract class VirtualPlayerFactory {
    
    static String NMS = VersionFactory.getNmsVersion().toString();
    protected static final Map<UUID, VirtualPlayer> vps = new HashMap<UUID, VirtualPlayer>();
    protected static final Map<String, VirtualPlayer> names = new HashMap<String, VirtualPlayer>();
    
    protected static final VirtualPlayerFactory factory = newInstance();
    
    /**
     * mc.alk.virtualplayers.nms.{version}.CraftVirtualPlayerFactory;.
     *
     * @return a new instance of the VirtualPlayerFactory
     */
    public static synchronized VirtualPlayerFactory newInstance() {
            if (factory != null) {
                return factory;
            }
            Class<?>[] args = {};
            Constructor con;
            VirtualPlayerFactory factory = null;
            try {
                con = getNmsClass("CraftVirtualPlayerFactory").getConstructor(args);
                factory = (VirtualPlayerFactory) con.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return factory;
    }
    
    private static Class<?> getNmsClass(String clazz) throws Exception {
        return Class.forName("mc.alk.virtualplayers.nms." + NMS + "." + clazz);
    }
    
    public abstract VirtualPlayer makeVirtualPlayer(String name) throws Exception;
    public abstract void deleteVirtualPlayer(String name);
    public abstract void deleteVirtualPlayer(VirtualPlayer vp);
    
    public static VirtualPlayer getOrCreate(String name) {
        VirtualPlayer vp = names.get(name);
        if (vp == null) {
            try {
                vp = factory.makeVirtualPlayer(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vp;
    }
    
    public static Collection<VirtualPlayer> getVirtualPlayers() {
        return names.values();
    }
    
    public static Map<UUID, VirtualPlayer> getVps() {
        return vps;
    }
    
    public static Map<String, VirtualPlayer> getNames() {
        return names;
    }
    
    public static List<VirtualPlayer> getNewPlayerList() {
        synchronized (vps) {
            return new ArrayList<>(vps.values());
        }
    }

}
