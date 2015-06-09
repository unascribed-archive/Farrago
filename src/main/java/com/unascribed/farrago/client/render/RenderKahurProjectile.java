package com.unascribed.farrago.client.render;

import com.unascribed.farrago.entity.EntityKahurProjectile;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderKahurProjectile extends Render {

    @Override
    public void doRender(Entity projectile, double x, double y, double z, float yaw, float partialTicks) {
    	Entity actuallyRender = ((EntityKahurProjectile)projectile).getDummyEntity();
    	renderManager.renderEntityWithPosYaw(actuallyRender, x, y, z, yaw, partialTicks);
    }

	@Override
	protected ResourceLocation getEntityTexture(Entity projectile) {
		System.out.println("Someone called getEntityTexture");
		return null;
	}
    
}
