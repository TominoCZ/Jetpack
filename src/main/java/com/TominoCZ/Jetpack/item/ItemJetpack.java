package com.TominoCZ.Jetpack.item;

import java.util.List;

import com.TominoCZ.Jetpack.Jetpack;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemJetpack extends ItemArmor {
	int tick = 5000;

	public ItemJetpack() {
		super(ArmorMaterial.IRON, 0, EntityEquipmentSlot.CHEST);
		this.setNoRepair();
		this.setMaxDamage(0);
		this.setUnlocalizedName("Jetpack");
		this.setRegistryName(new ResourceLocation(Jetpack.MODID, "jetpack"));
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if (!player.capabilities.isCreativeMode) {
			Item item = (Item) REGISTRY.getObjectById(Jetpack.fuelID);

			boolean hasItem = playerHasItemInInventory(player, item);

			if (hasItem && player.capabilities.isFlying && !world.isRemote) {
				if (tick >= Jetpack.fuelConsumptionRate / 50) {
					tick = -1;

					ItemStack stack = new ItemStack(item);

					if (player.inventory.hasItemStack(stack)) {
						stack = player.inventory.getStackInSlot(player.inventory.getSlotFor(stack));

						stack.setCount(stack.getCount() - 1);
					}
				}
			}

			hasItem = playerHasItemInInventory(player, item);

			if ((player.capabilities.isFlying && !hasItem) || !hasItem) {
				int dist = 0;

				for (int i = 0; (int) player.posY - i > 0 && world
						.isAirBlock(new BlockPos((int) player.posX, (int) player.posY - i, (int) player.posZ)); i++) {
					dist = i;
				}

				if (player.capabilities.isFlying || player.capabilities.allowFlying)
					player.fallDistance = -dist;

				player.capabilities.isFlying = false;
				player.capabilities.allowFlying = false;
			} else if (hasItem)
				player.capabilities.allowFlying = true;
		}

		if (tick < 5000 && !world.isRemote)
			tick++;
	}

	@Override
	public String getArmorTexture(ItemStack itemstack, Entity entity, EntityEquipmentSlot slot, String type) {
		return Jetpack.MODID + ":textures/models/armor/jetpack_chest.png";
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, World w, List<String> tooltip, ITooltipFlag f) {
		tooltip.add("\u00a7aUses \u00a76"
				+ new ItemStack((Item) Item.REGISTRY.getObjectById(Jetpack.fuelID)).getDisplayName()
				+ "\u00a7a as fuel.");
		tooltip.add("\u00a7a" + "Uses \u00a761\u00a7a piece of fuel every \u00a76" + Jetpack.fuelConsumptionRate
				+ "ms\u00a7a.");
	}

	public boolean playerHasItemInInventory(EntityPlayer player, Item item) {
		for (ItemStack i : player.inventory.mainInventory) {
			if (i != null && i.getItem() == item)
				return true;
		}
		for (ItemStack i : player.inventory.armorInventory) {
			if (i != null && i.getItem() == item)
				return true;
		}

		return false;
	}
}
