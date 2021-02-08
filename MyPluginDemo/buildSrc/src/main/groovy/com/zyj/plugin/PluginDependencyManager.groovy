package com.zyj.plugin

import com.zyj.plugin.collector.dependence.DependenceInfo

class PluginDependencyManager {
    //记录需要插件中修改的dependence
    private Collection<DependenceInfo> stripDependencies
    public Map hostDependenceMap
    private File mHostDependenceFile

    Collection<DependenceInfo> getStripDependencies() {
        return stripDependencies
    }

    void setStripDependencies(Collection<DependenceInfo> stripDependencies) {
        this.stripDependencies = stripDependencies
    }

    void setHostDependenceMap() {
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

    Map getHostDependenceMap() {
        if (hostDependenceMap == null) {
            setHostDependenceMap()
        }
        return hostDependenceMap
    }

    void setDependenciesFile(File dependenciesFile) {
        this.mHostDependenceFile = dependenciesFile
    }

    Set<String> getStripJarsPaths() {
        Set<String> stripJarsPaths = new HashSet<>()
        if (stripDependencies!=null) {
            stripDependencies.each {
                println("jarFile.absolutePath>>>>>>${it.jarFile.absolutePath}")
                stripJarsPaths.add(it.jarFile.absolutePath)
            }
        }
        return stripJarsPaths
    }
}