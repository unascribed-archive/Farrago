package com.gameminers.farrago;

import java.util.Random;

import net.minecraft.world.World;

import com.gameminers.farrago.entity.EntityRifleProjectile;

public class ServerProxy implements Proxy {

	@Override
	public void postInit() {
		for (Iota sub : FarragoMod.getSubMods()) {
			sub.serverPostInit();
		}
	}
	@Override public void init() {}
	@Override public void preInit() {}
	@Override public void spawnRifleParticle(RifleMode mode, EntityRifleProjectile proj) {}
	@Override public void stopSounds() {}
	@Override public void glowRandomDisplayTick(World world, int x, int y, int z,Random rand) {}

}
