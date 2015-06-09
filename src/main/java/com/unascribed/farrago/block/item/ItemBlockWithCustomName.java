package com.unascribed.farrago.block.item;

import com.unascribed.farrago.block.NameDelegate;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockWithCustomName extends ItemBlockWithMetadata {
	private final NameDelegate block;
	public ItemBlockWithCustomName(Block block) {
		super(block, block);
		this.block = (NameDelegate) block;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack p_77667_1_) {
		return block.getUnlocalizedName(p_77667_1_.getItemDamage());
	}
}
