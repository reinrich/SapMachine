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
 * @summary TODO: Materialize object in non-topframe at call returning an object
 * @author Richard Reingruber richard DOT reingruber AT sap DOT com
 *
 * @library /test/lib /test/hotspot/jtreg
 *
 * @run build TestScaffold VMConnection TargetListener TargetAdapter sun.hotspot.WhiteBox
 * @run main jdk.test.lib.FileInstaller compilerDirectives.json compilerDirectives.json
 * @run driver ClassFileInstaller sun.hotspot.WhiteBox
 *                                sun.hotspot.WhiteBox$WhiteBoxPermission
 * @run compile -g EATests.java
 * @run driver EATests
 *                 -Xbootclasspath/a:.
 *                 -XX:+UnlockDiagnosticVMOptions
 *                 -XX:+WhiteBoxAPI
 *                 -XX:+TraceDeoptimization
 *                 -XX:+PrintCompilation
 *                 -XX:+PrintInlining
 *                 -XX:-TieredCompilation
 *                 -Xbatch
 *                 -XX:CompilerDirectivesFile=compilerDirectives.json
 *                 -XX:CICompilerCount=1
 */

import java.util.List;

import com.sun.jdi.*;
import com.sun.jdi.event.*;

import compiler.testlibrary.CompilerUtils;
import jdk.test.lib.Asserts;
import sun.hotspot.WhiteBox;


/*
Manual execution:
    REPO=$1 # e.g. /priv/d038402/hg/jdk
    shift
    VMOPTS="$@"
    CLS_PATH="-cp ${REPO}/OpenJDKEclipseProjs/test.jdk/bin:${REPO}/OpenJDKEclipseProjs/test.lib/bin:${REPO}/OpenJDKEclipseProjs/test.compiler.testlibrary/bin"
    CMD=(
        ./images/jdk/bin/java
        -Dtest.jdk="$(pwd)/images/jdk"
        $CLS_PATH
        $VMOPTS
        -agentlib:jdwp=transport=dt_socket,address=9000,server=y,suspend=n
        EATests
        
        -Xbootclasspath/a:${REPO}/OpenJDKEclipseProjs/test.lib/bin  # WhiteBox.class
        -XX:+UnlockDiagnosticVMOptions
        -XX:+WhiteBoxAPI
        $CLS_PATH
        -XX:+TraceDeoptimization
        -XX:+PrintCompilation
        -XX:+PrintInlining
        -XX:-TieredCompilation
        -Xbatch
        $VMOPTS
        -XX:CompilerDirectivesFile=compilerDirectives.json
        -XX:CICompilerCount=1
    )
    print_and_run "${CMD[@]}"
*/

// TODO: remove trace options like '-XX:+PrintCompilation -XX:+PrintInlining' to avoid deadlock as in https://bugs.openjdk.java.net/browse/JDK-8213902

/********** target program **********/

class EATestsTarget {

    public static void main(String[] args) {
        new EAMaterializeLocalVariableUponGetTarget().run();
        new EAMaterializeLocalAtObjectReturnTarget() .run();
    }
}

abstract class EATargetTestCaseBase implements Runnable {

    public static final String TESTMETHOD_NAME = "dontinline_testMethod";

    public static final int COMPILE_THRESHOLD = 20000;

    public static final WhiteBox WB = WhiteBox.getWhiteBox();

    public int iResult;

    public void run() {
        msg(getName() + " is up and running.");
        compileTestMethod();
        warmupDone();
        checkCompLevel();
        dontinline_testMethod();
        checkResult();
        testCaseDone();
    }

    public abstract void dontinline_testMethod();

    public void dontinline_brkpt() {
        // will set breakpoint here
    }

    public String getName() {
        return getClass().getName();
    }

    public void warmupDone() {
        msg(getName() + " warmup done.");
    }

    public void testCaseDone() {
        msg(getName() + " done.");
    }

    public void compileTestMethod() {
        int callCount = COMPILE_THRESHOLD + 1000;
        while (callCount-- > 0) {
            dontinline_testMethod();
        }
    }

    public void checkCompLevel() {
        java.lang.reflect.Method m = null;
        try {
            m = getClass().getMethod(TESTMETHOD_NAME);
        } catch (NoSuchMethodException | SecurityException e) {
            Asserts.fail("could not check compilation level of", e);
        }
        int highest_level = CompilerUtils.getMaxCompilationLevel();
        Asserts.assertEQ(WB.getMethodCompilationLevel(m), highest_level,
                m + " not on expected compilation level");
    }

    // to be overridden as appropriate
    public int getExpectedIResult() {
        return 0;
    }

    private void checkResult() {
        Asserts.assertEQ(iResult, getExpectedIResult(), "checking iResult");
    }

    public void msg(String m) {
        System.out.println();
        System.out.println("### " + m);
        System.out.println();
    }
}

class PointXY {

    public int x;
    public int y;

    public PointXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class EAMaterializeLocalVariableUponGetTarget extends EATargetTestCaseBase {

    public static void main(String[] args) {
        new EAMaterializeLocalVariableUponGetTarget().run();
    }

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(4, 2);
        dontinline_brkpt();
        iResult = xy.x + xy.y;
    }

    @Override
    public int getExpectedIResult() {
        return 4 + 2;
    }
}

class EAMaterializeLocalAtObjectReturnTarget extends EATargetTestCaseBase {
    // TODO: Materialize object in non-topframe at call returning an object
    public void dontinline_testMethod() {
        PointXY xy = new PointXY(4, 2);
        Integer io = dontinline_brkpt_return_Integer();
        iResult = xy.x + xy.y + io;
    }

    public Integer dontinline_brkpt_return_Integer() {
        // We can't break directly in this method, as this results in making
        // the test method not entrant caused by an existing dependency
        dontinline_brkpt();
        return Integer.valueOf(23);
    }

    @Override
    public int getExpectedIResult() {
        return 4 + 2 + 23;
    }
}

 /********** test program **********/

abstract class EATestCaseBase implements Runnable {

    protected EATests env;

    private static final String targetTestCaseBase = EATargetTestCaseBase.class.getName();

    public abstract void runTestCase() throws Exception;

    public void run() {
        try {
            msgHL("Executing test case " + getClass().getName());
            env.testFailed = false;
            resumeToWarmupDone();
            runTestCase();
            resumeToTestCaseDone();
            checkPostConditions();
        } catch (Exception e) {
            Asserts.fail("Unexpected exception in test case " + getClass().getName(), e);
        }
    }

    public void resumeToWarmupDone() {
        msg("resuming to " + getTargetTestCaseBaseName() + ".warmupDone()V");
        env.resumeTo(getTargetTestCaseBaseName(), "warmupDone", "()V");
    }

    public void resumeToTestCaseDone() {
        env.resumeTo(getTargetTestCaseBaseName(), "testCaseDone", "()V");
    }

    public void checkPostConditions() throws Exception {
        Asserts.assertFalse(env.getExceptionCaught(), "Uncaught exception in Debuggee");

        String testName = getClass().getName();
        if (!env.testFailed) {
            env.println(testName  + ": passed");
        } else {
            throw new Exception(testName + ": failed");
        }
    }

    public EATestCaseBase setScaffold(EATests env) {
        this.env = env;
        return this;
    }

    public String getTargetTestCaseBaseName() {
        return targetTestCaseBase;
    }

    public void printStack(BreakpointEvent bpe) throws Exception {
        msg("Debuggee Stack:");
        List<StackFrame> stack_frames = bpe.thread().frames();
        int i = 0;
        for (StackFrame ff : stack_frames) {
            System.out.println("frame[" + i++ +"]: " + ff.location().method());
           
        }
    }

    public void msg(String m) {
        env.msg(m);
    }

    public void msgHL(String m) {
        env.msgHL(m);
    }

    public void checkLocalPointXYRef(StackFrame frame, String expectedMethodName, String lName) throws Exception {
        String lType = "PointXY";
        Asserts.assertEQ(expectedMethodName, frame.location().method().name());
        List<LocalVariable> localVars = frame.visibleVariables();
        msg("Check if the local variable " + lName + " in " + expectedMethodName + " has the expected value: ");
        boolean found = false;
        for (LocalVariable lv : localVars) {
            if (lv.name().equals(lName)) {
                found  = true;
                Value lVal = frame.getValue(lv);
                Asserts.assertNotNull(lVal);
                Asserts.assertEQ(lVal.type().name(), lType);
                ObjectReference lRef = (ObjectReference) lVal;
                // now check the fields
                ReferenceType rt = lRef.referenceType();
                Field xFd = rt.fieldByName("x");
                Value xVal = lRef.getValue(xFd);
                Asserts.assertEQ(((PrimitiveValue)xVal).intValue(), 4);
                Field yFd = rt.fieldByName("y");
                Value yVal = lRef.getValue(yFd);
                Asserts.assertEQ(((PrimitiveValue)yVal).intValue(), 2);
            }
        }
        Asserts.assertTrue(found);
        msg("OK.");
    }
}

class EAMaterializeLocalVariableUponGet extends EATestCaseBase {
    
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");

        printStack(bpe);

        // retrieve and check scalar replaced object
        checkLocalPointXYRef(bpe.thread().frame(1), EATargetTestCaseBase.TESTMETHOD_NAME, "xy");
    }
}

class EAMaterializeLocalAtObjectReturn extends EATestCaseBase {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");

        printStack(bpe);

        // retrieve and check scalar replaced object
        checkLocalPointXYRef(bpe.thread().frame(2), EATargetTestCaseBase.TESTMETHOD_NAME, "xy");
    }
}

public class EATests extends TestScaffold {

    EATests (String args[]) {
        super(args);
    }

    public static void main(String[] args) throws Exception {
        new EATests(args).startTests();
    }

    /********** test core **********/

    protected void runTests() throws Exception {
        String targetProgName = EATestsTarget.class.getName();
        msg("starting to main method in class " +  targetProgName);
        startToMain(targetProgName);

        new EAMaterializeLocalVariableUponGet().setScaffold(this).run();
        new EAMaterializeLocalAtObjectReturn() .setScaffold(this).run();

        // resume the target listening for events
        listenUntilVMDisconnect();
    }

    /**
     * Print a Message
     * @param m Message
     */
    public void msg(String m) {
        System.out.println();
        System.out.println("### " + m);
        System.out.println();
    }

    /**
     * Highlighted message.
     * @param m The message
     */
    public void msgHL(String m) {
        System.out.println();
        System.out.println();
        System.out.println("### ");
        System.out.println("### " + m);
        System.out.println("### ");
        System.out.println();
        System.out.println();
    }
}
