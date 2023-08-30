// original: net.minecraft.command.argument.PosArgument
// modified for use in my project

package com.antonilol.mc_multimeter.mc.command;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface ClientPosArgument {
	Vec3d toAbsolutePos(FabricClientCommandSource source);

	Vec2f toAbsoluteRotation(FabricClientCommandSource source);

	default BlockPos toAbsoluteBlockPos(FabricClientCommandSource fabricClientCommandSource) {
		return BlockPos.ofFloored(toAbsolutePos(fabricClientCommandSource));
	}

	boolean isXRelative();

	boolean isYRelative();

	boolean isZRelative();
}
