/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.cmd.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.ChatFormatting; // TextFormatting -> ChatFormatting
import net.minecraft.commands.CommandSourceStack; // CommandSource -> CommandSourceStack
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component; // StringTextComponent -> Component
import net.minecraft.server.level.ServerPlayer; // ServerPlayerEntity -> ServerPlayer

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandExp
 * Created by HellFirePvP
 * Date: 21.07.2019 / 20:19
 */
public class CommandExp implements Command<CommandSourceStack> {

    private static final CommandExp CMD = new CommandExp();

    private CommandExp() {}

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("exp")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("exp", LongArgumentType.longArg())
                                .executes(CMD)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        long exp = LongArgumentType.getLong(context, "exp");

        if (ResearchManager.setExp(player, exp)) {
            // Recordar usar la lambda () -> para el sendSuccess
            context.getSource().sendSuccess(() ->
                            Component.literal("Success! Player exp has been set to " + exp)
                                    .withStyle(ChatFormatting.GREEN),
                    true
            );
        } else {
            // sendFailure no requiere Supplier, pero para mantener consistencia
            // podemos usar sendSuccess con color rojo si queremos que sea "broadcasted"
            context.getSource().sendFailure(
                    Component.literal("Failed! Player specified doesn't seem to have a research progress!")
                            .withStyle(ChatFormatting.RED)
            );
        }
        return Command.SINGLE_SUCCESS;
    }
}
