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

package me.cephetir.skyskipped.utils

import net.minecraft.client.Minecraft
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import javax.imageio.ImageIO


object ScreenshotUtils {
    @JvmStatic
    fun takeScreenshot(): File {
        Minecraft.getSystemTime()
        val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
        val capture = Robot().createScreenCapture(screenRect)
        val filename = System.currentTimeMillis().toString().substring(System.currentTimeMillis().toString().length - 3)
        val temp = File.createTempFile(filename, ".png")
        ImageIO.write(capture, "png", temp)
        temp.deleteOnExit()
        return temp
    }
}