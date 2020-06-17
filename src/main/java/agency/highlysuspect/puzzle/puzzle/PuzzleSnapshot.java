package agency.highlysuspect.puzzle.puzzle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PuzzleSnapshot {
	public PuzzleSnapshot() {
		this(new Structure());
	}
	
	public PuzzleSnapshot(Structure structure) {
		this.structure = structure;
	}
	
	//todo also save things like player positions, player inventories
	private final Structure structure;
	
	private static final StructurePlacementData PLACEMENT_RULES = new StructurePlacementData();
	
	public void takeSnapshot(ServerWorld world, PuzzleRegion region) {
		structure.setAuthor(region.getName() + "-puzzle-snapshot");
		structure.saveFromWorld(world, region.getStart(), region.getEnd().subtract(region.getStart()), true, Blocks.STRUCTURE_VOID);
	}
	
	public void restoreSnapshot(ServerWorld world, PuzzleRegion region) {
		structure.place(world, region.getStart(), PLACEMENT_RULES, world.getRandom());
	}
	
	public Structure getStructure() {
		return structure;
	}
	
	//Someone stop me I'm having entirely too much fun with this.
	public static final Codec<PuzzleSnapshot> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		tagCodec(Structure::new, Structure::toTag, Structure::fromTag).fieldOf("structure").forGetter(PuzzleSnapshot::getStructure)
	).apply(inst, PuzzleSnapshot::new));
	
	private static <T> Codec<T> tagCodec(Supplier<T> factory, BiFunction<T, CompoundTag, CompoundTag> toTag, BiConsumer<T, CompoundTag> fromTag) {
		return CompoundTag.field_25128.xmap(tag -> { T thing = factory.get(); fromTag.accept(thing, tag); return thing; }, thing -> toTag.apply(thing, new CompoundTag()));
	}
}
