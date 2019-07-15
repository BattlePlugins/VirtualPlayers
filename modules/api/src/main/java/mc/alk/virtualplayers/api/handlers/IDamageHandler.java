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

    public void damageEntity(Player player, double dmg);

    public static final IDamageHandler DEFAULT_HANDLER = new IDamageHandler() {

        @Override
        public void damageEntity(Player player, double dmg) {
            player.damage((int) dmg);
        }
    };
}
