package mixin;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.PriorityBlockingQueue;

@Mixin(ChunkRenderDispatcher.class)
public interface IMixinChunkRenderDispatcher {
	@Accessor
	PriorityBlockingQueue<ChunkCompileTaskGenerator> getQueueChunkUpdates();

	@Invoker
	void callUploadVertexBuffer(BufferBuilder bufferBuilderIn, VertexBuffer vertexBufferIn);

	@Invoker
	void callUploadDisplayList(BufferBuilder bufferBuilderIn, int list, RenderChunk chunkRenderer);
}
