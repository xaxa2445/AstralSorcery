/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.input.KeyBindingWrapper;
import hellfirepvp.astralsorcery.client.input.KeyDisablePerkAbilities;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static hellfirepvp.astralsorcery.client.lib.KeyBindingsAS.DISABLE_PERK_ABILITIES;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryKeyBindings
 * Created by HellFirePvP
 * Date: 13.05.2020 / 18:43
 */
public class RegistryKeyBindings {

    private static final Set<KeyBindingWrapper> watchedKeyBindings = new HashSet<>();
    private static final Set<KeyBindingWrapper> bindingsPressed = new HashSet<>();

    // Este método ya no registra directamente, solo prepara los wrappers
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(RegistryKeyBindings::onKeyInput);
    }

    // Nuevo método para registrar teclas en el bus de eventos de Forge (FMLJavaModLoadingContext)
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        DISABLE_PERK_ABILITIES = register(event, "disable_perk_abilities", GLFW.GLFW_KEY_V, KeyDisablePerkAbilities::new);
    }

    private static KeyBindingWrapper register(RegisterKeyMappingsEvent event, String name, int glfwKey, Function<KeyMapping, KeyBindingWrapper> wrapperCreator) {
        KeyMapping keyMapping = new KeyMapping(
                String.format("key.%s.%s", AstralSorcery.MODID, name),
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                glfwKey,
                AstralSorcery.NAME
        );

        event.register(keyMapping); // Registro oficial en 1.20.1
        KeyBindingWrapper wrapper = wrapperCreator.apply(keyMapping);
        watchedKeyBindings.add(wrapper);
        return wrapper;
    }

    private static void onKeyInput(InputEvent.Key event) {
        // En 1.20.1 se usa InputEvent.Key
        for (KeyBindingWrapper wrapper : watchedKeyBindings) {
            KeyMapping mapping = wrapper.getKeyBinding();

            boolean isPressed = mapping.isDown();
            boolean wasPressed = bindingsPressed.contains(wrapper);

            if (isPressed != wasPressed) {
                if (isPressed) {
                    bindingsPressed.add(wrapper);
                    wrapper.onKeyDown();
                } else {
                    bindingsPressed.remove(wrapper);
                    wrapper.onKeyUp();
                }
            }
        }
    }
}