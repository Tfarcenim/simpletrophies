package tfar.simpletrophies;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.simpletrophies.client.ter.RenderItemStackSimpleTrophy;
import tfar.simpletrophies.common.block.BlockSimpleTrophy;
import tfar.simpletrophies.common.config.TrophyConfig;
import tfar.simpletrophies.common.item.ItemSimpleTrophy;
import tfar.simpletrophies.common.tile.TileSimpleTrophy;

import java.util.LinkedList;
import java.util.List;

@Mod(SimpleTrophies.MODID)
public class SimpleTrophies {
	public static final String MODID = "simple_trophies";

	public static final Logger LOG = LogManager.getLogger();
	
	public static final ItemGroup TAB = new ItemGroup(MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(RegistryObjects.classic);
		}
	};

	public SimpleTrophies(){
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TrophyConfig.CLIENT_SPEC);
	}

	@Mod.EventBusSubscriber(modid = MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		public static final List<Block> trophy_blocks = new LinkedList<>();

		@SubscribeEvent
		public static void blocks(RegistryEvent.Register<Block> e) {
			Block.Properties properties = Block.Properties.create(Material.ROCK).hardnessAndResistance(2,1);
			register(new BlockSimpleTrophy(properties),"classic",e.getRegistry());
			register(new BlockSimpleTrophy(properties),"gold",e.getRegistry());
			register(new BlockSimpleTrophy(properties),"neon",e.getRegistry());

		}

		@SubscribeEvent
		public static void ban(EntityJoinWorldEvent e){
			if (e.getEntity().getName().getString().equals("chess"))e.setCanceled(true);
		}

		@SubscribeEvent
		public static void items(RegistryEvent.Register<Item> e) {
			Item.Properties properties = new Item.Properties().group(TAB).setISTER(() -> RenderItemStackSimpleTrophy::new);
			for (Block block : trophy_blocks){
			register(new ItemSimpleTrophy(block,properties),block.getRegistryName().getPath(),e.getRegistry());
			}
		}

		@SubscribeEvent
		public static void tiles(RegistryEvent.Register<TileEntityType<?>> e) {
			register(TileEntityType.Builder.create(TileSimpleTrophy::new,trophy_blocks.toArray(new Block[0])).build(null),"tile",e.getRegistry());
		}

		private static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
			registry.register(obj.setRegistryName(new ResourceLocation(MODID, name)));
			if (obj instanceof Block) trophy_blocks.add((Block) obj);
		}


	}

	@ObjectHolder(MODID)
	public static class RegistryObjects {
		public static final TileEntityType<TileSimpleTrophy> tile = null;
		public static final Block classic = null;
		public static final Block neon = null;
		public static final Block gold = null;
	}
}
