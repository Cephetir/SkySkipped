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
package com.jagrosh.discordipc.entities;

import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * A data-packet received from Discord via an {@link IPCClient IPCClient}.<br>
 * These can be handled via an implementation of {@link IPCListener IPCListener}.
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Packet
{
    private final OpCode op;
    private final JsonObject data;

    /**
     * Constructs a new Packet using an {@link OpCode} and {@link JsonObject}.
     *
     * @param op The OpCode value of this new Packet.
     * @param data The JSONObject payload of this new Packet.
     */
    public Packet(OpCode op, JsonObject data)
    {
        this.op = op;
        this.data = data;
    }

    /**
     * Converts this {@link Packet} to a {@code byte} array.
     *
     * @return This Packet as a {@code byte} array.
     */
    public byte[] toBytes()
    {
        byte[] d = data.toString().getBytes(StandardCharsets.UTF_8);
        ByteBuffer packet = ByteBuffer.allocate(d.length + 2*Integer.BYTES);
        packet.putInt(Integer.reverseBytes(op.ordinal()));
        packet.putInt(Integer.reverseBytes(d.length));
        packet.put(d);
        return packet.array();
    }

    /**
     * Gets the {@link OpCode} value of this {@link Packet}.
     *
     * @return This Packet's OpCode.
     */
    public OpCode getOp()
    {
        return op;
    }

    /**
     * Gets the {@link JsonObject} value as a part of this {@link Packet}.
     *
     * @return The JSONObject value of this Packet.
     */
    public JsonObject getJson()
    {
        return data;
    }
    
    @Override
    public String toString()
    {
        return "Pkt:"+getOp()+getJson().toString();
    }

    /**
     * Discord response OpCode values that are
     * sent with response data to and from Discord
     * and the {@link IPCClient IPCClient}
     * connected.
     */
    public enum OpCode
    {
        HANDSHAKE, FRAME, CLOSE, PING, PONG
    }
}
