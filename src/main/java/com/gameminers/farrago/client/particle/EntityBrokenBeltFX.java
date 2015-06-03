package com.gameminers.farrago.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityBrokenBeltFX extends EntityFX {
	private static final ResourceLocation texture = new ResourceLocation("farrago", "textures/particle/broken_belt.png");
	private TextureManager textureManager;

	public EntityBrokenBeltFX(TextureManager textureManager, Entity entity) {
		super(entity.worldObj, entity.posX, entity.boundingBox.minY+0.3, entity.posZ, 0.0D, 0.0D, 0.0D);
		this.textureManager = textureManager;
		particleGravity = 1f;
		motionX = entity.motionX * 0.75;
		motionZ = entity.motionZ * 0.75;
		rotationYaw = entity.rotationYaw;
		particleMaxAge = 250;
		particleScale = 3f;
		setSize(0.6f, 0.05f);
	}

	
	@Override
	public void renderParticle(Tessellator tess, float partialTicks, float rotX, float rotXZ, float rotZ, float rotYZ, float rotXY) {
		float ageF = ((float) particleAge + partialTicks) / (float) particleMaxAge;
		float alpha = 2.5f - ageF * 2.5f;

		if (alpha > 1.0f) {
			alpha = 1.0f;
		} else if (alpha < 0.0f) {
			alpha = 0.0f;
		}
		
		particleAlpha = alpha;
		
		textureManager.bindTexture(texture);
		
        float minU = 0;
        float maxU = 1;
        float minV = 0;
        float maxV = 1;
        float scale = 0.1F * this.particleScale;

        rotX = 0;
        rotXZ = 0;
        rotZ = 1;
        rotYZ = -1;
        rotXY = 0;
        
        float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        tess.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tess.addVertexWithUV((double)(x - rotX * scale - rotYZ * scale), (double)(y - rotXZ * scale), (double)(z - rotZ * scale - rotXY * scale), (double)maxU, (double)maxV);
        tess.addVertexWithUV((double)(x - rotX * scale + rotYZ * scale), (double)(y + rotXZ * scale), (double)(z - rotZ * scale + rotXY * scale), (double)maxU, (double)minV);
        tess.addVertexWithUV((double)(x + rotX * scale + rotYZ * scale), (double)(y + rotXZ * scale), (double)(z + rotZ * scale + rotXY * scale), (double)minU, (double)minV);
        tess.addVertexWithUV((double)(x + rotX * scale - rotYZ * scale), (double)(y - rotXZ * scale), (double)(z + rotZ * scale - rotXY * scale), (double)minU, (double)maxV);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	@Override
	public int getFXLayer() {
		return 0;
	}
}
