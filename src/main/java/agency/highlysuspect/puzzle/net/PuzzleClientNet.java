package agency.highlysuspect.puzzle.net;

import agency.highlysuspect.puzzle.Init;
import agency.highlysuspect.puzzle.puzzle.PuzzleRegion;
import agency.highlysuspect.puzzle.world.ClientPuzzleRegionStateManagerManager;
import agency.highlysuspect.puzzle.world.PuzzleRegionStateManager;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;

public class PuzzleClientNet {
	public static void onInitialize() {
		ClientSidePacketRegistry.INSTANCE.register(PuzzleMessages.SYNC_PUZZLES, ((ctx, buf) -> {
			RegistryKey<World> worldKey = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
			boolean fullUpdate = buf.readBoolean();
			CompoundTag wrapped = buf.readCompoundTag();
			
			ctx.getTaskQueue().execute(() -> {
				Tag tag = wrapped.get("regions");
				List<PuzzleRegion> regionList = PuzzleRegion.THIN_CODEC.listOf().parse(NbtOps.INSTANCE, tag).getOrThrow(false, Init.LOGGER::error);
				
				PuzzleRegionStateManager state = ClientPuzzleRegionStateManagerManager.get(worldKey);
				if(fullUpdate) state.clear();
				regionList.forEach(state::putRegion);
			});
		}));
	}
}
