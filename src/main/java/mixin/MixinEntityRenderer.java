package mixin;

import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Inject(method = "renderWorldPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;mcProfiler:Lnet/minecraft/profiler/Profiler;", ordinal = 17, shift = At.Shift.BEFORE))
    public void postRender(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci){
        RenderSchematic.INSTANCE.onRenderWorldLast(partialTicks);
    }
    // TODO: fix proper inject to only send events to needed classes instead of randomly fire.
}
