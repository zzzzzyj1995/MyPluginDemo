package com.zyj.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.pipeline.TransformManager
import com.zyj.plugin.PluginDependencyManager
import groovy.io.FileType
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
/**
 * Strip Host classes and java resources from project, it's an equivalent of provided compile
 * @author zhengtao
 */
class StripClassAndResTransform extends Transform {

    private Project project
    private PluginDependencyManager pluginDependencyManager

    StripClassAndResTransform(Project project) {
        this.project = project
        this.pluginDependencyManager=project.rootProject.ext.pluginDependencyManager
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

    /**
     * Only copy the jars or classes and java resources of retained aar into output directory
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        transformInvocation.inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                println("jarInput>>>>>${jarInput.file.name}")

            }
            input.directoryInputs.forEach { directoryInput ->
                println("directoryInput>>>>>>>${directoryInput.file.name}")
            }
        }
    }
}