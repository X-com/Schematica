package mixin;

import com.github.lunatrius.schematica.util.ITileEntityRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher implements ITileEntityRendererDispatcher {
    private net.minecraft.client.renderer.Tessellator batchBuffer = new net.minecraft.client.renderer.Tessellator(0x200000);
    private boolean drawingBatch = false;

    @Shadow public TextureManager renderEngine;

    /**
     * Forge Method
     */
    public void preDrawBatch() {
        batchBuffer.getBuffer().begin(org.lwjgl.opengl.GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK);
        drawingBatch = true;
    }

    /**
     * Forge method
     */
    public void drawBatch(int pass) {
        renderEngine.bindTexture(net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (net.minecraft.client.Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(org.lwjgl.opengl.GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(org.lwjgl.opengl.GL11.GL_FLAT);
        }

        if (pass > 0) {
            net.minecraft.util.math.Vec3d cameraPos = IMixinActiveRenderInfo.getPosition();
            batchBuffer.getBuffer().sortVertexData((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
        }
        batchBuffer.draw();

        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        drawingBatch = false;
    }
}
