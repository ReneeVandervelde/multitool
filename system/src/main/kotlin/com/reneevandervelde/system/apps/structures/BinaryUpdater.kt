package com.reneevandervelde.system.apps.structures

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.jvm.javaio.*
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.asFlow
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.zip.GZIPInputStream
import kotlin.io.path.pathString

class BinaryUpdater(
    private val httpClient: HttpClient,
    private val logger: KimchiLogger,
) {
    suspend fun update(
        lockFile: LockFile,
        binFile: File,
        extractedBinaryName: String = binFile.name,
    ) {
        if (!binFile.exists()) {
            logger.warn("Aborting Update. Binary Application not installed: ${binFile.absolutePath}")
            return
        }

        if (lockMatches(lockFile, binFile)) {
            logger.trace("Lock file matches, up-to-date")
            return
        }

        val response = httpClient.get(lockFile.url)
        val tempFile = File.createTempFile(binFile.nameWithoutExtension, ".tmp")
        tempFile.outputStream().use { output ->
            response.bodyAsChannel().copyTo(output)
        }
        val tempDir = Files.createTempDirectory(binFile.nameWithoutExtension)

        logger.trace("Extracting ${tempFile.absolutePath} -> ${tempDir.toAbsolutePath().pathString}")
        extractTarGz(tempFile, tempDir.toFile())

        val extractedBin = File(tempDir.toFile(), extractedBinaryName)

        if (!lockMatches(lockFile, extractedBin)) {
            throw IllegalStateException("Downloaded file does not match lock file")
        }

        logger.trace("Moving extracted file: ${extractedBin.absolutePath} -> ${binFile.absolutePath}")
        Files.move(extractedBin.toPath(), binFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

        if (!binFile.canExecute()) {
            logger.trace("Setting executable permission on ${binFile.absolutePath}")
            binFile.setExecutable(true)
        }

        if (!lockMatches(lockFile, binFile)) {
            throw IllegalStateException("Double check failed: Installed file does not match lock file")
        }
    }

    private fun lockMatches(lock: LockFile, binFile: File): Boolean
    {

        val currentSum = MessageDigest.getInstance("SHA-1")
            .digest(binFile.readBytes())
            .joinToString("") { "%02x".format(it) }
        val expectedSum = lock.sha1

        return currentSum == expectedSum
    }

    private suspend fun extractTarGz(tarGzFile: File, outputDir: File)
    {
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
}
