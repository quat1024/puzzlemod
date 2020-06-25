package agency.highlysuspect.puzzle.net;

import agency.highlysuspect.puzzle.Init;
import net.minecraft.util.Identifier;

public class PuzzleMessages {
	public static final Identifier SYNC_PUZZLES = id("sync_puzzles");
	
	public static final Identifier UNDO = id("undo");
	public static final Identifier REDO = id("redo");
	public static final Identifier CHECKPOINT = id("checkpoint");
	
	private static Identifier id(String path) {
		return new Identifier(Init.MODID, path);
	}
}
