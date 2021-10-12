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

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import com.antonilol.mc_multimeter.Utils;
import com.antonilol.mc_multimeter.mc.command.ClientBlockPosArgumentType;
import com.antonilol.mc_multimeter.mc.command.ProbeArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class Commands {
	public static void register() {
		
		if (Main.DEBUG) {
			register(false,
				literal("debug").then(
					literal("send").then(
						argument("message", StringArgumentType.greedyString())
						.executes(c -> {
							if (Utils.running()) {
								String msg = StringArgumentType.getString(c, "message");
								Utils.jFrameStdin.println(msg);
								c.getSource().sendFeedback(new TranslatableText("commands.mc_multimeter.debug.send.succes"));
								return Command.SINGLE_SUCCESS;
							}
							c.getSource().sendFeedback(new TranslatableText("commands.mc_multimeter.debug.send.fail"));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
			);
		}
		
		final Command<FabricClientCommandSource> remove = c -> {
			Probe p = ProbeArgumentType.getProbe(c, "name");
			if (p == Probe.ALL) {
				Probe.getProbes().clear();
				Utils.jFrameStdin.println(Utils.REMOVEALL);
				c.getSource().sendFeedback(new TranslatableText("commands.mc_multimeter.probe.removeall"));
			} else {
				Probe.getProbes().remove(p);
				Utils.jFrameStdin.println(Utils.REMOVE + " " + p.getName());
				
				c.getSource().sendFeedback(new TranslatableText(
					"commands.mc_multimeter.probe.remove",
					p.getName(),
					p.getX(),
					p.getY(),
					p.getZ()
				));
			}
			return Command.SINGLE_SUCCESS;
		};
		
		register(true,
			literal("probe").then(
				literal("add").then(
					argument("position", ClientBlockPosArgumentType.blockPos())
					.executes(c -> {
						Probe p = new Probe(ClientBlockPosArgumentType.getBlockPos(c, "position"));
						Probe.getProbes().add(p);
						if (Probe.getProbes().size() == 1 && !Utils.running()) {
							Main.startJFrame();
						}
						c.getSource().sendFeedback(new TranslatableText(
							"commands.mc_multimeter.probe.add",
							p.getName(),
							p.getX(),
							p.getY(),
							p.getZ()
						));
						return Command.SINGLE_SUCCESS;
					})
				)
			).then(
				literal("remove").then(
					argument("name", new ProbeArgumentType())
					.suggests(new ProbeArgumentType(false, false, true))
					.executes(c -> {
						int status = remove.run(c);
						
						if (Probe.getProbes().size() == 0 && Utils.running()) {
							Main.stopJFrame();
						}
						
						return status;
					}).then(
						literal("keepWindow")
						.executes(remove)
					)
				)
			).then(
				literal("list")
				.executes(c -> {
					if (Probe.getProbes().size() == 0) {
						c.getSource().sendFeedback(new TranslatableText("commands.mc_multimeter.probe.list.none"));
					} else {
						c.getSource().sendFeedback(new TranslatableText("commands.mc_multimeter.probe.list.title"));
						
						for (Probe p : Probe.getProbes()) {
							c.getSource().sendFeedback(
								new TranslatableText(
									"commands.mc_multimeter.probe.list.entry." + p.isEnabled(),
									p.getName(),
									p.getX(),
									p.getY(),
									p.getZ()
								).formatted(p.isEnabled() ? Formatting.GREEN : Formatting.RED)
							);
						}
					}
					return Command.SINGLE_SUCCESS;
				})
				
			).then(
				literal("rename").then(
					argument("oldname", new ProbeArgumentType())
					.suggests(new ProbeArgumentType())
					.then(
						argument("newname", StringArgumentType.word()) // TODO command error on duplicate name here
						.executes(c -> {
							Probe p = ProbeArgumentType.getProbe(c, "oldname");
							String o = p.getName();
							String n = StringArgumentType.getString(c, "newname");
							
							c.getSource().sendFeedback(new TranslatableText("commands.mc_multimeter.probe.rename." + p.rename(n), o, n));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
			).then(
				literal("enable").then(
					argument("name", new ProbeArgumentType())
					.suggests(new ProbeArgumentType(true, false, false)) // TODO all
					.executes(c -> {
						Probe p = ProbeArgumentType.getProbe(c, "name");
						p.enable();
						c.getSource().sendFeedback(new TranslatableText(
							"commands.mc_multimeter.probe.enable",
							p.getName(),
							p.getX(),
							p.getY(),
							p.getZ()
						));
						return Command.SINGLE_SUCCESS;
					})
				)
			).then(
				literal("disable").then(
					argument("name", new ProbeArgumentType())
					.suggests(new ProbeArgumentType(true, true, false)) // TODO all
					.executes(c -> {
						Probe p = ProbeArgumentType.getProbe(c, "name");
						p.disable();
						c.getSource().sendFeedback(new TranslatableText(
							"commands.mc_multimeter.probe.disable",
							p.getName(),
							p.getX(),
							p.getY(),
							p.getZ()
						));
						return Command.SINGLE_SUCCESS;
					})
				)
			)
		);
		
		register(false,
			literal("graph").then(
				literal("setType").then(
					literal("line")
					.executes(c -> {
						Utils.jFrameStdin.println(Utils.LINE_GRAPH);
						return Command.SINGLE_SUCCESS;
					})
				).then(
					literal("bar")
					.executes(c -> {
						Utils.jFrameStdin.println(Utils.BAR_GRAPH);
						return Command.SINGLE_SUCCESS;
					})
				)
			)
		);

		register(false,
			literal("version")
			.executes(c -> {
				c.getSource().sendFeedback(new TranslatableText("commands.mc_multimeter.version.line1", Main.VERSION));
				c.getSource().sendFeedback(new TranslatableText("commands.mc_multimeter.version.line2")); // TODO is it possible to add a clickable link here?
				return Command.SINGLE_SUCCESS;
			})
		);
		
		ClientCommandManager.DISPATCHER.register(mainNode);
	}
	
	private static LiteralArgumentBuilder<FabricClientCommandSource> mainNode = literal("multimeter");
	
	private static void register(boolean separate, LiteralArgumentBuilder<FabricClientCommandSource> node) {
		mainNode = mainNode.then(node);
		if (separate) {
			ClientCommandManager.DISPATCHER.register(node);
		}
	}
}

