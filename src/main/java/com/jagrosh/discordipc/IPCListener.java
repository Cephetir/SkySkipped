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
package com.jagrosh.discordipc;

import com.google.gson.JsonObject;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.User;

/**
 * An implementable listener used to handle events caught by an {@link IPCClient}.<p>
 *
 * Can be attached to an IPCClient using {@link IPCClient#setListener(IPCListener)}.
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public interface IPCListener
{
    /**
     * Fired whenever an {@link IPCClient} sends a {@link Packet} to Discord.
     *
     * @param client The IPCClient sending the Packet.
     * @param packet The Packet being sent.
     */
    default void onPacketSent(IPCClient client, Packet packet) {}

    /**
     * Fired whenever an {@link IPCClient} receives a {@link Packet} to Discord.
     *
     * @param client The IPCClient receiving the Packet.
     * @param packet The Packet being received.
     */
    default void onPacketReceived(IPCClient client, Packet packet) {}

    /**
     * Fired whenever a RichPresence activity informs us that
     * a user has clicked a "join" button.
     *
     * @param client The IPCClient receiving the event.
     * @param secret The secret of the event, determined by the implementation and specified by the user.
     */
    default void onActivityJoin(IPCClient client, String secret) {}

    /**
     * Fired whenever a RichPresence activity informs us that
     * a user has clicked a "spectate" button.
     *
     * @param client The IPCClient receiving the event.
     * @param secret The secret of the event, determined by the implementation and specified by the user.
     */
    default void onActivitySpectate(IPCClient client, String secret) {}

    /**
     * Fired whenever a RichPresence activity informs us that
     * a user has clicked a "ask to join" button.<p>
     *
     * As opposed to {@link #onActivityJoin(IPCClient, String)},
     * this also provides packaged {@link User} data.
     *
     * @param client The IPCClient receiving the event.
     * @param secret The secret of the event, determined by the implementation and specified by the user.
     * @param user The user who clicked the clicked the event, containing data on the account.
     */
    default void onActivityJoinRequest(IPCClient client, String secret, User user) {}

    /**
     * Fired whenever an {@link IPCClient} is ready and connected to Discord.
     *
     * @param client The now ready IPCClient.
     */
    default void onReady(IPCClient client) {}

    /**
     * Fired whenever an {@link IPCClient} has closed.
     *
     * @param client The now closed IPCClient.
     * @param json A {@link JsonObject} with close data.
     */
    default void onClose(IPCClient client, JsonObject json) {}

    /**
     * Fired whenever an {@link IPCClient} has disconnected,
     * either due to bad data or an exception.
     *
     * @param client The now closed IPCClient.
     * @param t A {@link Throwable} responsible for the disconnection.
     */
    default void onDisconnect(IPCClient client, Throwable t) {}
}
