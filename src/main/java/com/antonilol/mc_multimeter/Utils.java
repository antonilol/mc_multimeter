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

package com.antonilol.mc_multimeter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.MinecraftClient;

public class Utils implements Runnable {
	public static final String DATA = "d";
	public static final String EXIT = "e";
	public static final String LINE_GRAPH = "l";
	public static final String BAR_GRAPH = "b";
	public static final String REMOVE = "r";
	public static final String REMOVEALL = "a";

	private static final String thisJarFile;
	private static final String javaExecutable;

	private static BufferedReader jFrameStderr = null;
	public static PrintWriter jFrameStdin = null;
	public static BufferedReader jFrameStdout = null;

	private static Process process = null;

	static {
		String jar_ = Utils.escapeShellDoubleQuoteString(
			URLDecoder.decode(
				Utils.class
					.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.getPath(),
				StandardCharsets.UTF_8),
			false);

		if (isWindows()) {
			thisJarFile = jar_.substring(1).replace('/', '\\');
		} else {
			thisJarFile = jar_;
		}

		javaExecutable = Utils.escapeShellDoubleQuoteString(
			System.getProperty("java.home") +
				(Utils.isWindows() ? "\\bin\\java.exe" : "/bin/java"),
			false);
	}

	private static String escape(String s, String escaper, List<String> targets) {
		// https://stackoverflow.com/a/63721259/13800918
		s = s.replace(escaper, escaper + escaper);
		for (String t : targets) {
			s = s.replace(t, escaper + t);
		}
		return s;
	}

	public static String escapeShellDoubleQuoteString(String s, boolean addOuterQuote) {
		// https://stackoverflow.com/a/63721259/13800918
		final List<String> targets = Arrays.asList("\"", "$", "`");
		String escape = escape(s, "\\", targets);
		return addOuterQuote ? '"' + escape + '"' : escape;
	}

	public static String getWorldName() {
		MinecraftClient c = MinecraftClient.getInstance();
		if (c.world == null) {
			return null;
		}
		return c.world.getRegistryKey().getValue().toString();
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	public static int map(int n, int a, int b, int x, int y) {
		final double v = (n - a) / (double) (b - a);
		return (int) ((1 - v) * x + v * y);
	}

	public static boolean running() {
		return process == null ? false : process.isAlive();
	}

	public static void setProcess(Process process) {
		jFrameStdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
		jFrameStdin = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);

		jFrameStderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		new Thread(new Utils()).start();

		Utils.process = process;
	}

	public static Process startGraphics() throws IOException {
		return Runtime.getRuntime().exec(javaExecutable + " -jar " + thisJarFile + " arg");
	}

	@Override
	public void run() {
		while (true) {
			try {
				String line = jFrameStderr.readLine();
				if (line != null) {
					System.err.println("Error message from JFrame: " + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
