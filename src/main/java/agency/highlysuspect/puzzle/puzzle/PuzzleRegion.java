package agency.highlysuspect.puzzle.puzzle;

import agency.highlysuspect.puzzle.Init;
import agency.highlysuspect.puzzle.etc.Bullshit;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class PuzzleRegion {
	//Client-only "thin" constructor
	private PuzzleRegion(String name, BlockPos start, BlockPos end) {
		this.name = name;
		this.start = start;
		this.end = end;
		
		fixCorners();
	}
	
	//Full constructor
	private PuzzleRegion(String name, BlockPos start, BlockPos end, List<PuzzleSnapshot> snaps, int undoCursor) {
		this(name, start, end);
		
		this.snapshots = new ArrayList<>(snaps); //Defensively copy, since DFU can be picky about giving you mutable lists for some reason
		this.undoCursor = undoCursor;
	}
	
	public static Either<PuzzleRegion, Text> tryCreate(ServerWorld world, String name, BlockPos start, BlockPos end) {
		int volume = Bullshit.blockPosVolume(start, end);
		if(volume > 30000) {
			return Either.right(new TranslatableText("puzzle.create.error.too_big", volume, 30000));
		}
		
		PuzzleRegion region = new PuzzleRegion(name, start, end);
		region.snapshotStartingState(world);
		return Either.left(region);
	}
	
	private String name;
	private BlockPos start;
	private BlockPos end;
	
	private List<PuzzleSnapshot> snapshots = new ArrayList<>();
	private int undoCursor = 0;
	
	//Jank
	private boolean needsSync;
	
	public static final Codec<PuzzleRegion> CODEC = RecordCodecBuilder.create(r -> r.group(
		Codec.STRING.fieldOf("name").forGetter(PuzzleRegion::getName),
		BlockPos.field_25064.fieldOf("start").forGetter(PuzzleRegion::getStart),
		BlockPos.field_25064.fieldOf("end").forGetter(PuzzleRegion::getEnd),
		PuzzleSnapshot.CODEC.listOf().fieldOf("snapshots").forGetter(region -> region.snapshots),
		Codec.INT.fieldOf("undoCursor").forGetter(region -> region.undoCursor)
	).apply(r, PuzzleRegion::new));
	
	public static final Codec<PuzzleRegion> THIN_CODEC = RecordCodecBuilder.create(inst -> inst.group(
		Codec.STRING.fieldOf("name").forGetter(PuzzleRegion::getName),
		BlockPos.field_25064.fieldOf("start").forGetter(PuzzleRegion::getStart),
		BlockPos.field_25064.fieldOf("end").forGetter(PuzzleRegion::getEnd)
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
	
	public boolean needsSync() {
		return needsSync;
	}
	
	public void setNeedsSync(boolean needsSync) {
		this.needsSync = needsSync;
	}
	
	public boolean contains(BlockPos pos) {
		return start.getX() <= pos.getX() && start.getY() <= pos.getY() && start.getZ() <= pos.getZ() && end.getX() > pos.getX() && end.getY() > pos.getY() && end.getZ() > pos.getZ();
	}
	
	public boolean entityInside(Entity e) {
		return contains(e.getBlockPos());
	}
	
	public void snapshotStartingState(ServerWorld world) {
		snapshots.clear();
		snapshot(world, "starting-state");
		undoCursor = 0; //reset it again (since snapshot bumps it up)
	}
	
	public void snapshot(ServerWorld world, String reason) {
		//cull the undo state after this snapshot
		for(int i = snapshots.size() - 1; i > undoCursor; i--) {
			snapshots.remove(i);
		}
		
		//take a snapshot
		PuzzleSnapshot snapshot = new PuzzleSnapshot(reason);
		snapshot.takeSnapshot(world, this);
		snapshots.add(snapshot);
		undoCursor++;
	}
	
	public void restoreStartingState(ServerWorld world) {
		restore(world, 0);
	}
	
	public void restore(ServerWorld world, int id) {
		snapshots.get(id).restoreSnapshot(world, this);
	}
	
	public void undo(ServerWorld world) {
		restore(world, undoCursor);
		if(undoCursor > 0) undoCursor--;
	}
	
	public void redo(ServerWorld world) {
		if(snapshots.size() > undoCursor + 1) {
			restore(world, undoCursor + 1);
			if(undoCursor < snapshots.size() - 1) undoCursor++;
		}
	}
	
	private void fixCorners() {
		Pair<BlockPos, BlockPos> fungled = Bullshit.fungleBlockPos(start, end);
		start = fungled.getFirst();
		end = fungled.getSecond();
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
