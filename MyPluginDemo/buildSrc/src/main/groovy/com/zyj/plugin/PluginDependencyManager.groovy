package com.zyj.plugin

import com.zyj.plugin.collector.dependence.DependenceInfo

class PluginDependencyManager {
    private Set<String> jarDependenceSet = new HashSet<>()
    private Set<String> aarDependenceSet = new HashSet<>()
    private Set<String> projectDependenceSet = new HashSet<>()
    private Set<String> javaModulesDependenceSet = new HashSet<>()
    //记录需要插件中修改的dependence
    private Collection<DependenceInfo> stripDependencies
    public Map hostDependenceMap
    private File mHostDependenceFile

    def addJarDependence(String jarPath) {
        jarDependenceSet.add(jarPath)
    }

    def addAarDependence(String aarPath) {
        aarDependenceSet.add(aarPath)
    }

    def addProjectDependence(String projectPath) {
        projectDependenceSet.add(projectPath)
    }

    def addJavaModuleDependence(String javaModulePath) {
        javaModulesDependenceSet.add(javaModulePath)
    }
    def getAarDependenceSet(){
        return aarDependenceSet
    }
    def getJarDependenceSet(){
        return jarDependenceSet
    }
    def getProjectDependenceSet(){
        return projectDependenceSet
    }
    def getJavaModuleDependenceSet(){
        return javaModulesDependenceSet
    }
    Collection<DependenceInfo> getStripDependencies() {
        return stripDependencies
    }
    void setStripDependencies(Collection<DependenceInfo> stripDependencies) {
        this.stripDependencies = stripDependencies
    }
    void setHostDependenceMap(){
        if (hostDependenceMap == null) {
            hostDependenceMap = [] as LinkedHashMap
        }
        mHostDependenceFile.splitEachLine('\\s+', { columns ->
            String id = columns[0]
            def module = [group: 'unspecified', name: 'unspecified', version: 'unspecified']
            def findResult = id =~ /[^@:]+/
            int matchIndex = 0
            findResult.each {
                if (matchIndex == 0) {
                    module.group = it
                }
                if (matchIndex == 1) {
                    module.name = it
                }
                if (matchIndex == 2) {
                    module.version = it
                }
                matchIndex++
            }
            hostDependenceMap.put("${module.group}:${module.name}", module)
        })
    }
    Map getHostDependenceMap(){
        if(hostDependenceMap==null){
            setHostDependenceMap()
        }
        return hostDependenceMap
    }
    void setDependenciesFile(File dependenciesFile) {
        this.mHostDependenceFile = dependenciesFile
    }

}