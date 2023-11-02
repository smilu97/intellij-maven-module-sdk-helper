package com.navercorp.idea.plugin.maven.sdkhelper.mavensdkhelper

import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.project.modules
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.Panel
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import java.io.File
import java.util.function.Consumer
import javax.swing.JComponent
import kotlin.io.path.pathString

class ElevateSDKAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        JdkSelectionDialog(ProjectJdkTable.getInstance()) { sdk ->
            autoSelectModuleSdks(sdk)
        }.show()
    }

    private fun autoSelectModuleSdks(sdk: Sdk) {
        val activeProject = ProjectUtil.getActiveProject()
        if (activeProject == null) {
            println("No active project")
            return
        }

        println("Active project found: ${activeProject.name} having ${activeProject.modules.size} modules")

        activeProject.modules.forEach { module ->
            try {
                applySdkIfJava17(module, sdk)
            } catch (e: Exception) {
                println("exception: $e")
            }
        }
    }

    private fun applySdkIfJava17(module: Module, sdk: Sdk) {
        println("Handling module: ${module.name}")
        if (isJava17Module(module)) {
            ModuleRootModificationUtil.setModuleSdk(module, sdk)
        }
    }

    private fun isJava17Module(module: Module): Boolean {
        val moduleDir = guessModuleDir(module) ?: return false

        val pomFile = File(moduleDir.pathString + "/pom.xml")
        if (!pomFile.exists()) {
            return false
        }
        println("  Found pom.xml: $pomFile")

        val pom = MavenXpp3Reader().read(pomFile.inputStream())
        val jdkVersion = pom.properties.getProperty("jdk.version") ?: return false
        println("  jdk.version found: $jdkVersion")

        return if (jdkVersion == "17" || jdkVersion == "11") {
            println("  SETTING SDK 17")
            true
        } else {
            println("  IGNORED: $jdkVersion")
            false
        }
    }

    private fun guessModuleDir(module: Module) = module.guessModuleDir()?.toNioPath()

    class JdkSelectionDialog(
        jdkTable: ProjectJdkTable,
        private val onSdkSelected: Consumer<Sdk>
    ) : DialogWrapper(true) {
        private val jdkComboBox = ComboBox<String>()

        init {
            init()
            title = "Select JDK 17"
            jdkTable.allJdks.forEach { this.jdkComboBox.addItem(it.name) }
        }

        override fun createCenterPanel(): JComponent {
            val panel = Panel()
            panel.add(jdkComboBox)
            return panel
        }

        // Handle user actions in this dialog, such as OK button click
        override fun doOKAction() {
            val selectedIndex = jdkComboBox.selectedIndex
            if (selectedIndex != -1) {
                val selectedJdkName = jdkComboBox.selectedItem as String
                // Retrieve the selected JDK based on its name
                val selectedSdk = ProjectJdkTable.getInstance().findJdk(selectedJdkName)
                if (selectedSdk != null) {
                    this.onSdkSelected.accept(selectedSdk)
                }
            }
            super.doOKAction()
        }
    }
}