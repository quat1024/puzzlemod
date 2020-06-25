package agency.highlysuspect.puzzle.puzzle;

import agency.highlysuspect.puzzle.etc.Bullshit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.stream.IntStream;

public class PuzzleSnapshot {
	public PuzzleSnapshot(String reason) {
		this.reason = reason;
	}
	
	private PuzzleSnapshot(String reason, BlockState[] palette, int[] states, List<CompoundTag> beTags, List<CompoundTag> entityTags, List<PlayerInfo> players) {
		this(reason);
		this.palette = palette;
		this.states = states;
		this.beTags = beTags;
		this.entityTags = entityTags;
		this.players = players;
	}
	
	private final String reason;
	
	private BlockState[] palette;
	private int[] states;
	//Quick note that Codec's return immutable lists, so don't assume these to be mutable
	//I overwrite instead of mutating these when I take a snapshot
	private List<CompoundTag> beTags;
	private List<CompoundTag> entityTags;
	private List<PlayerInfo> players;
	
	public void takeSnapshot(ServerWorld world, PuzzleRegion region) {
		try {
			int blockCount = Bullshit.blockPosVolume(region.getStart(), region.getEnd());
			
			palette = new BlockState[0];
			states = new int[blockCount];
			beTags = new ArrayList<>();
			entityTags = new ArrayList<>();
			players = new ArrayList<>();
			
			if(blockCount == 0) return;
			
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
			
			Box box = new Box(region.getStart(), region.getEnd());
			
			//save entities
			for(Entity e : world.getEntities((Entity) null, box, e -> {
				return !(e instanceof PlayerEntity);
			})) {
				CompoundTag uwu = new CompoundTag();
				e.saveSelfToTag(uwu);
				entityTags.add(uwu);
			}
			
			//save players
			for(PlayerEntity e : world.getEntities(EntityType.PLAYER, box, e -> true)) {
				players.add(new PlayerInfo(
					e.getUuid(),
					e.getPos(),
					e.pitch,
					e.yaw,
					e.getVelocity()
				));
			}
		} catch(Exception e) { e.printStackTrace(); } //TODO remove this debugging
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
		
		for(CompoundTag tag : entityTags) {
			UUID uuid = tag.getUuid("UUID");
			
			//delete entities that exist in both the world and the snapshot
			Entity existingEntity = world.getEntity(uuid);
			if(existingEntity != null) world.removeEntity(existingEntity);
		}
		
		//delete entities that exist in the space the snapshot is occupying
		for(Entity e : world.getEntities((Entity) null, new Box(region.getStart(), region.getEnd()), e -> {
			return !(e instanceof PlayerEntity);
		})) {
			world.removeEntity(e);
		}
		
		//summon new entities
		for(CompoundTag tag : entityTags) {
			EntityType.getEntityFromTag(tag, world).ifPresent(world::spawnEntity);
		}
		
		//load players
		for(PlayerInfo info : players) {
			Entity e = world.getEntity(info.uuid);
			if(e instanceof ServerPlayerEntity) {
				Set<PlayerPositionLookS2CPacket.Flag> memes = new HashSet<>(Arrays.asList(PlayerPositionLookS2CPacket.Flag.values()));
				((ServerPlayerEntity) e).networkHandler.teleportRequest(info.position.x, info.position.y, info.position.z, info.yaw, info.pitch, memes);
				
				e.setVelocity(info.motion);
				((ServerPlayerEntity) e).velocityDirty = true;
			}
		}
	}
	
	public static class PlayerInfo {
		public PlayerInfo(UUID uuid, Vec3d position, float pitch, float yaw, Vec3d motion) {
			this.uuid = uuid;
			this.position = position;
			this.pitch = pitch;
			this.yaw = yaw;
			this.motion = motion;
		}
		
		public final UUID uuid;
		public final Vec3d position;
		public final float pitch;
		public final float yaw;
		public final Vec3d motion;
		
		public static final Codec<PlayerInfo> CODEC = RecordCodecBuilder.create(r -> r.group(
			Bullshit.UUID_CODEC.fieldOf("uuid").forGetter(i -> i.uuid),
			Bullshit.VEC3D_CODEC.fieldOf("position").forGetter(i -> i.position),
			Codec.FLOAT.fieldOf("pitch").forGetter(i -> i.pitch),
			Codec.FLOAT.fieldOf("yaw").forGetter(i -> i.yaw),
			Bullshit.VEC3D_CODEC.fieldOf("motion").forGetter(i -> i.motion)
		).apply(r, PlayerInfo::new));
	}
	
	//Someone stop me! I'm having entirely too much fun with this
	public static final Codec<PuzzleSnapshot> CODEC = RecordCodecBuilder.create(r -> r.group(
		Codec.STRING.fieldOf("reason").forGetter(snap -> snap.reason),
		arrayOf(BlockState.CODEC, new BlockState[0]).fieldOf("palette").forGetter(snap -> snap.palette),
		Codec.INT_STREAM.fieldOf("states").xmap(IntStream::toArray, IntStream::of).forGetter(snap -> snap.states),
		CompoundTag.field_25128.listOf().fieldOf("block_entities").forGetter(snap -> snap.beTags),
		CompoundTag.field_25128.listOf().fieldOf("entities").forGetter(snap -> snap.entityTags),
		PlayerInfo.CODEC.listOf().fieldOf("players").forGetter(snap -> snap.players)
	).apply(r, PuzzleSnapshot::new));
	
	private static <T> Codec<T[]> arrayOf(Codec<T> codec, T[] poot) {
		return codec.listOf().xmap(list -> list.toArray(poot), Arrays::asList);
	}
}
