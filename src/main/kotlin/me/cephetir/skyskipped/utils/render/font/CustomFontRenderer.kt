/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.cephetir.skyskipped.utils.render.font

import me.cephetir.skyskipped.mixins.accessors.IMixinFontRenderer
import me.cephetir.skyskipped.utils.ColorUtils.fromHex
import me.cephetir.skyskipped.utils.mc
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.concurrent.atomic.AtomicInteger

object CustomFontRenderer {
    private lateinit var fontRenderer: IMixinFontRenderer

    @Volatile
    @JvmField
    var chromaStyle = false

    @Volatile
    @JvmField
    var drawingShadow = false

    fun renderStringAtPos(text: String, shadow: Boolean): Boolean {
        if (!::fontRenderer.isInitialized) fontRenderer = mc.fontRendererObj as IMixinFontRenderer
        drawingShadow = shadow

        var i = 0
        while (i < text.length) {
            var c0 = text[i]
            if (c0 == '\u00a7' && i + 1 < text.length) {
                if (text.lowercase()[i + 1] == '#') {
                    i += 2
                    val hex = StringBuilder("#")
                    var next = text.lowercase()[i]
                    while (next != '#' && hex.length < 8) {
                        hex.append(next)
                        i++
                        next = text.lowercase()[i]
                    }
                    if (hex.toString() == "#") {
                        ++i
                        continue
                    }
                    val color = hex.toString().fromHex()
                    if (color != null) {
                        if (shadow) GlStateManager.color(color.red / 4f / 255.0f, color.green / 4f / 255.0f, color.blue / 4f / 255.0f, fontRenderer.alphaaa)
                        else GlStateManager.color(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, fontRenderer.alphaaa)
                    }
                } else {
                    val i1 = "0123456789abcdefklmnopr".indexOf(text.lowercase()[i + 1])
                    if (i1 < 16) {
                        fontRenderer.randomStyle = false
                        fontRenderer.boldStyle = false
                        fontRenderer.strikethroughStyle = false
                        fontRenderer.underlineStyle = false
                        fontRenderer.italicStyle = false
                        chromaStyle = false
                        var index = if (i1 < 0) 15 else i1
                        if (shadow) index += 16
                        val j1 = fontRenderer.colorCode[index]
                        fontRenderer.textColor = j1
                        GlStateManager.color(
                            (j1 shr 16).toFloat() / 255.0f,
                            (j1 shr 8 and 0xFF).toFloat() / 255.0f,
                            (j1 and 0xFF).toFloat() / 255.0f,
                            fontRenderer.alphaaa
                        )
                    } else if (i1 == 16) fontRenderer.randomStyle = true
                    else if (i1 == 17) fontRenderer.boldStyle = true
                    else if (i1 == 18) fontRenderer.strikethroughStyle = true
                    else if (i1 == 19) fontRenderer.underlineStyle = true
                    else if (i1 == 20) fontRenderer.italicStyle = true
                    else if (i1 == 21) chromaStyle = true
                    else {
                        fontRenderer.randomStyle = false
                        fontRenderer.boldStyle = false
                        fontRenderer.strikethroughStyle = false
                        fontRenderer.underlineStyle = false
                        fontRenderer.italicStyle = false
                        chromaStyle = false
                        GlStateManager.resetColor()
                        GlStateManager.color(fontRenderer.reddd, fontRenderer.blueee, fontRenderer.greennn, fontRenderer.alphaaa)
                    }
                    ++i
                }
                ++i
                continue
            }
            var i1 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".indexOf(c0)
            if (fontRenderer.randomStyle && i1 != -1) {
                val j1 = fontRenderer.getCharWidth(c0)

                var c1: Char
                do {
                    i1 = fontRenderer.fontRandom.nextInt("ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".length)
                    c1 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"[i1]
                } while (j1 != fontRenderer.getCharWidth(c1))

                c0 = c1
            }
            val f1 = if (i1 == -1 || fontRenderer.unicodeFlag) 0.5f else 1.0f
            val flag = (c0 == '\u0000' || i1 == -1 || fontRenderer.unicodeFlag) && shadow
            if (flag) {
                fontRenderer.posX -= f1
                fontRenderer.posY -= f1
            }
            var f = fontRenderer.renderChar(c0, fontRenderer.italicStyle)
            if (flag) {
                fontRenderer.posX += f1
                fontRenderer.posY += f1
            }
            if (fontRenderer.boldStyle) {
                fontRenderer.posX += f1
                if (flag) {
                    fontRenderer.posX -= f1
                    fontRenderer.posY -= f1
                }
                fontRenderer.renderChar(c0, fontRenderer.italicStyle)
                fontRenderer.posX -= f1
                if (flag) {
                    fontRenderer.posX += f1
                    fontRenderer.posY += f1
                }
                f += 1.0f
            }
            doDraw(f)
            ++i
        }
        chromaStyle = false
        return true
    }

    fun getStringWidth(text: String): Int {
        if (!::fontRenderer.isInitialized) fontRenderer = mc.fontRendererObj as IMixinFontRenderer

        var i = 0
        var flag = false
        val j = AtomicInteger(0)
        while (j.get() < text.length) {
            var c0 = text[j.get()]
            var k = newGetCharWidth(c0, text, j)
            if (k < 0 && j.get() < text.length - 1) {
                if (text[j.incrementAndGet()].also { c0 = it } == 'l' || c0 == 'L') flag = true else if (c0 == 'r' || c0 == 'R') flag = false
                k = 0
            }
            i += k
            if (!flag || k <= 0) {
                j.incrementAndGet()
                continue
            }
            ++i
            j.incrementAndGet()
        }
        return i
    }

    private fun newGetCharWidth(character: Char, text: String, index: AtomicInteger): Int {
        if (character == '§') {
            var next = text[index.get() + 1]
            if (next == '#') {
                index.addAndGet(8)
                next = text[index.get()]
                if (next != '#') index.addAndGet(3)
            }
            return -1
        }
        if (character == ' ') return 4
        val i =
            "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(
                character
            )
        if (character > '\u0000' && i != -1 && !fontRenderer.unicodeFlag) return fontRenderer.charWidth[i]
        if (fontRenderer.glyphWidth[character.code].toInt() != 0) {
            val j: Int = fontRenderer.glyphWidth[character.code].toInt() ushr 4
            var k: Int = fontRenderer.glyphWidth[character.code].toInt() and 0xF
            return (++k - j) / 2 + 1
        }
        return 0
    }

    fun renderRainbowChar(ch: Char, italic: Boolean, shadow: Boolean): Float {
        if (!::fontRenderer.isInitialized) fontRenderer = mc.fontRendererObj as IMixinFontRenderer

        val i = ch.code % 16 * 8
        val j = ch.code / 16 * 8
        val k = if (italic) 1 else 0
        mc.renderEngine.bindTexture(fontRenderer.locationFontTexture)
        val l: Float = fontRenderer.charWidth[ch.code].toFloat()
        val f = l - 0.01f
        val time = System.currentTimeMillis()
        val y: Long = (fontRenderer.posY * 11.0f).toLong()
        var position: Long = time - ((fontRenderer.posX * 11.0f).toLong() - y)
        var color = Color.HSBtoRGB((position % 3000L).toFloat() / 3000.0f, 1f, 1f)
        val red = (color shr 16 and 0xFF).toFloat() / 255.0f
        val blue = (color shr 8 and 0xFF).toFloat() / 255.0f
        val green = (color and 0xFF).toFloat() / 255.0f
        position = time - (((fontRenderer.posX + l) * 11.0f).toLong() - y)
        color = Color.HSBtoRGB((position % 3000L).toFloat() / 3000.0f, 1f, 1f)
        val red2 = (color shr 16 and 0xFF).toFloat() / 255.0f
        val blue2 = (color shr 8 and 0xFF).toFloat() / 255.0f
        val green2 = (color and 0xFF).toFloat() / 255.0f
        GL11.glBegin(5)
        if (!shadow) GL11.glColor4f(red, green, blue, fontRenderer.alphaaa) else GL11.glColor4f(red / 4f, green / 4f, blue / 4f, fontRenderer.alphaaa)
        GL11.glTexCoord2f(i.toFloat() / 128.0f, j.toFloat() / 128.0f)
        GL11.glVertex3f(fontRenderer.posX + k.toFloat(), fontRenderer.posY, 0.0f)
        GL11.glTexCoord2f(i.toFloat() / 128.0f, (j.toFloat() + 7.99f) / 128.0f)
        GL11.glVertex3f(fontRenderer.posX - k.toFloat(), fontRenderer.posY + 7.99f, 0.0f)
        if (!shadow) GL11.glColor4f(red2, green2, blue2, fontRenderer.alphaaa) else GL11.glColor4f(red2 / 4f, green2 / 4f, blue2 / 4f, fontRenderer.alphaaa)
        GL11.glTexCoord2f((i.toFloat() + f - 1.0f) / 128.0f, j.toFloat() / 128.0f)
        GL11.glVertex3f(fontRenderer.posX + f - 1.0f + k.toFloat(), fontRenderer.posY, 0.0f)
        GL11.glTexCoord2f((i.toFloat() + f - 1.0f) / 128.0f, (j.toFloat() + 7.99f) / 128.0f)
        GL11.glVertex3f(fontRenderer.posX + f - 1.0f - k.toFloat(), fontRenderer.posY + 7.99f, 0.0f)
        GL11.glEnd()
        return l
    }

    fun renderUniRainbowChar(ch: Char, italic: Boolean, shadow: Boolean): Float {
        if (!::fontRenderer.isInitialized) fontRenderer = mc.fontRendererObj as IMixinFontRenderer

        return if (fontRenderer.glyphWidth[ch.code].toInt() == 0) 0.0f else {
            val i = ch.code / 256
            fontRenderer.loadGlyphTexture(i)
            val j: Int = fontRenderer.glyphWidth[ch.code].toInt() ushr 4
            val k: Int = fontRenderer.glyphWidth[ch.code].toInt() and 15
            val f = j.toFloat()
            val f1 = (k + 1).toFloat()
            val f2 = (ch.code % 16 * 16).toFloat() + f
            val f3 = ((ch.code and 255) / 16 * 16).toFloat()
            val f4 = f1 - f - 0.02f
            val f5 = if (italic) 1.0f else 0.0f
            val time = System.currentTimeMillis()
            val y: Long = (fontRenderer.posY * 11.0f).toLong()
            var position: Long = time - ((fontRenderer.posX * 11.0f).toLong() - y)
            var color = Color.HSBtoRGB((position % 3000L).toFloat() / 3000.0f, 1f, 1f)
            val red = (color shr 16 and 0xFF).toFloat() / 255.0f
            val blue = (color shr 8 and 0xFF).toFloat() / 255.0f
            val green = (color and 0xFF).toFloat() / 255.0f
            position = time - (((fontRenderer.posX + f) * 11.0f).toLong() - y)
            color = Color.HSBtoRGB((position % 3000L).toFloat() / 3000.0f, 1f, 1f)
            val red2 = (color shr 16 and 0xFF).toFloat() / 255.0f
            val blue2 = (color shr 8 and 0xFF).toFloat() / 255.0f
            val green2 = (color and 0xFF).toFloat() / 255.0f
            GL11.glBegin(5)
            if (!shadow) GL11.glColor4f(red, green, blue, fontRenderer.alphaaa) else GL11.glColor4f(red / 4f, green / 4f, blue / 4f, fontRenderer.alphaaa)
            GL11.glTexCoord2f(f2 / 256.0f, f3 / 256.0f)
            GL11.glVertex3f(fontRenderer.posX + f5, fontRenderer.posY, 0.0f)
            GL11.glTexCoord2f(f2 / 256.0f, (f3 + 15.98f) / 256.0f)
            GL11.glVertex3f(fontRenderer.posX - f5, fontRenderer.posY + 7.99f, 0.0f)
            if (!shadow) GL11.glColor4f(red2, green2, blue2, fontRenderer.alphaaa) else GL11.glColor4f(red2 / 4f, green2 / 4f, blue2 / 4f, fontRenderer.alphaaa)
            GL11.glTexCoord2f((f2 + f4) / 256.0f, f3 / 256.0f)
            GL11.glVertex3f(fontRenderer.posX + f4 / 2.0f + f5, fontRenderer.posY, 0.0f)
            GL11.glTexCoord2f((f2 + f4) / 256.0f, (f3 + 15.98f) / 256.0f)
            GL11.glVertex3f(fontRenderer.posX + f4 / 2.0f - f5, fontRenderer.posY + 7.99f, 0.0f)
            GL11.glEnd()
            (f1 - f) / 2.0f + 1.0f
        }
    }

    private fun doDraw(f: Float) {
        var tessellator1: Tessellator
        var worldrenderer1: WorldRenderer
        if (fontRenderer.strikethroughStyle) {
            tessellator1 = Tessellator.getInstance()
            worldrenderer1 = tessellator1.worldRenderer
            GlStateManager.disableTexture2D()
            worldrenderer1.begin(7, DefaultVertexFormats.POSITION)
            worldrenderer1.pos(fontRenderer.posX.toDouble(), (fontRenderer.posY + (mc.fontRendererObj.FONT_HEIGHT / 2).toFloat()).toDouble(), 0.0).endVertex()
            worldrenderer1.pos((fontRenderer.posX + f).toDouble(), (fontRenderer.posY + (mc.fontRendererObj.FONT_HEIGHT / 2).toFloat()).toDouble(), 0.0)
                .endVertex()
            worldrenderer1.pos((fontRenderer.posX + f).toDouble(), (fontRenderer.posY + (mc.fontRendererObj.FONT_HEIGHT / 2).toFloat() - 1.0f).toDouble(), 0.0)
                .endVertex()
            worldrenderer1.pos(fontRenderer.posX.toDouble(), (fontRenderer.posY + (mc.fontRendererObj.FONT_HEIGHT / 2).toFloat() - 1.0f).toDouble(), 0.0)
                .endVertex()
            tessellator1.draw()
            GlStateManager.enableTexture2D()
        }
        if (fontRenderer.underlineStyle) {
            tessellator1 = Tessellator.getInstance()
            worldrenderer1 = tessellator1.worldRenderer
            GlStateManager.disableTexture2D()
            worldrenderer1.begin(7, DefaultVertexFormats.POSITION)
            val l = if (fontRenderer.underlineStyle) -1 else 0
            worldrenderer1.pos((fontRenderer.posX + l.toFloat()).toDouble(), (fontRenderer.posY + mc.fontRendererObj.FONT_HEIGHT.toFloat()).toDouble(), 0.0)
                .endVertex()
            worldrenderer1.pos((fontRenderer.posX + f).toDouble(), (fontRenderer.posY + mc.fontRendererObj.FONT_HEIGHT.toFloat()).toDouble(), 0.0).endVertex()
            worldrenderer1.pos((fontRenderer.posX + f).toDouble(), (fontRenderer.posY + mc.fontRendererObj.FONT_HEIGHT.toFloat() - 1.0f).toDouble(), 0.0)
                .endVertex()
            worldrenderer1.pos(
                (fontRenderer.posX + l.toFloat()).toDouble(),
                (fontRenderer.posY + mc.fontRendererObj.FONT_HEIGHT.toFloat() - 1.0f).toDouble(),
                0.0
            ).endVertex()
            tessellator1.draw()
            GlStateManager.enableTexture2D()
        }
        fontRenderer.posX += f
    }
}