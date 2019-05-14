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
 * @summary (TODO:bugid above) Test JVMTI's GetOwnedMonitorStackDepthInfo with scalar replaced objects and eliminated locks on stack
 * @requires (vm.compMode != "Xcomp" & vm.compiler2.enabled)
 * @library /test/lib
 * @compile GetOwnedMonitorStackDepthInfoWithEATest.java
 * @run main/othervm/native
 *                  -agentlib:GetOwnedMonitorStackDepthInfoWithEATest
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 *                  GetOwnedMonitorStackDepthInfoWithEATest
 * @run main/othervm/native
 *                  -agentlib:GetOwnedMonitorStackDepthInfoWithEATest
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:-EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking -XX:-UseOptoBiasInlining
 *                  GetOwnedMonitorStackDepthInfoWithEATest
 * @run main/othervm/native
 *                  -agentlib:GetOwnedMonitorStackDepthInfoWithEATest
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 *                  GetOwnedMonitorStackDepthInfoWithEATest
 * @run main/othervm/native
 *                  -agentlib:GetOwnedMonitorStackDepthInfoWithEATest
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 *                  GetOwnedMonitorStackDepthInfoWithEATest
 * @run main/othervm/native
 *                  -agentlib:GetOwnedMonitorStackDepthInfoWithEATest
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:-UseBiasedLocking
 *                  GetOwnedMonitorStackDepthInfoWithEATest
 * @run main/othervm/native
 *                  -agentlib:GetOwnedMonitorStackDepthInfoWithEATest
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:-UseBiasedLocking
 *                  GetOwnedMonitorStackDepthInfoWithEATest
 * @run main/othervm/native
 *                  -agentlib:GetOwnedMonitorStackDepthInfoWithEATest
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:-UseBiasedLocking
 *                  GetOwnedMonitorStackDepthInfoWithEATest
 */

import jdk.test.lib.Asserts;

public class GetOwnedMonitorStackDepthInfoWithEATest {

    public static final int COMPILE_THRESHOLD = 20000;

    public static native int getOwnedMonitorStackDepthInfo(Thread t1, Object[] ownedMonitors, int[] depths);

    public static void main(String[] args) throws Exception {
        new GetOwnedMonitorStackDepthInfoWithEATest().runTest();
    }

    public void runTest() throws Exception {
        new TestCase_1().run();
        new TestCase_2().run();
    }

    public static abstract class TestCaseBase implements Runnable {

        public long checkSum;
        public volatile long loopCount;
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
            while (loopCount-- > 0) {
                targetIsInLoop = true;
                checkSum += checkSum % ++cs;
            }
            loopCount = 3;
            targetIsInLoop = false;
            return checkSum;
        }

        public void waitUntilTargetThreadHasEnteredEndlessLoop() throws Exception {
            while(!targetIsInLoop) {
                msg("Target has not yet entered the loop. Sleep 200ms.");
                try { Thread.sleep(200); } catch (InterruptedException e) { /*ignore */ }
            }
            msg("Target has entered the loop.");
        }

        public void terminateEndlessLoop() throws Exception {
            msg("Terminate endless loop");
            do {
                loopCount = 0;
            } while(targetIsInLoop);
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
            loopCount = 1L << 62; // endless loop
            Thread t1 = new Thread(() -> dontinline_testMethod(), "Target Thread");
            t1.start();
            waitUntilTargetThreadHasEnteredEndlessLoop();
            int expectedMonitorCount = 1;
            int resultSize = expectedMonitorCount + 3;
            Object[] ownedMonitors = new Object[resultSize];
            int[]    depths = new int[resultSize];
            msg("Get monitor info");
            int monitorCount = getOwnedMonitorStackDepthInfo(t1, ownedMonitors, depths);
            terminateEndlessLoop();
            t1.join();
            Asserts.assertGreaterThanOrEqual(monitorCount, 0, "getOwnedMonitorsFor() call failed");
            msg("Monitor info:");
            for (int i = 0; i < monitorCount; i++) {
                System.out.println(i + ": cls=" + (ownedMonitors[i] != null ? ownedMonitors[i].getClass() : null) + " depth=" + depths[i]);
            }
            Asserts.assertEQ(monitorCount, expectedMonitorCount, "unexpected monitor count returned by getOwnedMonitorsFor()");
            Asserts.assertNotNull(ownedMonitors[0]);
            Asserts.assertSame(ownedMonitors[0].getClass(), LockCls.class);
            Asserts.assertEQ(depths[0], 1, "unexpected depth for owned monitor at index 0");
        }

        public void dontinline_testMethod() {
            LockCls l1 = new LockCls();
            synchronized (l1) {
                inlinedTestMethodWithNestedLocking(l1);
            }
        }

        public void inlinedTestMethodWithNestedLocking(LockCls l1) {
            synchronized (l1) {
                dontinline_endlessLoop();
            }
        }
    }

    public static class TestCase_2 extends TestCaseBase {

        public void runTest() throws Exception {
            loopCount = 1L << 62; // endless loop
            Thread t1 = new Thread(() -> dontinline_testMethod(), "Target Thread");
            t1.start();
            waitUntilTargetThreadHasEnteredEndlessLoop();
            int expectedMonitorCount = 2;
            int resultSize = expectedMonitorCount + 3;
            Object[] ownedMonitors = new Object[resultSize];
            int[]    depths = new int[resultSize];
            msg("Get monitor info");
            int monitorCount = getOwnedMonitorStackDepthInfo(t1, ownedMonitors, depths);
            terminateEndlessLoop();
            t1.join();
            Asserts.assertGreaterThanOrEqual(monitorCount, 0, "getOwnedMonitorsFor() call failed");
            msg("Monitor info:");
            for (int i = 0; i < monitorCount; i++) {
                System.out.println(i + ": cls=" + (ownedMonitors[i] != null ? ownedMonitors[i].getClass() : null) + " depth=" + depths[i]);
            }
            Asserts.assertEQ(monitorCount, expectedMonitorCount, "unexpected monitor count returned by getOwnedMonitorsFor()");
            Asserts.assertNotNull(ownedMonitors[0]);
            Asserts.assertSame(ownedMonitors[0].getClass(), LockCls2.class);
            Asserts.assertEQ(depths[0], 1, "unexpected depth for owned monitor at index 0");

            Asserts.assertNotNull(ownedMonitors[1]);
            Asserts.assertSame(ownedMonitors[1].getClass(), LockCls.class);
            Asserts.assertEQ(depths[1], 3, "unexpected depth for owned monitor at index 1");
        }

        public void dontinline_testMethod() {
            LockCls l1 = new LockCls();
            synchronized (l1) {
                inlinedTestMethodWithNestedLocking(l1);
            }
        }

        public void inlinedTestMethodWithNestedLocking(LockCls l1) {
            synchronized (l1) {
                dontinline_testMethod2();
            }
        }

        public void dontinline_testMethod2() {
            new LockCls2().inline_synchronized_testMethod(this);
        }

        public long dontinline_endlessLoop() {
            long cs = checkSum;
            while (loopCount-- > 0) {
                targetIsInLoop = true;
                checkSum += checkSum % ++cs;
            }
            loopCount = 3;
            targetIsInLoop = false;
            return checkSum;
        }
    }

    public static class LockCls {
    }

    public static class LockCls2 {
        public synchronized void inline_synchronized_testMethod(TestCaseBase testCase) {
            testCase.dontinline_endlessLoop();
        }
    }
}

