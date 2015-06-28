package com.unascribed.farrago.client.render;

import java.util.BitSet;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.unascribed.farrago.FarragoMod;
import com.unascribed.farrago.block.BlockLightPipe;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class LightPipeBlockRenderer implements ISimpleBlockRenderingHandler {
	private BitSet sides = new BitSet(6);
	private BitSet connectSides = new BitSet(6);
	private Block block;
	private RenderBlocks renderer;
	private int x, y, z;
	private int type, count;
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
		
		int meta = world.getBlockMetadata(x, y, z);
		type = -1;
		count = 0;
		if (meta != 0) {
			type = ((int) Math.ceil(meta/3f))-1;
			int base = type*3;
			count = meta-base;
		}
		
		if (junction) {
			renderer.setOverrideBlockTexture(FarragoMod.LIGHT_PIPE.getJunctionIcon());
			draw(0.3125f, 0.3125f, 0.3125f, 0.6875f, 0.6875f, 0.6875f, false);
			renderer.setOverrideBlockTexture(null);
		}
		if (sides.get(0)) {
			BlockLightPipe.rot = true;
			draw(0.375f, 0f, 0.375f, 0.625f, 0.625f, 0.625f, true);
			BlockLightPipe.rot = false;
		}
		if (sides.get(1)) {
			BlockLightPipe.rot = true;
			draw(0.375f, 0.375f, 0.375f, 0.625f, 1.0f, 0.625f, true);
			BlockLightPipe.rot = false;
		}
		if (sides.get(2)) {
			draw(0.375f, 0.375f, 0f, 0.625f, 0.625f, 0.625f, true);
		}
		if (sides.get(3)) {
			draw(0.375f, 0.375f, 0.375f, 0.625f, 0.625f, 1.0f, true);
		}
		if (sides.get(4)) {
			BlockLightPipe.rot2 = true;
			draw(0f, 0.375f, 0.375f, 0.625f, 0.625f, 0.625f, true);
			BlockLightPipe.rot2 = false;
		}
		if (sides.get(5)) {
			BlockLightPipe.rot2 = true;
			draw(0.375f, 0.375f, 0.375f, 1.0f, 0.625f, 0.625f, true);
			BlockLightPipe.rot2 = false;
		}
		if (connectSides.get(1)) {
			renderer.setOverrideBlockTexture(FarragoMod.LIGHT_PIPE.getConnectIcon());
			draw(0.1875f, 0.8125f, 0.1875f, 0.8125f, 1.0f, 0.8125f, false);
			renderer.setOverrideBlockTexture(null);
		}
		block.setBlockBoundsBasedOnState(world, x, y, z);
		/*renderer.setOverrideBlockTexture(Blocks.dirt.getBlockTextureFromSide(0));
		renderer.setRenderBoundsFromBlock(block);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setOverrideBlockTexture(null);*/
		return true;
	}
	
	private void draw(float x1, float y1, float z1, float x2, float y2, float z2, boolean inside) {
		block.setBlockBounds(x1, y1, z1, x2, y2, z2);
		renderer.setRenderBoundsFromBlock(block);
		renderer.renderStandardBlock(block, x, y, z);
		if (inside && type != -1) {
			float mod = 0.025f;
			if (count == 1) {
				mod = 0.1f;
			} else if (count == 2) {
				mod = 0.075f;
			} else if (count == 3) {
				mod = 0.05f;
			}
			if (x1 != 0) {
				x1 += mod;
			}
			if (y1 != 0) {
				y1 += mod;
			}
			if (z1 != 0) {
				z1 += mod;
			}
			if (x2 != 1) {
				x2 -= mod;
			}
			if (y2 != 1) {
				y2 -= mod;
			}
			if (z2 != 1) {
				z2 -= mod;
			}
			renderer.setOverrideBlockTexture(Blocks.quartz_block.getIcon(0, 0));
			block.setBlockBounds(x1, y1, z1, x2, y2, z2);
			renderer.setRenderBoundsFromBlock(block);
			if (type == 0) {
				renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, 2, 2, 2);
			} else if (type == 1) {
				renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, 2, 2, 0);
			} else if (type == 2) {
				renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, 2, 0, 2);
			} else if (type == 3) {
				renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, 0, 0, 2);
			}
			renderer.setOverrideBlockTexture(null);
		}
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
