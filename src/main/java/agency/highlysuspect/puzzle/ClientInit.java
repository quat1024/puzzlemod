package agency.highlysuspect.puzzle;

import agency.highlysuspect.puzzle.client.PuzzleKeys;
import agency.highlysuspect.puzzle.net.PuzzleClientNet;
import agency.highlysuspect.puzzle.world.ClientPuzzleRegionStateManagerManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

public class ClientInit implements ClientModInitializer {
	//Doing it "the hard way" to try and avoid key conflicts
	private static boolean undoWasPressed, redoWasPressed, checkpointWasPressed;
	
	@Override
	public void onInitializeClient() {
		PuzzleClientNet.onInitialize();
		PuzzleKeys.onInitialize();
		
		ClientTickCallback.EVENT.register(client -> {
			if(client.world == null) {
				ClientPuzzleRegionStateManagerManager.clear();
			}
			
			if(!undoWasPressed && PuzzleKeys.UNDO.isPressed()) {
				PuzzleClientNet.undo();
			}
			
			if(!redoWasPressed && PuzzleKeys.REDO.isPressed()) {
				PuzzleClientNet.redo();
			}
			
			if(!checkpointWasPressed && PuzzleKeys.CHECKPOINT.isPressed()) {
				PuzzleClientNet.checkpoint();
			}
			
			undoWasPressed = PuzzleKeys.UNDO.isPressed();
			redoWasPressed = PuzzleKeys.REDO.isPressed();
			checkpointWasPressed = PuzzleKeys.CHECKPOINT.isPressed();
		});
	}
}
