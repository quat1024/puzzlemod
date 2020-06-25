package agency.highlysuspect.puzzle.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public interface HoldableEntity {
	default void onGrab(PlayerEntity carrier) {}
	
	default void whileHeld(PlayerEntity carrier, Vec3d spot) {}
	
	default boolean canDrop(PlayerEntity carrier, Vec3d spot) { return true; }
	
	default void drop(PlayerEntity carrier, Vec3d spot) {}
}
