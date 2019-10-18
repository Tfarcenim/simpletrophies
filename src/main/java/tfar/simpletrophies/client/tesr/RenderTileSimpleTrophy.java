package tfar.simpletrophies.client.tesr;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import tfar.simpletrophies.SimpleTrophies;
import tfar.simpletrophies.common.etc.DateHelpers;
import tfar.simpletrophies.common.tile.TileSimpleTrophy;

import static tfar.simpletrophies.common.config.TrophyConfig.ClientConfig.NO_TESR;
import static tfar.simpletrophies.common.config.TrophyConfig.ClientConfig.SHOW_EARNEDAT;

public class RenderTileSimpleTrophy extends TileEntityRenderer<TileSimpleTrophy> {
	@Override
	public void render(TileSimpleTrophy te, double x, double y, double z, float partialTicks, int destroyStage) {
		if(te == null || NO_TESR.get()) return;
		
		ItemStack displayedStack = te.displayedStack;
		
		if(!displayedStack.isEmpty()) {
			//float ticks = ClientGameEvents.getPauseAdjustedTicksAndPartialTicks();
			double ticks =  360d * (System.currentTimeMillis() & 0x3FFF) / 0x3FFF ;

			//spread out animations a little bit.
			//...Used to use an actually pretty good hash function here, but I like the way this one makes
			//lines of trophies on the ground make a little wave. Wooo!
			//ticks += (te.getPos().getX() ^ te.getPos().getZ()) * 30;
			
			GlStateManager.pushMatrix();
			
			GlStateManager.translated(x + .5, y + .6 + Math.sin(ticks / 25f) / 7f, z + .5);
			
	//		if(!Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(displayedStack).isGui3d()) {
		//		GlStateManager.translated(0, 0.2, 0);
		//	}
			
			GlStateManager.rotated((ticks * 2.5f) % 360, 0, 1, 0);
			GlStateManager.scaled(1.6, 1.6, 1.6);
			try {
				
				Minecraft.getInstance().getItemRenderer().renderItem(displayedStack, ItemCameraTransforms.TransformType.GROUND);
			} catch(Exception oof) {
				SimpleTrophies.LOG.error("Problem rendering item on a trophy TESR", oof);
			}
			
			GlStateManager.enableBlend(); //fix a stateleak in renderitem >.>
			
			GlStateManager.popMatrix();
		}
		
		RayTraceResult hit = rendererDispatcher.cameraHitResult;
		if(hit instanceof BlockRayTraceResult && te.getPos().equals(((BlockRayTraceResult)hit).getPos())) {
			setLightmapDisabled(true);
			if(SHOW_EARNEDAT.get() && te.earnedTime != 0) {
				String formattedTime = DateHelpers.epochToString(te.earnedTime);
				drawNameplate(te, formattedTime, x, y + 0.3, z, 12);
			}
			String name = te.getLocalizedName();
			if (!name.isEmpty()) {
				drawNameplate(te, name, x, y, z, 12);
			}
			setLightmapDisabled(false);
		}
	}
}
