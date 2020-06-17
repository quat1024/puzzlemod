package agency.highlysuspect.puzzle.net;

import agency.highlysuspect.puzzle.Init;
import agency.highlysuspect.puzzle.puzzle.PuzzleRegion;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Stream;

public class PuzzleServerNet {
	public static void syncPuzzleRegions(RegistryKey<World> worldKey, Stream<PlayerEntity> players, List<PuzzleRegion> regions, boolean full) {
		Tag tag = PuzzleRegion.THIN_CODEC.listOf().encodeStart(NbtOps.INSTANCE, regions).getOrThrow(false, Init.LOGGER::error);
		
		//Annoyingly PacketByteBuf doesn't have a method to write an arbitrary tag, just compound tags, so I have to wrap it
		CompoundTag wrapping = new CompoundTag();
		wrapping.put("regions", tag);
		
		players.forEach(player -> {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeIdentifier(worldKey.getValue());
			buf.writeBoolean(full);
			buf.writeCompoundTag(wrapping);
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PuzzleMessages.SYNC_PUZZLES, buf);
		});
	}
}
