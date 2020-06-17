package agency.highlysuspect.puzzle.mixin.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
	@Invoker("renderLayer") void puzzle$renderLayer(RenderLayer renderLayer, MatrixStack matrixStack, double d, double e, double f);
}
