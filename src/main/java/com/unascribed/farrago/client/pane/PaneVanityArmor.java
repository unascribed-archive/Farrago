package com.unascribed.farrago.client.pane;

import gminers.glasspane.GlassPane;
import gminers.glasspane.PaneBB;
import gminers.glasspane.component.PaneImage;
import gminers.kitchensink.Rendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class PaneVanityArmor extends GlassPane {
	private final ResourceLocation vanitySlots = new ResourceLocation("farrago", "textures/gui/container/inventory_slots.png");
	private final ResourceLocation orbGlow = new ResourceLocation("farrago", "textures/misc/orb_mask.png");
	private final ResourceLocation emptyHelmet = new ResourceLocation("textures/items/empty_armor_slot_helmet.png");
	private final ResourceLocation emptyChestplate = new ResourceLocation("textures/items/empty_armor_slot_chestplate.png");
	private final ResourceLocation emptyLeggings = new ResourceLocation("textures/items/empty_armor_slot_leggings.png");
	private final ResourceLocation emptyBoots = new ResourceLocation("textures/items/empty_armor_slot_boots.png");
	private final PaneBB goat = new PaneBB();
	@Override
	protected void doRender(int arg0, int arg1, float arg2) {
		super.doRender(arg0, arg1, arg2);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int x = (getWidth()/2)-88-42;
		final int y = (getHeight()/2)-83;
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		PaneImage.render(vanitySlots, x, y, 0, 0, 46, 86, 256, 256, 0xFFFFFF, 1.0f, false);
		PaneImage.render(emptyHelmet, x+26, y+8, 0, 0, 16, 16, 256, 256, 0xFFFFFF, 1.0f, true);
		PaneImage.render(emptyChestplate, x+26, y+26, 0, 0, 16, 16, 256, 256, 0xFFFFFF, 1.0f, true);
		PaneImage.render(emptyLeggings, x+26, y+44, 0, 0, 16, 16, 256, 256, 0xFFFFFF, 1.0f, true);
		PaneImage.render(emptyBoots, x+26, y+62, 0, 0, 16, 16, 256, 256, 0xFFFFFF, 1.0f, true);
		ItemStack cursor = player.inventory.getItemStack();
		goat.setX(x-16);
		goat.setY(y-16);
		goat.setWidth(46+32);
		goat.setHeight(86+32);
		if (cursor != null && goat.withinBounds(arg0, arg1)) {
			GL11.glPushMatrix();
			RenderItem.getInstance().zLevel+=100;
			RenderItem.getInstance().renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, cursor, arg0-8, arg1-8);
			RenderItem.getInstance().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, cursor, arg0-8, arg1-8);
			RenderItem.getInstance().zLevel-=100;
			GL11.glPopMatrix();
		}
		goat.setWidth(16);
		goat.setHeight(16);
		
		goat.setX(x+26);
		goat.setY(y+8);
		drawHoverIfBounds(arg0, arg1, false);
		
		goat.setX(x+26);
		goat.setY(y+26);
		drawHoverIfBounds(arg0, arg1, false);
		
		goat.setX(x+26);
		goat.setY(y+44);
		drawHoverIfBounds(arg0, arg1, false);
		
		goat.setX(x+26);
		goat.setY(y+62);
		drawHoverIfBounds(arg0, arg1, false);
		
		
		goat.setX(x+6);
		goat.setY(y+7);
		drawHoverIfBounds(arg0, arg1, true);
		
		goat.setX(x+6);
		goat.setY(y+25);
		drawHoverIfBounds(arg0, arg1, true);
		
		goat.setX(x+6);
		goat.setY(y+43);
		drawHoverIfBounds(arg0, arg1, true);
		
		goat.setX(x+6);
		goat.setY(y+61);
		drawHoverIfBounds(arg0, arg1, true);
		
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	private void drawHoverIfBounds(int arg0, int arg1, boolean orb) {
		if (goat.withinBounds(arg0, arg1)) {
			GL11.glColorMask(true, true, true, false);
			if (orb) {
				PaneImage.render(orbGlow, goat.getX(), goat.getY(), 0, 0, 16, 16, 256, 256, 0xFFFFFF, 0.5f, false);
			} else {
				Rendering.drawGradientRect(goat.getX(), goat.getY(), goat.getX() + goat.getWidth(), goat.getY() + goat.getHeight(), 0x80FFFFFF, 0x80FFFFFF);
			}
            GL11.glColorMask(true, true, true, true);
		}
	}
}
