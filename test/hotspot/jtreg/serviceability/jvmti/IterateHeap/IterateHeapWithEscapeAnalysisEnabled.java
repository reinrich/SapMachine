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
 * @bug 8227745
 * @summary Check if optimizations based on escape analysis are reverted before local objects are found by
 *          JVMTI agents walking the heap or all references.
 * @requires (vm.compMode != "Xcomp" & vm.compiler2.enabled)
 * @library /test/lib
 * @compile IterateHeapWithEscapeAnalysisEnabled.java
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithEscapeAnalysisEnabled
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 *                  IterateHeapWithEscapeAnalysisEnabled
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithEscapeAnalysisEnabled
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:+DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 *                  IterateHeapWithEscapeAnalysisEnabled
 * @run main/othervm/native
 *                  -agentlib:IterateHeapWithEscapeAnalysisEnabled
 *                  -XX:+UnlockDiagnosticVMOptions
 *                  -Xms32m -Xmx32m
 *                  -XX:CompileCommand=dontinline,*::dontinline_*
 *                  -XX:+PrintCompilation
 *                  -XX:+PrintInlining
 *                  -XX:-TieredCompilation
 *                  -Xbatch
 *                  -XX:CICompilerCount=1
 *                  -XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 *                  IterateHeapWithEscapeAnalysisEnabled
 */

import jdk.test.lib.Asserts;

public class IterateHeapWithEscapeAnalysisEnabled {

    public static final int COMPILE_THRESHOLD = 20000;

    public static native int jvmtiTagClass(Class<?> cls, long tag);

    // Methods to tag or count instances of a given class available in JVMTI
    public static enum TaggingAndCountingMethods {
        IterateOverReachableObjects,
        IterateOverHeap,
        IterateOverInstancesOfClass,
        FollowReferences,
        IterateThroughHeap
    }

    public static native int registerMethod(TaggingAndCountingMethods m, String name);
    public static native void agentTearDown();

    
    /**
     * Count and tag instances of a given class.
     * @param cls Used by the method {@link TaggingAndCountingMethods#IterateOverInstancesOfClass} as class to count and tag instances of.
     *        Ignored by other counting methods.
     * @param clsTag Tag of the class to count and tag instances of. Used by all methods except
     *        {@link TaggingAndCountingMethods#IterateOverInstancesOfClass}
     * @param instanceTag The tag to be set for selected instances.
     * @param method JVMTI counting and tagging method to be used.
     * @return The number of instances or -1 if the call fails.
     */
    public static native int countAndTagInstancesOfClass(Class<?> cls, long clsTag, long instanceTag, TaggingAndCountingMethods method);

    /**
     * Get all objects tagged with the given tag.
     * @param tag The tag used to select objects.
     * @param result Selected objects are copied into this array.
     * @return -1 to indicated failure and 0 for success.
     */
    public static native int getObjectsWithTag(long tag, Object[] result);

    public static void main(String[] args) throws Exception {
        new IterateHeapWithEscapeAnalysisEnabled().runTestCases();
    }

    public void runTestCases() throws Exception {
        // register various instance tagging and counting methods with agent
        for(TaggingAndCountingMethods m : TaggingAndCountingMethods.values()) {
            msg("register instance count method " + m.name());
            int rc = registerMethod(m, m.name());
            Asserts.assertGreaterThanOrEqual(rc, 0, "method " + m.name() + " is unknown to agent");
        }

        new TestCase01(100).run();
        new TestCase02a(200).run();
        new TestCase02b(300).run();
        agentTearDown();
    }

    static class ABox {
        public int val;

        public ABox(int v) {
            this.val = v;
        }
    }

    static class ABBox {
        public int aVal;
        public int bVal;
        public TestCaseBase testCase; 

        public ABBox(TestCaseBase testCase) {
            this.testCase = testCase;
        }

        /**
         * Increment {@link #aVal} and {@link #bVal} under lock. The method is supposed to
         * be inlined into the test method and locking is supposed to be eliminated. After
         * this object escaped to the JVMTI agent, the code with eliminated locking must
         * not be used anymore. 
         */
        public synchronized void synchronizedSlowInc() {
            aVal++;
            testCase.waitingForCheck = true;
            dontinline_waitForCheck(testCase);
            testCase.waitingForCheck = false;
            bVal++;
        }
        
        public static void dontinline_waitForCheck(TestCaseBase testCase) {
            if (testCase.warmUpDone) {
                while(!testCase.checkingNow) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) { /*ign*/ }
                }
            }
        }

        /**
         * This method and incrementing {@link #aVal} and {@link #bVal} are synchronized.
         * So {@link #aVal} and {@link #bVal} should always be equal. Unless the optimized version
         * of {@link #synchronizedSlowInc()} without locking is still used after this object
         * escaped to the JVMTI agent.
         * @return
         */
        public synchronized boolean check() {
            return aVal == bVal;
        }
    }

    public static abstract class TestCaseBase implements Runnable {
        public final long classTag;
        public final Class<?> taggedClass;

        public long checkSum;
        public long loopCount;
        public volatile boolean doLoop;
        public volatile boolean targetIsInLoop;

        public volatile boolean waitingForCheck;
        public volatile boolean checkingNow;

        public boolean warmUpDone;

        public TestCaseBase(long classTag, Class<?> taggedClass) {
            this.classTag = classTag;
            this.taggedClass = taggedClass;
        }

        public void setUp() {
            // Tag the class of instances to be scalar replaced
            msg("tagging " + taggedClass.getName() + " with tag " +  classTag);
            jvmtiTagClass(taggedClass, classTag);
        }

        // to be overridden by test cases
        abstract public void dontinline_testMethod();

        public void warmUp() {
            msg("WarmUp: START");
            int callCount = COMPILE_THRESHOLD + 1000;
            doLoop = true;
            while (callCount-- > 0) {
                dontinline_testMethod();
            }
            warmUpDone = true;
            msg("WarmUp: DONE");
        }

        public Object dontinline_endlessLoop(Object argEscape) {
            long cs = checkSum;
            while (loopCount-- > 0 && doLoop) {
                targetIsInLoop = true;
                checkSum += checkSum % ++cs;
            }
            loopCount = 3;
            targetIsInLoop = false;
            return argEscape;
        }

        public void waitUntilTargetThreadHasEnteredEndlessLoop() {
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
    }

    /**
     * Count scalar replaced instances of a class using the methods listed in {@link TaggingAndCountingMethods}
     */
    public static class TestCase01 extends TestCaseBase {

        public TestCase01(long classTag) {
            super(classTag, ABox.class);
        }

        public void run() {
            try {
                msgHL(getClass().getName() + ": test if scalar replaced objects are reallocated and found by object/heap walks");
                setUp();
                warmUp();
                for(TaggingAndCountingMethods m : TaggingAndCountingMethods.values()) {
                    msgHL("Test Instance Count with Scalar Replacements using " + m.name());
                    System.gc(); // get rid of dead instances from previous test cases
                    runTest(m);
                }
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

        public void runTest(TaggingAndCountingMethods m) throws Exception {
            loopCount = 1L << 62; // endless loop
            doLoop = true;
            Thread t1 = new Thread(() -> dontinline_testMethod(), "Target Thread (" + getClass().getName() + ")");
            t1.start();
            try {
                waitUntilTargetThreadHasEnteredEndlessLoop();
                msg("count instances of " + taggedClass.getName() + " using JVMTI " + m.name());
                int count = countAndTagInstancesOfClass(taggedClass, classTag, 0, m);
                msg("Done. Count is " + count);
                Asserts.assertGreaterThanOrEqual(count, 0, "countInstancesOfClass FAILED");
                Asserts.assertEQ(count, 1, "unexpected number of instances");
            } finally {
                terminateEndlessLoop();
                t1.join();
            }
        }

        @Override
        public void dontinline_testMethod() {
            ABox b = new ABox(4);        // will be scalar replaced
            dontinline_endlessLoop(null);
            checkSum = b.val;
        }
    }

    /**
     * {@link #dontinline_testMethod()} creates an ArgEscape instance of {@link TestCaseBase#taggedClass} on stack.
     * The jvmti agent tags all instances of this class using one of the {@link TaggingAndCountingMethods}. Then it gets the tagged
     * instances using <code>GetObjectsWithTags()</code>. This is where the ArgEscape globally escapes.
     * It happens at a location without eliminated locking, but there is
     * eliminated locking following, so the compiled frame must be deoptimized. This is checked by letting the agent call the
     * synchronized method {@link ABBox#check()} on the escaped instance.
     */
    public static class TestCase02a extends TestCaseBase {

        public long instanceTag;

        public TestCase02a(long classTag) {
            super(classTag, ABBox.class);
            instanceTag = classTag + 1;
        }

        public void run() {
            try {
                msgHL(getClass().getName() + ": test if owning frame is deoptimized if ArgEscape escapes globally");
                setUp();
                warmUp();
                for(TaggingAndCountingMethods m : TaggingAndCountingMethods.values()) {
                    msgHL(getClass().getName() + ": Tag and Get of ArgEscapes using " + m.name());
                    waitingForCheck = false;
                    checkingNow = false;
                    System.gc(); // get rid of dead instances from previous test cases
                    runTest(m);
                }
            } catch (Exception e) {
                Asserts.fail("Unexpected Exception", e);
            }
        }

        public void runTest(TaggingAndCountingMethods m) throws Exception {
            loopCount = 1L << 62; // endless loop
            doLoop = true;
            Thread t1 = new Thread(() -> dontinline_testMethod(), "Target Thread (" + getClass().getName() + ")");
            try {
                t1.start();
                try {
                    waitUntilTargetThreadHasEnteredEndlessLoop();
                    msg("count and tag instances of " + taggedClass.getName() + " with tag " + instanceTag + " using JVMTI " + m.name());
                    int count = countAndTagInstancesOfClass(taggedClass, classTag, instanceTag, m);
                    msg("Done. Count is " + count);
                    Asserts.assertGreaterThanOrEqual(count, 0, "countInstancesOfClass FAILED");
                    Asserts.assertEQ(count, 1, "unexpected number of instances");
                } finally {
                    terminateEndlessLoop();
                }

                ABBox[] result = new ABBox[1];
                msg("get instances tagged with " + instanceTag);
                int err = getObjectsWithTag(instanceTag, result);
                msg("Done.");
                Asserts.assertEQ(0, err, "getObjectsWithTag FAILED");

                ABBox abBoxArgEscape = result[0];
                while (!waitingForCheck) {
                    Thread.yield();
                }
                msg("Check abBoxArgEscape's state is consistent");
                checkingNow = true;
                Asserts.assertTrue(abBoxArgEscape.check(), "Detected inconsistent state. abBoxArgEscape.aVal != abBoxArgEscape.bVal");
                msg("Ok.");
            } finally {
                checkingNow = true;
                t1.join();
            }
        }

        @Override
        public void dontinline_testMethod() {
            ABBox ab = new ABBox(this);
            dontinline_endlessLoop(ab);
            ab.synchronizedSlowInc();
        }
    }

    /**
     * Like {@link TestCase02a}, with the exception that at the location in {@link #dontinline_testMethod()} where the
     * ArgEscape escapes it is not referenced by a local variable.
     */
    public static class TestCase02b extends TestCaseBase {

        public long instanceTag;

        public TestCase02b(long classTag) {
            super(classTag, ABBox.class);
            instanceTag = classTag + 1;
        }

        public void run() {
            try {
                msgHL(getClass().getName() + ": test if owning frame is deoptimized if ArgEscape escapes globally");
                setUp();
                warmUp();
                for(TaggingAndCountingMethods m : TaggingAndCountingMethods.values()) {
                    msgHL(getClass().getName() + ": Tag and Get of ArgEscapes using " + m.name());
                    waitingForCheck = false;
                    checkingNow = false;
                    System.gc(); // get rid of dead instances from previous test cases
                    runTest(m);
                }
            } catch (Exception e) {
                Asserts.fail("Unexpected Exception", e);
            }
        }

        public void runTest(TaggingAndCountingMethods m) throws Exception {
            loopCount = 1L << 62; // endless loop
            doLoop = true;
            Thread t1 = new Thread(() -> dontinline_testMethod(), "Target Thread (" + getClass().getName() + ")");
            try {
                t1.start();
                try {
                    waitUntilTargetThreadHasEnteredEndlessLoop();
                    msg("count and tag instances of " + taggedClass.getName() + " with tag " + instanceTag + " using JVMTI " + m.name());
                    int count = countAndTagInstancesOfClass(taggedClass, classTag, instanceTag, m);
                    msg("Done. Count is " + count);
                    Asserts.assertGreaterThanOrEqual(count, 0, "countInstancesOfClass FAILED");
                    Asserts.assertEQ(count, 1, "unexpected number of instances");
                } finally {
                    terminateEndlessLoop();
                }

                ABBox[] result = new ABBox[1];
                msg("get instances tagged with " + instanceTag);
                int err = getObjectsWithTag(instanceTag, result);
                msg("Done.");
                Asserts.assertEQ(0, err, "getObjectsWithTag FAILED");

                ABBox abBoxArgEscape = result[0];
                while (!waitingForCheck) {
                    Thread.yield();
                }
                msg("Check abBoxArgEscape's state is consistent");
                checkingNow = true;
                Asserts.assertTrue(abBoxArgEscape.check(), "Detected inconsistent state. abBoxArgEscape.aVal != abBoxArgEscape.bVal");
                msg("Ok.");
            } finally {
                checkingNow = true;
                t1.join();
            }
        }

        @Override
        public void dontinline_testMethod() {
            // The new instance is an ArgEscape instance and escapes to the JVMTI agent
            // while the target thread is in the call to dontinline_endlessLoop(). At this
            // location there is no local variable that references the ArgEscape.
            ((ABBox) dontinline_endlessLoop(new ABBox(this))).synchronizedSlowInc();;
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
