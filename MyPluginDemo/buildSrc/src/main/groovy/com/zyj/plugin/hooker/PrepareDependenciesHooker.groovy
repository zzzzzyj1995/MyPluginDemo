package com.zyj.plugin.hooker

import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.ide.DependencyFailureHandler
import com.android.build.gradle.internal.ide.dependencies.ArtifactDependencyGraph
import com.android.build.gradle.internal.ide.dependencies.ArtifactUtils
import com.android.build.gradle.internal.ide.dependencies.BuildMappingUtils
import com.android.build.gradle.internal.ide.dependencies.ResolvedArtifact
import com.android.build.gradle.internal.tasks.AppPreBuildTask
import com.android.builder.model.Dependencies
import com.android.builder.model.SyncIssue
import com.google.common.collect.ImmutableMap
import com.zyj.plugin.PluginDependencyManager
import com.zyj.plugin.collector.dependence.AarDependenceInfo
import com.zyj.plugin.collector.dependence.DependenceInfo
import com.zyj.plugin.collector.dependence.JarDependenceInfo
import com.zyj.plugin.collector.dependence.ProjectDependenceInfo
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.function.Consumer

import static com.android.build.gradle.internal.publishing.AndroidArtifacts.ConsumedConfigType.COMPILE_CLASSPATH

/**
 * Gather list of dependencies(aar&jar) need to be stripped&retained after the PrepareDependenciesTask finished.
 * The entire stripped operation throughout the build lifecycle is based on the result of this hooker。
 *
 * @author zhengtao
 */
class PrepareDependenciesHooker extends BaseTaskHooker<AppPreBuildTask> {


    PrepareDependenciesHooker(Project project, ApkVariant apkVariant) {
        super(project, apkVariant)
    }

    @Override
    String getTaskName() {
        return scope.getTaskName('pre', 'Build')
    }

    /**
     * Collect host dependencies via hostDependenceFile or exclude configuration before PrepareDependenciesTask execute,
     * @param task Gradle Task fo PrepareDependenciesTask
     */
    @Override
    void beforeTaskExecute(AppPreBuildTask task) {

    }

    /**
     * Classify all dependencies into retainedAarLibs & retainedJarLib & stripDependencies
     *
     * @param task Gradle Task fo PrepareDependenciesTask
     */
    @Override
    void afterTaskExecute(AppPreBuildTask task) {
        Consumer consumer = new Consumer<SyncIssue>() {
            @Override
            void accept(SyncIssue syncIssue) {
                println("PrepareDependenciesHooker>>>>>>Error: ${syncIssue}")
            }
        }
        PluginDependencyManager pluginDependencyManager = getPluginDependenceManager()
        Map hostDependence = pluginDependencyManager.hostDependenceMap
        ImmutableMap<String, String> buildMapping = BuildMappingUtils.computeBuildMapping(project.getGradle());
        Dependencies dependencies = new ArtifactDependencyGraph().createDependencies(scope, false, buildMapping, consumer)
        hostDependence.each {
            println("hostDependence.key>>>>>${it.key}")
        }

        def stripDependencies = [] as Collection<DependenceInfo>

//把需要删除的依赖存到stripDependencies
        dependencies.libraries.each {
            def mavenCoordinates = it.resolvedCoordinates
            println("PrepareDependenciesHooker>>>>>library ${mavenCoordinates.groupId}:${mavenCoordinates.artifactId}")
            def key=mavenCoordinates.artifactId.startsWith(":")?"${mavenCoordinates.groupId}${mavenCoordinates.artifactId}":"${mavenCoordinates.groupId}:${mavenCoordinates.artifactId}"
            if (hostDependence.containsKey(key)) {
                println("PrepareDependenciesHooker>>>>>Need strip aar: ${mavenCoordinates.groupId}:${mavenCoordinates.artifactId}:${mavenCoordinates.version}")
                if (it.getProject() == null) {
                    stripDependencies.add(
                            new AarDependenceInfo(
                                    mavenCoordinates.groupId,
                                    mavenCoordinates.artifactId,
                                    mavenCoordinates.version,
                                    it))
                } else {
                    stripDependencies.add(
                            new ProjectDependenceInfo(
                                    mavenCoordinates.groupId,
                                    mavenCoordinates.artifactId,
                                    mavenCoordinates.version,
                                    it))
                }
            } else {
                println("PrepareDependenciesHooker>>>>Need retain aar: ${mavenCoordinates.groupId}:${mavenCoordinates.artifactId}:${mavenCoordinates.version}")
            }
        }


        dependencies.javaLibraries.each {
            def mavenCoordinates = it.resolvedCoordinates
            if (hostDependence.containsKey("${mavenCoordinates.groupId}:${mavenCoordinates.artifactId}")) {
                println("PrepareDependenciesHooker>>>>>>Need strip jar: ${mavenCoordinates.groupId}:${mavenCoordinates.artifactId}:${mavenCoordinates.version}")
                stripDependencies.add(
                        new JarDependenceInfo(
                                mavenCoordinates.groupId,
                                mavenCoordinates.artifactId,
                                mavenCoordinates.version,
                                it))
            } else {
                println("PrepareDependenciesHooker>>>>>Need retain jar: ${mavenCoordinates.groupId}:${mavenCoordinates.artifactId}:${mavenCoordinates.version}")
            }
        }


        dependencies.javaModules.each {
            println("PrepareDependenciesHooker>>>>>>project:${it.projectPath}")
        }
        println("stripDependencies>>>>>>>${stripDependencies}")
        getPluginDependenceManager().setStripDependencies(stripDependencies)
    }

}