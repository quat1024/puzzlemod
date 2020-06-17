package agency.highlysuspect.puzzle.world;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/** I'm being a bit cheeky with the name, but it's accurate... */
public class ClientPuzzleRegionStateManagerManager {
	public static PuzzleRegionStateManager get(RegistryKey<World> worldKey) {
		return CLIENT_INSTANCES.computeIfAbsent(worldKey, x -> new PuzzleRegionStateManager());
	}
	
	public static void clear() {
		CLIENT_INSTANCES.clear();
	}
	
	public static void forEachKnown(BiConsumer<RegistryKey<World>, PuzzleRegionStateManager> action) {
		CLIENT_INSTANCES.forEach(action);
	}
	
	private static final Map<RegistryKey<World>, PuzzleRegionStateManager> CLIENT_INSTANCES = new HashMap<>();
}
