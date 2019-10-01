package quaternary.simpletrophies.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import quaternary.simpletrophies.SimpleTrophies;
import quaternary.simpletrophies.client.tesr.RenderTileSimpleTrophy;
import quaternary.simpletrophies.common.tile.TileSimpleTrophy;

import static quaternary.simpletrophies.common.config.TrophyConfig.ClientConfig.NO_TEISR;
import static quaternary.simpletrophies.common.config.TrophyConfig.ClientConfig.NO_TESR;

@Mod.EventBusSubscriber(modid = SimpleTrophies.MODID, value = Dist.CLIENT)
public class ClientGameEvents {
	@SubscribeEvent
	public static void models(ModelRegistryEvent e) {
		if (!NO_TEISR.get()) {

		/*	SimpleTrophiesItems.TROPHY.setTileEntityItemStackRenderer(new RenderItemStackSimpleTrophy());
			((SimpleReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(resourceManager -> {
				return RenderItemStackSimpleTrophy.dumpCache();
			});
		}*/

			if (!NO_TESR.get()) {
				ClientRegistry.bindTileEntitySpecialRenderer(TileSimpleTrophy.class, new RenderTileSimpleTrophy());
			}
		}
	}

	@SubscribeEvent
	public static void blockColors(ColorHandlerEvent.Block e) {
		BlockColors bc = e.getBlockColors();
		/*bc.register(
						(state, world, pos, tintIndex) -> {
			if(world == null || pos == null || tintIndex != 0) return 0xFFFFFF;
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileSimpleTrophy) {
				TileSimpleTrophy trophy = (TileSimpleTrophy) tile;
				return TrophyHelpers.getCombinedColor(trophy);
			} else return 0xFFFFFF;
		}, SimpleTrophiesBlocks.TROPHY);*/
	}
	
	@SubscribeEvent
	public static void itemColors(ColorHandlerEvent.Item e) {
		//Has no effect unless skipTeisr is on, btw
		/*ItemColors ic = e.getItemColors();
		ic.register((stack, tintIndex) -> {
			if(tintIndex != 0) return 0xFFFFFF;
			else return TrophyHelpers.getCombinedColor(stack);
		}, SimpleTrophiesItems.TROPHY);*/
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
