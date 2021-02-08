package com.zyj.plugin.collector.dependence

import com.android.builder.model.JavaLibrary

/**
 * Represents a Jar library. This could be the output of a Java project.
 */

class JarDependenceInfo extends DependenceInfo {

    JavaLibrary library

    JarDependenceInfo(String group, String artifact, String version, JavaLibrary library) {
        super(group, artifact, version)
        this.library = library
    }

    @Override
    File getJarFile() {
        return library.jarFile
    }

}