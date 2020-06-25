package agency.highlysuspect.puzzle.etc;

import agency.highlysuspect.puzzle.Permissions;
import agency.highlysuspect.puzzle.puzzle.PuzzleRegion;
import agency.highlysuspect.puzzle.world.PuzzleRegionStateManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PuzzleCommand {
	public static void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			//TODO more robust permissions system.
			LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("puzzle");
			
			//Hadouken!
			LiteralArgumentBuilder<ServerCommandSource> add = CommandManager.literal("add").requires(PuzzleCommand::isSpicy)
				.then(CommandManager.argument("start", BlockPosArgumentType.blockPos())
					.then(CommandManager.argument("end", BlockPosArgumentType.blockPos())
						.then(CommandManager.argument("name", StringArgumentType.string())
							.executes(c -> {
								PuzzleRegionStateManager state = PuzzleRegionStateManager.getFor(c.getSource().getWorld());
								PuzzleRegion.tryCreate(
									c.getSource().getWorld(),
									StringArgumentType.getString(c, "name"),
									BlockPosArgumentType.getBlockPos(c, "start"),
									BlockPosArgumentType.getBlockPos(c, "end")
								).ifLeft(state::putRegion).ifRight(c.getSource()::sendError);
								return 0;
							}))));
			
			LiteralArgumentBuilder<ServerCommandSource> baseSnapshot = CommandManager.literal("starting-state");
			LiteralArgumentBuilder<ServerCommandSource> snapshot = baseSnapshot.then(puzzle(CommandManager.literal("capture").requires(PuzzleCommand::isSpicy), (state, region) -> c -> {
				region.snapshotStartingState(c.getSource().getWorld());
				return 0;
			}));
			
			LiteralArgumentBuilder<ServerCommandSource> restore = baseSnapshot.then(puzzle(CommandManager.literal("reset"), (state, region) -> c -> {
				region.restoreStartingState(c.getSource().getWorld());
				return 0;
			}));
			
			LiteralArgumentBuilder<ServerCommandSource> remove = CommandManager.literal("remove").requires(PuzzleCommand::isSpicy);
			remove = puzzle(remove, (state, region) -> c -> {
				state.removeRegion(region);
				return 0;
			});
			
			dispatcher.register(root.then(add).then(snapshot).then(restore).then(remove));
		});
	}
	
	private static final SuggestionProvider<ServerCommandSource> PUZZLE_NAMES = (context, builder) -> {
		PuzzleRegionStateManager.getFor(context.getSource().getWorld()).regionStream().forEach(r -> builder.suggest(r.getName()));
		return builder.buildFuture();
	};
	
	private static LiteralArgumentBuilder<ServerCommandSource> puzzle(LiteralArgumentBuilder<ServerCommandSource> x, BiFunction<PuzzleRegionStateManager, PuzzleRegion, Command<ServerCommandSource>> next) {
		return x.then(CommandManager.literal("at").then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(c -> piss(
			state -> state.getRegionIntersecting(BlockPosArgumentType.getBlockPos(c, "pos")),
			c, next, () -> "No puzzle at that position"
		)))).then(CommandManager.literal("named").then(CommandManager.argument("name", StringArgumentType.word()).suggests(PUZZLE_NAMES).executes(c -> piss(
			state -> state.getRegionByName(StringArgumentType.getString(c, "name")),
			c, next, () -> "No puzzle with that name"
		)))).then(CommandManager.literal("here").executes(c -> piss(
			state -> state.getRegionIntersecting(new BlockPos(c.getSource().getPosition())),
			c, next, () -> "There's no puzzle there"
		)));
	}
	
	private static int piss(Piss piss, CommandContext<ServerCommandSource> c, BiFunction<PuzzleRegionStateManager, PuzzleRegion, Command<ServerCommandSource>> next, Supplier<String> error) throws CommandSyntaxException {
		PuzzleRegionStateManager state = PuzzleRegionStateManager.getFor(c.getSource().getWorld());
		Optional<PuzzleRegion> puzzle = piss.piss(state);
		if(puzzle.isPresent()) {
			return next.apply(state, puzzle.get()).run(c);
		} else {
			//TODO proper localized errors
			throw new CommandException(new TranslatableText(error.get()));
		}
	}
	
	//i dont know what im DOING!!!
	private interface Piss {
		Optional<PuzzleRegion> piss(PuzzleRegionStateManager state) throws CommandSyntaxException;
	}
	
	private static boolean isSpicy(ServerCommandSource src) {
		if(src.hasPermissionLevel(2)) return true;
		
		try {
			return Permissions.hasPermission(src.getPlayer());
		} catch (CommandSyntaxException e) {
			return false;
		}
	}
}
