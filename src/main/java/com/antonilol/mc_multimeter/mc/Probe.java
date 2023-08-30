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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.antonilol.mc_multimeter.Utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

public class Probe {

	public static final String NAME_PREFIX = "probe";

	public static final int NAME_MAX_LENGTH = 20;

	private static ArrayList<Probe> probes = new ArrayList<Probe>();

	public static final HashMap<String, String> PROPS = new HashMap<String, String>();

	public static final HashMap<String, Class<? extends Object>> TYPES = new HashMap<String, Class<? extends Object>>();

	public static final Probe ALL = new Probe();

	static {

		PROPS.put("minecraft:redstone_wire", "power");
		PROPS.put("minecraft:redstone_torch", "lit");
		PROPS.put("minecraft:redstone_wall_torch", "lit");
		PROPS.put("minecraft:repeater", "powered");
		// comparator has nbt OutputSignal
		PROPS.put("minecraft:piston", "extended");
		PROPS.put("minecraft:sticky_piston", "extended");
		PROPS.put("minecraft:observer", "powered");
		PROPS.put("minecraft:hopper", "enabled");
		PROPS.put("minecraft:dispenser", "triggered");
		PROPS.put("minecraft:dropper", "triggered");
		PROPS.put("minecraft:lectern", "has_book"); // TODO how do i get output signal strength?
		PROPS.put("minecraft:target", "power");
		PROPS.put("minecraft:lever", "powered");
		PROPS.put("minecraft:lightning_rod", "powered");
		PROPS.put("minecraft:daylight_detector", "power");
		PROPS.put("minecraft:tripwire_hook", "powered");
		// trapped chest and tnt have no props or nbt to determine power in/output
		PROPS.put("minecraft:redstone_lamp", "lit");
		PROPS.put("minecraft:note_block", "powered");
		PROPS.put("minecraft:button", "powered"); // TODO buttons
		PROPS.put("minecraft:pressure_plate", "powered"); // TODO
		PROPS.put("minecraft:light_weighted_pressure_plate", "power");
		PROPS.put("minecraft:heavy_weighted_pressure_plate", "power");
		PROPS.put("minecraft:door", "powered"); // TODO
		PROPS.put("minecraft:trapdoor", "powered"); // TODO
		PROPS.put("minecraft:fence_gate", "powered"); // TODO

		TYPES.put("power", Integer.class);
		TYPES.put("lit", Boolean.class);
		TYPES.put("extended", Boolean.class);
		TYPES.put("powered", Boolean.class);
		TYPES.put("enabled", Boolean.class);
		TYPES.put("triggered", Boolean.class);
		TYPES.put("has_book", Boolean.class);
	}

	public static String findFreeName() {
		int n = 0;
		while (true) {
			boolean free = true;
			for (Probe p : getProbes()) {
				if ((NAME_PREFIX + n).equals(p.name)) {
					free = false;
					break;
				}
			}
			if (free) {
				return NAME_PREFIX + n;
			}
			n++;
			if (n == 0) {
				throw new RuntimeException("That's a lot of probes!");
			}
		}
	}

	private boolean enabled;
	private String name;
	private String worldName;
	private int x;
	private int y;
	private int z;

	public Probe(String name, Vec3i pos) {
		this.name = name;
		enabled = true;
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		worldName = Utils.getWorldName();
	}

	public Probe(Vec3i pos) {
		this(findFreeName(), pos);
	}

	public static Probe deserialize(String line) throws Exception {
		if (line == null) {
			throw new Exception();
		}

		String[] s = line.split(" ");

		if (s.length < 6) {
			throw new Exception();
		}

		String name = s[0];
		int x = Integer.parseInt(s[1]);
		int y = Integer.parseInt(s[2]);
		int z = Integer.parseInt(s[3]);
		String worldName = s[4];
		boolean enabled = s[5].equals("1");

		return new Probe(name, x, y, z, worldName, enabled);
	}

	private Probe(String name, int x, int y, int z, String worldName, boolean enabled) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldName = worldName;
		this.enabled = enabled;
	}

	private Probe() {
	}

	public static ArrayList<Probe> getProbes() {
		return probes;
	}

	public String serialize() {
		return name + " " + x + " " + y + " " + z + " " + worldName + " " + (enabled ? 1 : 0);
	}

	public String getName() {
		return name;
	}

	public BlockPos getBlockPos() {
		return new BlockPos(x, y, z);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean rename(String name) {
		if (name == null) {
			return false;
		}
		for (Probe p : getProbes()) {
			if (name.equals(p.name)) {
				return false;
			}
		}
		if (name.length() > 0 && name.length() <= NAME_MAX_LENGTH) {
			this.name = name;
			return true;
		}
		return false;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
	}

	public void setPos(Vec3i pos) {
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	private BlockPower getBlockPower() {
		MinecraftClient c = MinecraftClient.getInstance();
		ClientWorld w = c.world;

		if (w != null) {
			BlockState bs = w.getBlockState(getBlockPos());
			String block = Registry.BLOCK.getId(bs.getBlock()).toString();
			String blockName = block + " at " + x + " " + y + " " + z;
			if (bs.getBlock() instanceof ComparatorBlock) {
				int power = ((ComparatorBlockEntity) w.getBlockEntity(getBlockPos())).getOutputSignal();
				return new BlockPower(power, blockName, name);
			}

			for (Property<?> p : bs.getProperties()) {
				for (Entry<String, Class<? extends Object>> v : TYPES.entrySet()) {
					if (p.getName().equals(v.getKey())) {
						if (!p.getType().equals(v.getValue())) {
							throw new RuntimeException("Type mismatch: expected " + v.getValue().getName() + " but got "
								+ p.getType().getName() + ". prop=" + p.getName() + " block=" + block);
						}
						if (v.getValue().equals(Boolean.class)) {
							return new BlockPower((Boolean) bs.get(p), blockName, name);
						}
						if (v.getValue().equals(Integer.class)) {
							return new BlockPower((Integer) bs.get(p), blockName, name);
						}
						throw new RuntimeException(
							"Unknown type " + p.getType().getName() + ". prop=" + p.getName() + " block=" + block);
					}
				}
			}
			return new BlockPower(0, blockName, name);
		}
		return null;
	}

	public static BlockPower[] collectData() {
		ArrayList<BlockPower> l = new ArrayList<BlockPower>();
		String w = Utils.getWorldName();
		for (Probe p : getProbes()) {
			if (p.enabled && p.worldName.equals(w)) {
				BlockPower b = p.getBlockPower();
				if (b != null) {
					l.add(b);
				}
			}
		}
		return l.toArray(new BlockPower[0]);
	}

	public String getWorldName() {
		return worldName;
	}

	public static class BlockPower {
		public final int power;
		public final String blockName;
		public final String probeName;

		public BlockPower(int power, String blockName, String probeName) {
			this.power = power;
			this.blockName = blockName;
			this.probeName = probeName;
		}

		public BlockPower(boolean power, String blockName, String probeName) {
			this(power ? 15 : 0, blockName, probeName);
		}
	}
}
