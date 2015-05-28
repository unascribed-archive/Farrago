package com.gameminers.farrago.client;

import gminers.kitchensink.Rendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.gameminers.farrago.FarragoMod;

public class UtilityBeltRenderer {
	private static final ResourceLocation wadjets = new ResourceLocation("textures/gui/widgets.png");
	private static final RenderItem itemRenderer = new RenderItem();
	private static int lastHotbar = 0;
	private static int lastHotbarTicks = 0;
	private static ItemStack[] lastHotbarContent;
	private static boolean switching = false;
	public static void render(Minecraft mc, ItemStack belt, float partialTicks, int width, int height) {
		int cur = FarragoMod.UTILITY_BELT.getCurrentRow(belt);
		if (lastHotbar != cur && !switching) {
			switching = true;
			lastHotbarContent = FarragoMod.UTILITY_BELT.getRowContents(belt, lastHotbar);
		}
		if (switching) {
			boolean dir = cur > lastHotbar;
			float ticks = lastHotbarTicks + partialTicks;
			float scale = 1;
			float scale2 = 1;
			float trans = 0;
			float trans2 = 0;
			if (dir) {
				trans = lastHotbar > cur ? ticks*2f : (11-ticks)*2f;
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
					if (!dir) {
						trans2 = lastHotbar > cur ? ticks*2f : (11-ticks)*2f;
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
			if (dir) {
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
				} else if (i == 2) {
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
		mc.mcProfiler.startSection("actionBarMulti");

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		mc.renderEngine.bindTexture(wadjets);

		InventoryPlayer inv = mc.thePlayer.inventory;
		Rendering.drawTexturedModalRect(width / 2 - 91, height - 22, 0, 0, 182, 22);
		Rendering.drawTexturedModalRect(width / 2 - 91 - 1 + inv.currentItem * 20, height - 22 - 1, 0, 22, 24, 22);

		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.enableGUIStandardItemLighting();

		for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
			int x = width / 2 - 90 + i * 20 + 2;
			int z = height - 16 - 3;
			renderInventorySlot(contents[i], x, z, partialTicks, mc);
		}

		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		
		String nm = FarragoMod.UTILITY_BELT.getRowName(belt, row);
		mc.fontRenderer.drawStringWithShadow(nm, width / 2 - 95 - mc.fontRenderer.getStringWidth(nm), height - 15, -1);
		
		mc.mcProfiler.endSection();
	}
	
	public static void renderInventorySlot(ItemStack itemstack, int x, int y, float partialTicks, Minecraft mc) {
		if (itemstack != null) {
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
			itemRenderer.zLevel = 3;
			itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);

			if (f1 > 0.0F) {
				GL11.glPopMatrix();
			}

			itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);
		}
	}
}
