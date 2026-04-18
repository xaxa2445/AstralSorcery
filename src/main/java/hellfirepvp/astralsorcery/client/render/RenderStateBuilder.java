/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderStateUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderStateBuilder
 * Created by HellFirePvP
 * Date: 05.06.2020 / 16:27
 */
public class RenderStateBuilder extends RenderStateShard {

    private final RenderType.CompositeState.CompositeStateBuilder builder;

    // Constructor privado corregido para 1.20.1
    private RenderStateBuilder(RenderType.CompositeState.CompositeStateBuilder builder) {
        super("as_render_state_builder", () -> {}, () -> {});
        this.builder = builder;
    }

    public static RenderStateBuilder builder() {
        return new RenderStateBuilder(RenderType.CompositeState.builder());
    }

    // --- TEXTURAS ---

    public RenderStateBuilder texture(AbstractRenderableTexture texture) {
        this.builder.setTextureState(texture.asState());
        return this;
    }

    public RenderStateBuilder altasTexture() {
        // Ahora accesible gracias a la herencia
        this.builder.setTextureState(BLOCK_SHEET);
        return this;
    }

    public RenderStateBuilder disableTexture() {
        this.builder.setTextureState(NO_TEXTURE);
        return this;
    }

    // --- ESTADOS DE RENDER ---

    public RenderStateBuilder blend(Blending blendMode) {
        this.builder.setTransparencyState(blendMode.asState());
        return this;
    }

    public RenderStateBuilder disableDepth() {
        this.builder.setDepthTestState(new RenderStateShard.DepthTestStateShard("always", GL11.GL_ALWAYS));
        return this;
    }

    public RenderStateBuilder disableDepthMask() {
        this.builder.setWriteMaskState(new RenderStateUtil.WriteMaskState(true, false));
        return this;
    }

    public RenderStateBuilder enableLighting() {
        this.builder.setLightmapState(LIGHTMAP);
        return this;
    }

    public RenderStateBuilder enableOverlay() {
        this.builder.setOverlayState(OVERLAY);
        return this;
    }

    public RenderStateBuilder disableCull() {
        this.builder.setCullState(NO_CULL);
        return this;
    }

    public RenderStateBuilder shader(RenderStateShard.ShaderStateShard shader) {
        this.builder.setShaderState(shader);
        return this;
    }

    // --- TARGETS ---

    public RenderStateBuilder particleShaderTarget() {
        this.builder.setOutputState(PARTICLES_TARGET);
        return this;
    }

    // --- FINALIZADORES ---

    public RenderType.CompositeState.CompositeStateBuilder vanillaBuilder() {
        return this.builder;
    }

    public RenderType.CompositeState buildAsOverlay() {
        return this.builder.createCompositeState(true);
    }

    public RenderType.CompositeState build() {
        return this.builder.createCompositeState(false);
    }

    // El ShadeModel y AlphaState se omiten en 1.20.1 ya que se manejan por Shaders.
    // Si necesitas soporte para items, usa RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER.
}