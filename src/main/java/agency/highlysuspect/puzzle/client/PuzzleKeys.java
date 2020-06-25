package agency.highlysuspect.puzzle.client;

import agency.highlysuspect.puzzle.Init;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class PuzzleKeys {
	private static final String category = Init.MODID + ".key_category";
	
	public static final KeyBinding UNDO = KeyBindingHelper.registerKeyBinding(new KeyBinding(Init.MODID + ".key.undo", GLFW.GLFW_KEY_Z, category));
	public static final KeyBinding REDO = KeyBindingHelper.registerKeyBinding(new KeyBinding(Init.MODID + ".key.redo", GLFW.GLFW_KEY_X, category));
	public static final KeyBinding CHECKPOINT = KeyBindingHelper.registerKeyBinding(new KeyBinding(Init.MODID + ".key.checkpoint", GLFW.GLFW_KEY_P, category));
	
	public static void onInitialize() {
		//No code, but the classloading up there has a side effect of registering the keybindings.
	}
}