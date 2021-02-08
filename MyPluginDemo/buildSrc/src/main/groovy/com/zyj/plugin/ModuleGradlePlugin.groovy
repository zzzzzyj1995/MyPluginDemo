package com.zyj.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.variant.VariantFactory
import com.zyj.plugin.hooker.PrepareDependenciesHooker
import com.zyj.plugin.hooker.TaskHookerManager
import com.zyj.plugin.transform.StripClassAndResTransform
import com.zyj.plugin.util.Reflect
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.ResolutionStrategy

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class ModuleGradlePlugin implements Plugin<Project> {
    Project project
    PluginDependencyManager pluginDependencyManager

    @Override
    void apply(Project project) {
        this.project = project
        project.rootProject.ext.pluginDependencyManager = pluginDependencyManager=new PluginDependencyManager()
        println("project.extensions" + project.extensions)
        def android = project.extensions.findByType(AppExtension.class)
        project.rootProject.allprojects { Project libProject ->
//            if (libProject.name == "pluginLib") {
////                collectLibDependencies(libProject)
//            }
        }
        //生命周期「解析variant之前」,可以修改一些build.gradle原定的配置
        hookVariantFactory()
        project.afterEvaluate {
            android.applicationVariants.all { ApplicationVariantImpl appVariant ->
                registerTaskHooker(project, appVariant)
            }
        }
        android.registerTransform(new StripClassAndResTransform(project))
    }
    def registerTaskHooker(Project project, ApkVariant apkVariant) {
        TaskHookerManager taskHookerManager = new TaskHookerManager(project)
        taskHookerManager.registerTaskHooker(new PrepareDependenciesHooker(project, apkVariant))


    }

    def hookVariantFactory() {
        AppPlugin appPlugin = project.plugins.findPlugin(AppPlugin)
        Reflect reflect = Reflect.on(appPlugin.variantManager)
        VariantFactory variantFactory = Proxy.newProxyInstance(this.class.classLoader, [VariantFactory.class] as Class[],
                new InvocationHandler() {
                    Object delegate = reflect.get('variantFactory')
                    @Override
                    Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ('preVariantWork' == method.name) {
                                //加载host中的dependence.txt
                                loadHostDependence()
                                //修改module中dependence的版本
                                modifyModuleDependenceVersion()
                        }
                        return method.invoke(delegate, args)
                    }
                })
        reflect.set('variantFactory', variantFactory)
    }

    def modifyModuleDependenceVersion() {
        HashSet<String> replacedSet = [] as HashSet
            project.configurations.all { Configuration configuration ->
                configuration.resolutionStrategy { ResolutionStrategy resolutionStrategy ->
                    resolutionStrategy.eachDependency { DependencyResolveDetails details ->
                        def hostDependency = pluginDependencyManager.hostDependenceMap.get("${details.requested.group}:${details.requested.name}")
                        if (hostDependency != null) {
                            if ("${details.requested.version}" != "${hostDependency['version']}") {
                                println("hostDependency>>>>>${hostDependency}")
                                println("pluginDependency>>>>>${details.requested}")
                                String key = "${project.name}:${details.requested}"
                                if (!replacedSet.contains(key)) {
                                    replacedSet.add(key)
                                }
                                details.useVersion(hostDependency['version'])
                                pluginDependencyManager
                            }
                        }
                    }
                }
        }
    }
    def loadHostDependence(){
        println("rootDir>>>>>>${project.getRootDir()}")
        //TODO 把绝对路径改掉
        def hostLocalDir="/Users/yingjuanzhou/studyspace/Android/2-demo/MyPlugin/MyPluginDemo/app"
        File hostDependencies = new File(hostLocalDir, "hooker/debug/dependencies.txt")
        pluginDependencyManager.setDependenciesFile(hostDependencies)
    }



//    def collectLibDependencies(Project libProject) {
//        libProject.afterEvaluate {
//            def libraryExtension = libProject.extensions.findByType(LibraryExtension.class)
//            libraryExtension.libraryVariants.all { LibraryVariantImpl libraryVariant ->
//                libraryVariant.preBuild.doFirst {
//                    Consumer consumer = new Consumer<SyncIssue>() {
//                        @Override
//                        void accept(SyncIssue syncIssue) {
//                            Log.i 'HostGradlePlugin collectLibraryDependencies', "Error: ${syncIssue}"
//                        }
//                    }
//                    ImmutableMap<String, String> buildMapping =
//                            BuildMappingUtils.computeBuildMapping(project.getGradle())
//
//                    Dependencies dependencies = new ArtifactDependencyGraph()
//                            .createDependencies(
//                                    libraryVariant.variantData.scope,
//                                    false,
//                                    buildMapping,
//                                    consumer)
//                    println("javalibraryDependency>>>>${dependencies.javaLibraries}")
//                    println("javaModulesDependency>>>>${dependencies.javaModules}")
//                    println("libraryDependency>>>>${dependencies.libraries}")
//                    println("projectsDependency>>>>${dependencies.projects}")
//                    PluginDependencyManager pluginDependencyManager=new PluginDependencyManager()
//                    dependencies.projects.each {
//                        pluginDependencyManager.addProjectDependence(it)
//                    }
//                    dependencies.libraries.each {
//                        pluginDependencyManager.addAarDependence(it.name)
//                    }
//                    dependencies.javaModules.each {
//                        pluginDependencyManager.addJavaModuleDependence(it)
//                    }
//                    dependencies.javaLibraries.each {
//                        pluginDependencyManager.addJarDependence(it.name)
//                    }
//
//                }
//            }
//        }
//    }

    }