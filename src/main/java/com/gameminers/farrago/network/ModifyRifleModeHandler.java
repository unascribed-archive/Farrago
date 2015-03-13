package com.gameminers.farrago.network;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gameminers.farrago.FarragoMod;
import com.gameminers.farrago.enums.RifleMode;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ModifyRifleModeHandler implements IMessageHandler<ModifyRifleModeMessage, IMessage> {

	@Override
	public IMessage onMessage(ModifyRifleModeMessage message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		if (player.getHeldItem() != null) {
			if (player.getHeldItem().getItem() == FarragoMod.RIFLE) {
				int value = message.getValue();
				if (message.isSet()) {
					if (value < 0) {
						FarragoMod.log.warn(player.getCommandSenderName()+" sent an illegal value for ModifyRifleMode (message is absolute and value is less than zero), trying to crash the server?");
						value = 0;
					}
					if (value >= RifleMode.values().length) {
						FarragoMod.log.warn(player.getCommandSenderName()+" sent an illegal value for ModifyRifleMode (message is absolute and value is greater than the limit), trying to crash the server?");
						value = RifleMode.values().length-1;
					}
				} else {
					if ((Math.abs(value) != 1 || value == 0)) {
						FarragoMod.log.warn(player.getCommandSenderName()+" sent an illegal value for ModifyRifleMode (message is relative but absolute value is not 1), trying to crash the server?");
						if (value < 0) value = -1;
						if (value > 0) value = 1;
						if (value == 0) return null;
					}
				}
				FarragoMod.RIFLE.modifyMode(player, player.getHeldItem(), message.isSet(), value);
			}
		}
		return null;
	}

}
