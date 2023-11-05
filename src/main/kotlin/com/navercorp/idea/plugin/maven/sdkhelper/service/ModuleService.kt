package com.navercorp.idea.plugin.maven.sdkhelper.service

import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk

interface ModuleService {

    fun readJdkVersion(module: Module): String?

    fun setModuleSdk(module: Module, sdk: Sdk)

}