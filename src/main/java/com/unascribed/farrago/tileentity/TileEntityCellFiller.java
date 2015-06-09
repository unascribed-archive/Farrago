package com.unascribed.farrago.tileentity;

import com.unascribed.farrago.enums.DustType;

import net.minecraft.item.ItemStack;

public class TileEntityCellFiller extends TileEntityMachine {
	public int cookTime;
	
	public TileEntityCellFiller() {
		super("container.cell_filler", 4);
	}


	public boolean isDust(ItemStack itemstack1) {
		// TODO Auto-generated method stub
		return false;
	}

	public DustType getDustType(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public DustType getDustType(ItemStack itemstack1) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getBurnTimeRemainingScaled(int i) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public int getInventoryStackLimit() {
		return 64;
	}


	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isOn() {
		// TODO Auto-generated method stub
		return false;
	}

}
