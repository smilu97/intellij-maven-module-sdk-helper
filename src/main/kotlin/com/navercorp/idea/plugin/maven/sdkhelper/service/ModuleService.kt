package com.navercorp.idea.plugin.maven.sdkhelper.service

import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk

interface ModuleService {

    /**
     * @param module module
     * @param keys the candidates of the property. The priority is from the first element to the last element.
     * @return value of the property, or null if not found
     */
    fun readProperty(module: Module, keys: List<String>): String?

    fun setModuleSdk(module: Module, sdk: Sdk)

}