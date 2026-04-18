/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.inventory.InventoryMenu; // Nueva ubicación de la referencia al Atlas de bloques

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAtlasTexture
 * Created by HellFirePvP
 * Date: 05.06.2020 / 21:54
 */
public class BlockAtlasTexture extends AbstractRenderableTexture.Full {

    private static final BlockAtlasTexture INSTANCE = new BlockAtlasTexture();

    private BlockAtlasTexture() {
        // Usamos una clave descriptiva interna
        super(AstralSorcery.key("block_atlas_reference"));
    }

    public static BlockAtlasTexture getInstance() {
        return INSTANCE;
    }

    @Override
    public void bindTexture() {
        // En 1.20.1, el bindeo manual se hace a través de RenderSystem
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
    }

    @Override
    public RenderStateShard.TextureStateShard asState() {
        // TextureState ahora es TextureStateShard
        // El constructor pide el ResourceLocation, y los booleanos para blur y mipmap
        return new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false);
    }
}