package agency.highlysuspect.puzzle.world;

import agency.highlysuspect.puzzle.Init;
import agency.highlysuspect.puzzle.net.PuzzleServerNet;
import agency.highlysuspect.puzzle.puzzle.PuzzleRegion;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PuzzleRegionStateManager extends PersistentState {
	public PuzzleRegionStateManager() {
		super("puzzle-regions");
	}
	
	public static PuzzleRegionStateManager getFor(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(PuzzleRegionStateManager::new, "puzzle-regions");
	}
	
	private final Map<String, PuzzleRegion> regions = new HashMap<>();
	
	public void putRegion(PuzzleRegion region) {
		regions.put(region.getName(), region);
		region.setNeedsSync(true);
		markDirty();
	}
	
	public Stream<PuzzleRegion> regionStream() {
		return regions.values().stream();
	}
	
	public List<PuzzleRegion> getRegionListCopy() {
		return new ArrayList<>(regions.values());
	}
	
	public int regionCount() {
		return regions.size();
	}
	
	public void clear() {
		regions.clear();
		markDirty();
	}
	
	public Optional<PuzzleRegion> getRegionByName(String name) {
		return Optional.ofNullable(regions.get(name));
	}
	
	public Optional<PuzzleRegion> getRegionIntersecting(BlockPos pos) {
		return regions.values().stream().filter(r -> r.contains(pos)).findFirst();
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		//The list copying hurts me; blame listOf only accepting lists
		tag.put("regions", PuzzleRegion.CODEC.listOf().encodeStart(NbtOps.INSTANCE, getRegionListCopy()).getOrThrow(true, Init.LOGGER::error));
		return tag;
	}
	
	@Override
	public void fromTag(CompoundTag tag) {
		List<PuzzleRegion> regionList = PuzzleRegion.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("regions")).getOrThrow(true, Init.LOGGER::error);
		
		clear();
		regionList.forEach(this::putRegion);
	}
	
	public void handlePartialSync(ServerWorld world) {
		List<PuzzleRegion> needsSync = regionStream().filter(PuzzleRegion::needsSync).collect(Collectors.toList());
		PuzzleServerNet.syncPuzzleRegions(world.getRegistryKey(), PlayerStream.world(world), needsSync, false);
		needsSync.forEach(r -> r.setNeedsSync(false));
	}
}
