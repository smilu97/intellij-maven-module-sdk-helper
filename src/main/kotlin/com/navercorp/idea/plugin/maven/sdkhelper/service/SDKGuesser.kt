package com.navercorp.idea.plugin.maven.sdkhelper.service

interface SDKGuesser {

    /**
     * @param jdkVersion JDK version in pom.xml
     * @param jdkNames JDK names which actually exist in system
     * @return one of the elements of `jdkNames`
     */
    fun guess(jdkVersion: String, jdkNames: List<String>): String?

}