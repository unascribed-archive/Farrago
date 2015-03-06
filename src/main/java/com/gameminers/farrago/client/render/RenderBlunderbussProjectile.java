package com.gameminers.farrago.client.render;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderBlunderbussProjectile extends Render {
	private final ModelBox box = new ModelBox(new ModelRenderer(new ModelNull(16, 16)), 0, 0, 0f, 0f, 0f, 4, 4, 4, 1f);
	private static final ResourceLocation TEX = new ResourceLocation("textures/blocks/cobblestone.png");
    @Override
    public void doRender(Entity projectile, double x, double y, double z, float yaw, float partialTicks) {
    	GL11.glPushMatrix();
			GL11.glTranslatef((float)x, (float)y, (float)z);
			GL11.glScalef(0.25f, 0.25f, 0.25f);
	    	bindEntityTexture(projectile);
	    	Tessellator tess = Tessellator.instance;
	    	box.render(tess, 0.0625f);
    	GL11.glPopMatrix();
    }

	@Override
	protected ResourceLocation getEntityTexture(Entity projectile) {
		return TEX;
	}
    
}
