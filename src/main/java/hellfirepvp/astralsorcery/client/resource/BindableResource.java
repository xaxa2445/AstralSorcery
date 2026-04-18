/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

    /**
     * This class is part of the Astral Sorcery Mod
     * The complete source code for this mod can be found on github.
     * Class: BindableResource
     * Created by HellFirePvP
     * Date: 07.05.2016 / 00:50
     */
    @OnlyIn(Dist.CLIENT)
    public class BindableResource extends AbstractRenderableTexture.Full implements ReloadableResource {

        private AbstractTexture resource = null;
        private String path = null;

        protected BindableResource(ResourceLocation key) {
            super(key);
        }

        public BindableResource(String path) {
            this(AstralSorcery.key(path.replaceAll("[^a-zA-Z0-9\\.\\-]", "_")));
            this.path = path;
            // En 1.20.1 no asignamos el ID manualmente aquí, dejamos que el TextureManager lo maneje.
        }

        public String getPath() {
            return path;
        }

        public SpriteSheetResource asSpriteSheet(int rows, int columns) {
            return new SpriteSheetResource(this, rows, columns);
        }

        public void invalidateAndReload() {
            Minecraft.getInstance().getTextureManager().release(this.getKey());
            this.resource = null;
        }

        protected AbstractTexture allocateGlId() {
            if (AssetLibrary.isReloading()) {
                return null;
            }
            TextureManager mgr = Minecraft.getInstance().getTextureManager();
            AbstractTexture tex = mgr.getTexture(this.getKey());

            // Si no existe o se perdió, la cargamos
            if (tex == null) {
                // Importante: El path del archivo físico debe ser una ResourceLocation válida
                ResourceLocation location = new ResourceLocation(this.getPath());
                mgr.register(this.getKey(), new SimpleTexture(location));
                tex = mgr.getTexture(this.getKey());
            }
            return tex;
        }

        @Override
        public void bindTexture() {
            if (AssetLibrary.isReloading()) {
                return;
            }
            if (this.resource == null) {
                this.resource = allocateGlId();
            }
            if (this.resource == null) {
                return;
            }
            // En 1.20.1 usamos setShaderTexture
            RenderSystem.setShaderTexture(0, this.getKey());
        }

        @Override
        public RenderStateShard.TextureStateShard asState() {
            // En 1.20.1 usamos TextureStateShard
            return new RenderStateShard.TextureStateShard(this.getKey(), false, false);
        }
    }