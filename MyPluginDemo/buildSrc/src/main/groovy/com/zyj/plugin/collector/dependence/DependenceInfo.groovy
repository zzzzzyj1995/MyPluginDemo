package com.zyj.plugin.collector.dependence

/**
 * Represents a library in Android Project
 *
 * @author zhengtao
 */
public abstract class DependenceInfo {

    /**
     * Group name of dependence in a Maven repository
     */
    private String group
    /**
     * Module name of dependence in a Maven repository
     */
    private String artifact
    /**
     * Version of dependence in a Maven repository
     */
    private String version


    DependenceInfo(String group, String artifact, String version) {
        this.group = group
        this.artifact = artifact
        this.version = version
    }


    String getGroup() {
        return group
    }

    String getArtifact() {
        return artifact
    }

    String getVersion() {
        return version
    }

    abstract File getJarFile()

    @Override
    String toString() {
        return "${group}:${artifact}:${version} -> ${jarFile} -> ${super.toString()}"
    }
}