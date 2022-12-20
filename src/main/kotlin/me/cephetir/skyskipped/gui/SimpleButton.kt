/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.cephetir.skyskipped.gui

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import java.awt.Color

class SimpleButton @JvmOverloads constructor(t: String, private val h: Boolean = false, private val w: Boolean = false) :
    UIBlock(Color(0, 0, 0, 80)) {

    val text = UIText(t).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        color = Color(14737632).toConstraint()
    } childOf this

    init {
        this
            .constrain {
                width = if (w) RelativeConstraint()
                else (text.getWidth() + 40).pixels()
                height = if (h) RelativeConstraint()
                else (text.getHeight() + 10).pixels()
            }
            .onMouseEnter {
                animate {
                    setColorAnimation(
                        Animations.OUT_EXP,
                        0.5f,
                        Color(255, 255, 255, 80).toConstraint(),
                        0f
                    )
                }
                text.constrain {
                    color = Color(16777120).toConstraint()
                }
            }.onMouseLeave {
                animate {
                    setColorAnimation(
                        Animations.OUT_EXP,
                        0.5f,
                        Color(0, 0, 0, 80).toConstraint()
                    )
                }
                text.constrain {
                    color = Color(14737632).toConstraint()
                }
            }
    }
}