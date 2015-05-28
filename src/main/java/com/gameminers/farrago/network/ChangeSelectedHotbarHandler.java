package com.gameminers.farrago.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.gameminers.farrago.FarragoMod;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ChangeSelectedHotbarHandler implements IMessageHandler<ChangeSelectedHotbarMessage, IMessage> {

	@Override
	public IMessage onMessage(ChangeSelectedHotbarMessage message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		if (player != null) {
			if (player.inventory.armorInventory[1] != null) {
				ItemStack legs = player.inventory.armorInventory[1];
				if (legs.getItem() == FarragoMod.UTILITY_BELT) {
					int hotbarSize = InventoryPlayer.getHotbarSize();
					ItemStack[] hotbar = new ItemStack[hotbarSize];
					System.arraycopy(player.inventory.mainInventory, 0, hotbar, 0, hotbarSize);
					int idx = FarragoMod.UTILITY_BELT.getCurrentRow(legs);
					int max = FarragoMod.UTILITY_BELT.getExtraRows(legs);
					FarragoMod.UTILITY_BELT.setRowContents(legs, idx, hotbar);
					int nextIdx = idx + (message.getDirection() ? 1 : -1);
					if (nextIdx < 0) {
						nextIdx = max;
					} else if (nextIdx > max) {
						nextIdx = 0;
					}
					player.worldObj.playSoundAtEntity(player, "farrago:switch_hotbar", 0.4f, 0.75f);
					System.arraycopy(FarragoMod.UTILITY_BELT.getRowContents(legs, nextIdx), 0, player.inventory.mainInventory, 0, hotbarSize);
					FarragoMod.UTILITY_BELT.deleteRow(legs, nextIdx);
					FarragoMod.UTILITY_BELT.setCurrentRow(legs, nextIdx);
				}
			}
		}
		return null;
	}

}
