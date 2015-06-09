package com.unascribed.farrago.container;

import com.unascribed.farrago.tileentity.TileEntityCellFiller;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCellFiller extends Container {
	private TileEntityCellFiller tile;
	private int lastCookTime;

	public ContainerCellFiller(final InventoryPlayer player, TileEntityCellFiller tile) {
		this.tile = tile;
		addSlotToContainer(new Slot(tile, 0, 80, 54));
		addSlotToContainer(new Slot(tile, 1, 8, 12));
		addSlotToContainer(new Slot(tile, 2, 152, 12));
		addSlotToContainer(new SlotRestricted(tile, 3, 80, 20));
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
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int idx) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(idx);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (idx == 2) {
				if (!mergeItemStack(itemstack1, 3, 39, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (idx != 1 && idx != 0) {
				if (tile.isDust(itemstack1)) {
					if ((tile.getDustType(0) != null && tile.getDustType(0) != tile.getDustType(itemstack1)) || !mergeItemStack(itemstack1, 1, 1, false)) {
						return null;
					} else if ((tile.getDustType(1) != null && tile.getDustType(1) != tile.getDustType(itemstack1)) || !mergeItemStack(itemstack1, 2, 2, false)) {
						return null;
					}
				} else if (idx >= 3 && idx < 30) {
					if (!mergeItemStack(itemstack1, 30, 39, false)) {
						return null;
					}
				} else if (idx >= 30 && idx < 39
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
		if (itemstack != null && itemstack.stackSize == 0) return null;
		return itemstack;
	}
	
	public void addCraftingToCrafters(ICrafting p_75132_1_) {
		super.addCraftingToCrafters(p_75132_1_);
		p_75132_1_.sendProgressBarUpdate(this, 0, tile.cookTime);
	}

	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) crafters.get(i);

			if (lastCookTime != tile.cookTime) {
				icrafting
						.sendProgressBarUpdate(this, 0, tile.cookTime);
			}
		}

		lastCookTime = tile.cookTime;
	}

	public void updateProgressBar(int p_75137_1_, int p_75137_2_) {
		if (p_75137_1_ == 0) {
			tile.cookTime = p_75137_2_;
		}
	}

	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return tile.isUseableByPlayer(p_75145_1_);
	}
}
