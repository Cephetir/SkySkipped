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

import com.jagrosh.discordipc.IPCClient;

import java.util.function.Consumer;

/**
 * A callback for asynchronous logic when dealing processes that
 * would normally block the calling thread.<p>
 *
 * This is most visibly implemented in {@link IPCClient IPCClient}.
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Callback
{
    private final Consumer<Packet> success;
    private final Consumer<String> failure;

    /**
     * Constructs an empty Callback.
     */
    public Callback()
    {
        this((Consumer<Packet>) null, null);
    }

    /**
     * Constructs a Callback with a success {@link Consumer} that
     * occurs when the process it is attached to executes without
     * error.
     *
     * @param success The Consumer to launch after a successful process.
     */
    public Callback(Consumer<Packet> success)
    {
        this(success, null);
    }

    /**
     * Constructs a Callback with a success {@link Consumer} <i>and</i>
     * a failure {@link Consumer} that occurs when the process it is
     * attached to executes without or with error (respectively).
     *
     * @param success The Consumer to launch after a successful process.
     * @param failure The Consumer to launch if the process has an error.
     */
    public Callback(Consumer<Packet> success, Consumer<String> failure)
    {
        this.success = success;
        this.failure = failure;
    }

    /**
     * @param success The Runnable to launch after a successful process.
     * @param failure The Consumer to launch if the process has an error.
     */
    @Deprecated
    public Callback(Runnable success, Consumer<String> failure)
    {
        this(p -> success.run(), failure);
    }

    /**
     * @param success The Runnable to launch after a successful process.
     */
    @Deprecated
    public Callback(Runnable success)
    {
        this(p -> success.run(), null);
    }

    /**
     * Gets whether or not this Callback is "empty" which is more precisely
     * defined as not having a specified success {@link Consumer} and/or a
     * failure {@link Consumer}.<br>
     * This is only true if the Callback is constructed with the parameter-less
     * constructor ({@link #Callback()}) or another constructor that leaves
     * one or both parameters {@code null}.
     *
     * @return {@code true} if and only if the
     */
    public boolean isEmpty()
    {
        return success == null && failure == null;
    }

    /**
     * Launches the success {@link Consumer}.
     */
    public void succeed(Packet packet)
    {
        if(success != null)
            success.accept(packet);
    }

    /**
     * Launches the failure {@link Consumer} with the
     * provided message.
     *
     * @param message The message to launch the failure consumer with.
     */
    public void fail(String message)
    {
        if(failure != null)
            failure.accept(message);
    }
}
