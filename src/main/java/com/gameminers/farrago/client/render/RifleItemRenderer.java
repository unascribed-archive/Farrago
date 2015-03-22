package com.gameminers.farrago.client.render;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.proxy.ClientProxy;

public class RifleItemRenderer implements IItemRenderer {
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return item.getItem() == FarragoMod.RIFLE && (type == ItemRenderType.EQUIPPED_FIRST_PERSON);
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		EntityClientPlayerMP player = ((EntityClientPlayerMP) data[1]);
		float scopeMult = Math.min((FarragoMod.scopeTicks+ClientProxy.timer.renderPartialTicks)/5f, 1.0f);
		GL11.glTranslatef(1.0f, 0f, 0f);
		GL11.glRotatef(180F, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(180F, 0.0f, 0.0f, 1.0f);
		if (FarragoMod.scoped) {
			if (player.isUsingItem()) {
				GL11.glTranslatef(0f, scopeMult*-0.5f, scopeMult*0.4f);
				GL11.glRotatef(5F, 0.0f, scopeMult, 0.0f);
				GL11.glRotatef(10F, scopeMult, 0.0f, 0.0f);
			} else {
				GL11.glTranslatef(0f, 0f, scopeMult);
				GL11.glRotatef(-2F, scopeMult, scopeMult, 0.0f);
			}
		}
		IIcon icon = item.getItem().getIcon(item, 0, player, player.getItemInUse(), player.getItemInUseCount());
		ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625f);
	}

}
