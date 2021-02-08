package com.zyj.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.api.LibraryVariantImpl
import com.android.build.gradle.internal.ide.dependencies.ArtifactDependencyGraph
import com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils
import com.android.builder.model.AndroidLibrary
import com.android.builder.model.Dependencies
import com.android.builder.model.JavaLibrary
import com.android.builder.model.SyncIssue
import com.google.common.collect.ImmutableMap
import com.zyj.plugin.collector.dependence.AarDependenceInfo
import com.zyj.plugin.collector.dependence.JarDependenceInfo
import com.zyj.plugin.hooker.PrepareDependenciesHooker
import com.zyj.plugin.hooker.TaskHookerManager
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier

import java.util.function.Consumer

class HostGradlePlugin implements Plugin<Project> {
    File zyjHostDir
    Project project

    @Override
    void apply(Project project) {
        this.project = project
        zyjHostDir = new File(project.getBuildDir(), "zyjHost")
        project.afterEvaluate {
            project.android.applicationVariants.each { ApplicationVariantImpl variant ->
                //生成hostDependence文件
                generateDependenceFile(variant)

            }
        }

    }

    def generateDependenceFile(ApplicationVariantImpl applicationVariant) {
        def collectAction = {
            List<String> dependenciesList = new ArrayList<String>()
            Consumer consumer = new Consumer<SyncIssue>() {
                @Override
                void accept(SyncIssue syncIssue) {
                }
            }
            ImmutableMap<String, String> buildMapping =
                    BuildMappingUtils.computeBuildMapping(project.getGradle())

            Dependencies dependencies = new ArtifactDependencyGraph()
                    .createDependencies(
                            applicationVariant.variantData.scope,
                            false,
                            buildMapping,
                            consumer)

            dependencies.getLibraries().each { AndroidLibrary androidLibrary ->
                dependenciesList.add(androidLibrary.name)

            }
            dependencies.getJavaLibraries().each { JavaLibrary library ->
                dependenciesList.add(library.name)
            }
            dependencies.getProjects().each { String path ->
                dependenciesList.add(path)
            }
            Collections.sort(dependenciesList)
            return dependenciesList
        }
        FileUtil.saveFile(zyjHostDir, "dependencies", collectAction)

    }


}