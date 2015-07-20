package com.unascribed.farrago.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class TileEntityInventoryMachine extends TileEntityMachine implements ISidedInventory {
	protected final String normalName;
	protected String customName;
	protected ItemStack[] slots;
	protected TileEntityInventoryMachine(String normalName, int inventorySize) {
		this.normalName = normalName;
		slots = new ItemStack[inventorySize];
	}
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (this.hasCustomInventoryName()) {
			tag.setString("CustomName", this.customName);
		}
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.slots.length; ++i) {
			if (this.slots[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.slots[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		tag.setTag("Items", nbttaglist);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("CustomName", 8)) {
			this.customName = tag.getString("CustomName");
		}
		NBTTagList nbttaglist = tag.getTagList("Items", 10);
		this.slots = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.slots.length) {
				this.slots[b0] = ItemStack
						.loadItemStackFromNBT(nbttagcompound1);
			}
		}
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
	
	public void setName(String name) {
		this.customName = name;
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : this.normalName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName != null && this.customName.length() > 0;
	}

}
