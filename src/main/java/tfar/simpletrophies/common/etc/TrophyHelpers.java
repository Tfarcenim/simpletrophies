package tfar.simpletrophies.common.etc;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import tfar.simpletrophies.common.block.BlockSimpleTrophy;
import tfar.simpletrophies.common.tile.TileSimpleTrophy;

import javax.annotation.Nullable;

public class TrophyHelpers {
	public static int getCombinedColor(ItemStack stack) {
		if(stack.hasTag()) return getCombinedColor(stack.getTag());
		else return 0xFFFFFF;
	}
	
	public static int getCombinedColor(TileSimpleTrophy tile) {
		return (tile.displayedColorRed << 16) | (tile.displayedColorGreen << 8) | tile.displayedColorBlue;
	}
	
	public static int getCombinedColor(CompoundNBT nbt) {
		int red = nbt.contains(BlockSimpleTrophy.KEY_COLOR_RED) ? nbt.getInt(BlockSimpleTrophy.KEY_COLOR_RED) : 255;
		int green = nbt.contains(BlockSimpleTrophy.KEY_COLOR_GREEN) ? nbt.getInt(BlockSimpleTrophy.KEY_COLOR_GREEN) : 255;
		int blue = nbt.contains(BlockSimpleTrophy.KEY_COLOR_BLUE) ? nbt.getInt(BlockSimpleTrophy.KEY_COLOR_BLUE) : 255;
		red = MathHelper.clamp(red, 0, 255);
		green = MathHelper.clamp(green, 0, 255);
		blue = MathHelper.clamp(blue, 0, 255);
		return (red << 16) | (green << 8) | blue;
	}
	
	public static ItemStack getDisplayedStack(ItemStack stack) {
		if(stack.hasTag()) return getDisplayedStack(stack.getTag());
		else return ItemStack.EMPTY;
	}
	
	public static ItemStack getDisplayedStack(CompoundNBT nbt) {
		if(nbt.contains(BlockSimpleTrophy.KEY_ITEM)) return ItemStack.read((CompoundNBT) nbt.get(BlockSimpleTrophy.KEY_ITEM));
		else return ItemStack.EMPTY;
	}

	@Nullable
	public static ITextComponent getDisplayedName(ItemStack stack) {
		if(stack.hasTag()) return new TranslationTextComponent(getDisplayedName(stack.getTag()));
		else return null;
	}
	
	public static String getDisplayedName(CompoundNBT nbt) {
		if(nbt.contains(BlockSimpleTrophy.KEY_NAME)) return nbt.getString(BlockSimpleTrophy.KEY_NAME);
		else return "";
	}

	public static long getEarnTime(ItemStack stack) {
		if(stack.hasTag()) return stack.getTag().getLong(BlockSimpleTrophy.KEY_EARNED_AT);
		else return 0;
	}
	
	public static void populateStackNBTFromTile(ItemStack stack, TileSimpleTrophy tile) {
		if(tile.displayedStack.isEmpty() && tile.displayedName.isEmpty()) return;
		
		if(!stack.hasTag()) stack.setTag(new CompoundNBT());
		CompoundNBT nbt = stack.getTag();
		assert nbt != null;
		
		nbt.merge(tile.writeToNBTInternal(new CompoundNBT()));
	}
	
	public static void populateTileNBTFromStack(ItemStack stack, TileSimpleTrophy tile) {
		if(!stack.hasTag()) {
			tile.displayedName = "";
			tile.displayedStack = ItemStack.EMPTY;
			tile.displayedColorRed = 255;
			tile.displayedColorGreen = 255;
			tile.displayedColorBlue = 255;
			tile.earnedTime = 0;
		} else tile.readFromNBTInternal(stack.getTag());
	}

	public static ItemStack createItemStackFromTile(BlockPos pos, IBlockReader world, Block block) {
		TileEntity tile = world.getTileEntity(pos);
		ItemStack trophy = new ItemStack(block);
		if(tile instanceof TileSimpleTrophy) populateStackNBTFromTile(trophy, (TileSimpleTrophy)tile);
		return trophy;
	}
}
