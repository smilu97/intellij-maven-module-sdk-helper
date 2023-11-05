package com.navercorp.idea.plugin.maven.sdkhelper

data class SelectionState(
    val key: String,
    val label: String,
    val options: List<String>,
    var selected: String? = null,
)
