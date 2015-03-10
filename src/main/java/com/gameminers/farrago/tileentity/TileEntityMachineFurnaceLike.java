package com.gameminers.farrago.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class TileEntityMachineFurnaceLike extends TileEntityMachine {
	protected static final int[] slotsTop = new int[] { 0 };
	protected static final int[] slotsBottom = new int[] { 2, 1 };
	protected static final int[] slotsSides = new int[] { 1 };
	protected ItemStack[] slots = new ItemStack[3];
	protected String customName;
	public int burnTime;
	public int currentItemBurnTime;
	public int cookTime;

	protected TileEntityMachineFurnaceLike(String normalName) {
		super(normalName);
	}

	@Override
	public int getSizeInventory() {
		return this.slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return this.slots[p_70301_1_];
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		if (this.slots[p_70298_1_] != null) {
			ItemStack itemstack;

			if (this.slots[p_70298_1_].stackSize <= p_70298_2_) {
				itemstack = this.slots[p_70298_1_];
				this.slots[p_70298_1_] = null;
				return itemstack;
			} else {
				itemstack = this.slots[p_70298_1_]
						.splitStack(p_70298_2_);

				if (this.slots[p_70298_1_].stackSize == 0) {
					this.slots[p_70298_1_] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		if (this.slots[p_70304_1_] != null) {
			ItemStack itemstack = this.slots[p_70304_1_];
			this.slots[p_70304_1_] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		this.slots[p_70299_1_] = p_70299_2_;

		if (p_70299_2_ != null
				&& p_70299_2_.stackSize > this.getInventoryStackLimit()) {
			p_70299_2_.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord,
				this.zCoord) != this ? false
						: p_70300_1_.getDistanceSq(this.xCoord + 0.5D,
								this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
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
		NBTTagList nbttaglist = p_145839_1_.getTagList("Items", 10);
		this.slots = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.slots.length) {
				this.slots[b0] = ItemStack
						.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		this.burnTime = p_145839_1_.getShort("BurnTime");
		this.cookTime = p_145839_1_.getShort("CookTime");
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		p_145841_1_.setShort("BurnTime", (short) this.burnTime);
		p_145841_1_.setShort("CookTime", (short) this.cookTime);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.slots.length; ++i) {
			if (this.slots[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.slots[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		p_145841_1_.setTag("Items", nbttaglist);
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
