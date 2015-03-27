package com.gameminers.farrago.client.render;

import java.util.BitSet;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.block.BlockLightPipe;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class LightPipeBlockRenderer implements ISimpleBlockRenderingHandler {
	private BitSet sides = new BitSet(6);
	private BitSet connectSides = new BitSet(6);
	private Block block;
	private RenderBlocks renderer;
	private int x, y, z;
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		this.block = block;
		this.renderer = renderer;
		renderer.setOverrideBlockTexture(FarragoMod.LIGHT_PIPE.getJunctionIcon());
		BlockLightPipe.inventory = true;
		GL11.glPushMatrix();
		GL11.glScalef(1.5f, 1.5f, 1.5f);
		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);
		renderer.renderBlockAsItem(block, 1, 1.0f);
		GL11.glPopMatrix();
		BlockLightPipe.inventory = false;
		renderer.setOverrideBlockTexture(null);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		this.block = block;
		this.renderer = renderer;
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		boolean junction = BlockLightPipe.discover(world, x, y, z, sides, connectSides);
		
		if (junction) {
			renderer.setOverrideBlockTexture(FarragoMod.LIGHT_PIPE.getJunctionIcon());
			draw(0.3125f, 0.3125f, 0.3125f, 0.6875f, 0.6875f, 0.6875f);
			renderer.setOverrideBlockTexture(null);
		}
		if (sides.get(0)) {
			BlockLightPipe.rot = true;
			draw(0.375f, 0f, 0.375f, 0.625f, 0.625f, 0.625f);
			BlockLightPipe.rot = false;
		}
		if (sides.get(1)) {
			BlockLightPipe.rot = true;
			draw(0.375f, 0.375f, 0.375f, 0.625f, 1.0f, 0.625f);
			BlockLightPipe.rot = false;
		}
		if (sides.get(2)) {
			draw(0.375f, 0.375f, 0f, 0.625f, 0.625f, 0.625f);
		}
		if (sides.get(3)) {
			draw(0.375f, 0.375f, 0.375f, 0.625f, 0.625f, 1.0f);
		}
		if (sides.get(4)) {
			BlockLightPipe.rot2 = true;
			draw(0f, 0.375f, 0.375f, 0.625f, 0.625f, 0.625f);
			BlockLightPipe.rot2 = false;
		}
		if (sides.get(5)) {
			BlockLightPipe.rot2 = true;
			draw(0.375f, 0.375f, 0.375f, 1.0f, 0.625f, 0.625f);
			BlockLightPipe.rot2 = false;
		}
		if (connectSides.get(1)) {
			renderer.setOverrideBlockTexture(FarragoMod.LIGHT_PIPE.getConnectIcon());
			draw(0.1875f, 0.8125f, 0.1875f, 0.8125f, 1.0f, 0.8125f);
			renderer.setOverrideBlockTexture(null);
		}
		block.setBlockBoundsBasedOnState(world, x, y, z);
		/*renderer.setOverrideBlockTexture(Blocks.dirt.getBlockTextureFromSide(0));
		renderer.setRenderBoundsFromBlock(block);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setOverrideBlockTexture(null);*/
		return true;
	}
	
	private void draw(float x1, float y1, float z1, float x2, float y2, float z2) {
		block.setBlockBounds(x1, y1, z1, x2, y2, z2);
		renderer.setRenderBoundsFromBlock(block);
		renderer.renderStandardBlock(block, x, y, z);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return FarragoMod.lightPipeRenderType;
	}

}
