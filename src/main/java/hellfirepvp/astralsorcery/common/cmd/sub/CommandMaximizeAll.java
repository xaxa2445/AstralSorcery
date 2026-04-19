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
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack; // CommandSource -> CommandSourceStack
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer; // ServerPlayerEntity -> ServerPlayer
import net.minecraft.world.entity.player.Player;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandMaximizeAll
 * Created by HellFirePvP
 * Date: 21.07.2019 / 16:33
 */
public class CommandMaximizeAll implements Command<CommandSourceStack> {

    private static final CommandMaximizeAll CMD = new CommandMaximizeAll();

    private CommandMaximizeAll() {}

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("maximize")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                            maximizeAll(target);
                            ctx.getSource().sendSuccess(() ->
                                    Component.literal("Success!").withStyle(ChatFormatting.GREEN), true);
                            return Command.SINGLE_SUCCESS;
                        }))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        maximizeAll(player);
        context.getSource().sendSuccess(() ->
                Component.literal("Success!").withStyle(ChatFormatting.GREEN), true);
        return Command.SINGLE_SUCCESS;
    }

    private static boolean maximizeAll(Player entity) {
        return ResearchManager.forceMaximizeAll(entity);
    }
}
