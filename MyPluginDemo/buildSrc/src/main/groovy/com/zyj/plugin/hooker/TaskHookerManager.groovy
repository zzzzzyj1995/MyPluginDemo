package com.zyj.plugin.hooker

import com.android.build.gradle.internal.pipeline.TransformTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

class TaskHookerManager {

    protected Map<String, BaseTaskHooker> taskHookerMap = new HashMap<>()
    protected Project project

    TaskHookerManager(Project project) {
        this.project = project
        println("debug:初始化taskHookmanager")
        project.gradle.addListener(new VirtualApkTaskListener())
    }
    void registerTaskHooker(BaseTaskHooker taskHooker) {
        taskHooker.setTaskHookerManager(this)
        taskHookerMap.put(taskHooker.taskName, taskHooker)
    }

    private class VirtualApkTaskListener implements TaskExecutionListener {

        @Override
        void beforeExecute(Task task){
            println("listener>>>>beforeExecute")
            if (task.project == project) {
                if (task in TransformTask) {
                    taskHookerMap["${task.transform.name}For${task.variantName.capitalize()}".toString()]?.beforeTaskExecute(task)
                } else {
                    taskHookerMap[task.name]?.beforeTaskExecute(task)
                }
            }
        }

        @Override
        void afterExecute(Task task, TaskState taskState) {
            if (task.project == project) {
                if (task in TransformTask) {
                    taskHookerMap["${task.transform.name}For${task.variantName.capitalize()}".toString()]?.afterTaskExecute(task)
                } else {
                    taskHookerMap[task.name]?.afterTaskExecute(task)
                }
            }
            recordInputAndOutput(task)
        }
        void recordInputAndOutput(Task task) {
            if (task.name == "lintVitalRelease") {
                return
            }
            println("task_name[${task.name} --- task_class[${task.class.name}]\n")
            ArrayList<String> record = new ArrayList<>()
            task.inputs.files.files.each {
                record.add("[input_path]:[${it.absolutePath}]")
            }
            task.outputs.files.files.each {
                record.add("[output_path]:[${it.absolutePath}]")
            }
            record.each {
                println(it)
            }
//            FileUtil.saveFile(project.getRootDir(), "allTaskInputAndOutput",
//                    {
//                        return record
//                    })
        }
    }
}