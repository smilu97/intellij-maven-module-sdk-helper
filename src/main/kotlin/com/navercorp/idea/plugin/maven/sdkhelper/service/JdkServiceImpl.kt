package com.navercorp.idea.plugin.maven.sdkhelper.service

import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk

class JdkServiceImpl: JdkService {

    private val jdkTable = ProjectJdkTable.getInstance()

    override fun getAvailableJDKNames(): List<String> {
        return this.jdkTable.allJdks.map { it.name }
    }

    override fun getJdk(name: String): Sdk? {
        return this.jdkTable.findJdk(name)
    }
}