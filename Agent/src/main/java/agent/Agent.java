// src/main/java/custom/agent/Agent.java
package agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        installAgent(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        installAgent(inst);
    }

    private static void installAgent(Instrumentation inst) {
        new AgentBuilder.Default()
                .ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
                .type(ElementMatchers.any()) // narrow this to packages you care about
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.method(ElementMatchers.isAnnotatedWith(Trace.class))
                                .intercept(Advice.to(TraceAdvice.class))
                )
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .installOn(inst);
    }
}
