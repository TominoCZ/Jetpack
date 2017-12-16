package com.TominoCZ.Jetpack.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ConfigurationPacket implements IMessage {
	public ConfigurationPacket() {
	}

	public int ID, fuelRate;

	public ConfigurationPacket(int ID, int fuelRate) {
		this.ID = ID;
		this.fuelRate = fuelRate;
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(ID);
		buf.writeInt(fuelRate);
	}

	public void fromBytes(ByteBuf buf) {
		ID = buf.readInt();
		fuelRate = buf.readInt();
	}
}