/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.cmd.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import hellfirepvp.astralsorcery.common.data.research.*;
import net.minecraft.ChatFormatting; // TextFormatting -> ChatFormatting
import net.minecraft.commands.CommandSourceStack; // CommandSource -> CommandSourceStack
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component; // StringTextComponent -> Component
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer; // PlayerEntity -> ServerPlayer
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.server.command.EnumArgument;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandProgress
 * Created by HellFirePvP
 * Date: 22.11.2020 / 13:23
 */
public class CommandProgress {

    private CommandProgress() {}

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("progress")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("progress", EnumArgument.enumArgument(ProgressionTier.class))
                                .executes(ctx -> {
                                    // Usamos getPlayerOrException para el emisor
                                    ServerPlayer src = ctx.getSource().getPlayerOrException();
                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                    ProgressionTier goal = ctx.getArgument("progress", ProgressionTier.class);
                                    return pushPlayerToProgress(ctx.getSource(), target, goal);
                                })));
    }

    private static int pushPlayerToProgress(CommandSourceStack src, ServerPlayer target, ProgressionTier goal) {
        Component targetName = target.getDisplayName();
        PlayerProgress progress = ResearchHelper.getProgress(target, LogicalSide.SERVER);
        if (!progress.isValid() || progress.getTierReached().isThisLaterOrEqual(goal)) {
            MutableComponent msg = Component.literal("Failed! ")
                    .append(targetName)
                    .append(Component.literal("'s progress is higher or equal to " + goal.name()))
                    .withStyle(ChatFormatting.RED);
            src.sendFailure(msg);
            return 0;
        }
        ResearchProgression research = null;
        switch (goal) {
            case DISCOVERY:
                research = ResearchProgression.DISCOVERY;
                break;
            case BASIC_CRAFT:
                research = ResearchProgression.BASIC_CRAFT;
                break;
            case ATTUNEMENT:
                research = ResearchProgression.ATTUNEMENT;
                break;
            case CONSTELLATION_CRAFT:
                research = ResearchProgression.CONSTELLATION;
                break;
            case TRAIT_CRAFT:
                research = ResearchProgression.RADIANCE;
                break;
            case BRILLIANCE:
                research = ResearchProgression.BRILLIANCE;
                break;
            default:
                break;
        }
        if (research == null) {
            src.sendFailure(Component.literal("Invalid progression tier: " + goal.name()).withStyle(ChatFormatting.RED));
            return 0;
        }
        if (ResearchManager.grantProgress(target, goal) && ResearchManager.grantResearch(target, research)) {
            src.sendSuccess(() -> Component.literal("Success!").withStyle(ChatFormatting.GREEN), true);
            return Command.SINGLE_SUCCESS;
        } else {
            src.sendFailure(Component.literal("Failed!").withStyle(ChatFormatting.RED));
            return 0;
        }
    }
}
