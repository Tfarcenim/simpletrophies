package tfar.simpletrophies.client.tesr;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import tfar.simpletrophies.SimpleTrophies;
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
	public void renderByItem(ItemStack stack) {
		if (NO_TEISR.get() || !(stack.getItem() instanceof ItemSimpleTrophy)) return;
		GlStateManager.pushMatrix();
		if (itemRenderer == null) itemRenderer = mc.getItemRenderer();
		model.handlePerspective(transform);
		GlStateManager.translatef(0.5F, 0.5F, 0.5F);
		this.itemRenderer.renderItem(stack, model.internal);

		ItemStack displayedStack = TrophyHelpers.getDisplayedStack(stack);

		if(!displayedStack.isEmpty()) {
			//float ticks = ClientGameEvents.getPauseAdjustedTicksAndPartialTicks();
			double ticks =  360d * (System.currentTimeMillis() & 0x3FFF) / 0x3FFF;

			//spread out animations a little bit.
			//...Used to use an actually pretty good hash function here, but I like the way this one makes
			//lines of trophies on the ground make a little wave. Wooo!
			//ticks += (te.getPos().getX() ^ te.getPos().getZ()) * 30;

			GlStateManager.pushMatrix();

			GlStateManager.translated( 0,  + .25 + Math.sin(ticks / 25f) / 7f,  + 0);

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

		GlStateManager.popMatrix();
	}
}
