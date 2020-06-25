package agency.highlysuspect.puzzle.item;

import agency.highlysuspect.puzzle.Init;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PuzzleItems {
	public static final ItemGroup GROUP = FabricItemGroupBuilder.build(id("tab"), PuzzleItems::pls);
	
	public static final CheatseyItem CHEATSEY_ITEM = new CheatseyItem(
		defaultSettings().maxCount(1)
	);
	
	public static void onInitialize() {
		Registry.register(Registry.ITEM, id("cheatsey_item"), CHEATSEY_ITEM);
	}
	
	private static Identifier id(String path) {
		return new Identifier(Init.MODID, path);
	}
	
	//breaks a circular initializer dependency - item.settings needs the group, group needs the item
	//so i defer to this method instead of creating a lambda
	//(i don't usually initialize items in static init so this problem is new to me)
	//java's wild
	private static ItemStack pls() {
		return new ItemStack(CHEATSEY_ITEM);
	}
	
	private static Item.Settings defaultSettings() {
		return new Item.Settings().group(GROUP);
	}
}
