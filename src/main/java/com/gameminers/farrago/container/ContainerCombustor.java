package com.gameminers.farrago.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import com.gameminers.farrago.tileentity.TileEntityCombustor;

public class ContainerCombustor extends Container {
	private TileEntityCombustor tileCombustor;
	private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

	public ContainerCombustor(InventoryPlayer player,
			TileEntityCombustor tileCombustor) {
		this.tileCombustor = tileCombustor;
		addSlotToContainer(new Slot(tileCombustor, 0, 56, 17));
		addSlotToContainer(new Slot(tileCombustor, 1, 56, 53));
		addSlotToContainer(new SlotFurnace(player.player, tileCombustor,
				2, 116, 35));
		int i;

		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player, j + i * 9 + 9,
						8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(p_82846_2_);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (p_82846_2_ == 2) {
				if (!mergeItemStack(itemstack1, 3, 39, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (p_82846_2_ != 1 && p_82846_2_ != 0) {
				if (FurnaceRecipes.smelting().getSmeltingResult(itemstack1) != null) {
					if (!mergeItemStack(itemstack1, 0, 1, false)) {
						return null;
					}
				} else if (tileCombustor.isItemFuel(itemstack1)) {
					if (!mergeItemStack(itemstack1, 1, 2, false)) {
						return null;
					}
				} else if (p_82846_2_ >= 3 && p_82846_2_ < 30) {
					if (!mergeItemStack(itemstack1, 30, 39, false)) {
						return null;
					}
				} else if (p_82846_2_ >= 30 && p_82846_2_ < 39
						&& !mergeItemStack(itemstack1, 3, 30, false)) {
					return null;
				}
			} else if (!mergeItemStack(itemstack1, 3, 39, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(p_82846_1_, itemstack1);
		}

		return itemstack;
	}
	
	public void addCraftingToCrafters(ICrafting p_75132_1_) {
		super.addCraftingToCrafters(p_75132_1_);
		p_75132_1_.sendProgressBarUpdate(this, 0, tileCombustor.cookTime);
		p_75132_1_.sendProgressBarUpdate(this, 1, tileCombustor.burnTime);
		p_75132_1_.sendProgressBarUpdate(this, 2,
				tileCombustor.currentItemBurnTime);
	}

	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) crafters.get(i);

			if (lastCookTime != tileCombustor.cookTime) {
				icrafting
						.sendProgressBarUpdate(this, 0, tileCombustor.cookTime);
			}

			if (lastBurnTime != tileCombustor.burnTime) {
				icrafting
						.sendProgressBarUpdate(this, 1, tileCombustor.burnTime);
			}

			if (lastItemBurnTime != tileCombustor.currentItemBurnTime) {
				icrafting.sendProgressBarUpdate(this, 2,
						tileCombustor.currentItemBurnTime);
			}
		}

		lastCookTime = tileCombustor.cookTime;
		lastBurnTime = tileCombustor.burnTime;
		lastItemBurnTime = tileCombustor.currentItemBurnTime;
	}

	public void updateProgressBar(int p_75137_1_, int p_75137_2_) {
		if (p_75137_1_ == 0) {
			tileCombustor.cookTime = p_75137_2_;
		}

		if (p_75137_1_ == 1) {
			tileCombustor.burnTime = p_75137_2_;
		}

		if (p_75137_1_ == 2) {
			tileCombustor.currentItemBurnTime = p_75137_2_;
		}
	}

	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return tileCombustor.isUseableByPlayer(p_75145_1_);
	}
}
