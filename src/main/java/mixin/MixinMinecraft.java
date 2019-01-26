package mixin;

import com.github.lunatrius.schematica.handler.client.RenderTickHandler;
import com.github.lunatrius.schematica.handler.client.TickHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    // TODO: Add close sign GUI when printing.

    @Inject(method = "runGameLoop", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;mcProfiler:Lnet/minecraft/profiler/Profiler;", ordinal = 12, shift = At.Shift.AFTER))
    public void postRender(CallbackInfo ci) {
        // TODO: add checks for non-constant loops through this check.
        RenderTickHandler.INSTANCE.onRenderTick();
    }

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;mcProfiler:Lnet/minecraft/profiler/Profiler;", ordinal = 12, shift = At.Shift.AFTER))
    public void postClientTick(CallbackInfo ci) {
        // TODO: add checks for non-constant loops through this check.
        TickHandler.INSTANCE.onClientTick();
    }
}
