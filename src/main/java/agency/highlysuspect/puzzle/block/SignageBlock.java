package agency.highlysuspect.puzzle.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SignageBlock extends Block {
	public SignageBlock(Settings settings) {
		super(settings);
		
		setDefaultState(getDefaultState().with(Properties.FACING, Direction.UP));
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(Properties.FACING, ctx.getSide());
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(Properties.FACING));
	}
	
	private static final VoxelShape NORTH = createCuboidShape(0, 0, 14, 16, 16, 16);
	private static final VoxelShape SOUTH = createCuboidShape(0, 0, 0, 16, 16, 2);
	private static final VoxelShape EAST = createCuboidShape(0, 0, 0, 2, 16, 16);
	private static final VoxelShape WEST = createCuboidShape(14, 0, 0, 16, 16, 16);
	private static final VoxelShape UP = createCuboidShape(0, 0, 0, 16, 2, 16);
	private static final VoxelShape DOWN = createCuboidShape(0, 14, 0, 16, 16, 16);
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch(state.get(Properties.FACING)) {
			case NORTH: return NORTH;
			case SOUTH: return SOUTH;
			case EAST: return EAST;
			case WEST: return WEST;
			case UP: return UP;
			case DOWN: default: return DOWN; 
		}
	}
	
	public static class Indicator extends SignageBlock {
		public Indicator(Settings settings) {
			super(settings);
			
			setDefaultState(getDefaultState().with(Properties.POWERED, false));
		}
		
		@Override
		public BlockState getPlacementState(ItemPlacementContext ctx) {
			//noinspection ConstantConditions
			return super.getPlacementState(ctx).with(Properties.POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
		}
		
		@Override
		public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
			boolean isPowered = state.get(Properties.POWERED);
			boolean shouldPower = world.isReceivingRedstonePower(pos);
			if(isPowered != shouldPower) {
				world.setBlockState(pos, state.with(Properties.POWERED, shouldPower));
			}
		}
		
		@Override
		protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
			super.appendProperties(builder.add(Properties.POWERED));
		}
	}
}
