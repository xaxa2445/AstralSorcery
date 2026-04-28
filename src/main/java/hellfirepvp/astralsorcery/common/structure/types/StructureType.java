/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.types;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.api.ChangeSubscriber;
import hellfirepvp.observerlib.api.ObserverHelper;
import hellfirepvp.observerlib.api.util.BlockArray;
import hellfirepvp.observerlib.common.change.ChangeObserverStructure;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import net.minecraft.core.BlockPos; // IMPORTANTE: net.minecraft.core
import net.minecraft.network.chat.Component; // ITextComponent -> Component
import net.minecraft.resources.ResourceLocation; // net.minecraft.util -> net.minecraft.resources
import net.minecraft.world.level.Level; // net.minecraft.world -> net.minecraft.world.level
import javax.annotation.Nullable;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureType
 * Created by HellFirePvP
 * Date: 30.05.2019 / 15:07
 */
public class StructureType  {

    private final ResourceLocation name;
    private final Supplier<BlockArray> structureSupplier;

    public StructureType(ResourceLocation name, Supplier<BlockArray> structureSupplier) {
        this.name = name;
        this.structureSupplier = structureSupplier;
    }

    public BlockArray getStructure() {
        return this.structureSupplier.get();
    }

    public Component getDisplayName() {
        return Component.translatable(String.format("structure.%s.%s.name", name.getNamespace(), name.getPath()));
    }

    public ChangeSubscriber<ChangeObserverStructure> observe(Level world, BlockPos pos) {
        return ObserverHelper.getHelper().observeArea(world, pos, new ObserverProviderStructure(getRegistryName()));
    }


    public final StructureType setRegistryName(ResourceLocation name) {
        return this;
    }

    @Nullable
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    public Class<StructureType> getRegistryType() {
        return StructureType.class;
    }
}
