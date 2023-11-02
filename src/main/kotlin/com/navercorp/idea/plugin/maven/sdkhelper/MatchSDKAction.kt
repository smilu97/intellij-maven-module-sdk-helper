package com.navercorp.idea.plugin.maven.sdkhelper

import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootModificationUtil

class MatchSDKAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = ProjectUtil.getActiveProject()
        if (project == null) {
            println("No active project")
            return
        }

        val versions = collectVersions(project)
        val jdkTable = ProjectJdkTable.getInstance()
        val sdkNames = jdkTable.allJdks.map { it.name }

        println("versions: $versions")
        println("sdks: $sdkNames")

        SDKMatchDialog(versions, sdkNames) { sdkNameMap ->
            val sdkMap = buildSdkMap(jdkTable, sdkNameMap)
            project.modules.forEach { module ->
                try {
                    val version = ModuleUtil.guessJdkVersion(module)
                    val sdk = sdkMap[version] ?: return@forEach
                    ModuleRootModificationUtil.setModuleSdk(module, sdk)
                } catch (_: Exception) {
                }
            }
        }.show()
    }

    private fun buildSdkMap(jdkTable: ProjectJdkTable, sdkNameMap: Map<String, String>): Map<String, Sdk> {
        val result = HashMap<String, Sdk>()
        sdkNameMap.entries.forEach { entry ->
            val jdk = jdkTable.findJdk(entry.value)
            if (jdk != null) {
                result[entry.key] = jdk
            }
        }
        return result
    }

    private fun collectVersions(proj: Project): List<String> {
        return proj.modules.mapNotNull { module ->
            return@mapNotNull try {
                ModuleUtil.guessJdkVersion(module)
            } catch (e: Exception) {
                null;
            }
        }.distinct()
    }

}