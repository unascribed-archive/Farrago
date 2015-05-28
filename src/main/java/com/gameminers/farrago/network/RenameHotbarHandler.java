package com.gameminers.farrago.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.gameminers.farrago.FarragoMod;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class RenameHotbarHandler implements IMessageHandler<RenameHotbarMessage, IMessage> {

	@Override
	public IMessage onMessage(RenameHotbarMessage message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		if (player != null) {
			if (player.inventory.armorInventory[1] != null) {
				ItemStack legs = player.inventory.armorInventory[1];
				if (legs.getItem() == FarragoMod.UTILITY_BELT) {
					FarragoMod.UTILITY_BELT.setRowName(legs, FarragoMod.UTILITY_BELT.getCurrentRow(legs), message.getName());
				}
			}
		}
		return null;
	}

}
