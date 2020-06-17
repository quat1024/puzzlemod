package agency.highlysuspect.puzzle;

import agency.highlysuspect.puzzle.net.PuzzleClientNet;
import agency.highlysuspect.puzzle.world.ClientPuzzleRegionStateManagerManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

public class ClientInit implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		PuzzleClientNet.onInitialize();
		
		ClientTickCallback.EVENT.register(client -> {
			if(client.world == null) {
				ClientPuzzleRegionStateManagerManager.clear();
				return;
			}
			
//			if(client.world.getTime() % 20 != 5) return;
//			
//			ClientPuzzleRegionStateManagerManager.forEachKnown((key, mgr) -> {
//				Init.LOGGER.info("world key " + key + " count " + mgr.regionCount());
//			});
		});
	}
}
