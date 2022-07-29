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

/**
 * Constants representing various Discord client builds,
 * such as Stable, Canary, Public Test Build (PTB)
 */
public enum DiscordBuild
{
    /**
     * Constant for the current Discord Canary release.
     */
    CANARY("//canary.discordapp.com/api"),

    /**
     * Constant for the current Discord Public Test Build or PTB release.
     */
    PTB("//ptb.discordapp.com/api"),

    /**
     * Constant for the current stable Discord release.
     */
    STABLE("//discordapp.com/api"),

    /**
     * 'Wildcard' build constant used in {@link IPCClient#connect(DiscordBuild...)
     * IPCClient#connect(DiscordBuild...)} to signify that the build to target is not important, and
     * that the first valid build will be used.<p>
     *
     * Other than this exact function, there is no use for this value.
     */
    ANY;

    private final String endpoint;

    DiscordBuild(String endpoint)
    {
        this.endpoint = endpoint;
    }

    DiscordBuild()
    {
        this(null);
    }

    /**
     * Gets a {@link DiscordBuild} matching the specified endpoint.<p>
     *
     * This is only internally implemented.
     *
     * @param endpoint The endpoint to get from.
     *
     * @return The DiscordBuild corresponding to the endpoint, or
     *         {@link DiscordBuild#ANY} if none match.
     */
    public static DiscordBuild from(String endpoint)
    {
        for(DiscordBuild value : values())
        {
            if(value.endpoint != null && value.endpoint.equals(endpoint))
            {
                return value;
            }
        }
        return ANY;
    }
}
