package com.zyj.plugin.collector.dependence

import com.android.builder.model.AndroidLibrary

class ProjectDependenceInfo extends AndroidLibraryDependenceInfo {
    private String projectPath

    ProjectDependenceInfo(String group, String artifact, String version, AndroidLibrary library) {
        super(group, artifact, version, library)
        this.projectPath = calculateProjectPath()
    }

    @Override
    File getJarFile() {
        String jarFilePath = "${projectPath}/build/intermediates/intermediate-jars/release/classes.jar"
        return new File(jarFilePath)
    }

    @Override
    File getJniFolder() {
        String projectJniPath = "${projectPath}/build/intermediates/intermediate-jars/release/jni"
        return new File(projectJniPath)
    }

    @Override
    Collection<File> getLocalJars() {
        return Collections.emptyList()
    }

    @Override
    File getAssetsFolder() {
        String assetsFolderPath = "${projectPath}/build/intermediates/library_assets/release/packageReleaseAssets/out"
        Log.i("ProjectDependenceInfo assets folder ", assetsFolderPath)
        return new File(assetsFolderPath)
    }

    private String calculateProjectPath() {
        String project = library.getProject()
        String[] columns = project.split(':')
        String pluginName = columns[columns.size() - 1]
        String jniFolder = library.getJniFolder().absolutePath
        String projectPath = jniFolder.substring(0, jniFolder.indexOf(pluginName) + pluginName.length())
        return projectPath
    }
}