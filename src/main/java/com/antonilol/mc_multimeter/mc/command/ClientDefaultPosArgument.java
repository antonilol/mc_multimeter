// original: net.minecraft.command.argument.DefaultPosArgument
// modified for use in my project

package com.antonilol.mc_multimeter.mc.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ClientDefaultPosArgument implements ClientPosArgument {
	private final CoordinateArgument x;
	private final CoordinateArgument y;
	private final CoordinateArgument z;

	public ClientDefaultPosArgument(CoordinateArgument x, CoordinateArgument y, CoordinateArgument z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public Vec3d toAbsolutePos(FabricClientCommandSource source) {
		Vec3d vec3d = source.getPosition();
		return new Vec3d(x.toAbsoluteCoordinate(vec3d.x), y.toAbsoluteCoordinate(vec3d.y),
			z.toAbsoluteCoordinate(vec3d.z));
	}

	@Override
	public Vec2f toAbsoluteRotation(FabricClientCommandSource source) {
		Vec2f vec2f = source.getRotation();
		return new Vec2f((float) x.toAbsoluteCoordinate(vec2f.x),
			(float) y.toAbsoluteCoordinate(vec2f.y));
	}

	@Override
	public boolean isXRelative() {
		return x.isRelative();
	}

	@Override
	public boolean isYRelative() {
		return y.isRelative();
	}

	@Override
	public boolean isZRelative() {
		return z.isRelative();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof ClientDefaultPosArgument)) {
			return false;
		} else {
			ClientDefaultPosArgument defaultPosArgument = (ClientDefaultPosArgument) o;
			if (!x.equals(defaultPosArgument.x)) {
				return false;
			} else {
				return !y.equals(defaultPosArgument.y) ? false : z.equals(defaultPosArgument.z);
			}
		}
	}

	public static ClientDefaultPosArgument parse(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();
		CoordinateArgument coordinateArgument = CoordinateArgument.parse(reader);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(reader);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(reader);
				return new ClientDefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3);
			} else {
				reader.setCursor(i);
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
			}
		} else {
			reader.setCursor(i);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
		}
	}

	public static ClientDefaultPosArgument parse(StringReader reader, boolean centerIntegers)
		throws CommandSyntaxException {
		int i = reader.getCursor();
		CoordinateArgument coordinateArgument = CoordinateArgument.parse(reader, centerIntegers);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(reader, false);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(reader, centerIntegers);
				return new ClientDefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3);
			} else {
				reader.setCursor(i);
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
			}
		} else {
			reader.setCursor(i);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
		}
	}

	public static ClientDefaultPosArgument absolute(double x, double y, double z) {
		return new ClientDefaultPosArgument(new CoordinateArgument(false, x), new CoordinateArgument(false, y),
			new CoordinateArgument(false, z));
	}

	public static ClientDefaultPosArgument absolute(Vec2f vec) {
		return new ClientDefaultPosArgument(new CoordinateArgument(false, vec.x),
			new CoordinateArgument(false, vec.y), new CoordinateArgument(true, 0.0D));
	}

	public static ClientDefaultPosArgument zero() {
		return new ClientDefaultPosArgument(new CoordinateArgument(true, 0.0D), new CoordinateArgument(true, 0.0D),
			new CoordinateArgument(true, 0.0D));
	}

	@Override
	public int hashCode() {
		int i = x.hashCode();
		i = 31 * i + y.hashCode();
		return 31 * i + z.hashCode();
	}
}
