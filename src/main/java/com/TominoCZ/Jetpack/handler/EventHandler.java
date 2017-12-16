package com.TominoCZ.Jetpack.handler;

import com.TominoCZ.Jetpack.Jetpack;
import com.TominoCZ.Jetpack.packet.ConfigurationPacket;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class EventHandler {
	Side side;

	public EventHandler(Side side) {
		this.side = side;
	}

	@SubscribeEvent
	public void onPlayerBreakSpeedChanged(PlayerEvent.BreakSpeed e) {
		if (e.getEntity() instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer) e.getEntity();

			if (!p.onGround && !p.capabilities.isCreativeMode && p.capabilities.isFlying == true && Jetpack.itemJetpack
					.playerHasItemInInventory(p, (Item) Item.REGISTRY.getObjectById(Jetpack.fuelID)))
				e.setNewSpeed(e.getNewSpeed() * 5);
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		try {
			if (!e.player.capabilities.isCreativeMode) {
				ItemStack is = e.player.inventory.armorItemInSlot(2);

				if (is == null || !is.getItem().getUnlocalizedName().equals("item.Jetpack")
						&& (e.player.capabilities.allowFlying || e.player.capabilities.isFlying)) {
					e.player.capabilities.allowFlying = false;
					e.player.capabilities.isFlying = false;
				}
			}
		} catch (Exception ex) {

		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent e) {
		if (e.getEntity() instanceof EntityPlayer) {
			if (side.isClient() && e.getEntity() instanceof EntityPlayerSP)
				ConfigHandler.read();
			else if (side.isServer() && e.getEntity() instanceof EntityPlayerMP)
				Jetpack.INSTANCE.sendTo(new ConfigurationPacket(Jetpack.fuelID, Jetpack.fuelConsumptionRate),
						(EntityPlayerMP) e.getEntity());
		}
	}
}