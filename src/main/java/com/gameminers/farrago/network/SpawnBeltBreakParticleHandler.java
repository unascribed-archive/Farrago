package com.gameminers.farrago.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import com.gameminers.farrago.client.particle.EntityBrokenBeltFX;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SpawnBeltBreakParticleHandler implements IMessageHandler<SpawnBeltBreakParticleMessage, IMessage> {

	@Override
	public IMessage onMessage(SpawnBeltBreakParticleMessage message, MessageContext ctx) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(message.getEntityId());
		Minecraft.getMinecraft().effectRenderer.addEffect(new EntityBrokenBeltFX(Minecraft.getMinecraft().getTextureManager(), e));
		return null;
	}
}
