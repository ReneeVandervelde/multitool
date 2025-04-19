package com.reneevandervelde.system.apps

import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.apps.structures.App
import com.reneevandervelde.system.apps.structures.BinaryUpdater
import com.reneevandervelde.system.apps.structures.LockFile
import com.reneevandervelde.system.apps.structures.Updatable
import com.reneevandervelde.system.info.systemSettings
import com.reneevandervelde.system.processes.Decision
import io.ktor.client.*
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.first
import java.io.File

class ResilioSync(
    private val settings: MultitoolSettings,
    private val httpClient: HttpClient,
    private val logger: KimchiLogger,
): App, Updatable {
    override suspend fun enabled(): Decision
    {
        val bin = getBinFile()
        return when {
            bin.exists() -> Decision.Yes("Resilio Sync installed")
            else -> Decision.No("Resilio Sync not installed at ${bin.absolutePath}")
        }
    }

    override suspend fun update()
    {
        val lockFile = LockFile.fromResourceFile("/locks/rslsync_x64.lock.properties")
        val binFile = getBinFile()

        val updater = BinaryUpdater(
            httpClient = httpClient,
            logger = logger,
        )

        updater.update(
            lockFile = lockFile,
            binFile = binFile,
        )
    }

    private suspend fun getBinFile(): File
    {
        return File(settings.systemSettings.first().hostBinDir, "rslsync")
    }
}
