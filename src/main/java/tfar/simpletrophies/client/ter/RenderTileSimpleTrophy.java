package tfar.simpletrophies.client.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import tfar.simpletrophies.SimpleTrophies;
import tfar.simpletrophies.common.config.TrophyConfig;
import tfar.simpletrophies.common.tile.TileSimpleTrophy;

import static tfar.simpletrophies.common.config.TrophyConfig.ClientConfig.NO_TESR;
import static tfar.simpletrophies.common.config.TrophyConfig.ClientConfig.SHOW_EARNEDAT;

public class RenderTileSimpleTrophy extends TileEntityRenderer<TileSimpleTrophy> {
	public RenderTileSimpleTrophy(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(TileSimpleTrophy te, float partialTicks, MatrixStack matrices, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if(NO_TESR.get()) return;
		
		ItemStack displayedStack = te.displayedStack;
		
		if(!displayedStack.isEmpty()) {
			//float ticks = ClientGameEvents.getPauseAdjustedTicksAndPartialTicks();
			double ticks = ( Util.milliTime()/20f + partialTicks) * .5;

			//spread out animations a little bit.
			//...Used to use an actually pretty good hash function here, but I like the way this one makes
			//lines of trophies on the ground make a little wave. Wooo!
			//ticks += (te.getPos().getX() ^ te.getPos().getZ()) * 30;
			matrices.push();
			matrices.translate(.5, .6 + Math.sin(ticks / 25f) / 7f,.5);
			
	//		if(!Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(displayedStack).isGui3d()) {
		//		GlStateManager.translated(0, 0.2, 0);
		//	}
			matrices.rotate(Vector3f.YP.rotationDegrees((float)(ticks * 2.5)));

			float scale = TrophyConfig.ClientConfig.SCALE.get().floatValue();
			matrices.scale(scale, scale, scale);
			try {
				Minecraft.getInstance().getItemRenderer().renderItem(displayedStack, ItemCameraTransforms.TransformType.GROUND,combinedLightIn,combinedOverlayIn,matrices,bufferIn);

				//Minecraft.getInstance().getItemRenderer().renderItem(displayedStack, ItemCameraTransforms.TransformType.GROUND);
			} catch(Exception oof) {
				SimpleTrophies.LOG.error("Problem rendering item on a trophy TESR", oof);
			}
			
			//GlStateManager.enableBlend(); //fix a stateleak in renderitem >.>
			
			matrices.pop();
		}
		
		RayTraceResult hit = renderDispatcher.cameraHitResult;
		if(hit instanceof BlockRayTraceResult && te.getPos().equals(((BlockRayTraceResult)hit).getPos())) {
			//setLightmapDisabled(true);
			if(SHOW_EARNEDAT.get() && te.earnedTime != 0) {
				//String formattedTime = DateHelpers.epochToString(te.earnedTime);
				renderName(te, displayedStack.getDisplayName().getFormattedText(),matrices,bufferIn,combinedLightIn);
			}
			String name = te.getLocalizedName();
			if (!name.isEmpty()) {
				//drawNameplate(te, name, x, y, z, 12);
			}
			//setLightmapDisabled(false);
		}
	}

	protected void renderName(TileSimpleTrophy blockEntity, String displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		double d0 = blockEntity.getDistanceSq(this.renderDispatcher.renderInfo.getProjectedView().x,
						this.renderDispatcher.renderInfo.getProjectedView().y, this.renderDispatcher.renderInfo.getProjectedView().z);
		if (!(d0 > 512.0D)) {
			matrixStackIn.push();
			matrixStackIn.translate(0.5, 1.3, 0.5);
			matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
			matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = matrixStackIn.getLast().getPositionMatrix();
			float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
			int j = (int)(f1 * 255.0F) << 24;
			FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
			float f2 = -fontrenderer.getStringWidth(displayNameIn) / 2f;
			fontrenderer.renderString(displayNameIn, f2, 0, 0xffffffff, false, matrix4f, bufferIn, false, j, packedLightIn);

			matrixStackIn.pop();
		}
	}

}
