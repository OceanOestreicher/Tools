To use the agent in a gradle application, add the following to your build.gradle file:

configurations {
    agentJar {
        canBeResolved=true
    }
}

dependencies {
    agentJar 'custom.striker:utils:1.1'
}

tasks.register('copyAgentJar', Copy) {
    from {
        configurations.agentJar.files.collect { file ->
            zipTree(file).matching { include 'agent/*.jar' }
        }
    }
    into layout.buildDirectory.dir('agents')
    includeEmptyDirs = false
    eachFile { details ->
        details.path = details.name
    }
    rename { 'agent.jar' }
}

// optional: produce agent with normal builds
tasks.named('build') {
    dependsOn(copyAgentJar)
}


then add this as a jvm arg:

-javaagent:build/agents/agent.jar