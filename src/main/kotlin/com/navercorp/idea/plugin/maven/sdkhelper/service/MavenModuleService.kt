package com.navercorp.idea.plugin.maven.sdkhelper.service

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootModificationUtil
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

class MavenModuleService: ModuleService {

    override fun readProperty(module: Module, keys: List<String>): String? {
        for (key in keys) {
            val value = this.readProperty(module, key)
            if (value != null) {
                return value
            }
        }
        return null
    }

    private fun readProperty(module: Module, key: String): String? {
        return try {
            this.readProperty0(module, key)
        } catch (e: Exception) {
            null
        }
    }

    private fun readProperty0(module: Module, key: String): String? {
        var moduleDir = module.guessModuleDir()?.toNioPath() ?: throw RuntimeException("module-dir not found")

        for (i in 0..10) {
            val jdkVersion = readPropertyAt(moduleDir, key)
            if (jdkVersion != null) {
                return jdkVersion
            }

            val parentModuleDir = moduleDir.parent
            if (parentModuleDir == null || parentModuleDir == moduleDir) {
                return null
            }
            moduleDir = parentModuleDir
        }

        return null
    }

    private fun readPropertyAt(moduleDir: Path, key: String): String? {
        val pomFile = File(moduleDir.pathString + "/pom.xml")
        if (!pomFile.exists()) {
            return null
        }

        val pom = MavenXpp3Reader().read(pomFile.inputStream())
        return pom.properties.getProperty(key)
    }

    override fun setModuleSdk(module: Module, sdk: Sdk) {
        ModuleRootModificationUtil.setModuleSdk(module, sdk)
    }

}