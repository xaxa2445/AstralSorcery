/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataSerializersAS
 * Created by HellFirePvP
 * Date: 06.07.2019 / 19:12
 */
public class DataSerializersAS {

    private DataSerializersAS() {}

    public static void writeVector(FriendlyByteBuf buf, Vector3 vec) {
        buf.writeDouble(vec.getX());
        buf.writeDouble(vec.getY());
        buf.writeDouble(vec.getZ());
    }

    public static Vector3 readVector(FriendlyByteBuf buf) {
        return new Vector3(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        );
    }

    public static void writeFluid(FriendlyByteBuf buf, FluidStack stack) {
        buf.writeFluidStack(stack);
    }

    public static FluidStack readFluid(FriendlyByteBuf buf) {
        return buf.readFluidStack();
    }

}
