package agency.highlysuspect.puzzle;

import agency.highlysuspect.puzzle.puzzle.PuzzleRegion;
import agency.highlysuspect.puzzle.world.PuzzleRegionStateManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Init implements ModInitializer {
	public static final String MODID = "puzzle";
	
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	@Override
	public void onInitialize() {
		LOGGER.info("hello world!");
		
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			//Todo clean this up this is just for testing.
			dispatcher.register(CommandManager.literal("puzzle").requires(s -> s.hasPermissionLevel(2)).then(CommandManager.literal("test").executes(s -> {
				PuzzleRegionStateManager state = PuzzleRegionStateManager.getFor(s.getSource().getWorld());
				state.addRegion(PuzzleRegion.create(
					s.getSource().getWorld(),
					"test",
					new BlockPos(10, 56, 10),
					new BlockPos(20, 60, 20)
				));
				return 0;
			})).then(CommandManager.literal("add").then(CommandManager.argument("start", BlockPosArgumentType.blockPos()).then(CommandManager.argument("end", BlockPosArgumentType.blockPos()).then(CommandManager.argument("name", StringArgumentType.string()).executes(s -> {
				PuzzleRegionStateManager state = PuzzleRegionStateManager.getFor(s.getSource().getWorld());
				state.addRegion(PuzzleRegion.create(
					s.getSource().getWorld(),
					StringArgumentType.getString(s, "name"),
					BlockPosArgumentType.getBlockPos(s, "start"),
					BlockPosArgumentType.getBlockPos(s, "end")
				));
				return 0;
			}))))).then(CommandManager.literal("restore").executes(s -> {
				PuzzleRegionStateManager state = PuzzleRegionStateManager.getFor(s.getSource().getWorld());
				ServerPlayerEntity player = s.getSource().getPlayer();
				state.getRegionIntersecting(player.getBlockPos()).ifPresent(r -> r.restoreStartingState(s.getSource().getWorld()));
				return 0;
			})).then(CommandManager.literal("snapshot").executes(s -> {
				PuzzleRegionStateManager state = PuzzleRegionStateManager.getFor(s.getSource().getWorld());
				ServerPlayerEntity player = s.getSource().getPlayer();
				state.getRegionIntersecting(player.getBlockPos()).ifPresent(r -> r.snapshotStartingState(s.getSource().getWorld()));
				state.markDirty();
				return 0;
			})));
		});
		
		ServerTickCallback.EVENT.register(server -> {
			if(server.getTicks() % 10 != 0) return;
			PlayerStream.all(server).findFirst().ifPresent(player -> {
				PuzzleRegionStateManager state = PuzzleRegionStateManager.getFor(player.getServerWorld());
				LOGGER.info(state.getRegionIntersecting(player.getBlockPos()).map(PuzzleRegion::getName).orElse("- none -"));
			});
		});
	}
}
