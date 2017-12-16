package com.TominoCZ.Jetpack.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import com.TominoCZ.Jetpack.Jetpack;

import net.minecraft.item.Item;
import scala.reflect.io.Directory;

public class ConfigHandler {
	static FileInputStream fis;
	static InputStreamReader isr;
	static BufferedReader br;

	static File f;

	public static void init() {
		try {
			f = Jetpack.config;

			if (!f.exists()) {
				if (!Directory.apply(f.getParent()).exists())
					Directory.apply(f.getParent()).createDirectory(true, false);

				f.createNewFile();

				write();
			}

			read();

			write();

			closeStreams();
		} catch (IOException e) {
			closeStreams();

			write();
		}
	}

	public static void write() {
		try {
			check();

			PrintWriter writer = new PrintWriter(f.getPath(), "UTF-8");
			writer.println("fuelID=" + Jetpack.fuelID);
			writer.print("fuelConsumptionRate=" + Jetpack.fuelConsumptionRate);
			writer.close();
		} catch (Exception e) {
			closeStreams();

			if (!f.exists()) {
				if (!Directory.apply(f.getParent()).exists())
					Directory.apply(f.getParent()).createDirectory(true, false);

				try {
					f.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			write();
		}
	}

	public static void read() {
		try {
			fis = new FileInputStream(f);
			isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			br = new BufferedReader(isr);

			String line;

			while ((line = br.readLine()) != null) {
				if (line.contains("fuelID="))
					Jetpack.fuelID = Integer.valueOf(line.replaceAll(" ", "").replace("fuelID=", ""));
				else if (line.contains("fuelConsumptionRate="))
					Jetpack.fuelConsumptionRate = Integer
							.valueOf(line.replaceAll(" ", "").replace("fuelConsumptionRate=", ""));
			}

			closeStreams();

			check();
		} catch (Exception e) {
			closeStreams();

			check();

			write();
		}
	}

	static void closeStreams() {
		try {
			br.close();
			isr.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void check() {
		boolean fixed = false;

		if (Jetpack.fuelConsumptionRate < 500) {
			Jetpack.fuelConsumptionRate = 500;
			fixed = true;
		}
		if (Jetpack.fuelConsumptionRate > 5000) {
			Jetpack.fuelConsumptionRate = 5000;
			fixed = true;
		}
		if (Item.getItemById(Jetpack.fuelID) == null || Jetpack.fuelID == Item.getIdFromItem(Jetpack.itemJetpack)) {
			Jetpack.fuelID = 331;
			fixed = true;
		}

		if (fixed)
			ConfigHandler.write();
	}
}
