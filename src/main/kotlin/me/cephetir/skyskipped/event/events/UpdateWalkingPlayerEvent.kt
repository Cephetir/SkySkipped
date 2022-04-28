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

package me.cephetir.skyskipped.event.events

import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event


@Cancelable
open class UpdateWalkingPlayerEvent(
    open var x: Double,
    open var y: Double,
    open var z: Double,
    open var yaw: Float,
    open var pitch: Float,
    open var onGround: Boolean,
    open var sprinting: Boolean,
    open var sneaking: Boolean
) : Event() {
    @Cancelable
    data class Pre(
        override var x: Double,
        override var y: Double,
        override var z: Double,
        override var yaw: Float,
        override var pitch: Float,
        override var onGround: Boolean,
        override var sprinting: Boolean,
        override var sneaking: Boolean
    ) : UpdateWalkingPlayerEvent(x, y, z, yaw, pitch, onGround, sprinting, sneaking)

    @Cancelable
    data class Post(val event: UpdateWalkingPlayerEvent) : UpdateWalkingPlayerEvent(
        event.x,
        event.y,
        event.z,
        event.yaw,
        event.pitch,
        event.onGround,
        event.sprinting,
        event.sneaking
    )
}