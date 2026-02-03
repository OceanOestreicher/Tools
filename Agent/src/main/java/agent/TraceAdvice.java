// src/main/java/custom/agent/TraceAdvice.java
package agent;

import net.bytebuddy.asm.Advice;

public class TraceAdvice {

    @Advice.OnMethodEnter
    public static long onEnter(@Advice.Origin String signature) {
        long start = System.nanoTime();
        System.out.println("[TRACE] enter: " + signature);
        return start;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit(@Advice.Origin String signature,
                              @Advice.Enter long start,
                              @Advice.Thrown Throwable thrown) {
        long duration = System.nanoTime() - start;
        System.out.println("[TRACE] exit: " + signature + " duration_ns=" + duration +
                (thrown == null ? "" : " threw=" + thrown));
    }
}
