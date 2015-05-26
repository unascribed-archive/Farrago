package com.gameminers.farrago.client;

import gminers.glasspane.component.PaneImage;
import gminers.kitchensink.Rendering;

import java.util.Locale;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.enums.RifleMode;
import com.google.common.collect.Maps;

public class RifleRenderer {
	private static final ResourceLocation[] crosshairs = new ResourceLocation[26];
	private static final Map<RifleMode, ResourceLocation> modeIcons = Maps.newEnumMap(RifleMode.class);
	private static final ResourceLocation wadjets = new ResourceLocation("textures/gui/widgets.png");
	static {
		for (int i = 0; i < crosshairs.length; i++) {
			crosshairs[i] = new ResourceLocation("farrago", "textures/crosshairs/rifle_crosshair_"+i+".png");
		}
		for (RifleMode rm : RifleMode.values()) {
			modeIcons.put(rm, new ResourceLocation("farrago", "textures/riflemode/"+rm.name().toLowerCase(Locale.ENGLISH)+".png"));
		}
	}
	public static void renderHotbar(Minecraft mc, ItemStack held, float partialTicks, int width, int height) {
		RifleMode mode = FarragoMod.RIFLE.getMode(held);
		GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(wadjets);
			Rendering.drawTexturedModalRect(width / 2 - 91, 0, 0, 0, 182, 22);  
			Rendering.drawTexturedModalRect(width / 2 - 91 - 1 + mode.ordinal() * 20, -1, 0, 22, 24, 22);
		GL11.glDisable(GL11.GL_BLEND);
		RifleMode[] vals = RifleMode.values();
		for (int i = 0; i < 9; ++i) {
			int x = width / 2 - 90 + i * 20 + 2;
			int y = 3;
			boolean available = FarragoMod.RIFLE.hasAmmoFor(mc.thePlayer, vals[i]);
			PaneImage.render(modeIcons.get(vals[i]), x, y, 0, 0, 16, 16, 256, 256, available ? -1 : 0, 1.0f, true);
			if (available) {
				String scount;
				if (!mc.thePlayer.capabilities.isCreativeMode) {
					int count = 0;
					InventoryPlayer inv = mc.thePlayer.inventory;
					for (int j = 0; j < inv.getSizeInventory(); j++) {
						ItemStack stack = inv.getStackInSlot(j);
						if (stack == null) continue;
						if (stack.getItem() == FarragoMod.CELL) {
							if (stack.getItemDamage() == vals[i].getCellType()) {
								count += stack.stackSize;
							}
						}
					}
					scount = Integer.toString(count);
				} else {
					scount = "∞";
				}
				mc.fontRenderer.drawStringWithShadow(scount, (x+17)-mc.fontRenderer.getStringWidth(scount), y+9, -1);
			}
		}
		Rendering.drawCenteredString(mc.fontRenderer, mode.getDisplayName(), width/2, 24, -1);
	}

	public static void renderCrosshairs(Minecraft mc, ItemStack held, float partialTicks, int width, int height) {
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
		int textColor = ready ? overloadImminent ? 0xFFFF0000 : 0xFF00FF00 : -1;
		PaneImage.render(crosshairs[Math.min(25, idx)], (width/2)-8, (height/2)-8, 0, 0, 16, 16, 256, 256, -1, 1.0f, true);
		if (ready) {
			PaneImage.render(crosshairs[Math.min(25, idx2)], (width/2)-8, (height/2)-8, 0, 0, 16, 16, 256, 256, 0xFF0000, 1.0f, true);
		}
		GL11.glPushMatrix();
        	GL11.glScalef(0.5f, 0.5f, 1.0f);
        	if (overloadImminent) {
        		Rendering.drawCenteredString(mc.fontRenderer, "\u00A7lOVERLOAD IMMINENT", width-2, height-28, textColor);
        	} else if (ready) {
        		Rendering.drawCenteredString(mc.fontRenderer, "\u00A7lREADY", width-2, height-28, textColor);
        	}
        GL11.glPopMatrix();
		if (!(overloadImminent || ready)) {
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			GL11.glDisable(GL11.GL_BLEND);
		}
		if (ready) {
			PaneImage.render(crosshairs[0], (width/2)-8, (height/2)-8, 0, 0, 16, 16, 256, 256, textColor, 1.0f, true);
		}
	}
}
