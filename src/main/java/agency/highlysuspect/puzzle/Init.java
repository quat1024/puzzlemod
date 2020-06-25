package agency.highlysuspect.puzzle;

import agency.highlysuspect.puzzle.block.PuzzleBlocks;
import agency.highlysuspect.puzzle.etc.PuzzleCommand;
import agency.highlysuspect.puzzle.item.PuzzleItems;
import agency.highlysuspect.puzzle.net.PuzzleServerNet;
import agency.highlysuspect.puzzle.world.PuzzleRegionStateManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Init implements ModInitializer {
	public static final String MODID = "puzzle";
	
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	@Override
	public void onInitialize() {
		PuzzleBlocks.onInitialize();
		PuzzleItems.onInitialize();
		
		PuzzleServerNet.onInitialize();
		PuzzleCommand.onInitialize();
		
		ServerTickCallback.EVENT.register(server -> {
			if(server.getTicks() % 10 != 0) return;
			
			server.getWorlds().forEach(world -> PuzzleRegionStateManager.getFor(world).handleSync(world));
		});
	}
}
