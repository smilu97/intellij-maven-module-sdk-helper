package com.navercorp.idea.plugin.maven.sdkhelper

import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootModificationUtil
import javax.swing.Popup

class MatchSDKAction: AnAction() {

    private val guesser: SDKGuesser = LevenshteinSDKGuesser()

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

        if (versions.isEmpty()) {
            TextDialog(
                title = "Error",
                content = "`jdk.version` not found",
            ).show()
            return
        }

        if (sdkNames.isEmpty()) {
            TextDialog(
                title = "Error",
                content = "Available JDK not found",
            ).show()
            return
        }

        val selections = versions.map { version ->
            SelectionState(
                key = version,
                label = "If `jdk.version` is $version, use",
                options = sdkNames,
                selected = this.guesser.guess(version, sdkNames),
            )
        }

        MultiSelectionDialog(
            title = "Select JDKs",
            selections = selections,
        ) {
            val sdkMap = buildSdkMap(jdkTable, selections)
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

    private fun buildSdkMap(jdkTable: ProjectJdkTable, selections: List<SelectionState>): Map<String, Sdk> {
        val result = HashMap<String, Sdk>()
        selections.forEach { selection ->
            if (selection.selected != null) {
                val jdk = jdkTable.findJdk(selection.selected!!)
                if (jdk != null) {
                    result[selection.key] = jdk
                }
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