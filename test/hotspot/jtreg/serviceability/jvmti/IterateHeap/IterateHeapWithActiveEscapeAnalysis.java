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
 * @summary TODO
 * @requires vm.compiler2.enabled
 * @library /test/lib
 * @compile IterateHeapWithActiveEscapeAnalysis.java
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithActiveEscapeAnalysis
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+TraceDeoptimization
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
 *                  -XX:+TraceDeoptimization
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:-EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking -XX:-UseOptoBiasInlining
 *                  IterateHeapWithActiveEscapeAnalysis
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithActiveEscapeAnalysis
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+TraceDeoptimization
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
 *                  -XX:+TraceDeoptimization
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 *                  IterateHeapWithActiveEscapeAnalysis
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithActiveEscapeAnalysis
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+TraceDeoptimization
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:-UseBiasedLocking
 *                  IterateHeapWithActiveEscapeAnalysis
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithActiveEscapeAnalysis
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+TraceDeoptimization
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:-UseBiasedLocking
 *                  IterateHeapWithActiveEscapeAnalysis
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithActiveEscapeAnalysis
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+TraceDeoptimization
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:-UseBiasedLocking
 *                  IterateHeapWithActiveEscapeAnalysis
 */

import jdk.test.lib.Asserts;

// TODO: revise section above

public class IterateHeapWithActiveEscapeAnalysis {

    public static final int COMPILE_THRESHOLD = 20000;

    private static long currentTag;

    public static native int jvmtiTagClass(Class<?> cls, long tag);
    
    public static native int countInstancesOfClass(long clsTag);

    public static void main(String[] args) throws Exception {
        new IterateHeapWithActiveEscapeAnalysis().runTest();
    }

    public void runTest() throws Exception {
        new TestCase_1().run();
    }

    public static long getNextTag() {
        return ++currentTag;
    }

    public static abstract class TestCaseBase implements Runnable {

        public long checkSum;
        public long loopCount;
        public volatile boolean doLoop;
        public volatile boolean targetIsInLoop;

        public void run() {
            try {
                msgHL("Executing test case " + getClass().getName());
                setUp();
                warmUp();
                runTest();
            } catch (Exception e) {
                Asserts.fail("Unexpected Exception", e);
            }
        }

        public void warmUp() {
            int callCount = COMPILE_THRESHOLD + 1000;
            while (callCount-- > 0) {
                dontinline_testMethod();
            }
        }

        public void setUp() {
        }

        public abstract void runTest() throws Exception;
        public abstract void dontinline_testMethod();

        public long dontinline_endlessLoop() {
            long cs = checkSum;
            while (loopCount-- > 0 && doLoop) {
                targetIsInLoop = true;
                checkSum += checkSum % ++cs;
            }
            loopCount = 3;
            doLoop = true;
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

        public void msg(String m) {
            System.out.println();
            System.out.println("### " + m);
            System.out.println();
        }
        
        public void msgHL(String m) {
            System.out.println();
            System.out.println("#####################################################");
            System.out.println("### " + m);
            System.out.println("###");
            System.out.println();
        }
    }

    public static class TestCase_1 extends TestCaseBase {

        public void runTest() throws Exception {
            Class<?> pCls = ABox.class;
            long classTag = getNextTag();
            msg("tagging " + pCls.getName() + " with tag " +  classTag);
            jvmtiTagClass(pCls, classTag);

            loopCount = 1L << 62; // endless loop
            Thread t1 = new Thread(() -> dontinline_testMethod(), "Target Thread (" + getClass().getName() + ")");
            t1.start();
            try {
                waitUntilTargetThreadHasEnteredEndlessLoop();
                msg("count instances of " + pCls.getName() + " using JVMTI IterateOverReachableObjects");
                int count = countInstancesOfClass(classTag);
                Asserts.assertEQ(count, 99, "unexpected number of instances");
                
            } finally {
                terminateEndlessLoop();
                t1.join();
            }
        }

        class ABox {
            public int val;

            public ABox(int v) {
                this.val = v;
            }
        }

        public void dontinline_testMethod() {
            ABox b = new ABox(4);
            dontinline_endlessLoop();
            checkSum = b.val;
        }

    }

}
