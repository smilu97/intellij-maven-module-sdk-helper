package com.navercorp.idea.plugin.maven.sdkhelper

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.guessModuleDir
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import java.io.File
import java.lang.RuntimeException
import kotlin.io.path.pathString

class ModuleUtil {

    companion object {

        fun guessJdkVersion(module: Module): String {
            val moduleDir = module.guessModuleDir()?.toNioPath() ?: throw RuntimeException("module-dir not found")

            val pomFile = File(moduleDir.pathString + "/pom.xml")
            if (!pomFile.exists()) {
                throw RuntimeException("pom.xml not found")
            }

            val pom = MavenXpp3Reader().read(pomFile.inputStream())
            return pom.properties.getProperty("jdk.version") ?: throw RuntimeException("jdk.version not defined")
        }

    }

}