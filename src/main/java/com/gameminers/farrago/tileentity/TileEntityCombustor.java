package com.gameminers.farrago.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCombustor extends TileEntityFurnace {
	private static final int[] slotsTop = new int[] { 0 };
	private static final int[] slotsBottom = new int[] { 2, 1 };
	private static final int[] slotsSides = new int[] { 1 };
	private ItemStack[] furnaceItemStacks = new ItemStack[3];
	private String field_145958_o;
	private boolean quick = false;

	@Override
	public int getSizeInventory() {
		return this.furnaceItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return this.furnaceItemStacks[p_70301_1_];
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		if (this.furnaceItemStacks[p_70298_1_] != null) {
			ItemStack itemstack;

			if (this.furnaceItemStacks[p_70298_1_].stackSize <= p_70298_2_) {
				itemstack = this.furnaceItemStacks[p_70298_1_];
				this.furnaceItemStacks[p_70298_1_] = null;
				return itemstack;
			} else {
				itemstack = this.furnaceItemStacks[p_70298_1_]
						.splitStack(p_70298_2_);

				if (this.furnaceItemStacks[p_70298_1_].stackSize == 0) {
					this.furnaceItemStacks[p_70298_1_] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		if (this.furnaceItemStacks[p_70304_1_] != null) {
			ItemStack itemstack = this.furnaceItemStacks[p_70304_1_];
			this.furnaceItemStacks[p_70304_1_] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		this.furnaceItemStacks[p_70299_1_] = p_70299_2_;

		if (p_70299_2_ != null
				&& p_70299_2_.stackSize > this.getInventoryStackLimit()) {
			p_70299_2_.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.field_145958_o : "container.combustor";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.field_145958_o != null && this.field_145958_o.length() > 0;
	}

	@Override
	public void func_145951_a(String p_145951_1_) {
		this.field_145958_o = p_145951_1_;
	}

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		NBTTagList nbttaglist = p_145839_1_.getTagList("Items", 10);
		this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.furnaceItemStacks.length) {
				this.furnaceItemStacks[b0] = ItemStack
						.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		this.furnaceBurnTime = p_145839_1_.getShort("BurnTime");
		this.furnaceCookTime = p_145839_1_.getShort("CookTime");
		this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
		quick = currentItemBurnTime > 20;
		if (p_145839_1_.hasKey("CustomName", 8)) {
			this.field_145958_o = p_145839_1_.getString("CustomName");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		p_145841_1_.setShort("BurnTime", (short) this.furnaceBurnTime);
		p_145841_1_.setShort("CookTime", (short) this.furnaceCookTime);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.furnaceItemStacks.length; ++i) {
			if (this.furnaceItemStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.furnaceItemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		p_145841_1_.setTag("Items", nbttaglist);

		if (this.hasCustomInventoryName()) {
			p_145841_1_.setString("CustomName", this.field_145958_o);
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int p_145953_1_) {
		return this.furnaceCookTime * p_145953_1_ / (quick ? 2 : 7);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int p_145955_1_) {
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 8;
		}

		return this.furnaceBurnTime * p_145955_1_ / this.currentItemBurnTime;
	}

	@Override
	public boolean isBurning() {
		return this.furnaceBurnTime > 0;
	}

	@Override
	public void updateEntity() {
		boolean flag = this.furnaceBurnTime > 0;
		boolean flag1 = false;

		if (this.furnaceBurnTime > 0) {
			--this.furnaceBurnTime;
		}

		//if (!this.worldObj.isRemote) {
			if (this.furnaceBurnTime != 0 || this.furnaceItemStacks[1] != null
					&& this.furnaceItemStacks[0] != null) {
				if (this.furnaceBurnTime == 0 && this.canSmelt()) {
					this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
					quick = currentItemBurnTime > 20;
					if (this.furnaceBurnTime > 0) {
						flag1 = true;

						if (this.furnaceItemStacks[1] != null) {
							--this.furnaceItemStacks[1].stackSize;

							if (this.furnaceItemStacks[1].stackSize == 0) {
								this.furnaceItemStacks[1] = furnaceItemStacks[1]
										.getItem().getContainerItem(
												furnaceItemStacks[1]);
							}
						}
					}
				}

				if (this.isBurning() && this.canSmelt()) {
					++this.furnaceCookTime;

					if (this.furnaceCookTime >= (quick ? 2 : 7)) {
						this.furnaceCookTime = 0;
						this.smeltItem();
						if (worldObj.rand.nextBoolean()) {
							smeltItem();
							if (worldObj.rand.nextBoolean()) {
								smeltItem();
							}
						}
						flag1 = true;
					}
				} else {
					this.furnaceCookTime = 0;
				}
			}

			if (flag != this.furnaceBurnTime > 0) {
				flag1 = true;
			}
		//}

		if (flag1) {
			this.markDirty();
		}
		if (worldObj.isRemote && isBurning()) {
			worldObj.spawnParticle("smoke", xCoord+0.5, yCoord+1, zCoord+0.5, 0, 0.05, 0);
		}
	}

	/**
	 * Returns true if the furnace can smelt an item, i.e. has a source item,
	 * destination stack isn't full, etc.
	 */
	private boolean canSmelt() {
		if (this.furnaceItemStacks[0] == null) {
			return false;
		} else {
			ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(
					this.furnaceItemStacks[0]);
			if (itemstack == null)
				return false;
			if (this.furnaceItemStacks[2] == null)
				return true;
			if (!this.furnaceItemStacks[2].isItemEqual(itemstack))
				return false;
			int result = furnaceItemStacks[2].stackSize + itemstack.stackSize;
			return result <= getInventoryStackLimit()
					&& result <= this.furnaceItemStacks[2].getMaxStackSize();
		}
	}

	/**
	 * Turn one item from the furnace source stack into the appropriate smelted
	 * item in the furnace result stack
	 */
	@Override
	public void smeltItem() {
		if (this.canSmelt()) {
			if (!worldObj.isRemote) {
				ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(
						this.furnaceItemStacks[0]);
	
				if (this.furnaceItemStacks[2] == null) {
					this.furnaceItemStacks[2] = itemstack.copy();
				} else if (this.furnaceItemStacks[2].getItem() == itemstack
						.getItem()) {
					this.furnaceItemStacks[2].stackSize += itemstack.stackSize;
				}
	
				--this.furnaceItemStacks[0].stackSize;
	
				if (this.furnaceItemStacks[0].stackSize <= 0) {
					this.furnaceItemStacks[0] = null;
				}
			}
			if (worldObj.isRemote) {
				EnumFacing p_82488_1_ = EnumFacing.values()[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)];
				int meta = p_82488_1_.getFrontOffsetX() + 1 + (p_82488_1_.getFrontOffsetZ() + 1) * 3;
				int j2 = meta % 3 - 1;
                int j1 = meta / 3 % 3 - 1;
                double d1 = (double)xCoord + (double)j2 * 0.6D + 0.5D;
                double d2 = (double)yCoord + 0.5D;
                double d9 = (double)zCoord + (double)j1 * 0.6D + 0.5D;

                for (int k2 = 0; k2 < 10; ++k2) {
                    double d11 = worldObj.rand.nextDouble() * 0.2D + 0.01D;
                    double d12 = d1 + (double)j2 * 0.01D + (worldObj.rand.nextDouble() - 0.5D) * (double)j1 * 0.5D;
                    double d4 = d2 + (worldObj.rand.nextDouble() - 0.5D) * 0.5D;
                    double d13 = d9 + (double)j1 * 0.01D + (worldObj.rand.nextDouble() - 0.5D) * (double)j2 * 0.5D;
                    double d5 = (double)j2 * d11 + worldObj.rand.nextGaussian() * 0.01D;
                    double d6 = -0.03D + worldObj.rand.nextGaussian() * 0.01D;
                    double d7 = (double)j1 * d11 + worldObj.rand.nextGaussian() * 0.01D;
                    worldObj.spawnParticle("explode", d12, d4, d13, d5, d6, d7);
                }
                for (int i = 0; i < 20; i++) {
                	worldObj.spawnParticle("smoke", (xCoord+0.5)+(worldObj.rand.nextGaussian()*0.1), (yCoord+1)+(worldObj.rand.nextGaussian()*0.1), (zCoord+0.5)+(worldObj.rand.nextGaussian()*0.1), 0, 0.05, 0);
                }
                worldObj.playSound(xCoord+0.5, yCoord+0.5, zCoord+0.5, "random.explode", 1.0f, 1.0f, false);
			}
		}
	}

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the
	 * furnace burning, or 0 if the item isn't fuel
	 */
	public static int getItemBurnTime(ItemStack p_145952_0_) {
		if (p_145952_0_ == null) {
			return 0;
		} else {
			Item item = p_145952_0_.getItem();
			if (item == Items.gunpowder)
				return 20;
			if (item == Item.getItemFromBlock(Blocks.tnt)) {
				return 40;
			}
			if (item == Item.getItemFromBlock(Blocks.bedrock)) {
				return 24000;
			}
			return 0;
		}
	}

	public static boolean isItemFuel(ItemStack p_145954_0_) {
		return getItemBurnTime(p_145954_0_) > 0;
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
}
