package mixin;

import com.github.lunatrius.schematica.handler.client.WorldHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public class MixinWorldClient {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void postClientTick(NetHandlerPlayClient netHandler, WorldSettings settings, int dimension, EnumDifficulty difficulty, Profiler profilerIn, CallbackInfo ci) {
        WorldHandler.INSTANCE.onLoad((World)(Object)this);
    }
}
