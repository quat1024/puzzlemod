package agency.highlysuspect.puzzle.block;

import agency.highlysuspect.puzzle.Init;
import agency.highlysuspect.puzzle.etc.Haha;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PuzzleBlocks {
	private static final AbstractBlock.Settings PORTAL_THEME_SETTINGS = AbstractBlock.Settings.of(Material.METAL, MaterialColor.BLACK)
		.strength(2, 2)
		.sounds(BlockSoundGroup.METAL);
	
	private static final AbstractBlock.Settings SIGN_SETTINGS = AbstractBlock.Settings.of(Material.CARPET, MaterialColor.WHITE)
		.strength(0.1f, 0.1f)
		.breakInstantly()
		.noCollision()
		.nonOpaque()
		.sounds(BlockSoundGroup.METAL);
	
	@Haha public static final PortalThemedBlock LARGE_SOLID_METAL = new PortalThemedBlock(PORTAL_THEME_SETTINGS);
	@Haha public static final PortalThemedBlock SMALL_SOLID_METAL = new PortalThemedBlock(PORTAL_THEME_SETTINGS);
	@Haha public static final PortalThemedBlock LARGE_SOLID_WHITE = new PortalThemedBlock(PORTAL_THEME_SETTINGS);
	@Haha public static final PortalThemedBlock SMALL_SOLID_WHITE = new PortalThemedBlock(PORTAL_THEME_SETTINGS);
	
	@Haha public static final PortalThemedBlock ONE_METAL = new PortalThemedBlock.Directional(PORTAL_THEME_SETTINGS);
	@Haha public static final PortalThemedBlock ONE_WHITE = new PortalThemedBlock.Directional(PORTAL_THEME_SETTINGS);
	@Haha public static final PortalThemedBlock METAL_PILLAR = new PortalThemedBlock.Pillar(PORTAL_THEME_SETTINGS);
	@Haha public static final PortalThemedBlock WHITE_PILLAR = new PortalThemedBlock.Pillar(PORTAL_THEME_SETTINGS);
	
	@Haha public static final SignageBlock SQUARE_SIGN = new SignageBlock(SIGN_SETTINGS);
	@Haha public static final SignageBlock X_SIGN = new SignageBlock(SIGN_SETTINGS);
	@Haha public static final SignageBlock CIRCLE_SIGN = new SignageBlock(SIGN_SETTINGS);
	@Haha public static final SignageBlock DOT_SIGN = new SignageBlock(SIGN_SETTINGS);
	
	@Haha public static final SignageBlock INDICATOR_SIGN = new SignageBlock.Indicator(SIGN_SETTINGS);
	
	public static void onInitialize() {
		Registry.register(Registry.BLOCK, id("large_solid_metal"), LARGE_SOLID_METAL);
		Registry.register(Registry.BLOCK, id("small_solid_metal"), SMALL_SOLID_METAL);
		Registry.register(Registry.BLOCK, id("large_solid_white"), LARGE_SOLID_WHITE);
		Registry.register(Registry.BLOCK, id("small_solid_white"), SMALL_SOLID_WHITE);
		
		Registry.register(Registry.BLOCK, id("one_metal"), ONE_METAL);
		Registry.register(Registry.BLOCK, id("one_white"), ONE_WHITE);
		Registry.register(Registry.BLOCK, id("white_pillar"), WHITE_PILLAR);
		Registry.register(Registry.BLOCK, id("metal_pillar"), METAL_PILLAR);
		
		Registry.register(Registry.BLOCK, id("square_sign"), SQUARE_SIGN);
		Registry.register(Registry.BLOCK, id("x_sign"), X_SIGN);
		Registry.register(Registry.BLOCK, id("circle_sign"), CIRCLE_SIGN);
		Registry.register(Registry.BLOCK, id("dot_sign"), DOT_SIGN);
		
		Registry.register(Registry.BLOCK, id("indicator_sign"), INDICATOR_SIGN);
	}
	
	private static Identifier id(String path) {
		return new Identifier(Init.MODID, path);
	}
}
