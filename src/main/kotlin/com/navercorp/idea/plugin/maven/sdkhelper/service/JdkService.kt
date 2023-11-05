package com.navercorp.idea.plugin.maven.sdkhelper.service

import com.intellij.openapi.projectRoots.Sdk

interface JdkService {

    fun getAvailableJDKNames(): List<String>

    fun getJdk(name: String): Sdk?

}