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
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import hellfirepvp.astralsorcery.common.cmd.argument.ArgumentTypeConstellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.ChatFormatting; // TextFormatting -> ChatFormatting
import net.minecraft.commands.CommandSourceStack; // CommandSource -> CommandSourceStack
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component; // StringTextComponent -> Component
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer; // ServerPlayerEntity -> ServerPlayer

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandAttune
 * Created by HellFirePvP
 * Date: 21.07.2019 / 20:19
 */
public class CommandAttune implements Command<CommandSourceStack> {

    private static final CommandAttune CMD = new CommandAttune();

    private CommandAttune() {}

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("attune")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("constellation", ArgumentTypeConstellation.major())
                                .executes(CMD)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        IMajorConstellation cst = (IMajorConstellation) context.getArgument("constellation", IConstellation.class);

        if (ResearchManager.setAttunedConstellation(player, cst)) {
            // Construcción de mensaje complejo:
            // .appendString() ahora es .append() con un Component.literal()
            MutableComponent successMsg = Component.literal("Success! Player has been attuned to ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(cst.getConstellationName().copy().withStyle(ChatFormatting.BLUE));

            context.getSource().sendSuccess(() -> successMsg, true);
        } else {
            context.getSource().sendFailure(
                    Component.literal("Failed! Player specified doesn't seem to have the research progress necessary!")
                            .withStyle(ChatFormatting.RED)
            );
        }
        return Command.SINGLE_SUCCESS;
    }
}
