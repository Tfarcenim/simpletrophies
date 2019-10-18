package tfar.simpletrophies.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tfar.simpletrophies.SimpleTrophies;
import tfar.simpletrophies.common.block.BlockSimpleTrophy;
import tfar.simpletrophies.common.etc.DateHelpers;
import tfar.simpletrophies.common.etc.TrophyHelpers;
import tfar.simpletrophies.common.tile.TileSimpleTrophy;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import static tfar.simpletrophies.common.config.TrophyConfig.ClientConfig.SHOW_EARNEDAT;

public class ItemSimpleTrophy extends BlockItem {
	public ItemSimpleTrophy(Block block, Properties properties) {
		super(block,properties);
	}


	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if(!stack.hasTag()) stack.setTag(new CompoundNBT());
		//Add all of the other NBT tags if they don't already exist
		CompoundNBT nbt = stack.getTag();
		assert nbt != null;
		if(!nbt.contains(BlockSimpleTrophy.KEY_COLOR_RED)) nbt.putInt(BlockSimpleTrophy.KEY_COLOR_RED, 255);
		if(!nbt.contains(BlockSimpleTrophy.KEY_COLOR_GREEN)) nbt.putInt(BlockSimpleTrophy.KEY_COLOR_GREEN, 255);
		if(!nbt.contains(BlockSimpleTrophy.KEY_COLOR_BLUE)) nbt.putInt(BlockSimpleTrophy.KEY_COLOR_BLUE, 255);
		if(!nbt.contains(BlockSimpleTrophy.KEY_ITEM)) nbt.put(BlockSimpleTrophy.KEY_ITEM, ItemStack.EMPTY.serializeNBT());
		if(!nbt.contains(BlockSimpleTrophy.KEY_NAME)) nbt.putString(BlockSimpleTrophy.KEY_NAME, "");
		if(!nbt.contains(BlockSimpleTrophy.KEY_VARIANT)) nbt.putString(BlockSimpleTrophy.KEY_VARIANT, "classic");
		if(!nbt.contains(BlockSimpleTrophy.KEY_EARNED_AT)) nbt.putLong(BlockSimpleTrophy.KEY_EARNED_AT, DateHelpers.now());
		
		if(entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative()) {
			//Move vanilla customname stuff (italic) over to my own system
			//This just lets people rename the trophy in an anvil instead of needing to manually NBT hack
			//and have it not show up all... italicy and weird
			if(stack.hasDisplayName()) {
				String customName = stack.getDisplayName().getUnformattedComponentText();
				nbt.putString(BlockSimpleTrophy.KEY_NAME, customName.equals("<CLEAR>") ? "" : customName);
				stack.clearCustomName();
				
				//remove the funky anvil tag too
				if(nbt.contains("RepairCost")) nbt.remove("RepairCost");
			}
		}
	}
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		ITextComponent trophyName = TrophyHelpers.getDisplayedName(stack);
		return trophyName != null ? trophyName : super.getDisplayName(stack);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag mistake) {
		ItemStack displayedStack = TrophyHelpers.getDisplayedStack(stack);
		if(!displayedStack.isEmpty()) {
			//add the "Displayed" tooltip
			tooltip.add(new TranslationTextComponent("simple_trophies.misc.tooltip.displaying",displayedStack.getDisplayName()).applyTextStyles(displayedStack.getRarity().color));
			
			//add additional debugging information
			if(mistake.isAdvanced()) {
				StringBuilder bob = new StringBuilder();
				bob.append("   ");
				bob.append(TextFormatting.DARK_GRAY);
				bob.append(displayedStack.getItem().getRegistryName());
				bob.append(" (#");
				bob.append(Item.getIdFromItem(displayedStack.getItem()));
				bob.append('/');
//				bob.append(displayedStack.getItemDamage());
				bob.append(')');
				tooltip.add(new StringTextComponent(bob.toString()));
			}
			
			//add the item itself's tooltip. Why not?
			List<ITextComponent> displayedTooltip = new ArrayList<>();
			displayedStack.getItem().addInformation(displayedStack, world, displayedTooltip, mistake);
			displayedTooltip.forEach(s -> tooltip.add(new StringTextComponent("   " + s)));
		}

		
		long time = TrophyHelpers.getEarnTime(stack);
		if(SHOW_EARNEDAT.get() && time != 0) {
			tooltip.add(new TranslationTextComponent("simple_trophies.misc.earnedAt", DateHelpers.epochToString(time)));
		}

		super.addInformation(stack, world, tooltip, mistake);
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		ItemStack displayedItem = TrophyHelpers.getDisplayedStack(stack);
		return displayedItem.isEmpty() ? Rarity.COMMON : displayedItem.getRarity();
	}

	@Override
	protected boolean placeBlock(BlockItemUseContext context, BlockState newState) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		PlayerEntity player = context.getPlayer();
		ItemStack stack = context.getItem();

		if (!world.setBlockState(pos, newState, 11)) return false;

		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == this.getBlock()) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileSimpleTrophy) {
				TrophyHelpers.populateTileNBTFromStack(stack, (TileSimpleTrophy) tile);
			}

			this.getBlock().onBlockPlacedBy(world, pos, state, player, stack);

			if (player instanceof ServerPlayerEntity) {
				CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
			}
		}

		return true;	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if(player.isCreative()) {
			ItemStack held = player.getHeldItem(hand);
			if(held.hasTag() && world.isRemote) {
				CompoundNBT cmp = held.getTag().copy();
				//Remove earned time since authors aren't likely to want that
				cmp.remove(BlockSimpleTrophy.KEY_EARNED_AT);
				String str = cmp.toString();
				
				SimpleTrophies.LOG.info(str);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(str), null);
				player.sendStatusMessage(new TranslationTextComponent("simple_trophies.misc.copied"), true);
			}
			return new ActionResult<>(ActionResultType.SUCCESS, held);
		}
		
		return super.onItemRightClick(world, player, hand);
	}
}
