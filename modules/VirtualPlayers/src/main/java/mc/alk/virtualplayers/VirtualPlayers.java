package mc.alk.virtualplayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import mc.alk.virtualplayers.api.VirtualPlayer;
import mc.alk.virtualplayers.api.VirtualPlayerFactory;
import mc.alk.virtualplayers.api.VirtualPlayersAPI;
import mc.alk.virtualplayers.executors.PlayerExecutor;
import mc.alk.virtualplayers.executors.VPBaseExecutor;
import mc.alk.virtualplayers.executors.VPExecutor;
import mc.alk.virtualplayers.listeners.VirtualPlayerListener;
import mc.euro.bukkitinterface.BukkitInterface;
import mc.euro.version.VersionFactory;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VirtualPlayers extends JavaPlugin implements VirtualPlayersAPI {

    private static final String NMS = VersionFactory.getNmsPackage();

    static final VirtualPlayerFactory factory = VirtualPlayerFactory.newInstance();

    @Override
    public void onEnable() {
        if (!isServerCompatible()) {
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(new VirtualPlayerListener(), this);
        getCommand("vdc").setExecutor(new PlayerExecutor(this));
        getCommand("virtualplayers").setExecutor(new VPExecutor(this));

        new Metrics(this);
    }

    @Override
    public void onDisable() {
        deleteVirtualPlayers();
    }
    
    private boolean isServerCompatible() {
        String className = "mc.alk.virtualplayers.nms." + NMS + ".CraftVirtualPlayer";
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ex) {
            getLogger().log(Level.WARNING, "VirtualPlayers is not compatible with your server.");
            getLogger().log(Level.WARNING, "IMPLEMENTATION NOT FOUND: ");
            getLogger().log(Level.WARNING, className);
            return false;
        }
    }

    @Override
    public Collection<? extends Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        VirtualPlayerFactory.getVirtualPlayers().stream().filter((p) -> (p.isOnline())).forEachOrdered((p) -> {
            players.add(p);
        });
        players.addAll(BukkitInterface.getOnlinePlayers());
        return players;
    }
    
    @Override
    public Player[] getOnlinePlayersArray() {
        return getOnlinePlayers().toArray(new Player[0]);
    }
    
    @Override
    public Collection<VirtualPlayer> getVirtualPlayers() {
        return VirtualPlayerFactory.getVirtualPlayers();
    }
    
    @Override
    public List<VirtualPlayer> getVirtualPlayersList() {
        return VirtualPlayerFactory.getNewPlayerList();
    }
    
    @Override
    public void setGlobalMessages(boolean visibility) {
        VirtualPlayerFactory.getVirtualPlayers().forEach((vp) -> {
            vp.setShowMessages(visibility);
        });
    }

    @Override
    public void setPlayerMessages(boolean visibility) {
        getVirtualPlayers().forEach((vp) -> {
            vp.setShowMessages(visibility);
        });
    }

    @Override
    public void setEventMessages(boolean visibility) {
        VPBaseExecutor.setEventMessages(visibility);
    }

    @Override
    public Map<UUID, VirtualPlayer> getVps() {
        return VirtualPlayerFactory.getVps();
    }

    @Override
    public Map<String, VirtualPlayer> getNames() {
        return VirtualPlayerFactory.getNames();
    }

    @Override
    public Player getPlayer(String pname) {
        Player vp = Bukkit.getPlayer(pname);
        if (vp == null) {
            vp = getNames().get(pname);
        }
        return vp;
    }

    @Override
    public Player getPlayer(UUID id) {
        Player vp = Bukkit.getPlayer(id);
        if (vp == null) {
            vp = getVps().get(id);
        }
        return vp;
    }

    @Override
    public Player getPlayerExact(String pname) {
        Player vp = Bukkit.getPlayerExact(pname);
        if (vp == null) {
            vp = getNames().get(pname);
        }
        return vp;
    }

    @Override
    public Player getOrMakePlayer(String pname) {
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

    @Override
    public VirtualPlayer getOrCreate(String name) {
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

    @Override
    public Player makeVirtualPlayer() throws Exception {
        return makeVirtualPlayer(null);
    }

    @Override
    public synchronized Player makeVirtualPlayer(String name) throws Exception {
        return factory.makeVirtualPlayer(name);
    }

    @Override
    public void deleteVirtualPlayer(String name) {
        VirtualPlayer vp = getNames().get(name);
        deleteVirtualPlayer(vp);
    }

    @Override
    public void deleteVirtualPlayer(VirtualPlayer vp) {
        factory.deleteVirtualPlayer(vp);
    }

    @Override
    public void deleteVirtualPlayers() {
        getVirtualPlayers().forEach((vp) -> {
            factory.deleteVirtualPlayer(vp);
        });
    }

}
