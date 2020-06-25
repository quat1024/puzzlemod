package agency.highlysuspect.puzzle.etc;

import com.mojang.datafixers.optics.Lens;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class Bullshit {
	/**
	 * Describes the same corners of box that A and B describe, but the values of all coordinates in B will be larger than those in A.<br><br>
	 * Left of the pair is the smaller one, right is the larger one.
	 */
	public static Pair<BlockPos, BlockPos> fungleBlockPos(BlockPos a, BlockPos b) {
		return Pair.of(
			new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ())),
			new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()))
		);
	}
	
	public static int blockPosVolume(BlockPos a, BlockPos b) {
		return Math.abs(b.getX() - a.getX()) * Math.abs(b.getY() - a.getY()) * Math.abs(b.getZ() - a.getZ());
	}
	
	/** Codec for UUIDs */
	public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
	/** Shitty codec for Vec3ds. There's no Double Stream in Dynamic so we have to make do. */
	public static final Codec<Vec3d> VEC3D_CODEC = Codec.DOUBLE.listOf().xmap(l -> new Vec3d(l.get(0), l.get(1), l.get(2)), v -> Util.make(new ArrayList<>(), l -> {
		l.add(v.x); l.add(v.y); l.add(v.z);
	}));
	
	/**
	 * Runs the thing and returns "true" if it didn't throw any exceptions.
	 */
	public static boolean doesntThrow(Runnable thing) {
		try {
			thing.run();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Returns a lens onto the X, Y, or Z coordinate of a BlockPos.<br><br>
	 * "view" returns the coordinate, and "update" returns a new BlockPos with the coordinate set.
	 */
	public static Lens<BlockPos, BlockPos, Integer, Integer> lensOnToBlockPosCoordinate(Direction.Axis axis) {
		return Optics.lens(
			pos -> axis.choose(pos.getX(), pos.getY(), pos.getZ()),
			(n, pos) -> new BlockPos(
				axis.choose(n, pos.getY(), pos.getZ()),
				axis.choose(pos.getX(), n, pos.getZ()),
				axis.choose(pos.getX(), pos.getY(), n)
			)
		);
	}
	
	/**
	 * Makes a lens out of a getter and setter. Calling update() on the lens will simply mutate and return the enclosing object.
	 */
	public static <OUT, IN> Lens<OUT, OUT, IN, IN> dorkyLens(Function<OUT, IN> getter, Consumer<IN> setter) {
		return Optics.lens(
			getter,
			(in, out) -> { setter.accept(in); return out; }
		);
	}
	
	/**
	 * Compose two (non-typechanging) lenses.
	 * There is probably a better way to do this (using DFU's utilities), but I don't know what it is.
	 */
	public static <OUT, MID, IN> Lens<OUT, OUT, IN, IN> composeLenses(Lens<OUT, OUT, MID, MID> lens1, Lens<MID, MID, IN, IN> lens2) {
		return Optics.lens(
			out -> lens2.view(lens1.view(out)),
			(in, out) -> lens1.update(lens2.update(in, lens1.view(out)), out)
		);
	}
}
