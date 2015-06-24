package com.unascribed.farrago.network;

import net.minecraft.client.Minecraft;
import com.unascribed.farrago.FarragoMod;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SpawnBeltBreakParticleHandler implements IMessageHandler<SpawnBeltBreakParticleMessage, IMessage> {

	@Override
	public IMessage onMessage(SpawnBeltBreakParticleMessage message, MessageContext ctx) {
		FarragoMod.proxy.spawnBeltBreakParticle(Minecraft.getMinecraft().theWorld.getEntityByID(message.getEntityId()));
		return null;
	}
}
