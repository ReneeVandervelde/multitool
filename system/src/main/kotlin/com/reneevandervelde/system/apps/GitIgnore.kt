package com.reneevandervelde.system.apps

import com.reneevandervelde.settings.MultitoolSettings
import com.reneevandervelde.system.commands.configure.SystemConfiguration
import com.reneevandervelde.system.info.systemSettings
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import java.io.File

class GitIgnore(
    private val settings: MultitoolSettings,
    clock: Clock,
    logger: KimchiLogger,
): LinkedConfigFile(logger, clock), SystemConfiguration {
    override val id: String = "git-ignore"

    override suspend fun getSystemFile(): File
    {
        return File(System.getProperty("user.home"), ".gitignore")
    }

    override suspend fun getSourceFile(): File
    {
        val settings = settings.systemSettings.first()
        return File(settings.systemSrcRoot, "main/resources/configs/global.gitignore")
    }
}
