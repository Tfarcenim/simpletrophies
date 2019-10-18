package tfar.simpletrophies.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import tfar.simpletrophies.SimpleTrophies;
import tfar.simpletrophies.client.tesr.RenderItemStackSimpleTrophy;
import tfar.simpletrophies.client.tesr.RenderTileSimpleTrophy;
import tfar.simpletrophies.client.tesr.TrophyModelWrapper;
import tfar.simpletrophies.common.etc.TrophyHelpers;
import tfar.simpletrophies.common.tile.TileSimpleTrophy;

import java.util.Map;

import static tfar.simpletrophies.common.config.TrophyConfig.ClientConfig.NO_TEISR;

@Mod.EventBusSubscriber(modid = SimpleTrophies.MODID, value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientGameEvents {
	@SubscribeEvent
	public static void models(ModelRegistryEvent e) {
		if (!NO_TEISR.get()) {

		}
				ClientRegistry.bindTileEntitySpecialRenderer(TileSimpleTrophy.class, new RenderTileSimpleTrophy());
		}

	@SubscribeEvent
	public static void blockColors(ColorHandlerEvent.Block e) {
		BlockColors bc = e.getBlockColors();
		IBlockColor iBlockColor = (state, world, pos, tintIndex) -> {
			if(world == null || pos == null || tintIndex != 0) return 0xFFFFFF;
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileSimpleTrophy) {
				TileSimpleTrophy trophy = (TileSimpleTrophy) tile;
				return TrophyHelpers.getCombinedColor(trophy);
			} else return 0xFFFFFF;
		};
		bc.register(iBlockColor, SimpleTrophies.RegistryEvents.trophy_blocks.toArray(new Block[0]));
	}
	
	@SubscribeEvent
	public static void itemColors(ColorHandlerEvent.Item e) {
		//Has no effect unless skipTeisr is on, btw
		ItemColors ic = e.getItemColors();
		IItemColor iItemColor = (stack, tintIndex) -> {
			if(tintIndex != 0) return 0xFFFFFF;
			else return TrophyHelpers.getCombinedColor(stack);
		};
		ic.register(iItemColor, SimpleTrophies.RegistryEvents.trophy_blocks.toArray(new Block[0]));
	}
	
	private static long ticksInGame = 0;
	private static boolean paused = false;
	private static float lastNonPausedPartialTicks = 0;
	
	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent e) {
		if(e.phase == TickEvent.Phase.END) {
			Minecraft mc = Minecraft.getInstance();
			Screen ui = mc.currentScreen;
			//same method mc uses to determine if the game is paused
			if(mc.isSingleplayer() && ui != null && ui.isPauseScreen() && mc.getIntegratedServer() != null && !mc.getIntegratedServer().getPublic()) {
				paused = true;
			} else {
				ticksInGame++;
				paused = false;
			}
		}
	}

	public static final ModelResourceLocation[] modelResourceLocations = new ModelResourceLocation[]{
					new ModelResourceLocation(SimpleTrophies.MODID+":classic","inventory"),
					new ModelResourceLocation(SimpleTrophies.MODID+":gold","inventory"),
					new ModelResourceLocation(SimpleTrophies.MODID+":neon","inventory"),
	};

	@SubscribeEvent
	public static void modelbake(ModelBakeEvent e){
		Map<ResourceLocation, IBakedModel> modelResourceLocationIBakedModelMap = e.getModelRegistry();
		for (int i = 0; i < 3;i++) {
			TrophyModelWrapper model = new TrophyModelWrapper(modelResourceLocationIBakedModelMap.get(modelResourceLocations[i]));
			RenderItemStackSimpleTrophy.teisrs.get(i).setModel(model);
			modelResourceLocationIBakedModelMap.put(modelResourceLocations[i], model);
		}
	}
	
	public static long getTicksInGame() {
		return ticksInGame;
	}
	
	/** Doesn't return a changing partialTicks value when the game is paused, to prevent "jitter" behavior */
	public static float getPauseAdjustedPartialTicks() {
		//honestly should just prolly AT that shit in Minecraft.java, i mean
		if(paused) return lastNonPausedPartialTicks;
		else {
			return lastNonPausedPartialTicks = Minecraft.getInstance().getRenderPartialTicks();
		}
	}
	
	public static float getPauseAdjustedTicksAndPartialTicks() {
		return getTicksInGame() + getPauseAdjustedPartialTicks();
	}
}
