package tfar.simpletrophies.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tfar.simpletrophies.common.etc.TrophyHelpers;
import tfar.simpletrophies.common.tile.TileSimpleTrophy;

import javax.annotation.Nullable;

public class BlockSimpleTrophy extends Block {
	//basically a bunch of stuff uses these keys so might as well slap them here
	public static final String KEY_NAME = "TrophyName";
	public static final String KEY_ITEM = "TrophyItem";
	public static final String KEY_COLOR_RED = "TrophyColorRed";
	public static final String KEY_COLOR_GREEN = "TrophyColorGreen";
	public static final String KEY_COLOR_BLUE = "TrophyColorBlue";
	public static final String KEY_VARIANT = "TrophyVariant";
	public static final String KEY_EARNED_AT = "TrophyEarnedAt";

	public BlockSimpleTrophy(Properties properties) {
		super(properties);
	}
	
	private static final VoxelShape SHAPE = Block.makeCuboidShape(0,0,0,16,6,16);

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileSimpleTrophy();
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		if(!player.isCreative()) return false;
		
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileSimpleTrophy) {
			TileSimpleTrophy trophy = (TileSimpleTrophy) tile;			
			int averageColor = -1;//getAverageDyeColorHeldByPlayer(player);
			if(averageColor == -1) {
				trophy.displayedStack = player.getHeldItem(hand).copy();
			} else {
				trophy.displayedColorRed = (averageColor & 0xFF0000) >> 16;
				trophy.displayedColorGreen = (averageColor & 0x00FF00) >> 8;
				trophy.displayedColorBlue = averageColor & 0x0000FF;
			}
			
			BlockState hahaYes = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, hahaYes, hahaYes, 2);
			trophy.markDirty();
			return true;
		}
		
		return false;
	}

	@Override
	public void onReplaced(BlockState state,World world, BlockPos pos, BlockState newState,boolean isMoving) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileSimpleTrophy) {
			spawnAsEntity(world, pos, TrophyHelpers.createItemStackFromTile(pos,world,this));
		}
		
		super.onReplaced(state,world, pos, newState,isMoving);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return TrophyHelpers.createItemStackFromTile(pos,world,this);
	}
}
