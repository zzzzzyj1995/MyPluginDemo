package com.zyj.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.pipeline.TransformManager
import com.zyj.plugin.PluginDependencyManager
import groovy.io.FileType
import org.apache.commons.io.FileUtils
import org.gradle.api.Project


class StripClassAndResTransform extends Transform {

    private Project project
    private PluginDependencyManager pluginDependencyManager

    StripClassAndResTransform(Project project) {
        this.project = project
        this.pluginDependencyManager = project.rootProject.ext.pluginDependencyManager
    }


    @Override
    String getName() {
        return 'stripClassAndRes'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }


    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        Set<String> stripJarPaths = pluginDependencyManager.getStripJarsPaths()
        def curStripJarPath
        stripJarPaths.each {
            if(it.contains("release")) {
                println("pluginDependencyManager.getVariant().name>>>>>>>${pluginDependencyManager.getVariant().name}")
                curStripJarPath=it.replace("release", pluginDependencyManager.getVariant().name)
            }
        }
        if(curStripJarPath!=null) {
            stripJarPaths.add(curStripJarPath)
        }
        stripJarPaths.each {
            println("stripJarPaths>>>>>>>${it}")
        }
        transformInvocation.inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                println("alllJarInput>>>>>${jarInput.file.absolutePath}")
                if(!stripJarPaths.contains(jarInput.file.absolutePath)){
                    println("stainJarInput>>>>>${jarInput.file.absolutePath}")
                    def dest = transformInvocation.outputProvider.getContentLocation(jarInput.name,
                            jarInput.contentTypes, jarInput.scopes, Format.JAR)
                    FileUtils.copyFile(jarInput.file, dest)
                }
                println("-----------------------------------------")
            }
            input.directoryInputs.forEach { directoryInput ->
                def destDir = transformInvocation.outputProvider.getContentLocation(
                        directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                directoryInput.file.traverse(type: FileType.FILES) {
                    def entryName = it.path.substring(directoryInput.file.path.length() + 1)
                    println("directoryInput>>>>>>${entryName}")
                    if (!stripJarPaths.contains(entryName)) {
                        def dest = new File(destDir, entryName)
                        FileUtils.copyFile(it, dest)
                    }
                }
            }
        }
    }
}