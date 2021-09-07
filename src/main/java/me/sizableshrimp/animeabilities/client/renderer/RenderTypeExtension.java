package me.sizableshrimp.animeabilities.client.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;

public class RenderTypeExtension extends RenderType {
    private RenderTypeExtension(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_,
            Runnable p_i225992_8_) {
        super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
    }

    public static RenderType aura(ResourceLocation location) {
        State state = State.builder()
                .setTextureState(new TextureState(location, false, false))
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setTransparencyState(LIGHTNING_TRANSPARENCY)
                .setWriteMaskState(COLOR_WRITE)
                .setDiffuseLightingState(DIFFUSE_LIGHTING)
                .setAlphaState(DEFAULT_ALPHA)
                .setCullState(NO_CULL)
                .setLightmapState(NO_LIGHTMAP)
                .setOverlayState(NO_OVERLAY)
                .createCompositeState(false);
        return RenderType.create("aura", DefaultVertexFormats.NEW_ENTITY, 7, 256, false, true, state);
    }

    public static final RenderType ENTITY_TRANSLUCENT_NO_DIFFUSE = create("animeabilities_entity_translucent_no_diffuse", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true,
            State.builder()
                    .setTextureState(new TextureState(PlayerContainer.BLOCK_ATLAS, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    //.setDiffuseLightingState(DIFFUSE_LIGHTING) - need to disable this
                    .setAlphaState(DEFAULT_ALPHA)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false));
}
