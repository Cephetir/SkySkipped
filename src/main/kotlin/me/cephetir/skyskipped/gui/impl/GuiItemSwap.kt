/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2022 Cephetir
 *
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  0. You just DO WHAT THE FUCK YOU WANT TO.
 */

package me.cephetir.skyskipped.gui.impl

import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.universal.UKeyboard
import gg.essential.vigilance.utils.onLeftClick
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.gui.SimpleButton
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.ChatAllowedCharacters
import java.awt.Color

class GuiItemSwap : WindowScreen(newGuiScale = 2) {

    private val scrollComponent: ScrollComponent
    private var clickedButton: Entry? = null

    private val components = HashMap<UIContainer, Entry>()

    init {
        UIText("Item Swap Keybinds").childOf(window).constrain {
            x = CenterConstraint()
            y = RelativeConstraint(0.075f)
            height = 14.pixels()
        }

        scrollComponent = ScrollComponent(
            innerPadding = 4f,
        ).childOf(window).constrain {
            x = CenterConstraint()
            y = 15.percent()
            width = 90.percent()
            height = 70.percent() + 2.pixels()
        }

        val bottomButtons = UIContainer().childOf(window).constrain {
            x = CenterConstraint()
            y = 90.percent()
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        SimpleButton("Save and Exit").childOf(bottomButtons).constrain {
            x = 0.pixels()
            y = 0.pixels()
        }.onLeftClick {
            mc.displayGuiScreen(null)
        }

        SimpleButton("Add KeyBind").childOf(bottomButtons).constrain {
            x = SiblingConstraint(5f)
            y = 0.pixels()
        }.onLeftClick {
            addNewKeybind()
        }

        SkySkipped.keybinds.forEach {
            addNewKeybind(it.message, it.keyCode, it.modifiers)
        }
    }

    private fun addNewKeybind(command: String = "", keyCode: Int = 0, modifiers: Int = 0) {
        val modifiersList = Modifiers.fromBitfield(modifiers)
        val container = UIContainer().childOf(scrollComponent).constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
            width = 80.percent()
            height = 9.5.percent()
        }.effect(OutlineEffect(Color(160, 40, 255), 1f))

        val commandToRun = (UITextInput("Names of items (Split with \":\" (Ex: wither goggles:rabbit hat))").childOf(container).constrain {
            x = 5.pixels()
            y = CenterConstraint()
            width = 70.percent()
        }.onLeftClick {
            if (clickedButton == null) grabWindowFocus()
        } as UITextInput).also {
            it.setText(command)
            it.onKeyType { _, _ ->
                it.setText(it.getText().filter(ChatAllowedCharacters::isAllowedCharacter).take(256))
            }
        }

        val keybindButton = SimpleButton("placeholder").childOf(container).constrain {
            x = SiblingConstraint(5f)
            y = CenterConstraint()
            height = 75.percent()
        }

        SimpleButton("Remove").childOf(container).constrain {
            x = 85.percent()
            y = CenterConstraint()
            height = 75.percent()
        }.onLeftClick {
            container.parent.removeChild(container)
            components.remove(container)
        }
        val entry = Entry(container, commandToRun, keybindButton, keyCode, modifiersList)

        keybindButton.onLeftClick {
            clickedButton = entry
        }

        components[container] = entry
    }

    override fun onScreenClose() {
        super.onScreenClose()
        SkySkipped.keybinds.clear()

        for ((_, entry) in components) {
            val command = entry.input.getText()
            val keyCode = entry.keyCode
            if (command.isBlank() || keyCode == 0) continue

            SkySkipped.keybinds.add(Keybind(command, keyCode, entry.modifiers))
        }
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        if (clickedButton != null) {
            val extra =
                if (modifiers != null) Modifiers.fromUCraft(modifiers)
                else Modifiers.getPressed()
            when {
                keyCode == 1 -> {
                    clickedButton!!.keyCode = 0
                    clickedButton!!.modifiers = emptyList()
                }
                keyCode != 0 -> {
                    clickedButton!!.keyCode = keyCode
                    clickedButton!!.modifiers = extra
                }
                typedChar.code > 0 -> {
                    clickedButton!!.keyCode = typedChar.code + 256
                    clickedButton!!.modifiers = extra
                }
            }
            clickedButton = null
        } else super.onKeyPressed(keyCode, typedChar, modifiers)
    }

    override fun onMouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int) {
        if (clickedButton != null) {
            clickedButton!!.keyCode = -100 + mouseButton
            clickedButton!!.modifiers = Modifiers.getPressed()
            clickedButton = null
        } else super.onMouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun onTick() {
        super.onTick()
        for ((_, entry) in components) {
            val button = entry.button
            val keyCode = entry.keyCode
            button.text.setText(entry.getDisplayString())
            val pressed = clickedButton === entry
            val reused =
                keyCode != 0 && (mc.gameSettings.keyBindings.any { it.keyCode == keyCode } || components.any { it.value.keyCode != 0 && it.value !== entry && it.value.keyCode == keyCode })
            if (pressed) {
                button.text.setText("§f> §e${button.text.getText()}§f <")
            } else if (reused) {
                button.text.setText("§c${button.text.getText()}")
            }
        }
    }

    data class Entry(
        val container: UIContainer,
        val input: UITextInput,
        val button: SimpleButton,
        var keyCode: Int,
        var modifiers: List<Modifiers>
    ) {
        fun getDisplayString() =
            "${
                if (modifiers.isNotEmpty()) modifiers.joinToString(
                    "+",
                    postfix = " + "
                ) { it.shortName } else ""
            }${getKeyDisplayStringSafe(keyCode)}"
    }

    data class Keybind(val message: String, val keyCode: Int, val modifiers: Int, var lastState: Boolean) {
        constructor(message: String, keyCode: Int, modifiers: List<Modifiers>) : this(
            message,
            keyCode,
            Modifiers.getBitfield(modifiers),
            false
        )
    }

    enum class Modifiers(val shortName: String, val pressed: () -> Boolean) {
        CONTROL("Ctrl", { UKeyboard.isCtrlKeyDown() }),
        ALT("Alt", { UKeyboard.isAltKeyDown() }),
        SHIFT("Sft", { UKeyboard.isShiftKeyDown() });

        val bitValue by lazy {
            1 shl ordinal
        }

        fun inBitfield(field: Int) = (field and bitValue) == bitValue

        companion object {
            fun getPressed() = values().filter { it.pressed() }
            fun getBitfield(modifiers: List<Modifiers>): Int {
                var bits = 0
                for (modifier in modifiers) {
                    bits = bits or modifier.bitValue
                }
                return bits
            }

            fun fromBitfield(field: Int) = values().filter { it.inBitfield(field) }

            fun fromUCraftBitfield(modifiers: UKeyboard.Modifiers) = getBitfield(fromUCraft(modifiers))

            fun fromUCraft(modifiers: UKeyboard.Modifiers) = modifiers.run {
                mutableListOf<Modifiers>().apply {
                    if (isCtrl) add(CONTROL)
                    if (isAlt) add(ALT)
                    if (isShift) add(SHIFT)
                }
            }
        }
    }

    companion object {
        fun getKeyDisplayStringSafe(keyCode: Int): String =
            runCatching { GameSettings.getKeyDisplayString(keyCode) }.getOrNull() ?: "Key $keyCode"
    }
}