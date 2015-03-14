package com.gameminers.farrago.client.effect;

import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.world.World;

public class EntityRifleFX extends EntityReddustFX {
	public EntityRifleFX(World world, double x, double y, double z, float scale, float r, float g, float b) {
		super(world, x, y, z, scale, r, g, b);
		particleMaxAge *= 4;
	}

	@Override
	public int getBrightnessForRender(float p_70070_1_) {
		return 15728640;
	}
	
	@Override
	public void onUpdate() {
		prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (particleAge++ >= particleMaxAge) {
            setDead();
        }

        setParticleTextureIndex(7 - particleAge * 8 / particleMaxAge);
	}
}
