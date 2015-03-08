package com.gameminers.farrago;

import java.util.Random;

import net.minecraft.world.World;

import com.gameminers.farrago.entity.EntityRifleProjectile;

public interface Proxy {
	void postInit();
	void init();
	void preInit();
	void spawnRifleParticle(RifleMode mode, EntityRifleProjectile proj);
	void stopSounds();
	void glowRandomDisplayTick(World world, int x, int y, int z, Random rand);
}
