package agency.highlysuspect.puzzle.puzzle;

import agency.highlysuspect.puzzle.Init;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class PuzzleSnapshot {
	public PuzzleSnapshot() {}
	
	public PuzzleSnapshot(BlockState[] palette, int[] states, List<CompoundTag> beTags, List<CompoundTag> entityTags) {
		this.palette = palette;
		this.states = states;
		this.beTags = beTags;
		this.entityTags = entityTags;
	}
	
	private BlockState[] palette;
	private int[] states;
	//Quick note that Codec's return immutable lists, so don't assume these to be mutable
	private List<CompoundTag> beTags;
	private List<CompoundTag> entityTags;
	
	public void takeSnapshot(ServerWorld world, PuzzleRegion region) {
		try {
			int blockCount = (region.getEnd().getX() - region.getStart().getX()) * (region.getEnd().getY() - region.getStart().getY()) * (region.getEnd().getZ() - region.getStart().getZ());
			
			palette = null;
			states = new int[blockCount];
			beTags = new ArrayList<>();
			entityTags = new ArrayList<>();
			
			//save paletted states and block entities
			int i = 0;
			List<BlockState> paletteList = new ArrayList<>();
			for (BlockPos pos : BlockPos.iterate(region.getStart(), region.getEnd().add(-1, -1, -1))) {
				BlockState state = world.getBlockState(pos);
				
				int index = paletteList.indexOf(state);
				if (index == -1) {
					index = paletteList.size();
					paletteList.add(state);
				}
				
				states[i] = index;
				i++;
				
				BlockEntity be = world.getBlockEntity(pos);
				if (be != null) {
					CompoundTag tag = be.toTag(new CompoundTag());
					beTags.add(tag);
				}
			}
			palette = paletteList.toArray(new BlockState[0]);
			
			//save entities
			for (Entity e : world.getEntities((Entity) null, new Box(region.getStart(), region.getEnd()), e -> {
				return !(e instanceof PlayerEntity);
			})) {
				CompoundTag uwu = new CompoundTag();
				e.saveSelfToTag(uwu);
				entityTags.add(uwu);
			}
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public void restoreSnapshot(ServerWorld world, PuzzleRegion region) {
		//place blocks
		int i = 0;
		for(BlockPos pos : BlockPos.iterate(region.getStart(), region.getEnd().add(-1, -1, -1))) {
			world.setBlockState(pos, palette[states[i]], 2 | 16 | 32);
			i++;
		}
		
		//place block entities
		for(CompoundTag tag : beTags) {
			BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
			BlockState state = world.getBlockState(pos);
			
			BlockEntity be = world.getBlockEntity(pos);
			if(be != null) {
				be.fromTag(state, tag.copy());
				be.markDirty();
			}
		}
		
		//summon entities
		for(CompoundTag tag : entityTags) {
			UUID uuid = tag.getUuid("UUID");
			Entity existingEntity = world.getEntity(uuid);
			//kinda calling an internal method here.
			//however: re-spawning with the same UUID would fail
			//and just calling entity.remove would only remove it at the next tick
			if(existingEntity != null) world.removeEntity(existingEntity);
			
			EntityType.getEntityFromTag(tag, world).ifPresent(world::spawnEntity);
		}
	}
	
	//Someone stop me! I'm having entirely too much fun with this
	public static final Codec<PuzzleSnapshot> CODEC = RecordCodecBuilder.create(r -> r.group(
		arrayOf(BlockState.CODEC, new BlockState[0]).fieldOf("palette").forGetter(snap -> snap.palette),
		Codec.INT_STREAM.fieldOf("states").xmap(IntStream::toArray, IntStream::of).forGetter(snap -> snap.states),
		CompoundTag.field_25128.listOf().fieldOf("block_entities").forGetter(snap -> snap.beTags),
		CompoundTag.field_25128.listOf().fieldOf("entities").forGetter(snap -> snap.entityTags)
	).apply(r, PuzzleSnapshot::new));
	
	private static <T> Codec<T[]> arrayOf(Codec<T> codec, T[] poot) {
		//noinspection unchecked
		return codec.listOf().xmap(list -> list.toArray(poot), Arrays::asList);
	}
}
