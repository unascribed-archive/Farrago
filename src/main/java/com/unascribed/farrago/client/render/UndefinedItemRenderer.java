package com.unascribed.farrago.client.render;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.unascribed.farrago.proxy.ClientProxy;

public class UndefinedItemRenderer implements IItemRenderer {
	private final Random rand = new Random();
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return rand.nextInt(80) > 0;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return (helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION) && rand.nextBoolean();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float mult = 1;
		if (type == ItemRenderType.INVENTORY) {
			mult = 16;
		}
		if (Minecraft.getMinecraft().thePlayer != null) {
			rand.setSeed((int)((Minecraft.getMinecraft().thePlayer.ticksExisted+ClientProxy.timer.renderPartialTicks)*100)*item.hashCode());
		}
		IIcon icon = item.getIconIndex();
		for (int i = 0; i < rand.nextInt(80)+30; i++) {
			GL11.glPushMatrix();
				GL11.glScalef(mult*rand.nextFloat(), mult*rand.nextFloat(), 0);
				GL11.glTranslatef((mult*((rand.nextFloat()*4)-2)), (mult*((rand.nextFloat()*4)-2)), (mult*((rand.nextFloat()*4)-2)));
				ItemRenderer.renderItemIn2D(Tessellator.instance, rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), icon.getIconWidth(), icon.getIconHeight(), rand.nextFloat());
			GL11.glPopMatrix();
		}
	}

}
