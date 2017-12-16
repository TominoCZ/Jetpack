package com.TominoCZ.Jetpack.handler;

import com.TominoCZ.Jetpack.Jetpack;
import com.TominoCZ.Jetpack.packet.ConfigurationPacket;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHandler implements IMessageHandler<IMessage, IMessage> {
	// Do note that the default constructor is required, but implicitly defined
	// in this case

	@Override
	public IMessage onMessage(IMessage message, MessageContext ctx) {
		if (message instanceof PacketHandler) {
			ConfigurationPacket packet = (ConfigurationPacket) message;
			Jetpack.fuelID = packet.ID;
			Jetpack.fuelConsumptionRate = packet.fuelRate;
		}
		return null;
	}
}