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

package me.cephetir.skyskipped.features.impl.cofl

import com.google.gson.JsonObject
import me.cephetir.skyskipped.SkySkipped
import net.minecraftforge.fml.common.Loader
import java.io.File
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*


object CoflSessions {
    data class CoflSession(val uuid: String, val timestamp: ZonedDateTime)

    fun getSession(username: String): CoflSession {
        updateCoflSessions()
        return getCoflSession(username)
    }

    private fun updateCoflSessions() {
        val sessions = getCoflSessions()
        for ((username, session) in sessions)
            if (!isValidSession(session))
                deleteCoflSession(username)
    }

    private fun getTempFileFolder(): File {
        val dataPath = File(Loader.instance().configDir.path, "/CoflSky/sessions")
        dataPath.mkdirs()
        return dataPath
    }

    private fun getCoflSessions(): Map<String, CoflSession> {
        val sessions = getTempFileFolder().listFiles() ?: emptyArray()
        val map = HashMap<String, CoflSession>()
        for (i in sessions.indices)
            map[sessions[i].name] = getCoflSession(sessions[i].name)
        return map
    }

    private fun isValidSession(session: CoflSession): Boolean {
        return session.timestamp.plus(Duration.ofDays(180)).isAfter(ZonedDateTime.now())
    }

    private fun getUserPath(username: String): File {
        return File(getTempFileFolder().toString() + "/" + username)
    }

    private fun deleteCoflSession(username: String) {
        getUserPath(username).delete()
    }

    private fun getCoflSession(username: String): CoflSession {
        val file = getUserPath(username)
        if (!file.exists()) {
            val session = CoflSession(UUID.randomUUID().toString(), ZonedDateTime.now())
            overwriteCoflSession(username, session)
            return session
        }
        val raw = file.readLines().joinToString("\n")
        val obj = SkySkipped.gson.fromJson(raw, JsonObject::class.java)
        return CoflSession(obj.getAsJsonPrimitive("SessionUUID").asString, ZonedDateTime.parse(obj.getAsJsonPrimitive("timestampCreated").asString))
    }

    private fun overwriteCoflSession(username: String, session: CoflSession) {
        val file = getUserPath(username)
        file.createNewFile()
        val obj = JsonObject()
        obj.addProperty("SessionUUID", session.uuid)
        obj.addProperty("timestampCreated", session.timestamp.toString())
        file.writeText(SkySkipped.gson.toJson(obj))
    }
}