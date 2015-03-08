package com.gameminers.farrago.client.effect;

import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.world.World;

public class EntityRifleFX extends EntityReddustFX {
	public EntityRifleFX(World p_i1223_1_, double p_i1223_2_, double p_i1223_4_, double p_i1223_6_, float p_i1223_8_, float p_i1223_9_, float p_i1223_10_) {
		super(p_i1223_1_, p_i1223_2_, p_i1223_4_, p_i1223_6_, p_i1223_8_, p_i1223_9_, p_i1223_10_);
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
