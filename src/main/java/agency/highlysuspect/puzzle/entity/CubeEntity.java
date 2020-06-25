package agency.highlysuspect.puzzle.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

import java.util.Collections;

@SuppressWarnings("EntityConstructor")
public class CubeEntity extends LivingEntity implements HoldableEntity {
	public CubeEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
		
		stepHeight = 0;
	}
	
	//LivingEntity junk. Mostly copied from ArmorStandEntity since that is another living-but-not-really entity
	@Override
	public Iterable<ItemStack> getArmorItems() {
		return Collections.emptySet();
	}
	
	@Override
	public ItemStack getEquippedStack(EquipmentSlot slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		//Nope!
	}
	
	@Override
	public Arm getMainArm() {
		return Arm.RIGHT;
	}
	
	@Override
	public boolean isPushable() {
		return false;
	}
	
	@Override
	public void pushAwayFrom(Entity entity) {
		//Nope!
	}
}
