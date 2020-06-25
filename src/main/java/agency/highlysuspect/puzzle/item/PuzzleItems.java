package agency.highlysuspect.puzzle.item;

import agency.highlysuspect.puzzle.Init;
import agency.highlysuspect.puzzle.block.PuzzleBlocks;
import agency.highlysuspect.puzzle.etc.Haha;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;

public class PuzzleItems {
	public static final ItemGroup GROUP = FabricItemGroupBuilder.build(id("tab"), PuzzleItems::pls);
	
	public static final CheatseyItem CHEATSEY_ITEM = new CheatseyItem(
		defaultSettings().maxCount(1)
	);
	
	public static void onInitialize() {
		Registry.register(Registry.ITEM, id("cheatsey_item"), CHEATSEY_ITEM);
		
		//Someone stop me
		try {
			for (Field f : PuzzleBlocks.class.getDeclaredFields()) {
				if (f.isAnnotationPresent(Haha.class)) {
					Block b = (Block) f.get(null);
					Registry.register(Registry.ITEM, Registry.BLOCK.getId(b), new BlockItem(b, defaultSettings()));
				}
			}
		} catch(Exception piss) {
			Init.LOGGER.error("piss ", piss);
		}
	}
	
	private static Identifier id(String path) {
		return new Identifier(Init.MODID, path);
	}
	
	private static ItemStack pls() {
		return new ItemStack(PuzzleBlocks.LARGE_SOLID_METAL.asItem());
	}
	
	private static Item.Settings defaultSettings() {
		return new Item.Settings().group(GROUP);
	}
}
