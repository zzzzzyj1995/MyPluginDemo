package com.zyj.plugin.collector.dependence

import com.android.builder.model.AndroidLibrary


class AarDependenceInfo extends AndroidLibraryDependenceInfo {

    AarDependenceInfo(String group, String artifact, String version, AndroidLibrary library) {
        super(group, artifact, version, library)
    }

    @Override
    File getJarFile() {
        return library.jarFile
    }

    @Override
    File getJniFolder() {
        return library.jniFolder
    }

    @Override
    Collection<File> getLocalJars() {
        return library.localJars
    }

    @Override
    File getAssetsFolder() {
        Log.i("AarDependenceInfo assets folder ", library.assetsFolder.path)
        return library.assetsFolder
    }
}