package mc.alk.virtualplayers.nms.v1_2_5;

import mc.alk.virtualplayers.api.handlers.IDamageHandler;

import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityPlayer;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class DamageHandler implements IDamageHandler {

    @Override
    public void damageEntity(Player player, double dmg) {
        EntityPlayer entity = ((CraftPlayer)player).getHandle();
        entity.damageEntity(DamageSource.GENERIC, (int) dmg);
    }
}