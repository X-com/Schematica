package mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Template.class)
public interface IMixinTemplate {
	@Accessor
	List<Template.BlockInfo> getBlocks();

	@Accessor
	List<Template.EntityInfo> getEntities();

	@Accessor
	BlockPos getSize();

	@Accessor
	void setSize(BlockPos pos);
}
