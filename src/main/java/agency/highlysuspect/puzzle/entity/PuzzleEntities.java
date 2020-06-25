package agency.highlysuspect.puzzle.entity;

import agency.highlysuspect.puzzle.Init;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PuzzleEntities {
	public static final EntityType<CubeEntity> CUBE = FabricEntityTypeBuilder.create(SpawnGroup.MISC, CubeEntity::new)
		.dimensions(EntityDimensions.fixed(0.8f, 0.8f))
		.fireImmune()
		.trackable(40, 3, true)
		.build();
	
	public static void onInitialize() {
		Registry.register(Registry.ENTITY_TYPE, new Identifier(Init.MODID, "cube"), CUBE);
		FabricDefaultAttributeRegistry.register(CUBE, CubeEntity.createLivingAttributes());
	}
}
