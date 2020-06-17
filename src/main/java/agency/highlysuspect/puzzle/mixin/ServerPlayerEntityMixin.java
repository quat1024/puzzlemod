package agency.highlysuspect.puzzle.mixin;

import agency.highlysuspect.puzzle.Init;
import agency.highlysuspect.puzzle.net.PuzzleServerNet;
import agency.highlysuspect.puzzle.world.PuzzleRegionStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
	@Inject(
		method = "changeDimension",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerWorld;getLevelProperties()Lnet/minecraft/world/WorldProperties;"
		)
	)
	private void onChangeDimension(ServerWorld targetWorld, CallbackInfoReturnable<Entity> cir) {
		Init.LOGGER.error("ON DIMENSION CHANGE");
		PuzzleServerNet.syncPuzzleRegions(targetWorld.getRegistryKey(), Stream.of((PlayerEntity) (Object) this), PuzzleRegionStateManager.getFor(targetWorld).getRegionListCopy(), true);
	}
}
