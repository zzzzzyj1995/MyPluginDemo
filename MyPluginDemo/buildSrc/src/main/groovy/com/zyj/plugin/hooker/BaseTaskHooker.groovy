package com.zyj.plugin.hooker

import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.variant.BaseVariantData
import com.zyj.plugin.PluginDependencyManager
import com.zyj.plugin.hooker.TaskHookerManager
import org.gradle.api.Project
import org.gradle.api.Task
abstract class BaseTaskHooker<T extends Task> {
    private TaskHookerManager taskHookerManager
    private Project project
    private ApkVariant apkVariant

    BaseTaskHooker(Project project, ApkVariant apkVariant) {
        this.project = project
        this.apkVariant = apkVariant
    }

    Project getProject() {
        return this.project
    }

    ApkVariant getApkVariant() {
        return this.apkVariant
    }

    BaseVariantData getVariantData() {
        return ((ApplicationVariantImpl) this.apkVariant).variantData
    }

    VariantScope getScope() {
        return variantData.scope
    }

    /**
     * Return the transform name of the hooked task(transform task)
     */
    String getTransformName() {
        return ""
    }

    /**
     * Return the task name(exclude transform task)
     */
    String getTaskName() {
        return "${transformName}For${apkVariant.name.capitalize()}"
    }

    void setTaskHookerManager(TaskHookerManager taskHookerManager) {
        this.taskHookerManager = taskHookerManager
    }

    TaskHookerManager getTaskHookerManager() {
        return this.taskHookerManager
    }

    PluginDependencyManager getPluginDependenceManager() {
        return project.rootProject.ext.pluginDependencyManager
    }

    /**
     * Callback function before the hooked task executes
     * @param task Hooked task
     */
    abstract void beforeTaskExecute(T task)
    /**
     * Callback function after the hooked task executes
     * @param task Hooked task
     */
    abstract void afterTaskExecute(T task)
}