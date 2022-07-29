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

package me.cephetir.skyskipped.utils.threading

import kotlinx.coroutines.*
import me.cephetir.skyskipped.SkySkipped

@OptIn(ObsoleteCoroutinesApi::class)
internal object BackgroundScope : CoroutineScope by CoroutineScope(newFixedThreadPoolContext(1, "SkySkipped Threads")) {
    private val jobs = LinkedHashMap<BackgroundJob, Job?>()
    private var started = false

    fun start() {
        started = true
        for ((job, _) in jobs)
            jobs[job] = startJob(job)
    }

    fun launchLooping(name: String, delay: Long, block: suspend CoroutineScope.() -> Unit): BackgroundJob =
        launchLooping(BackgroundJob(name, delay, block))

    fun launchLooping(job: BackgroundJob): BackgroundJob {
        if (!started) jobs[job] = null
        else jobs[job] = startJob(job)

        return job
    }

    fun cancel(job: BackgroundJob) = jobs.remove(job)?.cancel()

    private fun startJob(job: BackgroundJob): Job {
        return launch {
            while (isActive) {
                try {
                    job.block(this)
                } catch (e: Exception) {
                    SkySkipped.logger.warn("Error occurred while running background job ${job.name}", e)
                }
                delay(job.delay)
            }
        }
    }
}