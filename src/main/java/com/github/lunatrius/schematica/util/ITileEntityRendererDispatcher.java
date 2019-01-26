package com.github.lunatrius.schematica.util;

public interface ITileEntityRendererDispatcher {
    public void preDrawBatch();
    public void drawBatch(int pass);
}
