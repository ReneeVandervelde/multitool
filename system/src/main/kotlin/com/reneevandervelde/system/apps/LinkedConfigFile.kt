package com.reneevandervelde.system.apps

import com.reneevandervelde.system.commands.configure.ConfigurationStatus
import com.reneevandervelde.system.commands.configure.SystemConfiguration
import kimchi.logger.KimchiLogger
import kotlinx.datetime.Clock
import java.io.File
import java.nio.file.Files

abstract class LinkedConfigFile(
    private val logger: KimchiLogger,
    private val clock: Clock,
): SystemConfiguration {
    abstract suspend fun getSystemFile(): File
    abstract suspend fun getSourceFile(): File

    override suspend fun getStatus(): ConfigurationStatus
    {
        if (false == Files.isSymbolicLink(getSystemFile().toPath())) {
            return ConfigurationStatus.NotConfigured
        }
        if (Files.readSymbolicLink(getSystemFile().toPath()).toAbsolutePath() == getSourceFile().toPath().toAbsolutePath()) {
            return ConfigurationStatus.Configured
        } else {
            return ConfigurationStatus.NotConfigured
        }
    }

    override suspend fun configure()
    {
        archiveSystemFile()
        logger.debug("Creating symlink: ${getSystemFile().absolutePath} -> ${getSourceFile().absolutePath}")
        Files.createSymbolicLink(getSystemFile().toPath(), getSourceFile().toPath())
    }

    private suspend fun archiveSystemFile()
    {
        val systemFile = getSystemFile()
        if (false == systemFile.exists()) {
            logger.trace("Config does not exist")
            return
        }
        if (Files.isSymbolicLink(systemFile.toPath())) {
            logger.debug("Deleting existing config symlink: ${systemFile.absolutePath}")
            systemFile.delete() || throw RuntimeException("Unable to delete symlink: ${systemFile.absolutePath}")
            return
        }
        val archiveFile = File(systemFile.parentFile, "${systemFile.name}_${clock.now().toEpochMilliseconds()}")
        logger.debug("Archiving existing config file to: ${archiveFile.absolutePath}")
        Files.move(systemFile.toPath(), archiveFile.toPath())
    }
}
