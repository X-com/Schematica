package mixin;

import com.github.lunatrius.schematica.handler.QueueTickHandler;
import com.github.lunatrius.schematica.handler.client.GuiHandler;
import com.github.lunatrius.schematica.handler.client.RenderTickHandler;
import com.github.lunatrius.schematica.handler.client.TickHandler;
import com.github.lunatrius.schematica.handler.client.WorldHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    // TODO: Add close sign GUI when printing.

    @Shadow public WorldClient world;

    @Inject(method = "runGameLoop", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;mcProfiler:Lnet/minecraft/profiler/Profiler;", ordinal = 12, shift = At.Shift.AFTER))
    public void postRender(CallbackInfo ci) {
        // TODO: add checks for non-constant loops through this check.
        RenderTickHandler.INSTANCE.onRenderTick();
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    public void preClientTick(CallbackInfo ci) {
        // TODO: add checks for non-constant loops through this check.
        QueueTickHandler.INSTANCE.onClientTick();
    }

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;mcProfiler:Lnet/minecraft/profiler/Profiler;", ordinal = 12, shift = At.Shift.AFTER))
    public void postClientTick(CallbackInfo ci) {
        // TODO: add checks for non-constant loops through this check.
        TickHandler.INSTANCE.onClientTick();
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    public void onUnloadWorld(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        WorldHandler.INSTANCE.onUnload(world);
    }

    @ModifyVariable(method = "displayGuiScreen", at = @At("HEAD"), argsOnly = true)
    private Gui GuiScreen(Gui gui) {
        return GuiHandler.onGuiOpen(gui);
    }
}
