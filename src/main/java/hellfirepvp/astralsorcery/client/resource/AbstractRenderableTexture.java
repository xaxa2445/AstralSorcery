/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import net.minecraft.client.renderer.RenderStateShard; // Antes RenderState
import net.minecraft.resources.ResourceLocation; // Cambio de paquete

import java.util.Objects;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractRenderableTexture
 * Created by HellFirePvP
 * Date: 25.02.2018 / 23:24
 */
public abstract class AbstractRenderableTexture {

    private final ResourceLocation key;

    protected AbstractRenderableTexture(ResourceLocation key) {
        this.key = key;
    }

    public final ResourceLocation getKey() {
        return key;
    }

    /**
     * En 1.20.1 el bindeo manual es menos común debido al sistema de Shards,
     * pero se mantiene por compatibilidad con el sistema de Astral.
     */
    public abstract void bindTexture();

    /**
     * Representación del estado de la textura para el pipeline de renderizado.
     */
    public abstract RenderStateShard.TextureStateShard asState();

    // Reemplazamos Tuple por un método más directo para evitar errores de compilación
    public abstract float getUOffset();

    public abstract float getVOffset();

    public abstract float getUWidth();

    public abstract float getVWidth();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRenderableTexture that = (AbstractRenderableTexture) o;
        return Objects.equals(this.getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getKey());
    }

    /**
     * Implementación para texturas completas (no atlas/sub-texturas).
     */
    public static abstract class Full extends AbstractRenderableTexture {

        public Full(ResourceLocation key) {
            super(key);
        }

        @Override
        public float getUOffset() {
            return 0.0F;
        }

        @Override
        public float getVOffset() {
            return 0.0F;
        }

        @Override
        public final float getUWidth() {
            return 1.0F;
        }

        @Override
        public final float getVWidth() {
            return 1.0F;
        }
    }
}
