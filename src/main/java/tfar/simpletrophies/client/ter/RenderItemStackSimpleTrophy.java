package tfar.simpletrophies.client.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import tfar.simpletrophies.SimpleTrophies;
import tfar.simpletrophies.common.config.TrophyConfig;
import tfar.simpletrophies.common.etc.TrophyHelpers;
import tfar.simpletrophies.common.item.ItemSimpleTrophy;

import java.util.LinkedList;
import java.util.List;

import static tfar.simpletrophies.common.config.TrophyConfig.ClientConfig.NO_TEISR;

public class RenderItemStackSimpleTrophy extends ItemStackTileEntityRenderer {

	public static final Minecraft mc = Minecraft.getInstance();
	public TrophyModelWrapper model;
	public static ItemCameraTransforms.TransformType transform;
	public static List<RenderItemStackSimpleTrophy> teisrs = new LinkedList<>();
	private ItemRenderer itemRenderer;

	public RenderItemStackSimpleTrophy() {
		teisrs.add(this);
	}

	public RenderItemStackSimpleTrophy setModel(TrophyModelWrapper model) {
		this.model = model;
		return this;
	}

	@Override
	public void render(ItemStack stack, MatrixStack matrices, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if (NO_TEISR.get() || !(stack.getItem() instanceof ItemSimpleTrophy)) return;
		matrices.push();
		if (itemRenderer == null) itemRenderer = mc.getItemRenderer();
		//model.handlePerspective(transform,matrices);
		matrices.translate(0.5F, 0.5F, 0.5F);

			itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED,false,matrices,
						 bufferIn,combinedLightIn,combinedOverlayIn,model.internal);

		//this.itemRenderer.renderItem(stack, model.internal);

		ItemStack displayedStack = TrophyHelpers.getDisplayedStack(stack);

		if(!displayedStack.isEmpty()) {
			//float ticks = ClientGameEvents.getPauseAdjustedTicksAndPartialTicks();
			double ticks = ( Util.milliTime()/20f + mc.getRenderPartialTicks()) * .5;

			//spread out animations a little bit.
			//...Used to use an actually pretty good hash function here, but I like the way this one makes
			//lines of trophies on the ground make a little wave. Wooo!
			//ticks += (te.getPos().getX() ^ te.getPos().getZ()) * 30;

			matrices.push();

			matrices.translate( 0,  + .25 + Math.sin(ticks / 25f) / 7f,  + 0);

			//		if(!Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(displayedStack).isGui3d()) {
			//		GlStateManager.translated(0, 0.2, 0);
			//	}

			//GlStateManager.rotated((ticks * 2.5f) % 360, 0, 1, 0);
			matrices.rotate(Vector3f.YP.rotationDegrees((float)(ticks * 2.5) % 360));

			float scale = TrophyConfig.ClientConfig.SCALE.get().floatValue();
			matrices.scale(scale, scale, scale);

			try {

				Minecraft.getInstance().getItemRenderer().renderItem(displayedStack, ItemCameraTransforms.TransformType.GROUND,combinedLightIn,combinedOverlayIn,matrices,bufferIn);
			} catch(Exception oof) {
				SimpleTrophies.LOG.error("Problem rendering item on a trophy TESR", oof);
			}
			//GlStateManager.enableBlend(); //fix a stateleak in renderitem >.>
			matrices.pop();
		}
		matrices.pop();
	}
}
