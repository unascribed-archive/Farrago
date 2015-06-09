package com.unascribed.farrago.tileentity;

import com.unascribed.farrago.FarragoMod;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityCombustor extends TileEntityMachineFurnaceLike implements ISidedInventory {
	public TileEntityCombustor() {
		super("container.combustor", 3);
	}

	private boolean quick = false;
	
	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		this.currentItemBurnTime = getItemBurnTime(this.slots[1]);
		quick = currentItemBurnTime > 20;
	}

	@Override
	public void updateEntity() {
		boolean flag = this.burnTime > 0;
		boolean flag1 = false;

		if (this.burnTime > 0) {
			--this.burnTime;
		}

		//if (!this.worldObj.isRemote) {
			if (this.burnTime != 0 || this.slots[1] != null
					&& this.slots[0] != null) {
				if (this.burnTime == 0 && this.canSmelt()) {
					this.currentItemBurnTime = this.burnTime = getItemBurnTime(this.slots[1]);
					quick = currentItemBurnTime > 20;
					if (this.burnTime > 0) {
						flag1 = true;

						if (this.slots[1] != null) {
							--this.slots[1].stackSize;

							if (this.slots[1].stackSize == 0) {
								this.slots[1] = slots[1]
										.getItem().getContainerItem(
												slots[1]);
							}
						}
					}
				}

				if (this.isBurning() && this.canSmelt()) {
					++this.cookTime;

					if (this.cookTime >= (quick ? 2 : 7)) {
						this.cookTime = 0;
						this.smeltItem();
						if (FarragoMod.config.getBoolean("machines.combustor.enableMultiSmelt") && worldObj.rand.nextBoolean()) {
							smeltItem();
							if (worldObj.rand.nextBoolean()) {
								smeltItem();
							}
						}
						flag1 = true;
					}
				} else {
					this.cookTime = 0;
				}
			}

			if (flag != this.burnTime > 0) {
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

	private boolean canSmelt() {
		if (this.slots[0] == null) {
			return false;
		} else {
			ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(
					this.slots[0]);
			if (itemstack == null)
				return false;
			if (this.slots[2] == null)
				return true;
			if (!this.slots[2].isItemEqual(itemstack))
				return false;
			int result = slots[2].stackSize + itemstack.stackSize;
			return result <= getInventoryStackLimit()
					&& result <= this.slots[2].getMaxStackSize();
		}
	}

	public void smeltItem() {
		if (this.canSmelt()) {
			if (!worldObj.isRemote) {
				ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(
						this.slots[0]);
	
				if (this.slots[2] == null) {
					this.slots[2] = itemstack.copy();
				} else if (this.slots[2].getItem() == itemstack
						.getItem()) {
					this.slots[2].stackSize += itemstack.stackSize;
				}
	
				--this.slots[0].stackSize;
	
				if (this.slots[0].stackSize <= 0) {
					this.slots[0] = null;
				}
			}
			if (worldObj.isRemote) {
				EnumFacing p_82488_1_ = EnumFacing.values()[getDirection()];
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

	public boolean isItemFuel(ItemStack p_145954_0_) {
		return getItemBurnTime(p_145954_0_) > 0;
	}

	@Override
	public int getCookProgressScaled(int scale) {
		return this.cookTime * scale / (quick ? 2 : 7);
	}
	
	@Override
	public boolean isOn() {
		return false;
	}
}
