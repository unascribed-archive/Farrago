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
	public static void render(Minecraft mc, ItemStack belt, float partialTicks, int width, int height) {
		renderHotbar(mc, belt, partialTicks, width, height, 0);
	}
	
	public static void renderHotbar(Minecraft mc, ItemStack belt, float partialTicks, int width, int height, int row) {
		mc.mcProfiler.startSection("actionBarMulti");

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(wadjets);

		InventoryPlayer inv = mc.thePlayer.inventory;
		Rendering.drawTexturedModalRect(width / 2 - 91, height - 22, 0, 0, 182, 22);
		Rendering.drawTexturedModalRect(width / 2 - 91 - 1 + inv.currentItem * 20, height - 22 - 1, 0, 22, 24, 22);

		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.enableGUIStandardItemLighting();

		for (int i = 0; i < 9; ++i) {
			int x = width / 2 - 90 + i * 20 + 2;
			int z = height - 16 - 3;
			renderInventorySlot(inv.mainInventory[i], x, z, partialTicks, mc);
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

			if (f1 > 0.0F) {
				GL11.glPushMatrix();
				float f2 = 1.0F + f1 / 5.0F;
				GL11.glTranslatef((float) (x + 8), (float) (y + 12), 0.0F);
				GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
				GL11.glTranslatef((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
			}

			itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);

			if (f1 > 0.0F) {
				GL11.glPopMatrix();
			}

			itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);
		}
	}
}
