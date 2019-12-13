package mc.alk.virtualplayers.api.handlers;

import org.bukkit.entity.Player;

/**
 * This class was originally in BattleBukkitLib which used to
 * be shaded into this plugin, but since this was the only
 * class inside of BattleBukkitLib that VirtualPlayers used,
 * it has since been moved inside of VirtualPlayers directly.
 *
 * @author Europia79, Redned
 */
public interface IDamageHandler {

    void damageEntity(Player player, double dmg);

    IDamageHandler DEFAULT_HANDLER = (player, dmg) -> player.damage((int) dmg);
}
