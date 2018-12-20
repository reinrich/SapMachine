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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

/////////////////////////////////////////////////////////////////////////////
// Shared base class for test cases for both, debugger and debuggee.
/////////////////////////////////////////////////////////////////////////////

class EATestCaseBaseShared {
    // If the property is given, then just the test case it refers to is executed.
    // Use it to diagnose test failures.
    public static final String RUN_ONLY_TEST_CASE_PROPERTY = "test.jdk.com.sun.jdi.EATests.onlytestcase";
    public static final String RUN_ONLY_TEST_CASE = System.getProperty(RUN_ONLY_TEST_CASE_PROPERTY);

    public final String testCaseName;

    public EATestCaseBaseShared() {
        String clName = getClass().getName();
        int tidx = clName.lastIndexOf("Target");
        testCaseName = tidx > 0 ? clName.substring(0, tidx) : clName;
    }

    public boolean shouldSkip() {
        return EATestCaseBaseShared.RUN_ONLY_TEST_CASE != null && !testCaseName.equals(EATestCaseBaseShared.RUN_ONLY_TEST_CASE);
    }
}

/////////////////////////////////////////////////////////////////////////////
//Target main class, i.e. the program to be debugged.
/////////////////////////////////////////////////////////////////////////////

class EATestsTarget {

    public static void main(String[] args) {
        new EAMaterializeLocalVariableUponGetTarget().run();
        new EAGetWithoutMaterializeTarget()          .run();
        new EAMaterializeLocalAtObjectReturnTarget() .run();
        new EAMaterializeIntArrayTarget()            .run();
        new EAMaterializeLongArrayTarget()           .run();
        new EAMaterializeFloatArrayTarget()          .run();
        new EAMaterializeDoubleArrayTarget()         .run();
        new EAMaterializeObjectArrayTarget()         .run();
        new EAMaterializeObjectWithConstantAndNotConstantValuesTarget().run();
        new EAMaterializeObjReferencedBy2LocalsTarget().run();
        new EAMaterializeObjReferencedBy2LocalsAndModifyTarget().run();
    }

}

/////////////////////////////////////////////////////////////////////////////
// Debugger main class
/////////////////////////////////////////////////////////////////////////////

public class EATests extends TestScaffold {

    EATests(String args[]) {
        super(args);
    }

    public static void main(String[] args) throws Exception {
        if (EATestCaseBaseShared.RUN_ONLY_TEST_CASE != null) {
            args = Arrays.copyOf(args, args.length + 1);
            args[args.length - 1] = "-D" + EATestCaseBaseShared.RUN_ONLY_TEST_CASE_PROPERTY + "=" + EATestCaseBaseShared.RUN_ONLY_TEST_CASE;
        }
        new EATests(args).startTests();
    }

    // Execute known test cases
    protected void runTests() throws Exception {
        msg("EATestsTarget.RUN_ONLY_TEST_CASE=" + EATestCaseBaseShared.RUN_ONLY_TEST_CASE);

        String targetProgName = EATestsTarget.class.getName();
        msg("starting to main method in class " +  targetProgName);
        startToMain(targetProgName);

        new EAMaterializeLocalVariableUponGet().setScaffold(this).run();
        new EAGetWithoutMaterialize()          .setScaffold(this).run();
        new EAMaterializeLocalAtObjectReturn() .setScaffold(this).run();
        new EAMaterializeIntArray()            .setScaffold(this).run();
        new EAMaterializeLongArray()           .setScaffold(this).run();
        new EAMaterializeFloatArray()          .setScaffold(this).run();
        new EAMaterializeDoubleArray()         .setScaffold(this).run();
        new EAMaterializeObjectArray()         .setScaffold(this).run();
        new EAMaterializeObjectWithConstantAndNotConstantValues().setScaffold(this).run();
        new EAMaterializeObjReferencedBy2Locals().setScaffold(this).run();
        new EAMaterializeObjReferencedBy2LocalsAndModify().setScaffold(this).run();

        // resume the target listening for events
        listenUntilVMDisconnect();
    }

    // Print a Message
    public void msg(String m) {
        System.out.println();
        System.out.println("###(Debugger) " + m);
        System.out.println();
    }

    // Highlighted message.
    public void msgHL(String m) {
        System.out.println();
        System.out.println();
        System.out.println("##########################################################");
        System.out.println("### " + m);
        System.out.println("### ");
        System.out.println();
        System.out.println();
    }
}

/////////////////////////////////////////////////////////////////////////////
// Base class for debugger side of test cases.
/////////////////////////////////////////////////////////////////////////////

abstract class EATestCaseBaseDebugger  extends EATestCaseBaseShared implements Runnable {

    protected EATests env;

    private static final String targetTestCaseBase = EATestCaseBaseTarget.class.getName();

    public abstract void runTestCase() throws Exception;

    public void run() {
        if (shouldSkip()) {
            msg("skipping " + testCaseName);
            return;
        }
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

    public EATestCaseBaseDebugger setScaffold(EATests env) {
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

    // See 4.3.2. Field Descriptors in The Java Virtual Machine Specification
    // (https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.3.2)
    enum FD {
        I, // int
        J, // long
        F, // float
        D, // double
    }


    // Map field descriptor to jdi type string
    public static final Map<FD, String> FD2JDIType = Map.of(FD.I, "int[]", FD.J, "long[]", FD.F, "float[]", FD.D, "double[]");

    // Map field descriptor to PrimitiveValue getter
    public static final Function<PrimitiveValue, Integer> v2I = PrimitiveValue::intValue;
    public static final Function<PrimitiveValue, Long>    v2J = PrimitiveValue::longValue;
    public static final Function<PrimitiveValue, Float>   v2F = PrimitiveValue::floatValue;
    public static final Function<PrimitiveValue, Double>  v2D = PrimitiveValue::doubleValue;
    Map<FD, Function<PrimitiveValue, ?>> FD2getter = Map.of(FD.I, v2I, FD.J, v2J, FD.F, v2F, FD.D, v2D);

    protected void checkLocalPrimitiveArray(StackFrame frame, String lName, FD desc, Object expVals) throws Exception {
        String lType = FD2JDIType.get(desc);
        Asserts.assertNotNull(lType, "jdi type not found");
        Asserts.assertEQ(EATestCaseBaseTarget.TESTMETHOD_NAME, frame .location().method().name());
        List<LocalVariable> localVars = frame.visibleVariables();
        msg("Check if the local array variable '" + lName  + "' in " + EATestCaseBaseTarget.TESTMETHOD_NAME + " has the expected elements: ");
        boolean found = false;
        for (LocalVariable lv : localVars) {
            if (lv.name().equals(lName)) {
                found  = true;
                Value lVal = frame.getValue(lv);
                Asserts.assertNotNull(lVal);
                Asserts.assertEQ(lVal.type().name(), lType);
                ArrayReference aRef = (ArrayReference) lVal;
                Asserts.assertEQ(3, aRef.length());
                // now check the elements
                for (int i = 0; i < aRef.length(); i++) {
                    Object actVal = FD2getter.get(desc).apply((PrimitiveValue)aRef.getValue(i));
                    Object expVal = Array.get(expVals, i);
                    Asserts.assertEQ(expVal, actVal, "checking element at index " + i);
                }
            }
        }
        Asserts.assertTrue(found);
        msg("OK.");
    }

    protected void checkLocalObjectArray(StackFrame frame, String expectedMethodName, String lName, String lType, ObjectReference[] expVals) throws Exception {
        Asserts.assertEQ(EATestCaseBaseTarget.TESTMETHOD_NAME, frame .location().method().name());
        List<LocalVariable> localVars = frame.visibleVariables();
        msg("Check if the local array variable '" + lName  + "' in " + EATestCaseBaseTarget.TESTMETHOD_NAME + " has the expected elements: ");
        boolean found = false;
        for (LocalVariable lv : localVars) {
            if (lv.name().equals(lName)) {
                found  = true;
                Value lVal = frame.getValue(lv);
                Asserts.assertNotNull(lVal);
                Asserts.assertEQ(lType, lVal.type().name());
                ArrayReference aRef = (ArrayReference) lVal;
                Asserts.assertEQ(3, aRef.length());
                // now check the elements
                for (int i = 0; i < aRef.length(); i++) {
                    ObjectReference actVal = (ObjectReference)aRef.getValue(i);
                    Asserts.assertSame(expVals[i], actVal, "checking element at index " + i);
                }
            }
        }
        Asserts.assertTrue(found);
        msg("OK.");
    }

    protected ObjectReference getLocalRef(StackFrame frame, String lType, String lName) throws Exception {
        String expectedMethodName = EATestCaseBaseTarget.TESTMETHOD_NAME;
        Asserts.assertEQ(expectedMethodName, frame.location().method().name());
        List<LocalVariable> localVars = frame.visibleVariables();
        msg("Get and check local variable '" + lName + "' in " + expectedMethodName);
        ObjectReference lRef = null;
        for (LocalVariable lv : localVars) {
            if (lv.name().equals(lName)) {
                Value lVal = frame.getValue(lv);
                Asserts.assertNotNull(lVal);
                Asserts.assertEQ(lType, lVal.type().name());
                lRef = (ObjectReference) lVal;
            }
        }
        Asserts.assertNotNull(lRef, "Local variable '" + lName + "' not found");
        msg("OK.");
        return lRef;
    }

    protected void checkField(ObjectReference o, FD desc, String fName, Object expVal) throws Exception {
        msg("check field " + fName);
        ReferenceType rt = o.referenceType();
        Field fld = rt.fieldByName(fName);
        Value val = o.getValue(fld);
        Object actVal = FD2getter.get(desc).apply((PrimitiveValue) val);
        Asserts.assertEQ(expVal, actVal, "field '" + fName + "' has unexpected value.");
        msg("ok");
    }

    protected void checkObjField(ObjectReference o, String type, String fName, Object expVal) throws Exception {
        msg("check field " + fName);
        ReferenceType rt = o.referenceType();
        Field fld = rt.fieldByName(fName);
        Value actVal = o.getValue(fld);
        Asserts.assertEQ(expVal, actVal, "field '" + fName + "' has unexpected value.");
        msg("ok");
    }

    protected void setField(ObjectReference o, FD desc, String fName, Value val) throws Exception {
        msg("set field " + fName + " = " + val);
        ReferenceType rt = o.referenceType();
        Field fld = rt.fieldByName(fName);
        o.setValue(fld, val);
        msg("ok");
    }
}

/////////////////////////////////////////////////////////////////////////////
// Base class for debuggee side of test cases.
/////////////////////////////////////////////////////////////////////////////

abstract class EATestCaseBaseTarget extends EATestCaseBaseShared implements Runnable {

    public static final String TESTMETHOD_NAME = "dontinline_testMethod";

    public static final int COMPILE_THRESHOLD = 20000;

    public static final WhiteBox WB = WhiteBox.getWhiteBox();

    public int  iResult;
    public long lResult;
    public float  fResult;
    public double dResult;

    public int testMethodDepth;

    public boolean testFrameShouldBeDeoptimized;

    private boolean warmupDone;


    public static int    NOT_CONST_1I = 1;
    public static long   NOT_CONST_1L = 1L;
    public static float  NOT_CONST_1F = 1.1F;
    public static double NOT_CONST_1D = 1.1D;

    public static          Long NOT_CONST_1_OBJ = Long.valueOf(1);
    public static final    Long CONST_2_OBJ     = Long.valueOf(2);
    public static final    Long CONST_3_OBJ     = Long.valueOf(3);

    public void run() {
        msg("EATestsTarget.RUN_ONLY_TEST_CASE=" + EATestCaseBaseShared.RUN_ONLY_TEST_CASE);
        if (shouldSkip()) {
            msg("skipping " + testCaseName);
            return;
        }
        setUp();
        msg(testCaseName + " is up and running.");
        compileTestMethod();
        warmupDone();
        checkCompLevel();
        dontinline_testMethod();
        checkResult();
        testCaseDone();
    }

    public void setUp() {
        testMethodDepth = 1;
        testFrameShouldBeDeoptimized = true;
    }

    public abstract void dontinline_testMethod();

    public void dontinline_brkpt() {
        // will set breakpoint here after warmup
        if (warmupDone) {
            if (testFrameShouldBeDeoptimized) {
                Asserts.assertTrue(WB.isFrameDeoptimized(testMethodDepth+1), testCaseName + ": expected test method frame at depth " + testMethodDepth + " to be deoptimized");
            } else {
                Asserts.assertFalse(WB.isFrameDeoptimized(testMethodDepth+1), testCaseName + ": expected test method frame at depth " + testMethodDepth + " not to be deoptimized");
            }
        }
    }

    public void warmupDone() {
        msg(testCaseName + " warmup done.");
        warmupDone = true;
    }

    public void testCaseDone() {
        msg(testCaseName + " done.");
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
        Asserts.assertEQ(highest_level, WB.getMethodCompilationLevel(m),
                m + " not on expected compilation level");
    }

    // to be overridden as appropriate
    public int getExpectedIResult() {
        return 0;
    }

    // to be overridden as appropriate
    public long getExpectedLResult() {
        return 0;
    }

    // to be overridden as appropriate
    public float getExpectedFResult() {
        return 0f;
    }

    // to be overridden as appropriate
    public double getExpectedDResult() {
        return 0d;
    }

    private void checkResult() {
        Asserts.assertEQ(getExpectedIResult(), iResult, "checking iResult");
        Asserts.assertEQ(getExpectedLResult(), lResult, "checking lResult");
        Asserts.assertEQ(getExpectedFResult(), fResult, "checking fResult");
        Asserts.assertEQ(getExpectedDResult(), dResult, "checking dResult");
    }

    public void msg(String m) {
        System.out.println();
        System.out.println("###(Target) " + m);
        System.out.println();
    }
}

/////////////////////////////////////////////////////////////////////////////
// Test Cases
/////////////////////////////////////////////////////////////////////////////

//make sure a compiled frame is not deoptimized if an escaping local is accessed
class EAGetWithoutMaterializeTarget extends EATestCaseBaseTarget {

    public PointXY getAway;

    @Override
    public void setUp() {
        super.setUp();
        testFrameShouldBeDeoptimized = false;
    }

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(4, 2);
        getAway = xy;
        dontinline_brkpt();
        iResult = xy.x + xy.y;
    }

    @Override
    public int getExpectedIResult() {
        return 4 + 2;
    }
}

class EAGetWithoutMaterialize extends EATestCaseBaseDebugger {
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        ObjectReference o = getLocalRef(bpe.thread().frame(1), "PointXY", "xy");
        checkField(o, FD.I, "x", 4);
        checkField(o, FD.I, "y", 2);
    }
}

/////////////////////////////////////////////////////////////////////////////

// Tests the following:
//
// 1. Debugger can obtain a reference to a scalar replaced object R from java thread J.
//
// 2. Subsequent modifications of R by J are noticed by the debugger.
//
class EAMaterializeLocalVariableUponGetTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(4, 2);
        dontinline_brkpt();
        xy.x += 1;                // change scalar replaced object after debugger obtained a reference to it
        iResult = xy.x + xy.y;
    }

    @Override
    public int getExpectedIResult() {
        return 4 + 2 + 1;
    }
}

class EAMaterializeLocalVariableUponGet extends EATestCaseBaseDebugger {

    private ObjectReference o;

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        // check 1.
        o = getLocalRef(bpe.thread().frame(1), "PointXY", "xy");
        checkField(o, FD.I, "x", 4);
        checkField(o, FD.I, "y", 2);
    }

    @Override
    public void checkPostConditions() throws Exception {
        super.checkPostConditions();
        // check 2.
        checkField(o, FD.I, "x", 5);
    }
}

/////////////////////////////////////////////////////////////////////////////

class EAMaterializeLocalAtObjectReturnTarget extends EATestCaseBaseTarget {
    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

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

class EAMaterializeLocalAtObjectReturn extends EATestCaseBaseDebugger {
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        ObjectReference o = getLocalRef(bpe.thread().frame(2), "PointXY", "xy");
        checkField(o, FD.I, "x", 4);
        checkField(o, FD.I, "y", 2);
    }
}

/////////////////////////////////////////////////////////////////////////////
// Test case collection that tests rematerialization of different
// array types, where the first element is always not constant and the
// other elements are constants. Not constant values are stored in
// the stack frame for rematerialization whereas constants are kept
// in the debug info of the nmethod.

class EAMaterializeIntArrayTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        int nums[] = {NOT_CONST_1I , 2, 3};
        dontinline_brkpt();
        iResult = nums[0] + nums[1] + nums[2];
    }

    @Override
    public int getExpectedIResult() {
        return NOT_CONST_1I + 2 + 3;
    }
}

class EAMaterializeIntArray extends EATestCaseBaseDebugger {
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        int[] expectedVals = {1, 2, 3};
        checkLocalPrimitiveArray(bpe.thread().frame(1), "nums", FD.I, expectedVals);
    }
}

/////////////////////////////////////////////////////////////////////////////

class EAMaterializeLongArrayTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        long nums[] = {NOT_CONST_1L , 2, 3};
        dontinline_brkpt();
        lResult = nums[0] + nums[1] + nums[2];
    }

    @Override
    public long getExpectedLResult() {
        return NOT_CONST_1L + 2 + 3;
    }
}

class EAMaterializeLongArray extends EATestCaseBaseDebugger {
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        long[] expectedVals = {1, 2, 3};
        checkLocalPrimitiveArray(bpe.thread().frame(1), "nums", FD.J, expectedVals);
    }
}

/////////////////////////////////////////////////////////////////////////////

class EAMaterializeFloatArrayTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        float nums[] = {NOT_CONST_1F , 2.2f, 3.3f};
        dontinline_brkpt();
        fResult = nums[0] + nums[1] + nums[2];
    }

    @Override
    public float getExpectedFResult() {
        return NOT_CONST_1F + 2.2f + 3.3f;
    }
}

class EAMaterializeFloatArray extends EATestCaseBaseDebugger {
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        float[] expectedVals = {1.1f, 2.2f, 3.3f};
        checkLocalPrimitiveArray(bpe.thread().frame(1), "nums", FD.F, expectedVals);
    }
}

/////////////////////////////////////////////////////////////////////////////

class EAMaterializeDoubleArrayTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        double nums[] = {NOT_CONST_1D , 2.2d, 3.3d};
        dontinline_brkpt();
        dResult = nums[0] + nums[1] + nums[2];
    }

    @Override
    public double getExpectedDResult() {
        return NOT_CONST_1D + 2.2d + 3.3d;
    }
}

class EAMaterializeDoubleArray extends EATestCaseBaseDebugger {
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        double[] expectedVals = {1.1d, 2.2d, 3.3d};
        checkLocalPrimitiveArray(bpe.thread().frame(1), "nums", FD.D, expectedVals);
    }
}

/////////////////////////////////////////////////////////////////////////////

class EAMaterializeObjectArrayTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        Long nums[] = {NOT_CONST_1_OBJ , CONST_2_OBJ, CONST_3_OBJ};
        dontinline_brkpt();
        lResult = nums[0] + nums[1] + nums[2];
    }

    @Override
    public long getExpectedLResult() {
        return 1 + 2 + 3;
    }
}

class EAMaterializeObjectArray extends EATestCaseBaseDebugger {
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        ReferenceType clazz = bpe.thread().frame(0).location().declaringType();
        ObjectReference[] expectedVals = {
                (ObjectReference) clazz.getValue(clazz.fieldByName("NOT_CONST_1_OBJ")),
                (ObjectReference) clazz.getValue(clazz.fieldByName("CONST_2_OBJ")),
                (ObjectReference) clazz.getValue(clazz.fieldByName("CONST_3_OBJ"))
        };
        checkLocalObjectArray(bpe.thread().frame(1), EATestCaseBaseTarget.TESTMETHOD_NAME, "nums", "java.lang.Long[]", expectedVals);
    }
}

/////////////////////////////////////////////////////////////////////////////

// Materialize an object whose fields have constant and not constant values at
// the point where the object is materialize.
class EAMaterializeObjectWithConstantAndNotConstantValuesTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        ILFDO o = new ILFDO(NOT_CONST_1I, 2,
                            NOT_CONST_1L, 2L,
                            NOT_CONST_1F, 2.1F,
                            NOT_CONST_1D, 2.1D,
                            NOT_CONST_1_OBJ, CONST_2_OBJ
                            );
        dontinline_brkpt();
        dResult =
            o.i + o.i2 + o.l + o.l2 + o.f + o.f2 + o.d + o.d2 + o.o + o.o2;
    }

    @Override
    public double getExpectedDResult() {
        return NOT_CONST_1I + 2 + NOT_CONST_1L + 2L + NOT_CONST_1F + 2.1F + NOT_CONST_1D + 2.1D + NOT_CONST_1_OBJ + CONST_2_OBJ;
    }
}

class EAMaterializeObjectWithConstantAndNotConstantValues extends EATestCaseBaseDebugger {
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        ObjectReference o = getLocalRef(bpe.thread().frame(1), "ILFDO", "o");
        checkField(o, FD.I, "i", 1);
        checkField(o, FD.I, "i2", 2);
        checkField(o, FD.J, "l", 1L);
        checkField(o, FD.J, "l2", 2L);
        checkField(o, FD.F, "f", 1.1f);
        checkField(o, FD.F, "f2", 2.1f);
        checkField(o, FD.D, "d", 1.1d);
        checkField(o, FD.D, "d2", 2.1d);
        ReferenceType clazz = bpe.thread().frame(1).location().declaringType();
        ObjectReference[] expVals = {
                (ObjectReference) clazz.getValue(clazz.fieldByName("NOT_CONST_1_OBJ")),
                (ObjectReference) clazz.getValue(clazz.fieldByName("CONST_2_OBJ")),
        };
        checkObjField(o, "java.lang.Long[]", "o", expVals[0]);
        checkObjField(o, "java.lang.Long[]", "o2", expVals[1]);
    }
}

/////////////////////////////////////////////////////////////////////////////

// Two local variables reference the same object.
// Check if the debugger obtains the same object when reading the two variables
class EAMaterializeObjReferencedBy2LocalsTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(2, 3);
        PointXY alias = xy;
        dontinline_brkpt();
        iResult = xy.x + alias.x;
    }

    @Override
    public int getExpectedIResult() {
        return 2 + 2;
    }
}

class EAMaterializeObjReferencedBy2Locals extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        // check 1.
        ObjectReference xy = getLocalRef(bpe.thread().frame(1), "PointXY", "xy");
        ObjectReference alias = getLocalRef(bpe.thread().frame(1), "PointXY", "alias");
        Asserts.assertSame(xy, alias, "xy and alias are expected to reference the same object");
    }

}

/////////////////////////////////////////////////////////////////////////////

// Two local variables reference the same object.
// Check if it has the expected effect in the target, if the debugger modifies the object.
class EAMaterializeObjReferencedBy2LocalsAndModifyTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(2, 3);
        PointXY alias = xy;
        dontinline_brkpt(); // debugger: alias.x = 42
        iResult = xy.x + alias.x;
    }

    @Override
    public int getExpectedIResult() {
        return 42 + 42;
    }
}

class EAMaterializeObjReferencedBy2LocalsAndModify extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe);
        ObjectReference alias = getLocalRef(bpe.thread().frame(1), "PointXY", "alias");
        setField(alias, FD.I, "x", env.vm().mirrorOf(42));
    }
}

// End of test case collection
/////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////
// Helper classes
class PointXY {

    public int x;
    public int y;

    public PointXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class ILFDO {

    public int i;
    public int i2;
    public long l;
    public long l2;
    public float f;
    public float f2;
    public double d;
    public double d2;
    public Long o;
    public Long o2;

    public ILFDO(int i,
                 int i2,
                 long l,
                 long l2,
                 float f,
                 float f2,
                 double d,
                 double d2,
                 Long o,
                 Long o2) {
        this.i = i;
        this.i2 = i2;
        this.l = l;
        this.l2 = l2;
        this.f = f;
        this.f2 = f2;
        this.d = d;
        this.d2 = d2;
        this.o = o;
        this.o2 = o2;
    }

}
