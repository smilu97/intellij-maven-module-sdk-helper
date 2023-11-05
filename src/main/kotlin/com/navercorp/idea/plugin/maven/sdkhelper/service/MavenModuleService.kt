package com.navercorp.idea.plugin.maven.sdkhelper.service

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootModificationUtil
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import java.io.File
import kotlin.io.path.pathString

class MavenModuleService: ModuleService {

    override fun readJdkVersion(module: Module): String? {
        return try {
            this.guessJdkVersion(module)
        } catch (e: Exception) {
            null;
        }
    }

    private fun guessJdkVersion(module: Module): String {
        val moduleDir = module.guessModuleDir()?.toNioPath() ?: throw RuntimeException("module-dir not found")

        val pomFile = File(moduleDir.pathString + "/pom.xml")
        if (!pomFile.exists()) {
            throw RuntimeException("pom.xml not found")
        }

        val pom = MavenXpp3Reader().read(pomFile.inputStream())
        return pom.properties.getProperty("jdk.version") ?: throw RuntimeException("jdk.version not defined")
    }

    override fun setModuleSdk(module: Module, sdk: Sdk) {
        ModuleRootModificationUtil.setModuleSdk(module, sdk)
    }

}