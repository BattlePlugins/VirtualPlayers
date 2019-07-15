package mc.alk.virtualplayers.api;

import mc.alk.virtualplayers.api.handlers.IDamageHandler;
import mc.euro.version.Version;
import mc.euro.version.VersionFactory;
import org.bukkit.Server;

/**
 * This class was originally in BattleBukkitLib which used to
 * be shaded into this plugin, but since this was the only
 * class inside of BattleBukkitLib that VirtualPlayers used,
 * it has since been moved inside of VirtualPlayers directly.
 *
 * @author Europia79, Redned
 */
public class DamageHandlerFactory {

    public static IDamageHandler getNewInstance() {
        Version<Server> server = VersionFactory.getServerVersion();
        IDamageHandler handler = null;
        Class clazz = null;
        try {
            Class<?>[] args = {};
            if (server.isGreaterThanOrEqualTo("1.2.5") && server.isLessThan("1.6.1")) {
                clazz = Class.forName("mc.alk.virtualplayers.nms.v1_2_5.DamageHandler");
            } else if (server.isGreaterThanOrEqualTo("1.6.1")) {
                clazz = Class.forName("mc.alk.virtualplayers.nms.v1_6_R1.DamageHandler");
            }

            handler = (IDamageHandler) clazz.getConstructor(args).newInstance((Object[]) args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return (handler == null) ? IDamageHandler.DEFAULT_HANDLER : handler;
    }
}