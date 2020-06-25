package agency.highlysuspect.puzzle.client;

import agency.highlysuspect.puzzle.mixin.client.WorldRendererAccessor;
import agency.highlysuspect.puzzle.world.ClientPuzzleRegionStateManagerManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public class PuzzleRegionRenderer {
	//TODO pare the argument list down, this is mainly for mixin hot-reloading, just threw in as much as i could.
	public static void renderPuzzleRegions(WorldRenderer renderer, MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f) {
		MinecraftClient client = MinecraftClient.getInstance();
		ClientWorld world = client.world;
		if(world == null) return;
		
		client.getProfiler().swap("puzzle-regions");
		
		matrices.push();
		double x = camera.getPos().x;
		double y = camera.getPos().y;
		double z = camera.getPos().z;
		matrices.translate(-x, -y, -z);
		
		VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer lineConsumer = immediate.getBuffer(Asdf.LINES);
		
		ClientPuzzleRegionStateManagerManager.get(world.getRegistryKey()).regionStream().forEach(region -> {
			BlockPos a = region.getStart();
			BlockPos b = region.getEnd();
			drawBoxCooler(matrices, lineConsumer, region.getStart(), region.getEnd(), 0.8f, 0.4f, 0.2f, 1f);
		});
		
		((WorldRendererAccessor) renderer).puzzle$renderLayer(Asdf.LINES, matrices, x, y, z);
		
		matrices.pop();
	}
	
	private static void drawBoxCooler(MatrixStack matrices, VertexConsumer consumer, BlockPos a, BlockPos b, float red, float green, float blue, float alpha) {
		WorldRenderer.drawBox(matrices, consumer, a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ(), red, green, blue, alpha);
	}
	
	//Can I have an Access Widener?
	//"We have an Access Widener at home"
	//I just want protected static RenderPhase fields.
	public static class Asdf extends RenderPhase {
		public Asdf() {
			super(null, null, null);
		}
		
		public static final RenderLayer LINES = RenderLayer.of("puzzle-regions-stroke", VertexFormats.POSITION_COLOR, GL11.GL_LINES, 256, RenderLayer.MultiPhaseParameters.builder()
			.lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(3)))
			.layering(VIEW_OFFSET_Z_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(MAIN_TARGET)
			.writeMaskState(ALL_MASK)
			.build(false)
		);
	}
}
