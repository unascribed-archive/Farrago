package com.gameminers.farrago.block.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

import com.gameminers.farrago.block.BlockResource;

public class ItemBlockResource extends ItemBlockWithMetadata {
	private final BlockResource block;
	public ItemBlockResource(Block b, BlockResource block) {
		super(b, block);
		this.block = block;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack p_77667_1_) {
		return block.getUnlocalizedName(p_77667_1_.getItemDamage());
	}
}
