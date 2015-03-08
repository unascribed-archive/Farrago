package com.gameminers.farrago;

import com.gameminers.farrago.entity.EntityRifleProjectile;

public interface Proxy {
	void postInit();
	void init();
	void preInit();
	void spawnRifleParticle(RifleMode mode, EntityRifleProjectile proj);
	void stopSounds();
}
