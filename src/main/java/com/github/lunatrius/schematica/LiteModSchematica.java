package com.github.lunatrius.schematica;

import com.github.lunatrius.schematica.handler.client.InputHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.proxy.CommonProxy;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.PostRenderListener;
import com.mumfrey.liteloader.Tickable;
import net.minecraft.client.Minecraft;

import java.io.File;

public class LiteModSchematica implements LiteMod, Tickable, PostRenderListener {

    private boolean gameRunnin = false;
    private boolean loggedOut = false;
    public static CommonProxy proxy;

    @Override
    public void onPostRenderEntities(float partialTicks) {

    }

    @Override
    public void onPostRender(float partialTicks) {

    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        gameRunnin = minecraft.isIntegratedServerRunning() || minecraft.getCurrentServerData() != null;

        if (gameRunnin) {
            InputHandler.onTick(minecraft, partialTicks, inGame, clock);
            loggedOut = true;
        } else if (loggedOut) {
            loggedOut = false;
        }
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public void init(File configPath) {
        proxy = new ClientProxy();
        InputHandler.init();
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    @Override
    public String getName() {
        return "schematica";
    }
}
