package agency.highlysuspect.puzzle.puzzle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class PuzzleRegion {
	private PuzzleRegion(String name, BlockPos start, BlockPos end, PuzzleSnapshot startingState) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.startingState = startingState;
		
		fixCorners();
	}
	
	public static PuzzleRegion create(ServerWorld world, String name, BlockPos start, BlockPos end) {
		PuzzleRegion region = new PuzzleRegion(name, start, end, new PuzzleSnapshot());
		region.snapshotStartingState(world);
		return region;
	}
	
	public String name;
	public BlockPos start;
	public BlockPos end;
	public PuzzleSnapshot startingState;
	
	public static final Codec<PuzzleRegion> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		Codec.STRING.fieldOf("name").forGetter(PuzzleRegion::getName),
		BlockPos.field_25064.fieldOf("start").forGetter(PuzzleRegion::getStart),
		BlockPos.field_25064.fieldOf("end").forGetter(PuzzleRegion::getEnd),
		PuzzleSnapshot.CODEC.fieldOf("startingState").forGetter(PuzzleRegion::getStartingState)
	).apply(inst, PuzzleRegion::new));
	
	public String getName() {
		return name;
	}
	
	public BlockPos getStart() {
		return start;
	}
	
	public BlockPos getEnd() {
		return end;
	}
	
	public PuzzleSnapshot getStartingState() {
		return startingState;
	}
	
	public boolean contains(BlockPos pos) {
		return start.getX() <= pos.getX() && start.getY() <= pos.getY() && start.getZ() <= pos.getZ() && end.getX() >= pos.getX() && end.getY() >= pos.getY() && end.getZ() >= pos.getZ();
	}
	
	public void snapshotStartingState(ServerWorld world) {
		startingState.takeSnapshot(world, this);
	}
	
	public void restoreStartingState(ServerWorld world) {
		startingState.restoreSnapshot(world, this);
	}
	
	private void fixCorners() {
		BlockPos lowerCorner = new BlockPos(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()));
		BlockPos upperCorner = new BlockPos(Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
		
		start = lowerCorner;
		end = upperCorner;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PuzzleRegion that = (PuzzleRegion) o;
		return Objects.equals(name, that.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
