package com.reneevandervelde.system.apps

import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.info.systemSettings
import com.reneevandervelde.system.processes.Decision
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.jvm.javaio.*
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.Properties
import java.util.zip.GZIPInputStream
import kotlin.io.path.pathString

class ResilioSync(
    private val settings: MultitoolSettings,
    private val httpClient: HttpClient,
    private val logger: KimchiLogger,
): App, Updatable {
    private val lockInfo by lazy {
        val lockResource = javaClass.getResourceAsStream("/locks/rslsync_x64.lock.properties")

        Properties().apply {
            load(lockResource)
        }
    }

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
        val binFile = getBinFile()
        if (!binFile.exists()) {
            logger.warn("Resilio Sync not installed. Cannot update")
            return
        }

        if (lockMatches(binFile)) {
            logger.trace("Lock file matches, up-to-date")
            return
        }

        val response = httpClient.get(lockInfo.getProperty("url"))
        val tempFile = File.createTempFile("rslsync", ".tmp")
        tempFile.outputStream().use { output ->
            response.bodyAsChannel().copyTo(output)
        }
        val tempDir = Files.createTempDirectory("rslsync")

        extractTarGz(tempFile, tempDir.toFile())

        logger.trace("Extracting ${tempFile.absolutePath} -> ${tempDir.toAbsolutePath().pathString}")
        val extractedBin = File(tempDir.toFile(), "rslsync")

        if (!lockMatches(extractedBin)) {
            throw IllegalStateException("Downloaded file does not match lock file")
        }

        logger.trace("Moving extracted file: ${extractedBin.absolutePath} -> ${binFile.absolutePath}")
        Files.move(extractedBin.toPath(), binFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

        if (!binFile.canExecute()) {
            logger.trace("Setting executable permission on ${binFile.absolutePath}")
            binFile.setExecutable(true)
        }

        if (!lockMatches(binFile)) {
            throw IllegalStateException("Double check failed: Installed file does not match lock file")
        }
    }

    suspend fun extractTarGz(tarGzFile: File, outputDir: File) {
        GZIPInputStream(FileInputStream(tarGzFile)).use { gis ->
            TarArchiveInputStream(gis).use { tis ->
                generateSequence { tis.nextEntry }
                    .asFlow()
                    .collect { entry ->
                        val outputFile = File(outputDir, entry.name)
                        if (entry.isDirectory) {
                            outputFile.mkdirs()
                        } else {
                            outputFile.parentFile?.mkdirs()
                            FileOutputStream(outputFile).use { fos -> tis.copyTo(fos) }
                        }
                    }
            }
        }
    }

    private fun lockMatches(binFile: File): Boolean
    {

        val currentSum = MessageDigest.getInstance("SHA-1")
            .digest(binFile.readBytes())
            .joinToString("") { "%02x".format(it) }
        val expectedSum = lockInfo.getProperty("sha1")

        return currentSum == expectedSum
    }

    private suspend fun getBinFile(): File
    {
        return File(settings.systemSettings.first().hostBinDir, "rslsync")
    }
}
