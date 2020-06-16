package agency.highlysuspect.puzzle.world;

import agency.highlysuspect.puzzle.Init;
import agency.highlysuspect.puzzle.etc.SetCodec;
import agency.highlysuspect.puzzle.puzzle.PuzzleRegion;
import com.mojang.serialization.Codec;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PuzzleRegionStateManager extends PersistentState {
	public PuzzleRegionStateManager() {
		super("puzzle-regions");
	}
	
	public static PuzzleRegionStateManager getFor(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(PuzzleRegionStateManager::new, "puzzle-regions");
	}
	
	private static final Codec<Set<PuzzleRegion>> PUZZLE_REGION_SET_CODEC = new SetCodec<>(PuzzleRegion.CODEC);
	private Set<PuzzleRegion> regions = new HashSet<>();
	
	public void addRegion(PuzzleRegion region) {
		regions.add(region);
		markDirty();
	}
	
	public Optional<PuzzleRegion> getRegionIntersecting(BlockPos pos) {
		return regions.stream().filter(r -> r.contains(pos)).findFirst();
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.put("regions", PUZZLE_REGION_SET_CODEC.encodeStart(NbtOps.INSTANCE, regions).getOrThrow(true, Init.LOGGER::error));
		return tag;
	}
	
	@Override
	public void fromTag(CompoundTag tag) {
		regions = PUZZLE_REGION_SET_CODEC.parse(NbtOps.INSTANCE, tag.get("regions")).getOrThrow(true, Init.LOGGER::error);
	}
}
