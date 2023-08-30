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

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class DebugMessageReceiver implements Runnable {

	private MinecraftClient client;

	@Override
	public void run() {
		client = MinecraftClient.getInstance();

		while (true) {
			String line;
			try {
				line = Utils.jFrameStdout.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}

			if (line == null) {
				mcChat("DEBUG: stream EOF");
				break;
			}

			mcChat("DEBUG: " + line);
		}
	}

	private void mcChat(String msg) {
		if (client.player != null && Main.DEBUG) {
			client.inGameHud.getChatHud().addMessage(Text.of(msg));
		}
	}

	public static void start() {
		new Thread(new DebugMessageReceiver()).start();
	}
}
