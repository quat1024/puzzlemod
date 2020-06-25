package agency.highlysuspect.puzzle.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class PortalThemedBlock extends Block {
	public PortalThemedBlock(Settings settings) {
		super(settings);
	}
	
	//TODO: There's currently no sense of direction for these blocks.
	// If I actually want to use the white and black surfaces for like, portals...
	// I will need to actually know which sides are which.
	public static class Directional extends PortalThemedBlock {
		public Directional(Settings settings) {
			super(settings);
			
			setDefaultState(getDefaultState().with(Properties.FACING, Direction.NORTH));
		}
		
		@Override
		public BlockState getPlacementState(ItemPlacementContext ctx) {
			return getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite());
		}
		
		@Override
		protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
			super.appendProperties(builder.add(Properties.FACING));
		}
	}
	
	public static class Pillar extends Directional {
		public Pillar(Settings settings) {
			super(settings);
		}
		
		@Override
		public BlockState getPlacementState(ItemPlacementContext ctx) {
			return getDefaultState().with(Properties.FACING, ctx.getSide());
		}
	}
}
