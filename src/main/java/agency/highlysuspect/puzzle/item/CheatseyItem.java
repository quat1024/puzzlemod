package agency.highlysuspect.puzzle.item;

import agency.highlysuspect.puzzle.Init;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.List;

public class CheatseyItem extends Item {
	public CheatseyItem(Settings settings) {
		super(settings);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new TranslatableText(getTranslationKey() + ".tooltip"));
		tooltip.add(new TranslatableText(getTranslationKey() + ".tooltip2"));
	}
	
	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
	}
}
