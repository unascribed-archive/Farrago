package com.gameminers.farrago.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.ArrayUtils;

import com.gameminers.farrago.FarragoMod;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class LockSlotHandler implements IMessageHandler<LockSlotMessage, IMessage> {

	@Override
	public IMessage onMessage(LockSlotMessage message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		if (player != null) {
			if (player.inventory.armorInventory[1] != null) {
				ItemStack legs = player.inventory.armorInventory[1];
				if (legs.getItem() == FarragoMod.UTILITY_BELT) {
					byte slot = message.getSlot();
					if (slot < 0 || slot > 8) {
						FarragoMod.log.warn(player.getCommandSenderName()+" sent an illegal value for LockSlot (slot number out of bounds), trying to crash the server?");
						return null;
					}
					int cur = FarragoMod.UTILITY_BELT.getCurrentRow(legs);
					byte[] slots = FarragoMod.UTILITY_BELT.getLockedSlots(legs, cur);
					if (ArrayUtils.contains(slots, slot)) {
						slots = ArrayUtils.removeElement(slots, slot);
						player.worldObj.playSoundAtEntity(player, "farrago:lock_slot", 0.4f, 1.5f);
					} else {
						slots = ArrayUtils.add(slots, slot);
						player.worldObj.playSoundAtEntity(player, "farrago:lock_slot", 0.4f, 1.0f);
					}
					FarragoMod.UTILITY_BELT.setLockedSlots(legs, cur, slots);
				}
			}
		}
		return null;
	}
}
