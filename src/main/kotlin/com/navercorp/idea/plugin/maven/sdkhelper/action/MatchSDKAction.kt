package com.navercorp.idea.plugin.maven.sdkhelper.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.modules
import com.intellij.openapi.projectRoots.Sdk
import com.navercorp.idea.plugin.maven.sdkhelper.service.*
import com.navercorp.idea.plugin.maven.sdkhelper.ui.MultiSelectionDialog
import com.navercorp.idea.plugin.maven.sdkhelper.ui.SelectionState
import com.navercorp.idea.plugin.maven.sdkhelper.ui.TextDialog

class MatchSDKAction: AnAction() {

    companion object {
        private val JDK_VERSION_PROPERTIES = listOf("jdk.version", "maven.compiler.target", "maven.compiler.source")
    }

    private val guesser: SDKGuesser = LevenshteinSDKGuesser()
    private val jdkService: JdkService = JdkServiceImpl()
    private val moduleService: ModuleService = MavenModuleService()

    override fun actionPerformed(e: AnActionEvent) {
        try {
            showSelectionDialog(e)
        } catch (e: Exception) {
            TextDialog("error", e.message ?: "No message").show()
        }
    }

    private fun showSelectionDialog(e: AnActionEvent) {
        val project = e.project
            ?: throw RuntimeException("Project is ambiguous or does not exist")

        val modules = project.modules.mapNotNull { module ->
            val version = this.moduleService.readProperty(module, JDK_VERSION_PROPERTIES) ?: return@mapNotNull null
            ModuleAndJdkVersion(module, version)
        }

        val jdkVersions = modules.map { it.jdkVersion }.distinct()
        if (jdkVersions.isEmpty()) {
            throw RuntimeException("`jdk.version` not found")
        }
        println("Defined JDK versions: $jdkVersions")

        val jdkNames = this.jdkService.getAvailableJDKNames()
        if (jdkNames.isEmpty()) {
            throw RuntimeException("Available JDK not found")
        }
        println("JDKs: $jdkNames")

        val selections = jdkVersions.map { version ->
            SelectionState(
                key = version,
                label = "If `jdk.version` is $version, use",
                options = jdkNames,
                selected = this.guesser.guess(version, jdkNames),
            )
        }

        MultiSelectionDialog(
            title = "Select JDKs",
            selections = selections,
        ) {
            val jdkMap = buildVersionJdkMap(selections)
            modules.forEach { module ->
                try {
                    val jdk = jdkMap[module.jdkVersion] ?: return@forEach
                    this.moduleService.setModuleSdk(module.module, jdk)
                } catch (_: Exception) {
                }
            }
        }.show()
    }

    private fun buildVersionJdkMap(selections: List<SelectionState>): Map<String, Sdk> {
        val result = HashMap<String, Sdk>()
        selections.forEach { selection ->
            if (selection.selected != null) {
                val jdk = this.jdkService.getJdk(selection.selected!!)
                if (jdk != null) {
                    result[selection.key] = jdk
                }
            }
        }
        return result
    }

    data class ModuleAndJdkVersion(
        val module: Module,
        val jdkVersion: String,
    )

}