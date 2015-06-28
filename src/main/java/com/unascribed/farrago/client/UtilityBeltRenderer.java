package com.unascribed.farrago.client;

import gminers.kitchensink.Rendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.unascribed.farrago.FarragoMod;

public class UtilityBeltRenderer {
	private static final ResourceLocation wadjets = new ResourceLocation("textures/gui/widgets.png");
	private static final RenderItem itemRenderer = new RenderItem();
	private static int lastHotbar = 0;
	private static int lastHotbarTicks = 0;
	private static ItemStack[] lastHotbarContent;
	private static boolean switching = false;
	public static boolean showSwapSpace;
	public static boolean direction;
	public static void render(Minecraft mc, ItemStack belt, float partialTicks, int width, int height) {
		mc.mcProfiler.startSection("actionBarMulti");
		int cur = FarragoMod.UTILITY_BELT.getCurrentRow(belt);
		if (lastHotbar != cur && !switching) {
			switching = true;
			lastHotbarContent = FarragoMod.UTILITY_BELT.getRowContents(belt, lastHotbar);
			byte[] lastLocked = FarragoMod.UTILITY_BELT.getLockedSlots(belt, lastHotbar);
			byte[] locked = FarragoMod.UTILITY_BELT.getLockedSlots(belt, cur);
			ItemStack[] swap = FarragoMod.UTILITY_BELT.getSwapContents(belt);
			for (byte b : lastLocked) {
				if (swap[b] != null) {
					lastHotbarContent[b] = swap[b];
				} else if (ArrayUtils.contains(locked, b)) {
					lastHotbarContent[b] = mc.thePlayer.inventory.mainInventory[b];
				}
			}
		}
		if (switching) {
			float ticks = lastHotbarTicks + partialTicks;
			float scale = 1;
			float scale2 = 1;
			float trans = 0;
			float trans2 = 0;
			if (direction) {
				trans = direction ? (11-ticks)*2f : ticks*2f;
			} else {
				scale = ((ticks/11f)*0.5f)+0.5f;
				trans = (6-ticks) * 2f;
				if (trans < 0) {
					trans = 0;
				}
				if (scale > 1) {
					scale = 1;
				}
			}
			
			if (lastHotbar != FarragoMod.UTILITY_BELT.getCurrentRow(belt) && lastHotbarTicks < 11) {
				if (switching) {
					if (!direction) {
						trans2 = direction ? (11-ticks)*2f : ticks*2f;
					} else {
						scale2 = (((11-ticks)/11f)*0.5f)+0.5f;
						trans2 = ticks * 2f;
						if (scale2 > 1) {
							scale2 = 1;
						}
					}
				}
				
			}
			int[] order;
			if (direction) {
				order = new int[] {2, 1};
			} else {
				order = new int[] {1, 2};
			}
			GL11.glPushMatrix();
			for (int i : order) {
				GL11.glPushMatrix();
				if (i == 1) {
					GL11.glTranslatef(0, trans, 0);
					GL11.glScalef(scale, scale, 1);
					renderHotbar(mc, belt, partialTicks, (int)(width*(1f/scale)), (int)(height*(1f/scale)), cur, mc.thePlayer.inventory.mainInventory);
				} else if (i == 2 && lastHotbarContent != null) {
					GL11.glTranslatef(0, trans2, 0);
					GL11.glScalef(scale2, scale2, 1);
					renderHotbar(mc, belt, partialTicks, (int)(width*(1f/scale2)), (int)(height*(1f/scale2)), lastHotbar, lastHotbarContent);
				}
				GL11.glPopMatrix();
				GL11.glTranslatef(0, 0, 100);
			}
			GL11.glPopMatrix();
		} else {
			renderHotbar(mc, belt, partialTicks, width, height, cur, mc.thePlayer.inventory.mainInventory);
		}
		if (showSwapSpace) {
			renderHotbar(mc, belt, partialTicks, 182, 23, -1, FarragoMod.UTILITY_BELT.getSwapContents(belt));
		}
		mc.mcProfiler.endSection();
	}
	
	public static void tick(Minecraft mc, ItemStack belt) {
		if (lastHotbar != FarragoMod.UTILITY_BELT.getCurrentRow(belt) && lastHotbarTicks < 10) {
			switching = true;
			lastHotbarTicks++;
		} else {
			lastHotbar = FarragoMod.UTILITY_BELT.getCurrentRow(belt);
			lastHotbarContent = null;
			lastHotbarTicks = 0;
			switching = false;
		}
	}
	
	public static void renderHotbar(Minecraft mc, ItemStack belt, float partialTicks, int width, int height, int row, ItemStack[] contents) {
		mc.mcProfiler.startSection("actionBar");
		byte[] locked = FarragoMod.UTILITY_BELT.getLockedSlots(belt, row);
		
		for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
			int x = width / 2 - 90 + i * 20 + 2;
			int y = height - 16 - 3;
			int u = (20 * i) + 1;
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			mc.renderEngine.bindTexture(wadjets);
			if (ArrayUtils.contains(locked, (byte)i)) {
				GL11.glColor3f(1.0f, 0.5f, 0.0f);
			} else {
				GL11.glColor3f(1.0f, 1.0f, 1.0f);
			}
			if (i == 0) {
				Rendering.drawTexturedModalRect(x-3, y-3, u-1, 0, 21, 22);
			} else if (i == 8) {
				Rendering.drawTexturedModalRect(x-2, y-3, u, 0, 21, 22);
			} else {
				Rendering.drawTexturedModalRect(x-2, y-3, u, 0, 20, 22);
			}
			if (i == mc.thePlayer.inventory.currentItem) {
				Rendering.drawTexturedModalRect(x-4, y-4, 0, 22, 24, 23, 1);
			}
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.enableGUIStandardItemLighting();
			renderInventorySlot(contents[i], x, y, partialTicks, mc);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		
		String nm = FarragoMod.UTILITY_BELT.getRowName(belt, row);
		mc.fontRenderer.drawStringWithShadow(nm, width / 2 - 95 - mc.fontRenderer.getStringWidth(nm), height - 15, -1);
		
		mc.mcProfiler.endSection();
	}
	
	public static void renderInventorySlot(ItemStack itemstack, int x, int y, float partialTicks, Minecraft mc) {
		if (itemstack != null) {
			GL11.glPushMatrix();
			float f1 = (float) itemstack.animationsToGo - partialTicks;
			if (switching) {
				f1 = 0;
			}
			if (f1 > 0.0F) {
				GL11.glPushMatrix();
				float f2 = 1.0F + f1 / 5.0F;
				GL11.glTranslatef((float) (x + 8), (float) (y + 12), 0.0F);
				GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
				GL11.glTranslatef((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
			}
			itemRenderer.zLevel += 3;
			itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);

			if (f1 > 0.0F) {
				GL11.glPopMatrix();
			}

			itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);
			itemRenderer.zLevel -= 3;
			GL11.glPopMatrix();
		}
	}
}
