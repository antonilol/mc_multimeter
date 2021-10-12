/*
 * Copyright (c) 2021 Antoni Spaanderman
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.antonilol.mc_multimeter.mc;

import java.io.IOException;

import com.antonilol.mc_multimeter.Utils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.MinecraftClient;

public class Main implements ClientModInitializer, EndTick {
	
	public static final boolean DEBUG = true;
	
	private static Process jframe;
	
	public static final String MOD_ID = "mc_multimeter"; // is this needed? i saw other people do it in tutorials
	
	public static final String VERSION = "0.0.1-76"; // updated by updateVersion script with sed :)
	
	public static boolean startJFrame() {
		if (Utils.running()) {
			return false;
		}
		try {
			jframe = Utils.startGraphics();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		Utils.setProcess(jframe);
		
		if (DEBUG) {
			DebugMessageReceiver.start();
		}
		
		return true;
	}
	
	public static boolean stopJFrame() {
		if (Utils.running()) {
			Utils.jFrameStdin.println(Utils.EXIT);
			return true;
		}
		return false;
	}

	@Override
	public void onEndTick(MinecraftClient c) {
		if (c.isPaused()) {
			return;
		}
		
		Probe.BlockPower[] data = Probe.collectData();
		
		if (data.length > 0 && Utils.jFrameStdin != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(Utils.DATA);
			
			for (Probe.BlockPower b : data) {
				sb.append(" ");
				sb.append(b.probeName);
				sb.append(",");
				sb.append(b.blockName.replace(' ', '+'));
				sb.append(",");
				sb.append(b.power);
			}
			
			Utils.jFrameStdin.println(sb.toString());
		}
	}

	@Override
	public void onInitializeClient() {
		Commands.register();
		
		ClientTickEvents.END_CLIENT_TICK.register(this);
	}
}

