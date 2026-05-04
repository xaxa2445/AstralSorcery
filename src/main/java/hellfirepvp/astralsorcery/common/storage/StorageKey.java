/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.storage;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageKey
 * Created by HellFirePvP
 * Date: 30.05.2019 / 14:45
 */
public class StorageKey {

    @Nonnull
    private final ItemStack stack;

    private StorageKey(@Nonnull ItemStack stack) {
        this.stack = stack;
    }

    public static StorageKey from(ItemStack stack) {
        return new StorageKey(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageKey that = (StorageKey) o;

        // 1.20.1: Usamos ForgeRegistries para obtener la ResourceLocation de forma segura
        ResourceLocation thisName = ForgeRegistries.ITEMS.getKey(this.stack.getItem());
        ResourceLocation thatName = ForgeRegistries.ITEMS.getKey(that.stack.getItem());

        return Objects.equals(thisName, thatName);
    }

    @Override
    public int hashCode() {
        // Obtenemos la ResourceLocation para generar el hash
        ResourceLocation name = ForgeRegistries.ITEMS.getKey(this.stack.getItem());
        return Objects.hash(name);
    }

    @Nonnull
    public CompoundTag serialize() {
        CompoundTag keyTag = new CompoundTag();
        ResourceLocation name = ForgeRegistries.ITEMS.getKey(stack.getItem());

        // Es buena práctica manejar el caso donde el nombre sea nulo
        if (name != null) {
            keyTag.putString("name", name.toString());
        }
        return keyTag;
    }

    //If the item in question does no longer exist in the registry, return null.
    @Nullable
    public static StorageKey deserialize(CompoundTag nbt) {
        ResourceLocation rl = new ResourceLocation(nbt.getString("name"));
        Item i = ForgeRegistries.ITEMS.getValue(rl);
        if (i == null || i == Items.AIR) {
            return null;
        }
        return new StorageKey(new ItemStack(i));
    }
}
