package com.navercorp.idea.plugin.maven.sdkhelper

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class TextDialog(
    title: String,
    private val content: String,
): DialogWrapper(true) {

    init {
        init()
        this.title = title
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row {
                label(content)
            }
        }
    }

}