package quaternary.simpletrophies;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quaternary.simpletrophies.common.block.BlockSimpleTrophy;
import quaternary.simpletrophies.common.config.TrophyConfig;
import quaternary.simpletrophies.common.tile.TileSimpleTrophy;

import java.util.HashSet;
import java.util.Set;

@Mod(SimpleTrophies.MODID)
public class SimpleTrophies {
	public static final String MODID = "simple_trophies";

	public static final Logger LOG = LogManager.getLogger();
	
	public static final ItemGroup TAB = new ItemGroup(MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(RegistryEvents.trophy_blocks.iterator().next());
		}
	};

	public SimpleTrophies(){
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TrophyConfig.CLIENT_SPEC);
	}

	@Mod.EventBusSubscriber(modid = MODID)
	public static class RegistryEvents {
		public static final Set<Block> trophy_blocks = new HashSet<>();

		@SubscribeEvent
		public static void blocks(RegistryEvent.Register<Block> e) {
			Block.Properties properties = Block.Properties.create(Material.ROCK).hardnessAndResistance(2,1);
			register(new BlockSimpleTrophy(properties),"",e.getRegistry());
		}

		@SubscribeEvent
		public static void items(RegistryEvent.Register<Item> e) {
			Item.Properties properties = new Item.Properties().group(TAB);
			for (Block block : trophy_blocks){
			register(new BlockItem(block,properties),block.getRegistryName().getPath(),e.getRegistry());
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
		public static final TileEntityType<?> tile = null;
	}
}
