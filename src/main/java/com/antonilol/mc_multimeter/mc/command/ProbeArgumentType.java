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

package com.antonilol.mc_multimeter.mc.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.antonilol.mc_multimeter.mc.Probe;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class ProbeArgumentType implements ArgumentType<Probe>, SuggestionProvider<FabricClientCommandSource> {

	private static final Collection<String> EXAMPLES = new ArrayList<String>();

	static {
		EXAMPLES.add("probe0");
		EXAMPLES.add("yourcustomname");
		EXAMPLES.add("abcABC123_-.+");
	}

	public static Probe getProbe(final CommandContext<FabricClientCommandSource> context, final String name) {
		return context.getArgument(name, Probe.class);
	}

	private final boolean allowAll, enabled, filter;

	public ProbeArgumentType() {
		filter = false;
		enabled = false;
		allowAll = false;
	}

	public ProbeArgumentType(boolean filter, boolean enabled, boolean allowAll) {
		this.filter = filter;
		this.enabled = enabled;
		this.allowAll = allowAll;
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context,
		SuggestionsBuilder builder) throws CommandSyntaxException {
		if (allowAll) {
			builder.suggest("*");
		}

		for (Probe p : Probe.getProbes()) {
			if (!filter || enabled == p.isEnabled()) {
				builder.suggest(p.getName());
			}
		}

		return builder.buildFuture();
	}

	@Override
	public Probe parse(StringReader reader) throws CommandSyntaxException {
		final int start = reader.getCursor();
		while (reader.canRead() && (StringReader.isAllowedInUnquotedString(reader.peek()) || reader.peek() == '*')) {
			reader.skip();
		}
		String s = reader.getString().substring(start, reader.getCursor());

		if ("*".equals(s)) {
			return Probe.ALL;
		}

		for (Probe p : Probe.getProbes()) {
			if (p.getName().equals(s)) {
				return p;
			}
		}
		throw new SimpleCommandExceptionType(Text.translatable("commands.mc_multimeter.probe.notfound"))
			.createWithContext(reader);
	}
}
