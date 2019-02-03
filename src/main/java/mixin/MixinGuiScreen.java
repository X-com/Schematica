package mixin;

import forgerelated.ClientCommandHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {
	@Shadow
	protected Minecraft mc;

	@Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;sendChatMessage(Ljava/lang/String;)V"), cancellable = true)
	private void addExecute(String msg, boolean addToChat, CallbackInfo ci) {
		if(ClientCommandHandler.instance.executeCommand(this.mc.player, msg) != 0) {
			ci.cancel();
		}
	}
}
