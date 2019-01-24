package com.github.lunatrius.schematica;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.PostRenderListener;
import com.mumfrey.liteloader.Tickable;
import net.minecraft.client.Minecraft;

import java.io.File;

public class LiteModSchematica implements LiteMod, Tickable, PostRenderListener {

    @Override
    public void onPostRenderEntities(float partialTicks) {
        
    }

    @Override
    public void onPostRender(float partialTicks) {

    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {

    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public void init(File configPath) {

    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    @Override
    public String getName() {
        return null;
    }
}
