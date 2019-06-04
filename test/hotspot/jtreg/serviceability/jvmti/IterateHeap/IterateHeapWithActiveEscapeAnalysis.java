/*
 * Copyright (c) 2019 SAP SE. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * @test
 * @bug 7777777
 * @comment TODO:change bug id
 * @summary Check by counting instances if scalar replaced objects (see escape analysis) are visited
 *          when iterating the java heap with JVMTI means.
 * @requires (vm.compMode != "Xcomp" & vm.compiler2.enabled)
 * @library /test/lib
 * @compile IterateHeapWithActiveEscapeAnalysis.java
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithActiveEscapeAnalysis
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 *                  IterateHeapWithActiveEscapeAnalysis
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithActiveEscapeAnalysis
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 *                  IterateHeapWithActiveEscapeAnalysis
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithActiveEscapeAnalysis
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 *                  IterateHeapWithActiveEscapeAnalysis
 */

import jdk.test.lib.Asserts;

public class IterateHeapWithActiveEscapeAnalysis {

    public static final int COMPILE_THRESHOLD = 20000;

    /**
     * Class of objects which are supposed to be scalar replaced in {@link TestCase#dontinline_testMethod()}
     */
    public static final Class<?> SCALAR_REPLACEMENTS_CLASS = ABox.class;

    
    /**
     * The class {@link #SCALAR_REPLACEMENTS_CLASS} will be tagged with this tag.
     */
    public static final long CLASS_TAG = 2525;

    public static native int jvmtiTagClass(Class<?> cls, long tag);

    // Methods to count instances of a given class available in JVMTI
    public static enum InstanceCountMethod {
        IterateOverReachableObjects,
        IterateOverHeap,
        IterateOverInstancesOfClass,
        FollowReferences,
        IterateThroughHeap
    }

    public static native int registerMethod(InstanceCountMethod m, String name);
    public static native void agentTearDown();

    
    /**
     * Count instances of a given class.
     * @param scalarReplCls Used by the method {@link InstanceCountMethod#IterateOverInstancesOfClass} as class to count instances of.
     *        Ignored by other counting methods.
     * @param clsTag Tag of the class to count instances of. Used by all methods except
     *        {@link InstanceCountMethod#IterateOverInstancesOfClass}
     * @param method JVMTI counting method to be used.
     * @return The number of instances or -1 if the call fails.
     */
    public static native int countInstancesOfClass(Class<?> scalarReplCls, long clsTag, InstanceCountMethod method);

    public static void main(String[] args) throws Exception {
        new IterateHeapWithActiveEscapeAnalysis().runTest();
    }

    public void runTest() throws Exception {
        setUp();
        for(InstanceCountMethod m : InstanceCountMethod.values()) {
            new TestCase(m).run();
        }
        agentTearDown();
    }

    public static void setUp() {
        // Tag the class of instances to be scalar replaced
        msg("tagging " + SCALAR_REPLACEMENTS_CLASS.getName() + " with tag " +  CLASS_TAG);
        jvmtiTagClass(SCALAR_REPLACEMENTS_CLASS, CLASS_TAG);

        // register various instance counting methods with agent
        for(InstanceCountMethod m : InstanceCountMethod.values()) {
            msg("register instance count method " + m.name());
            int rc = registerMethod(m, m.name());
            Asserts.assertGreaterThanOrEqual(rc, 0, "method " + m.name() + " is unknown to agent");
        }
    }

    static class ABox {
        public int val;

        public ABox(int v) {
            this.val = v;
        }
    }

    public static class TestCase implements Runnable {

        public InstanceCountMethod method;

        public long checkSum;
        public long loopCount;
        public volatile boolean doLoop;
        public volatile boolean targetIsInLoop;

        public void run() {
            try {
                msgHL("Testing " + method.name());
                warmUp();
                System.gc(); // get rid of dead instances from previous test cases
                runTest();
            } catch (Exception e) {
                Asserts.fail("Unexpected Exception", e);
            }
        }

        public void warmUp() {
            int callCount = COMPILE_THRESHOLD + 1000;
            doLoop = true;
            while (callCount-- > 0) {
                dontinline_testMethod();
            }
        }

        public long dontinline_endlessLoop() {
            long cs = checkSum;
            while (loopCount-- > 0 && doLoop) {
                targetIsInLoop = true;
                checkSum += checkSum % ++cs;
            }
            loopCount = 3;
            targetIsInLoop = false;
            return checkSum;
        }

        public void waitUntilTargetThreadHasEnteredEndlessLoop() throws Exception {
            while(!targetIsInLoop) {
                msg("Target has not yet entered the loop. Sleep 100ms.");
                try { Thread.sleep(100); } catch (InterruptedException e) { /*ignore */ }
            }
            msg("Target has entered the loop.");
        }

        public void terminateEndlessLoop() throws Exception {
            msg("Terminate endless loop");
            doLoop = false;
        }

       public TestCase(InstanceCountMethod m) {
            method = m;
        }

        public void runTest() throws Exception {
            loopCount = 1L << 62; // endless loop
            doLoop = true;
            Thread t1 = new Thread(() -> dontinline_testMethod(), "Target Thread (" + getClass().getName() + ")");
            t1.start();
            try {
                waitUntilTargetThreadHasEnteredEndlessLoop();
                msg("count instances of " + SCALAR_REPLACEMENTS_CLASS.getName() + " using JVMTI " + method.name());
                int count = countInstancesOfClass(SCALAR_REPLACEMENTS_CLASS, CLASS_TAG, method);
                msg("Done. Count is " + count);
                Asserts.assertGreaterThanOrEqual(count, 0, "countInstancesOfClass FAILED");
                Asserts.assertEQ(count, 1, "unexpected number of instances");
            } finally {
                terminateEndlessLoop();
                t1.join();
            }
        }

        public void dontinline_testMethod() {
            ABox b = new ABox(4);        // will be scalar replaced
            dontinline_endlessLoop();
            checkSum = b.val;
        }
    }

    public static void msg(String m) {
        System.out.println();
        System.out.println("### " + m);
        System.out.println();
    }

    public static void msgHL(String m) {
        System.out.println(); System.out.println(); System.out.println();
        System.out.println("#####################################################");
        System.out.println("### " + m);
        System.out.println("###");
        System.out.println();
    }
}
