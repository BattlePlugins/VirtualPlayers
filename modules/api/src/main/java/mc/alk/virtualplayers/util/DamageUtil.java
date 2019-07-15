package mc.alk.virtualplayers.util;

import mc.alk.virtualplayers.api.DamageHandlerFactory;
import mc.alk.virtualplayers.api.handlers.IDamageHandler;
import org.bukkit.entity.Player;

/**
 * This class was originally in BattleBukkitLib which used to
 * be shaded into this plugin, but since this was the only
 * class inside of BattleBukkitLib that VirtualPlayers used,
 * it has since been moved inside of VirtualPlayers directly.
 *
 * @author Europia79, Redned
 */
public class DamageUtil {

    private static IDamageHandler handler = DamageHandlerFactory.getNewInstance();

    public static void damageEntity(Player player, double dmg) {
        handler.damageEntity(player, dmg);
    }

}