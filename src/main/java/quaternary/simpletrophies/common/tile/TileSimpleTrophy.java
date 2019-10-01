package quaternary.simpletrophies.common.tile;

import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import quaternary.simpletrophies.SimpleTrophies;
import quaternary.simpletrophies.common.block.BlockSimpleTrophy;
import quaternary.simpletrophies.common.etc.EnumTrophyVariant;

import javax.annotation.Nullable;

public class TileSimpleTrophy extends TileEntity {
	public ItemStack displayedStack = ItemStack.EMPTY;
	public String displayedName = "";
	public int displayedColorRed = 255;
	public int displayedColorGreen = 255;
	public int displayedColorBlue = 255;
	public EnumTrophyVariant displayedVariant = EnumTrophyVariant.CLASSIC;
	public long earnedTime = 0;

	public TileSimpleTrophy() {
		super(SimpleTrophies.RegistryObjects.tile);
	}

	public String getLocalizedName() {
		return I18n.format(displayedName);
	}
	
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 6969, getUpdateTag());
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}
	
	@Override
	public void handleUpdateTag(CompoundNBT nbt) {
		readFromNBTInternal(nbt);
		BlockState hahaYes = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, hahaYes, hahaYes, 3);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		writeToNBTInternal(nbt);
		return super.write(nbt);
	}
	
	@Override
	public void read(CompoundNBT nbt) {
		super.read(nbt);
		readFromNBTInternal(nbt);
	}
	
	public CompoundNBT writeToNBTInternal(CompoundNBT nbt) {
		nbt.put(BlockSimpleTrophy.KEY_ITEM, displayedStack.serializeNBT());
		nbt.putString(BlockSimpleTrophy.KEY_NAME, displayedName);
		nbt.putInt(BlockSimpleTrophy.KEY_COLOR_RED, displayedColorRed);
		nbt.putInt(BlockSimpleTrophy.KEY_COLOR_GREEN, displayedColorGreen);
		nbt.putInt(BlockSimpleTrophy.KEY_COLOR_BLUE, displayedColorBlue);
		nbt.putString(BlockSimpleTrophy.KEY_VARIANT, displayedVariant.getName());
		nbt.putLong(BlockSimpleTrophy.KEY_EARNED_AT, earnedTime);
		
		return nbt;
	}
	
	public void readFromNBTInternal(CompoundNBT nbt) {
		displayedStack = ItemStack.read((CompoundNBT) nbt.get(BlockSimpleTrophy.KEY_ITEM));
		displayedName = nbt.getString(BlockSimpleTrophy.KEY_NAME);
		displayedColorRed = nbt.getInt(BlockSimpleTrophy.KEY_COLOR_RED);
		displayedColorGreen = nbt.getInt(BlockSimpleTrophy.KEY_COLOR_GREEN);
		displayedColorBlue = nbt.getInt(BlockSimpleTrophy.KEY_COLOR_BLUE);
		displayedVariant = EnumTrophyVariant.fromString(nbt.getString(BlockSimpleTrophy.KEY_VARIANT));
		earnedTime = nbt.getLong(BlockSimpleTrophy.KEY_EARNED_AT);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos).expand(0, 0.5, 0);
	}
}
