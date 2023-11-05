package com.navercorp.idea.plugin.maven.sdkhelper.ui

import com.intellij.openapi.observable.util.whenItemSelected
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class MultiSelectionDialog(
    title: String,
    private val selections: List<SelectionState>,
    private val onClickOK: Runnable,
): DialogWrapper(true) {

    init {
        init()
        this.title = title
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            selections.forEach { selection ->
                row {
                    label(selection.label)
                    val box  = comboBox(selection.options + listOf("ignore")).component
                    box.whenItemSelected<String> {
                        selection.selected = if (it == "ignore") null else it
                    }
                    box.item = if (selection.selected == null) "ignore" else selection.selected
                }
            }
        }
    }

    override fun doOKAction() {
        this.onClickOK.run()
        super.doOKAction()
    }

}