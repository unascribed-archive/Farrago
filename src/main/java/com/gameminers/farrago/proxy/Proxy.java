package com.gameminers.farrago.proxy;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.gameminers.farrago.entity.EntityRifleProjectile;
import com.gameminers.farrago.enums.RifleMode;

public interface Proxy {
	void postInit();
	void init();
	void preInit();
	void spawnRifleParticle(RifleMode mode, EntityRifleProjectile proj);
	void stopSounds();
	void glowRandomDisplayTick(World world, int x, int y, int z, Random rand);
	void scope(EntityPlayer player);
}
