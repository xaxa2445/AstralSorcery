/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MaterialBuilderAS
 * Created by HellFirePvP
 * Date: 30.05.2019 / 23:00
 */
public class MaterialBuilderAS {

    private final MapColor color;
    private PushReaction pushReaction = PushReaction.NORMAL;
    private boolean blocksMovement = true;
    private boolean canBurn = false;
    private boolean isLiquid = false;
    private boolean isReplaceable = false;
    private boolean isSolid = true;
    private boolean isOpaque = true;

    public MaterialBuilderAS(MapColor color) {
        this.color = color;
    }

    public MaterialBuilderAS liquid() {
        this.isLiquid = true;
        return this;
    }

    public MaterialBuilderAS notSolid() {
        this.isSolid = false;
        return this;
    }

    public MaterialBuilderAS doesNotBlockMovement() {
        this.blocksMovement = false;
        return this;
    }

    public MaterialBuilderAS notOpaque() {
        this.isOpaque = false;
        return this;
    }

    public MaterialBuilderAS flammable() {
        this.canBurn = true;
        return this;
    }

    public MaterialBuilderAS replaceable() {
        this.isReplaceable = true;
        return this;
    }

    public MaterialBuilderAS pushDestroys() {
        this.pushReaction = PushReaction.DESTROY;
        return this;
    }

    public MaterialBuilderAS pushBlocks() {
        this.pushReaction = PushReaction.BLOCK;
        return this;
    }

    public BlockBehaviour.Properties build() {
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
                .mapColor(this.color)
                .pushReaction(this.pushReaction);

        if (this.isLiquid) {
            props.noCollission().instabreak().liquid();
        }
        if (!this.isSolid) {
            props.noCollission();
        }
        if (!this.blocksMovement) {
            props.noCollission();
        }
        if (this.isReplaceable) {
            props.replaceable();
        }
        if (this.canBurn) {
            props.ignitedByLava();
        }

        return props;
    }
}
