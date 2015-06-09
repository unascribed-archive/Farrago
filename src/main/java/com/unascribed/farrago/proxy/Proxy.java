package com.unascribed.farrago.proxy;

import java.util.Random;

import com.unascribed.farrago.entity.EntityRifleProjectile;
import com.unascribed.farrago.enums.RifleMode;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public interface Proxy {
	void postInit();
	void init();
	void preInit();
	void spawnRifleParticle(RifleMode mode, EntityRifleProjectile proj);
	void stopSounds();
	void glowRandomDisplayTick(World world, int x, int y, int z, Random rand);
	void scope(EntityPlayer player);
	void tooltip(ItemTooltipEvent e);
	void breakUtilityBelt(ItemStack belt);
}
