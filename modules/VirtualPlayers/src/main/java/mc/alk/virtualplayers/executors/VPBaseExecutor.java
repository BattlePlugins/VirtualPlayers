package mc.alk.virtualplayers.executors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import mc.alk.virtualplayers.VirtualPlayers;
import mc.alk.virtualplayers.api.VirtualPlayer;
import mc.alk.virtualplayers.api.Vps;
import mc.alk.virtualplayers.util.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author alkarin
 */
public class VPBaseExecutor extends CustomCommandExecutor {

    static boolean showTransitionsToPlayer = true;
    static boolean showEventMessages = true;
    
    protected VPBaseExecutor(Plugin plugin) {
        super(plugin);
    }

    public VPBaseExecutor(Plugin plugin, int i) {
        super(plugin, i);
    }

    @Override
    protected String getUsageString(Class<?> clazz) {
        if (VirtualPlayer.class == clazz) {
            return "<vp>";
        }
        return super.getUsageString(clazz);
    }

    @Override
    protected Object verifyArg(CommandSender sender, Class<?> clazz, Command command, String[] args, int curIndex, AtomicInteger numUsedStrings) {
        if (VirtualPlayer.class == clazz) {
            return verifyVirtualPlayer(args[curIndex], numUsedStrings);
        }
        return super.verifyArg(sender, clazz, command, args, curIndex, numUsedStrings);
    }

    private Object verifyVirtualPlayer(String name, AtomicInteger numUsedStrings) {
        Player p = Vps.getApi().getPlayerExact(name);
        if (p == null) {
            if (name.contains("-")) { /// return range
                return getVps(name);
            }
            p = Vps.getApi().getOrCreate(name);
        }
        if (!(p instanceof VirtualPlayer)) {
            throw new IllegalArgumentException("Player " + name + " is not a VirtualPlayer");
        }
        numUsedStrings.set(1);
        return p;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object invoke(CustomCommandExecutor.MethodWrapper mwrapper, CustomCommandExecutor.Arguments args)
            throws InvocationTargetException, IllegalAccessException {
        if (args.args.length > 1 && args.args[1] instanceof List) {
            Object ret = null;
            Object[] o = Arrays.copyOfRange((Object[]) args.args[2], 1, ((Object[]) args.args[2]).length);
            args.args[2] = o;
            List<VirtualPlayer> list = (List<VirtualPlayer>) args.args[1];
            for (Object vp : list) {
                args.args[1] = vp;
                ret = mwrapper.method.invoke(mwrapper.obj, args.args);
            }
            return ret;
        } else {
            return mwrapper.method.invoke(mwrapper.obj, args.args);
        }
    }

    @Override
    public boolean sendMessage(CommandSender sender, String string) {
        if (string == null || !showEventMessages) {
            return true;
        }
        return super.sendMessage(sender, string);
    }

    protected boolean sendMessage(CommandSender sender, VirtualPlayer p, String string) {
        if (string == null || !showEventMessages) {
            return true;
        }
        if (showTransitionsToPlayer) {
            Util.sendMessage(p, string);
        }
        return super.sendMessage(sender, string);
    }

    private List<VirtualPlayer> getVps(String names) {
        String name = names;
        List<VirtualPlayer> players = new ArrayList<VirtualPlayer>();
        if (names.contains("-")) {
            String[] split = names.split("-");
            name = split[0];
            Integer start = Integer.valueOf(split[1]);
            Integer end = Integer.valueOf(split[2]);
            for (int i = start; i <= end; i++) {
                players.add(Vps.getApi().getOrCreate(name + i));
            }
        } else {
            players.add(Vps.getApi().getOrCreate(name));
        }
        return players;
    }

    public static void setEventMessages(boolean visibility) {
        showEventMessages = visibility;
    }
}
