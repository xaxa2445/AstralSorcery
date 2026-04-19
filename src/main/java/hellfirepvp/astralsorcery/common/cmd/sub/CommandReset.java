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
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import net.minecraft.ChatFormatting; // TextFormatting -> ChatFormatting
import net.minecraft.commands.CommandSourceStack; // CommandSource -> CommandSourceStack
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component; // StringTextComponent -> Component
import net.minecraft.server.level.ServerPlayer; // ServerPlayerEntity -> ServerPlayer

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandReset
 * Created by HellFirePvP
 * Date: 21.07.2019 / 20:19
 */
public class CommandReset implements Command<CommandSourceStack> {

    private static final CommandReset CMD = new CommandReset();

    private CommandReset() {}

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("reset")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        ResearchHelper.wipeKnowledge(player);

        String name = player.getGameProfile().getName();
        context.getSource().sendSuccess(() ->
                        Component.literal("Wiped " + name + "'s data!")
                                .withStyle(ChatFormatting.GREEN),
                true
        );

        return Command.SINGLE_SUCCESS;
    }
}
