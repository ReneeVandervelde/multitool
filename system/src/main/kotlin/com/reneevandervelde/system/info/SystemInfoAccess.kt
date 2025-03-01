package com.reneevandervelde.system.info

import kotlinx.datetime.LocalDate
import java.io.File
import java.util.*

class SystemInfoAccess {
    fun getSystemInfo(): SystemInfo
    {
        val osName = System.getProperty("os.name")
        val osVersion = System.getProperty("os.version")
        val linuxProps = getLinuxProps()
        val isLinux = osName.contains("linux", ignoreCase = true)
        val isMac = osName.contains("mac", ignoreCase = true)
        val os = when {
            isLinux && linuxProps.getProperty("VARIANT_ID") == "silverblue" -> OperatingSystem.Linux.Fedora.Silverblue(
                versionId = linuxProps.getProperty("VERSION_ID")!!.toInt(),
                supportEnd = linuxProps.getProperty("SUPPORT_END")!!.let(LocalDate::parse),
            )
            isMac -> OperatingSystem.MacOS(
                version = osVersion,
            )
            else -> throw NotImplementedError("Unknown OS: $osName Version: $osVersion")
        }
        val path = System.getenv("PATH") ?: throw IllegalStateException("PATH environment variable not set")
        val executables = path.split(File.pathSeparator)
            .flatMap {
                File(it).listFiles { file, s -> file.canExecute() }
                    ?.toList()
                    ?: emptyList()
            }

        return SystemInfo(
            operatingSystem = os,
            executables = executables,
        )
    }

    private fun getLinuxProps(): Properties
    {
        val osRelease = Properties()
        val osReleaseFile = File("/etc/os-release")
        if (osReleaseFile.exists())
        {
            osRelease.load(osReleaseFile.inputStream())
        }
        return osRelease
    }
}

