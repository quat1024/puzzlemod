package agency.highlysuspect.puzzle.mixin;

import agency.highlysuspect.puzzle.Init;
import agency.highlysuspect.puzzle.net.PuzzleServerNet;
import agency.highlysuspect.puzzle.world.PuzzleRegionStateManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Inject(
		method = "onPlayerConnect",
		at = @At("TAIL")
	)
	private void onOnPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		//Send an initial full-sync packet
		Init.LOGGER.error("ON PLAYER CONNECT");
		ServerWorld world = player.getServerWorld();
		PuzzleServerNet.syncPuzzleRegions(world.getRegistryKey(), Stream.of(player), PuzzleRegionStateManager.getFor(world).getRegionListCopy(), true);
	}
}
