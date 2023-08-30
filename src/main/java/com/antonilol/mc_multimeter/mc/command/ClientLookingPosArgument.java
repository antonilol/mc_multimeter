// original: net.minecraft.command.argument.LookingPosArgument
// modified for use in my project

package com.antonilol.mc_multimeter.mc.command;

import java.util.Objects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ClientLookingPosArgument implements ClientPosArgument {
	public static final char CARET = '^';
	private final double x;
	private final double y;
	private final double z;

	public ClientLookingPosArgument(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public Vec3d toAbsolutePos(FabricClientCommandSource source) {
		Vec2f vec2f = source.getRotation();
		// old: Vec3d vec3d = source.getEntityAnchor().positionAt(source);
		// this works!!!
		Vec3d vec3d = source.getPosition();
		float f = MathHelper.cos((vec2f.y + 90.0F) * 0.017453292F);
		float g = MathHelper.sin((vec2f.y + 90.0F) * 0.017453292F);
		float h = MathHelper.cos(-vec2f.x * 0.017453292F);
		float i = MathHelper.sin(-vec2f.x * 0.017453292F);
		float j = MathHelper.cos((-vec2f.x + 90.0F) * 0.017453292F);
		float k = MathHelper.sin((-vec2f.x + 90.0F) * 0.017453292F);
		Vec3d vec3d2 = new Vec3d(f * h, i, g * h);
		Vec3d vec3d3 = new Vec3d(f * j, k, g * j);
		Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0D);
		double d = vec3d2.x * z + vec3d3.x * y + vec3d4.x * x;
		double e = vec3d2.y * z + vec3d3.y * y + vec3d4.y * x;
		double l = vec3d2.z * z + vec3d3.z * y + vec3d4.z * x;
		return new Vec3d(vec3d.x + d, vec3d.y + e, vec3d.z + l);
	}

	@Override
	public Vec2f toAbsoluteRotation(FabricClientCommandSource source) {
		return Vec2f.ZERO;
	}

	@Override
	public boolean isXRelative() {
		return true;
	}

	@Override
	public boolean isYRelative() {
		return true;
	}

	@Override
	public boolean isZRelative() {
		return true;
	}

	public static ClientLookingPosArgument parse(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();
		double d = readCoordinate(reader, i);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			double e = readCoordinate(reader, i);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				double f = readCoordinate(reader, i);
				return new ClientLookingPosArgument(d, e, f);
			} else {
				reader.setCursor(i);
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
			}
		} else {
			reader.setCursor(i);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
		}
	}

	private static double readCoordinate(StringReader reader, int startingCursorPos) throws CommandSyntaxException {
		if (!reader.canRead()) {
			throw CoordinateArgument.MISSING_COORDINATE.createWithContext(reader);
		} else if (reader.peek() != '^') {
			reader.setCursor(startingCursorPos);
			throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext(reader);
		} else {
			reader.skip();
			return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof ClientLookingPosArgument)) {
			return false;
		} else {
			ClientLookingPosArgument lookingPosArgument = (ClientLookingPosArgument) o;
			return x == lookingPosArgument.x && y == lookingPosArgument.y && z == lookingPosArgument.z;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}
