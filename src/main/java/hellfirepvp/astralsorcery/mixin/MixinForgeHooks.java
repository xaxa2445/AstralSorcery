/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyMagnetDrops;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.loot.LootUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext; // net.minecraft.loot -> world.level.storage.loot
import net.minecraft.world.level.storage.loot.LootParams; // LootParameters -> LootParams (en algunas versiones)
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets; // LootParameterSets -> LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams; // LootParameters -> LootContextParams
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinForgeHooks
 * Created by HellFirePvP
 * Date: 01.01.2022 / 10:06
 */
@Mixin(ForgeHooks.class)
public class MixinForgeHooks {

    @Inject(
            // El método ahora usa ResourceLocation, List<ItemStack> y LootContext
            method = "modifyLoot",
            at = @At("RETURN"),
            cancellable = true,
            remap = false // Importante: ForgeHooks es código de Forge, no de Mojang, por eso remap = false
    )
    private static void runLootTeleportation(ResourceLocation lootTableId, List<ItemStack> generatedLoot, LootContext context, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> loot = cir.getReturnValue();

        // LootParameterSets.BLOCK -> LootContextParamSets.BLOCK
        if (!LootUtil.doesContextFulfillSet(context, LootContextParamSets.BLOCK)) {
            return;
        }

        // LootParameters.THIS_ENTITY -> LootContextParams.THIS_ENTITY
        Entity e = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(e instanceof Player player)) {
            return;
        }

        PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!prog.isValid() || !prog.getPerkData().hasPerkEffect(perk -> perk instanceof KeyMagnetDrops)) {
            return;
        }

        // Manejo de compatibilidad con Curios para evitar duplicación de loot
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool != null && tool.hasTag() && tool.getOrCreateTag().contains("HasCuriosFortuneBonus")) {
            loot.removeIf(result -> ItemUtils.dropItemToPlayer(player, result).isEmpty());
            return;
        }

        // Acceso a la API de Curios en 1.20.1
        int curiosFortuneBonus = CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.getFortuneLevel(context)) // Usamos el nuevo método con contexto
                .orElse(0);

        if (curiosFortuneBonus > 0) {
            return; // Curios re-procesará el loot después
        }

        // Teletransportar los ítems al jugador y remover los que se entregaron con éxito
        loot.removeIf(result -> ItemUtils.dropItemToPlayer(player, result).isEmpty());
    }
}
