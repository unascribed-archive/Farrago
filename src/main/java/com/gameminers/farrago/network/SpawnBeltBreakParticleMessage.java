package com.gameminers.farrago.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class SpawnBeltBreakParticleMessage implements IMessage {
	private int entityId;
	public SpawnBeltBreakParticleMessage() {} // Required constructor for Forge
	public SpawnBeltBreakParticleMessage(int entityId) {
		this.entityId = entityId;
	}

	public int getEntityId() {
		return entityId;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		entityId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityId);
	}

}
