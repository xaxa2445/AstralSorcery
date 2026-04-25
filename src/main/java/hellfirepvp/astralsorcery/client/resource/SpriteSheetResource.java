/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth; // MathHelper ahora es Mth (Math) en 1.20.1

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpriteSheetResource
 * Created by HellFirePvP
 * Date: 14.09.2016 / 09:15
 */
public class SpriteSheetResource extends AbstractRenderableTexture {

    protected float uPart, vPart;
    protected int frameCount;
    protected int rows, columns;

    private final AbstractRenderableTexture resource;

    public SpriteSheetResource(AbstractRenderableTexture resource) {
        this(resource, 1, 1);
    }

    public SpriteSheetResource(AbstractRenderableTexture resource, int rows, int columns) {
        super(NameUtil.suffixPath(resource.getKey(), "_sprite"));
        if (rows <= 0 || columns <= 0)
            throw new IllegalArgumentException("¡No se puede instanciar una hoja de sprites sin filas o columnas!");

        this.frameCount = rows * columns;
        this.rows = rows;
        this.columns = columns;
        this.resource = resource;

        this.uPart = 1F / ((float) columns);
        this.vPart = 1F / ((float) rows);
    }

    @Override
    public void bindTexture() {
        this.resource.bindTexture();
    }

    @Override
    public RenderStateShard.TextureStateShard asState() {
        return this.resource.asState();
    }



    // --- Implementación de offsets individuales (Sin Tuplas) ---

    @Override
    public float getUOffset() {
        long timer = ClientScheduler.getClientTick();
        return getUOffset(timer);
    }

    @Override
    public float getVOffset() {
        long timer = ClientScheduler.getClientTick();
        return getVOffset(timer); // false para indicar que es V
    }

    @Override
    public float getUWidth() {
        return uPart;
    }

    @Override
    public float getVWidth() {
        return vPart;
    }

    // --- Lógica de cálculo de frames ---

    public float getUOffset(long frameTimer) {
        int frame = (int) (frameTimer % frameCount);
        return (frame % columns) * uPart;
    }

    // Método auxiliar para obtener V sin tuplas
    public float getVOffset(long frameTimer) {
        int frame = (int) (frameTimer % frameCount);
        return (frame / columns) * vPart;
    }

    // Sobrecarga para cálculos basados en la vida de la partícula (EntityComplexFX)
    public float getUOffset(EntityComplexFX fx, float partialTick, float spriteDisplayFactor) {
        float agePart = fx.getAge() * spriteDisplayFactor + partialTick;
        float perc = agePart / fx.getMaxAge();
        long timer = Mth.floor(this.getFrameCount() * perc); // MathHelper -> Mth
        return getUOffset(timer);
    }

    public float getVOffset(EntityComplexFX fx, float partialTick, float spriteDisplayFactor) {
        float agePart = fx.getAge() * spriteDisplayFactor + partialTick;
        float perc = agePart / fx.getMaxAge();
        long timer = Mth.floor(this.getFrameCount() * perc);
        return (timer % frameCount / columns) * vPart;
    }

    public ResourceLocation getTextureLocation() {
        return this.resource.getKey();
    }

    // --- Getters requeridos por SpriteQuery ---

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public AbstractRenderableTexture getResource() {
        return resource;
    }

    public int getFrameCount() {
        return frameCount;
    }
}

