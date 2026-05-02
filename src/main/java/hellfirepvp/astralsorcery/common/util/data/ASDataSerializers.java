/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASIDataSerializers
 * Created by HellFirePvP
 * Date: 18.07.2017 / 23:46
 */
public class ASDataSerializers {

    public static final EntityDataSerializer<Long> LONG = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buf, Long value) {
            buf.writeLong(value);
        }

        @Override
        public Long read(FriendlyByteBuf buf) {
            return buf.readLong();
        }

        @Override
        public Long copy(Long value) {
            return value;
        }
    };

    public static final EntityDataSerializer<Vector3> VECTOR = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buf, Vector3 value) {
            buf.writeDouble(value.getX());
            buf.writeDouble(value.getY());
            buf.writeDouble(value.getZ());
        }

        @Override
        public Vector3 read(FriendlyByteBuf buf) {
            return new Vector3(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
            );
        }

        @Override
        public Vector3 copy(Vector3 value) {
            return value.clone();
        }
    };

    public static final EntityDataSerializer<FluidStack> FLUID = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buf, FluidStack value) {
            buf.writeFluidStack(value);
        }

        @Override
        public FluidStack read(FriendlyByteBuf buf) {
            return buf.readFluidStack();
        }

        @Override
        public FluidStack copy(FluidStack value) {
            return value.copy();
        }
    };

}
