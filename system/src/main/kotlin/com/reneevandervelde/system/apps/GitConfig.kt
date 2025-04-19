package com.reneevandervelde.system.apps

import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.apps.structures.LinkedConfigFile
import com.reneevandervelde.system.commands.configure.SystemConfiguration
import com.reneevandervelde.system.info.systemSettings
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import java.io.File

class GitConfig(
    private val settings: MultitoolSettings,
    clock: Clock,
    logger: KimchiLogger,
): LinkedConfigFile(logger, clock), SystemConfiguration {
    override val id: String = "git-config"

    override suspend fun getSystemFile(): File
    {
        return File(System.getProperty("user.home"), ".gitconfig")
    }

    override suspend fun getSourceFile(): File
    {
        val settings = settings.systemSettings.first()
        return File(settings.systemSrcRoot, "main/resources/configs/global.gitconfig")
    }
}

