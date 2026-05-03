/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.entity.InteractableEntity;
import hellfirepvp.astralsorcery.common.item.ItemChisel;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCrystalBase;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityCrystal
 * Created by HellFirePvP
 * Date: 21.08.2019 / 21:57
 */
public class EntityCrystal extends EntityItemExplosionResistant implements InteractableEntity {

    public EntityCrystal(EntityType<? extends ItemEntity> type, Level world) {
        super(type, world);
    }

    public EntityCrystal(EntityType<? extends ItemEntity> type, Level world, double x, double y, double z) {
        super(type, world, x, y, z);
    }

    public EntityCrystal(EntityType<? extends ItemEntity> type, Level world, double x, double y, double z, ItemStack stack) {
        super(type, world, x, y, z, stack);
    }

    public static EntityCrystal createCrystal(EntityType<EntityCrystal> type, Level level) {
        return new EntityCrystal(type, level);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        if (!this.level().isClientSide() && entity instanceof ServerPlayer player) {
            ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);

            if (!held.isEmpty() && held.getItem() instanceof ItemChisel) {
                ItemStack thisStack = this.getItem();

                if (!thisStack.isEmpty() && thisStack.getItem() instanceof ItemCrystalBase crystalBase) {
                    CrystalAttributes thisAttributes = crystalBase.getAttributes(thisStack);

                    if (thisAttributes != null) {
                        boolean doDamage = false;
                        if (this.random.nextFloat() < 0.35F) {
                            int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, held);
                            doDamage = this.splitCrystal(thisAttributes, fortuneLevel);
                        }
                        if (doDamage || this.random.nextFloat() < 0.35F) {
                            held.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                        }
                        // 2. Retornamos true para decirle a Minecraft: "Ya manejé yo el golpe, no hagas nada más"
                        return true;
                    }
                }
            }
        }
        // 3. Si no es un jugador con un cincel, dejamos que siga el comportamiento normal
        return super.skipAttackInteraction(entity);
    }

    private boolean splitCrystal(CrystalAttributes thisAttributes, int fortuneLevel) {
        ItemCrystalBase newBase = ((ItemCrystalBase) this.getItem().getItem()).getInertDuplicateItem();
        if (newBase == null) {
            return false;
        }
        ItemStack created = new ItemStack(newBase);
        if (created.isEmpty()) {
            return false;
        }
        int maxSplit = Mth.ceil(thisAttributes.getTotalTierLevel() / 2F);
        if (maxSplit >= thisAttributes.getTotalTierLevel()) {
            return false;
        }

        int lostModifiers = 0;
        if (maxSplit > 1 && level().random.nextFloat() < (0.6F / (fortuneLevel + 1))) {
            lostModifiers++;
            if (maxSplit > 2 && level().random.nextFloat() < (0.2F / (fortuneLevel + 1))) {
                lostModifiers++;
            }
        }

        CrystalAttributes resultThisAttributes = thisAttributes;
        CrystalAttributes.Builder resultSplitAttributes = CrystalAttributes.Builder.newBuilder(false);
        for (int i = 0; i < maxSplit; i++) {
            CrystalProperty prop = MiscUtils.getRandomEntry(resultThisAttributes.getProperties(), random);
            if (prop == null) {
                break;
            }
            resultThisAttributes = resultThisAttributes.modifyLevel(prop, -1);
            if (lostModifiers > 0) {
                lostModifiers--;
            } else {
                resultSplitAttributes.addProperty(prop, 1);
            }
        }

        ((ItemCrystalBase) this.getItem().getItem()).setAttributes(this.getItem(), resultThisAttributes);
        newBase.setAttributes(created, resultSplitAttributes.build());
        ItemUtils.dropItemNaturally(level(), this.getX(), this.getY() + 0.25F, this.getZ(), created);
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.getAge() + 10 >= this.lifespan) {
            // "age" es el nombre del campo en los mapeos de Mojang
            ObfuscationReflectionHelper.setPrivateValue(ItemEntity.class, this, 0, "age");
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
