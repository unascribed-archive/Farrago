package com.gameminers.farrago.pane;

import gminers.glasspane.GlassPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gameminers.farrago.FarragoMod;

public class PaneOrbGlow extends GlassPane {
	protected static final ResourceLocation vignetteTexPath = new ResourceLocation("farrago", "textures/misc/bright_vignette.png");
	private float prevVignetteBrightness = 0.0f;
	private int prevOrbColor;
	@Override
	protected void doRender(int arg0, int arg1, float arg2) {
		super.doRender(arg0, arg1, arg2);
		if (Minecraft.getMinecraft().thePlayer != null) {
			ItemStack held = Minecraft.getMinecraft().thePlayer.getHeldItem();
			boolean holdingOrb = (held != null && held.getItem() == FarragoMod.VIVID_ORB);
			int color;
			if (holdingOrb) {
				color = prevOrbColor = FarragoMod.VIVID_ORB.getColorFromItemStack(held, 0);
			} else {
				color = prevOrbColor;
			}
			renderVignette(holdingOrb ? 0.0f : 1.0f, color);
		}
	}
	protected void renderVignette(float vignetteBrightness, int vignetteColor) {
		vignetteBrightness = 1.0F - vignetteBrightness;

		if (vignetteBrightness < 0.0F) {
			vignetteBrightness = 0.0F;
		}

		if (vignetteBrightness > 1.0F) {
			vignetteBrightness = 1.0F;
		}

		this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(vignetteBrightness - this.prevVignetteBrightness) * 0.05D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(0, 769, 1, 0);
		float r = (1.0f-(((vignetteColor >> 16) & 0xFF)/255f))*2f;
		float g = (1.0f-(((vignetteColor >> 8) & 0xFF)/255f))*2f;
		float b = (1.0f-((vignetteColor & 0xFF)/255f))*2f;
		GL11.glColor4f(this.prevVignetteBrightness*r, this.prevVignetteBrightness*g, this.prevVignetteBrightness*b, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(vignetteTexPath);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(0.0D, (double)height, -90.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV((double)width, (double)height, -90.0D, 1.0D, 1.0D);
		tessellator.addVertexWithUV((double)width, 0.0D, -90.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
		tessellator.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	}
}
