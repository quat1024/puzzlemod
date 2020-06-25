package agency.highlysuspect.puzzle;

import agency.highlysuspect.puzzle.item.PuzzleItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class Permissions {
	//TODO: Remove this after Modfest!!! Lmao!!!
	// I just don't want to pester mods on the showcase server to add puzzle regions for me. Lmao
	private static final UUID IDK_PROBABLY_SOME_RANDOM_DERG_NOTHING_TO_SEE_HERE = UUID.fromString("873dea16-d058-4343-861c-f62c21da124b");
	
	public static boolean hasPermission(ServerPlayerEntity player) {
		return player.hasPermissionLevel(2) ||
			player.getUuid().equals(IDK_PROBABLY_SOME_RANDOM_DERG_NOTHING_TO_SEE_HERE) ||
			player.inventory.contains(new ItemStack(PuzzleItems.CHEATSEY_ITEM));
	}
}
