package com.TominoCZ.Jetpack;

import java.io.File;

import com.TominoCZ.Jetpack.handler.ConfigHandler;
import com.TominoCZ.Jetpack.handler.PacketHandler;
import com.TominoCZ.Jetpack.item.ItemJetpack;
import com.TominoCZ.Jetpack.packet.ConfigurationPacket;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
@Mod(modid = Jetpack.MODID, name = Jetpack.NAME, version = Jetpack.VERSION)
public class Jetpack {
	@Instance(Jetpack.MODID)
	public static Jetpack instance;

	public final static String MODID = "jetpack";
	public final static String NAME = "Jetpack Mod";
	public final static String VERSION = "1.0 Beta";
	public static File config;

	public static int fuelID = 331;
	public static int fuelConsumptionRate = 2500;

	private File configDir;

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Jetpack.MODID);

	com.TominoCZ.Jetpack.handler.EventHandler eventhandler;

	public static ItemJetpack itemJetpack = new ItemJetpack();

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		configDir = evt.getModConfigurationDirectory();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		ForgeRegistries.ITEMS.register(itemJetpack);
		
		if (evt.getSide().isServer())
			INSTANCE.registerMessage(PacketHandler.class, IMessage.class, 0, Side.SERVER);
		if (evt.getSide().isClient())
			INSTANCE.registerMessage(PacketHandler.class, IMessage.class, 0, Side.CLIENT);

		eventhandler = new com.TominoCZ.Jetpack.handler.EventHandler(evt.getSide());

		MinecraftForge.EVENT_BUS.register(eventhandler);

		FMLCommonHandler.instance().bus().register(eventhandler);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		config = new File(configDir + "/Jetpack.cfg");

		ConfigHandler.init();

		if (evt.getSide().isServer()) {
			new Thread() {
				@Override
				public void run() {
					long lastModified = 0;

					while (true) {
						try {
							if (lastModified < config.lastModified()) {
								lastModified = config.lastModified();

								ConfigHandler.read();

								INSTANCE.sendToAll(new ConfigurationPacket(fuelID, fuelConsumptionRate));
							}
							Thread.sleep(1000);
						} catch (Exception e) {
						}
					}
				}
			}.start();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelRegistry(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(itemJetpack, 0, new ModelResourceLocation(itemJetpack.getRegistryName(), "inventory"));
	}
}