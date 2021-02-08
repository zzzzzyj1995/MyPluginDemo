package com.zyj.plugin.collector.dependence


import com.android.builder.model.AndroidLibrary

/**
 * Represents a AAR dependence from Maven repository or Android library module
 *
 * @author zhengtao
 */
abstract class AndroidLibraryDependenceInfo extends DependenceInfo {

    /**
     * Android library dependence in android build system, delegate of AarDependenceInfo
     */
    protected AndroidLibrary library

    AndroidLibraryDependenceInfo(String group, String artifact, String version, AndroidLibrary library) {
        super(group, artifact, version)
        this.library = library
    }

    abstract File getJarFile()

    abstract File getJniFolder()


//    File getJniFolder() {
////        Log.i 'AarDependenceInfo', "Found [${library.resolvedCoordinates}]'s jni folder: ${library.jniFolder}"
//        return library.jniFolder
//        if (library.getProject() == null) {
//            return library.jniFolder
//        } else {
//            File projectJniFolder = getProjectJniFolder()
//            if (projectJniFolder.exists()) {
//                return projectJniFolder
//            } else {
//                return library.jniFolder
//            }
//        }
//    }

    private File getProjectJniFolder() {
        String project = library.getProject()
        String[] columns = project.split(':')
        String pluginName = columns[columns.size() - 1]
        String jniFolder = library.getJniFolder().absolutePath
        String projectPath = jniFolder.substring(0, jniFolder.indexOf(pluginName) + pluginName.length())
        String projectJniPath = "${projectPath}/build/intermediates/intermediate-jars/release/jni"
        Log.i("AarDependence project path ",projectJniPath)
        return new File(projectJniPath)
    }

    abstract Collection<File> getLocalJars()

    abstract File getAssetsFolder()
//
//    {
////        Log.i 'AarDependenceInfo', "Found [${library.resolvedCoordinates}]'s local jars: ${library.localJars}"
//        return library.localJars
//    }

    @Override
    String toString() {
        return "${super.toString()} -> ${library}"
    }
}