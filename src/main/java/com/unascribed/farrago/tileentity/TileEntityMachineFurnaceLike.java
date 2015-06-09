package com.unascribed.farrago.tileentity;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityMachineFurnaceLike extends TileEntityMachine {
	protected static final int[] slotsTop = new int[] { 0 };
	protected static final int[] slotsBottom = new int[] { 2, 1 };
	protected static final int[] slotsSides = new int[] { 1 };
	protected String customName;
	public int burnTime;
	public int currentItemBurnTime;
	public int cookTime;

	protected TileEntityMachineFurnaceLike(String normalName, int inventorySize) {
		super(normalName, inventorySize);
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return p_94041_1_ == 2 ? false
				: (p_94041_1_ == 1 ? isItemFuel(p_94041_2_) : true);
	}

	public abstract boolean isItemFuel(ItemStack stack);

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return p_94128_1_ == 0 ? slotsBottom : (p_94128_1_ == 1 ? slotsTop
				: slotsSides);
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		return this.isItemValidForSlot(p_102007_1_, p_102007_2_);
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		return p_102008_3_ != 0 || p_102008_1_ != 1
				|| p_102008_2_.getItem() == Items.bucket;
	}

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);

		this.burnTime = p_145839_1_.getShort("BurnTime");
		this.cookTime = p_145839_1_.getShort("CookTime");
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		p_145841_1_.setShort("BurnTime", (short) this.burnTime);
		p_145841_1_.setShort("CookTime", (short) this.cookTime);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public abstract int getCookProgressScaled(int scale);

	public int getBurnTimeRemainingScaled(int p_145955_1_) {
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 8;
		}

		return this.burnTime * p_145955_1_ / this.currentItemBurnTime;
	}

	public boolean isBurning() {
		return this.burnTime > 0;
	}
}
