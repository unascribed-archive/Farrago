package com.gameminers.farrago.pane;

import gminers.glasspane.GlassPane;
import gminers.glasspane.component.PaneImage;
import gminers.kitchensink.Rendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;

import org.lwjgl.opengl.GL11;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.enums.RifleMode;

public class PaneRifle extends GlassPane {
	private static final ResourceLocation[] crosshairs = new ResourceLocation[26];
	static {
		for (int i = 0; i < crosshairs.length; i++) {
			crosshairs[i] = new ResourceLocation("farrago", "textures/crosshairs/rifle_crosshair_"+i+".png");
		}
	}
	@Override
	protected void doRender(int mouseX, int mouseY, float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer != null) {
			if (mc.thePlayer.getHeldItem() != null) {
				ItemStack held = mc.thePlayer.getHeldItem();
				if (held.getItem() == FarragoMod.RIFLE) {
					GuiIngameForge.renderCrosshairs = false;
					int idx = 0;
					int idx2 = 0;
					boolean overloadImminent = false;
					boolean ready = false;
					float useTime = 0; 
					int ticksToFire = FarragoMod.RIFLE.getTicksToFire(held);
					RifleMode mode = FarragoMod.RIFLE.getMode(held);
					if (mc.thePlayer.isUsingItem()) {
						useTime = mc.thePlayer.getItemInUseDuration()+partialTicks;
						ready = (useTime >= ticksToFire); 
						int maxUseTime = held.getItem().getMaxItemUseDuration(held);
						idx = (int)(((float)Math.min(useTime, ticksToFire))/((float)ticksToFire)*25f)+1;
						if (ready) {
							idx2 = (int)(((float)useTime-ticksToFire)/((float)maxUseTime-ticksToFire)*25f)+1;
						}
						overloadImminent = (useTime >= ((FarragoMod.RIFLE.getChargeTicks(mode) + 15)/mode.getChargeSpeed()));
					}
					if (!(overloadImminent || ready)) {
						GL11.glEnable(GL11.GL_BLEND);
				        OpenGlHelper.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
					}
					int color = ready ? 0xFF00FF00 : -1;
					int color2 = overloadImminent ? 0xFFFF0000 : -1;
					PaneImage.render(crosshairs[Math.min(25, idx)], (getWidth()/2)-8, (getHeight()/2)-8, 0, 0, 16, 16, 256, 256, color, 1.0f, true);
					if (ready) {
						PaneImage.render(crosshairs[Math.min(25, idx2)], (getWidth()/2)-8, (getHeight()/2)-8, 0, 0, 16, 16, 256, 256, color2, 1.0f, true);
					}
					GL11.glPushMatrix();
			        	GL11.glScalef(0.5f, 0.5f, 1.0f);
			        	if (overloadImminent) {
			        		Rendering.drawCenteredString(mc.fontRenderer, "\u00A7lOVERLOAD IMMINENT", getWidth()-2, getHeight()-28, color);
			        	} else if (ready) {
			        		Rendering.drawCenteredString(mc.fontRenderer, "\u00A7lREADY", getWidth()-2, getHeight()-28, color);
			        	}
			        	Rendering.drawCenteredString(mc.fontRenderer, mode.getDisplayName(), getWidth()-2, getHeight()+20, color);
			        GL11.glPopMatrix();
					if (!(overloadImminent || ready)) {
						OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
						GL11.glDisable(GL11.GL_BLEND);
					}
					if (ready) {
						PaneImage.render(crosshairs[0], (getWidth()/2)-8, (getHeight()/2)-8, 0, 0, 16, 16, 256, 256, -1, 1.0f, true);
					}
					return;
				}
			}
		}
		GuiIngameForge.renderCrosshairs = true;
	}
}
