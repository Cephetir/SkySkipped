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
package com.jagrosh.discordipc.exceptions;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;

/**
 * An exception thrown when an {@link IPCClient IPCClient}
 * when the client cannot find the proper application to use for RichPresence when
 * attempting to {@link IPCClient#connect(DiscordBuild...) connect}.<p>
 *
 * This purely and always means the IPCClient in question (specifically the client ID)
 * is <i>invalid</i> and features using this library cannot be accessed using the instance.
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class NoDiscordClientException extends Exception
{
    
}
