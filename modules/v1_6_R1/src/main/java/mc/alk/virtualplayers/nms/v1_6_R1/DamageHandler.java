package mc.alk.virtualplayers.nms.v1_6_R1;

import mc.alk.virtualplayers.api.handlers.IDamageHandler;

import net.minecraft.server.v1_6_R1.DamageSource;
import net.minecraft.server.v1_6_R1.EntityPlayer;

import org.bukkit.craftbukkit.v1_6_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class DamageHandler implements IDamageHandler {

    @Override
    public void damageEntity(Player player, double dmg) {
        EntityPlayer entity = ((CraftPlayer)player).getHandle();
        entity.damageEntity(DamageSource.GENERIC, (float) dmg);
    }
}