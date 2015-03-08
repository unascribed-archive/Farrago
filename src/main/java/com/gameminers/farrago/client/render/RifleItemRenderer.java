package com.gameminers.farrago.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.RifleMode;

public class RifleItemRenderer implements IItemRenderer {
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return item.getItem() == FarragoMod.RIFLE && type == ItemRenderType.INVENTORY;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderItem.getInstance().renderIcon(0, 0, item.getItem().getIcon(item, 0), 16, 16);
		GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 3);
			RifleMode mode = FarragoMod.RIFLE.getMode(item);
			mc.fontRenderer.drawStringWithShadow(mode.getAbbreviation(), 10, 0, mode.getColor());
		GL11.glPopMatrix();
	}

}
