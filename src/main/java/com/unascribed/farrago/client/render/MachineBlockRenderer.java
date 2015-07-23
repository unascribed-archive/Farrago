package com.unascribed.farrago.client.render;

import com.unascribed.farrago.FarragoMod;
import com.unascribed.farrago.block.BlockMachine;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class MachineBlockRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		if (metadata != 3) {
			BlockMachine.inventory = true;
			block.setBlockBoundsForItemRender();
			renderer.renderBlockAsItem(block, metadata, 1.0f);
			BlockMachine.inventory = false;
		} else {
			renderRadio(renderer);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		block.setBlockBoundsBasedOnState(world, x, y, z);
		doRender(world.getBlockMetadata(x, y, z), block, renderer, x, y, z);
		return true;
	}
	
	private void doRender(int metadata, Block block, RenderBlocks renderer, int x, int y, int z) {
		switch (metadata) {
		case 3:
			renderRadio(renderer);
			break;
		default:
			renderer.setRenderBoundsFromBlock(block);
			renderer.renderStandardBlock(block, x, y, z);
			break;
		}
	}

	private void renderRadio(RenderBlocks renderer) {
		
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return FarragoMod.machineRenderType;
	}

}
