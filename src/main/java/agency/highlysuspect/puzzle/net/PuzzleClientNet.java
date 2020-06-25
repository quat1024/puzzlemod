package agency.highlysuspect.puzzle.net;

import agency.highlysuspect.puzzle.Init;
import agency.highlysuspect.puzzle.puzzle.PuzzleRegion;
import agency.highlysuspect.puzzle.world.ClientPuzzleRegionStateManagerManager;
import agency.highlysuspect.puzzle.world.PuzzleRegionStateManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;

public class PuzzleClientNet {
	public static void onInitialize() {
		ClientSidePacketRegistry.INSTANCE.register(PuzzleMessages.SYNC_PUZZLES, (ctx, buf) -> {
			RegistryKey<World> worldKey = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
			boolean fullUpdate = buf.readBoolean();
			CompoundTag wrapped = buf.readCompoundTag();
			if(wrapped == null) return;
			
			ctx.getTaskQueue().execute(() -> {
				Tag tag = wrapped.get("regions");
				List<PuzzleRegion> regionList = PuzzleRegion.THIN_CODEC.listOf().parse(NbtOps.INSTANCE, tag).getOrThrow(false, Init.LOGGER::error);
				
				PuzzleRegionStateManager state = ClientPuzzleRegionStateManagerManager.get(worldKey);
				if(fullUpdate) state.clear();
				regionList.forEach(state::putRegion);
			});
		});
	}
	
	public static void undo() {
		ClientSidePacketRegistry.INSTANCE.sendToServer(PuzzleMessages.UNDO, new PacketByteBuf(Unpooled.buffer()));
	}
	
	public static void redo() {
		ClientSidePacketRegistry.INSTANCE.sendToServer(PuzzleMessages.REDO, new PacketByteBuf(Unpooled.buffer()));
	}
	
	public static void checkpoint() {
		ClientSidePacketRegistry.INSTANCE.sendToServer(PuzzleMessages.CHECKPOINT, new PacketByteBuf(Unpooled.buffer()));
	}
}
