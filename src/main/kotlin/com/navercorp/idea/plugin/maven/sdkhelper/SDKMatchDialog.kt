package com.navercorp.idea.plugin.maven.sdkhelper

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import java.util.function.Consumer
import javax.swing.JComponent

class SDKMatchDialog(
    private val versions: List<String>,
    private val sdkNames: List<String>,
    private val onClickOK: Consumer<Map<String, String>>
): DialogWrapper(true) {

    private val comboBoxes = HashMap<String, ComboBox<String>>()

    init {
        init()
        this.title = "Select SDKs for versions"
    }

    override fun createCenterPanel(): JComponent {
        val sdkOptions = this.sdkNames + listOf("Ignore")
        return panel {
            versions.forEach { version ->
                row {
                    label("If jdk.version == $version, then use below SDK")
                    val box  = comboBox(sdkOptions).component
                    comboBoxes[version] = box
                    box.item = "Ignore"
                }
            }
        }
    }

    override fun doOKAction() {
        val payload = HashMap<String, String>()
        this.comboBoxes.entries.forEach { entry ->
            val version = entry.key
            val comboBox = entry.value
            val selectedIndex = comboBox.selectedIndex
            if (selectedIndex != -1) {
                payload[version] = comboBox.selectedItem as String
            }
        }
        this.onClickOK.accept(payload)
        super.doOKAction()
    }

}