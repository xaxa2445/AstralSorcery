/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityIlluminationSpark
 * Created by HellFirePvP
 * Date: 17.08.2019 / 10:45
 */
public class EntityIlluminationSpark extends ThrowableItemProjectile {

    public EntityIlluminationSpark(EntityType<? extends EntityIlluminationSpark> type, Level level) {
        super(type, level);
    }

    public EntityIlluminationSpark(Level world) {
        // Se usa .get() porque EntityTypesAS.ILLUMINATION_SPARK es un RegistryObject
        super(EntityTypesAS.ILLUMINATION_SPARK.get(), world);
    }

    public EntityIlluminationSpark(double x, double y, double z, Level world) {
        super(EntityTypesAS.ILLUMINATION_SPARK.get(), x, y, z, world);
    }

    public EntityIlluminationSpark(LivingEntity thrower, Level world) {
        super(EntityTypesAS.ILLUMINATION_SPARK.get(), thrower, world);
        // shootFromRotation fue reemplazado por shoot o shootFromRotation con parámetros de precisión
        this.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 0.7F, 0.9F);
    }

    @Override
    protected Item getDefaultItem() {
        return ItemsAS.ILLUMINATION_POWDER;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            spawnEffects();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnEffects() {
        FXFacingParticle p;
        for (int i = 0; i < 6; i++) {
            p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(Vector3.atEntityCorner(this))
                    .setMotion(new Vector3(
                            0.04F - random.nextFloat() * 0.08F,
                            0.04F - random.nextFloat() * 0.08F,
                            0.04F - random.nextFloat() * 0.08F
                    ))
                    .setScaleMultiplier(0.25F);
            randomizeColor(p);
        }

        p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(Vector3.atEntityCorner(this));
        p.setScaleMultiplier(0.6F);
        randomizeColor(p);

        p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(Vector3.atEntityCorner(this)
                        .add(getDeltaMovement().multiply(0.5, 0.5, 0.5)));
        p.setScaleMultiplier(0.6F);
        randomizeColor(p);

    }

    @OnlyIn(Dist.CLIENT)
    private void randomizeColor(FXFacingParticle p) {
        switch (random.nextInt(3)) {
            case 0:
                p.color(VFXColorFunction.constant(ColorsAS.ILLUMINATION_POWDER_1));
                break;
            case 1:
                p.color(VFXColorFunction.constant(ColorsAS.ILLUMINATION_POWDER_2));
                break;
            case 2:
                p.color(VFXColorFunction.constant(ColorsAS.ILLUMINATION_POWDER_3));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide()) {
            return;
        }
        if (!(result instanceof BlockHitResult bhr) || !(getOwner() instanceof Player player)) {
            discard();
            return;
        }
        BlockPos pos = bhr.getBlockPos();

        UseOnContext ctx = new UseOnContext(player, InteractionHand.MAIN_HAND, bhr);
        BlockPlaceContext placeContext = new BlockPlaceContext(ctx);
        if (!BlockUtils.isReplaceable(level(), pos)) {
            pos = pos.relative(bhr.getDirection());
        }

        if (!ForgeEventFactory.onBlockPlace(
                player,
                BlockSnapshot.create(level().dimension(), level(), pos),
                bhr.getDirection()
        )) {
            level().setBlock(pos, BlocksAS.FLARE_LIGHT.defaultBlockState(), 3);
        }

        discard();
    }

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getAddEntityPacket() {
        return net.minecraftforge.network.NetworkHooks.getEntitySpawningPacket(this);
    }
}
