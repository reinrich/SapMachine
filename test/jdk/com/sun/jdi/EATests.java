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
 * @requires vm.compiler2.enabled
 * @library /test/lib /test/hotspot/jtreg
 *
 * @run build TestScaffold VMConnection TargetListener TargetAdapter sun.hotspot.WhiteBox
 * @run driver ClassFileInstaller sun.hotspot.WhiteBox
 *                                sun.hotspot.WhiteBox$WhiteBoxPermission
 * @run compile -g EATests.java
 * @run driver EATests
 *                 -XX:+UnlockDiagnosticVMOptions
 *                 -Xms32m -Xmx32m
 *                 -Xbootclasspath/a:.
 *                 -XX:CompileCommand=dontinline,*::dontinline_*
 *                 -XX:+WhiteBoxAPI
 *                 -XX:+TraceDeoptimization
 *                 -XX:+PrintCompilation
 *                 -XX:+PrintInlining
 *                 -XX:-TieredCompilation
 *                 -Xbatch
 *                 -XX:CICompilerCount=1
 *                 -XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 * @run driver EATests
 *                 -XX:+UnlockDiagnosticVMOptions
 *                 -Xms32m -Xmx32m
 *                 -Xbootclasspath/a:.
 *                 -XX:CompileCommand=dontinline,*::dontinline_*
 *                 -XX:+WhiteBoxAPI
 *                 -XX:+TraceDeoptimization
 *                 -XX:+PrintCompilation
 *                 -XX:+PrintInlining
 *                 -XX:-TieredCompilation
 *                 -Xbatch
 *                 -XX:CICompilerCount=1
 *                 -XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:-EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking -XX:-UseOptoBiasInlining
 * @run driver EATests
 *                 -XX:+UnlockDiagnosticVMOptions
 *                 -Xms32m -Xmx32m
 *                 -Xbootclasspath/a:.
 *                 -XX:CompileCommand=dontinline,*::dontinline_*
 *                 -XX:+WhiteBoxAPI
 *                 -XX:+TraceDeoptimization
 *                 -XX:+PrintCompilation
 *                 -XX:+PrintInlining
 *                 -XX:-TieredCompilation
 *                 -Xbatch
 *                 -XX:CICompilerCount=1
 *                 -XX:+DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 * @run driver EATests
 *                 -XX:+UnlockDiagnosticVMOptions
 *                 -Xms32m -Xmx32m
 *                 -Xbootclasspath/a:.
 *                 -XX:CompileCommand=dontinline,*::dontinline_*
 *                 -XX:+WhiteBoxAPI
 *                 -XX:+TraceDeoptimization
 *                 -XX:+PrintCompilation
 *                 -XX:+PrintInlining
 *                 -XX:-TieredCompilation
 *                 -Xbatch
 *                 -XX:CICompilerCount=1
 *                 -XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:+UseBiasedLocking
 * @run driver EATests
 *                 -XX:+UnlockDiagnosticVMOptions
 *                 -Xms32m -Xmx32m
 *                 -Xbootclasspath/a:.
 *                 -XX:CompileCommand=dontinline,*::dontinline_*
 *                 -XX:+WhiteBoxAPI
 *                 -XX:+TraceDeoptimization
 *                 -XX:+PrintCompilation
 *                 -XX:+PrintInlining
 *                 -XX:-TieredCompilation
 *                 -Xbatch
 *                 -XX:CICompilerCount=1
 *                 -XX:+DoEscapeAnalysis -XX:+EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:-UseBiasedLocking
 * @run driver EATests
 *                 -XX:+UnlockDiagnosticVMOptions
 *                 -Xms32m -Xmx32m
 *                 -Xbootclasspath/a:.
 *                 -XX:CompileCommand=dontinline,*::dontinline_*
 *                 -XX:+WhiteBoxAPI
 *                 -XX:+TraceDeoptimization
 *                 -XX:+PrintCompilation
 *                 -XX:+PrintInlining
 *                 -XX:-TieredCompilation
 *                 -Xbatch
 *                 -XX:CICompilerCount=1
 *                 -XX:+DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:-UseBiasedLocking
 * @run driver EATests
 *                 -XX:+UnlockDiagnosticVMOptions
 *                 -Xms32m -Xmx32m
 *                 -Xbootclasspath/a:.
 *                 -XX:CompileCommand=dontinline,*::dontinline_*
 *                 -XX:+WhiteBoxAPI
 *                 -XX:+TraceDeoptimization
 *                 -XX:+PrintCompilation
 *                 -XX:+PrintInlining
 *                 -XX:-TieredCompilation
 *                 -Xbatch
 *                 -XX:CICompilerCount=1
 *                 -XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:+EliminateLocks -XX:+EliminateNestedLocks -XX:-UseBiasedLocking
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
        -XX:CompileCommand=dontinline,*::dontinline_*
        -XX:CICompilerCount=1
    )
    print_and_run "${CMD[@]}"
*/

// TODO: remove trace options like '-XX:+PrintCompilation -XX:+PrintInlining' to avoid deadlock as in https://bugs.openjdk.java.net/browse/JDK-8213902

/////////////////////////////////////////////////////////////////////////////
//
// Shared base class for test cases for both, debugger and debuggee.
//
/////////////////////////////////////////////////////////////////////////////

class EATestCaseBaseShared {
    public static final boolean TODO_INTERACTIVE = false;
    
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
        return EATestCaseBaseShared.RUN_ONLY_TEST_CASE != null &&
               EATestCaseBaseShared.RUN_ONLY_TEST_CASE.length() > 0 &&
               !testCaseName.equals(EATestCaseBaseShared.RUN_ONLY_TEST_CASE);
    }
}

/////////////////////////////////////////////////////////////////////////////
//
// Target main class, i.e. the program to be debugged.
//
/////////////////////////////////////////////////////////////////////////////

class EATestsTarget {

    public static void main(String[] args) {
        EATestCaseBaseTarget.staticSetUp();
        EATestCaseBaseTarget.staticSetUpDone();

        // Materializing test cases
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
        new EAMaterializeObjReferencedBy2LocalsInDifferentVirtFramesTarget().run();
        new EAMaterializeObjReferencedBy2LocalsInDifferentVirtFramesAndModifyTarget().run();
        new EAMaterializeObjReferencedFromOperandStackTarget().run();
        new EAMaterializeLocalVariableUponGetAfterSetIntegerTarget().run();

        // Relocking test cases
        new EARelockingSimpleTarget()                .run();
        new EARelockingRecursiveTarget()             .run();
        new EARelockingNestedInflatedTarget()        .run();
        new EARelockingNestedInflated_02Target()     .run();
        new EARelockingArgEscapeLWLockedInCalleeFrameTarget().run();
        new EAGetOwnedMonitorsTarget()               .run();
        new EAEntryCountTarget()                     .run();

        // Test cases that require deoptimization even though neither
        // locks nor allocations are eliminated at the point where
        // escape state is changed.
        new EADeoptFrameAfterReadLocalObject_01Target().run();
        new EADeoptFrameAfterReadLocalObject_01BTarget().run();
        new EADeoptFrameAfterReadLocalObject_02Target().run();
        new EADeoptFrameAfterReadLocalObject_02CTarget().run();
        new EADeoptFrameAfterReadLocalObject_02DTarget().run();
        new EADeoptFrameAfterReadLocalObject_03Target().run();

        // PopFrame test cases
        new EAPopFrameNotInlinedTarget().run();
        new EAPopFrameNotInlinedReallocFailureTarget().run();
        new EAPopInlinedMethodWithScalarReplacedObjectsReallocFailureTarget().run();

        // ForceEarlyReturn test cases
        new EAForceEarlyReturnNotInlinedTarget().run();
        new EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjectsTarget().run();
        new EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjectsReallocFailureTarget().run();

    }
}

/////////////////////////////////////////////////////////////////////////////
//
// Debugger main class
//
/////////////////////////////////////////////////////////////////////////////

public class EATests extends TestScaffold {

    public TargetVMOptions targetVMOptions;
    public ThreadReference targetMainThread;

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

    public static class TargetVMOptions {

        public boolean EliminateAllocations;

        public static TargetVMOptions get(EATests env, ReferenceType type) {
            TargetVMOptions result = new TargetVMOptions();
            ClassType testCaseBaseTargetClass = (ClassType) type;
            Value val;

            val = testCaseBaseTargetClass.getValue(testCaseBaseTargetClass.fieldByName("EliminateAllocations"));
            result.EliminateAllocations = ((PrimitiveValue) val).booleanValue();

            return result;
        }

    }

    // Execute known test cases
    protected void runTests() throws Exception {
        String targetProgName = EATestsTarget.class.getName();
        msg("starting to main method in class " +  targetProgName);
        startToMain(targetProgName);
        msg("resuming to EATestCaseBaseTarget.staticSetUpDone()V");
        targetMainThread = resumeTo("EATestCaseBaseTarget", "staticSetUpDone", "()V").thread();
        Location loc = targetMainThread.frame(0).location();
        Asserts.assertEQ("staticSetUpDone", loc.method().name());

        targetVMOptions = TargetVMOptions.get(this, loc.declaringType());

        // Materializing test cases
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
        new EAMaterializeObjReferencedBy2LocalsInDifferentVirtFrames().setScaffold(this).run();
        new EAMaterializeObjReferencedBy2LocalsInDifferentVirtFramesAndModify().setScaffold(this).run();
        new EAMaterializeObjReferencedFromOperandStack().setScaffold(this).run();
        new EAMaterializeLocalVariableUponGetAfterSetInteger().setScaffold(this).run();

        // Relocking test cases
        new EARelockingSimple()                .setScaffold(this).run();
        new EARelockingRecursive()             .setScaffold(this).run();
        new EARelockingNestedInflated()        .setScaffold(this).run();
        new EARelockingNestedInflated_02()     .setScaffold(this).run();
        new EARelockingArgEscapeLWLockedInCalleeFrame().setScaffold(this).run();
        new EAGetOwnedMonitors()               .setScaffold(this).run();
        new EAEntryCount()                     .setScaffold(this).run();

        // Test cases that require deoptimization even though neither
        // locks nor allocations are eliminated at the point where
        // escape state is changed.
        new EADeoptFrameAfterReadLocalObject_01().setScaffold(this).run();
        new EADeoptFrameAfterReadLocalObject_01B().setScaffold(this).run();
        new EADeoptFrameAfterReadLocalObject_02().setScaffold(this).run();
        new EADeoptFrameAfterReadLocalObject_02C().setScaffold(this).run();
        new EADeoptFrameAfterReadLocalObject_02D().setScaffold(this).run();
        new EADeoptFrameAfterReadLocalObject_03().setScaffold(this).run();

        // PopFrame test cases
        new EAPopFrameNotInlined().setScaffold(this).run();
        new EAPopFrameNotInlinedReallocFailure().setScaffold(this).run();
        new EAPopInlinedMethodWithScalarReplacedObjectsReallocFailure().setScaffold(this).run();

        // ForceEarlyReturn test cases
        new EAForceEarlyReturnNotInlined().setScaffold(this).run();
        new EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjects().setScaffold(this).run();
        new EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjectsReallocFailure().setScaffold(this).run();

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
//
// Base class for debugger side of test cases.
//
/////////////////////////////////////////////////////////////////////////////

abstract class EATestCaseBaseDebugger  extends EATestCaseBaseShared implements Runnable {

    protected EATests env;

    public ObjectReference testCase;

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

            if (TODO_INTERACTIVE)
                env.waitForInput();

            resumeToWarmupDone();
            runTestCase();
            resumeToTestCaseDone();
            checkPostConditions();
        } catch (Exception e) {
            Asserts.fail("Unexpected exception in test case " + getClass().getName(), e);
        }
    }

    public void resumeToWarmupDone() throws Exception {
        msg("resuming to " + getTargetTestCaseBaseName() + ".warmupDone()V");
        env.resumeTo(getTargetTestCaseBaseName(), "warmupDone", "()V");
        testCase = env.targetMainThread.frame(0).thisObject();
    }

    public void resumeToTestCaseDone() {
        msg("resuming to " + getTargetTestCaseBaseName() + ".testCaseDone()V");
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

    public void printStack(ThreadReference thread) throws Exception {
        msg("Debuggee Stack:");
        List<StackFrame> stack_frames = thread.frames();
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
    public static final Map<FD, String> FD2JDIArrType = Map.of(FD.I, "int[]", FD.J, "long[]", FD.F, "float[]", FD.D, "double[]");

    // Map field descriptor to PrimitiveValue getter
    public static final Function<PrimitiveValue, Integer> v2I = PrimitiveValue::intValue;
    public static final Function<PrimitiveValue, Long>    v2J = PrimitiveValue::longValue;
    public static final Function<PrimitiveValue, Float>   v2F = PrimitiveValue::floatValue;
    public static final Function<PrimitiveValue, Double>  v2D = PrimitiveValue::doubleValue;
    Map<FD, Function<PrimitiveValue, ?>> FD2getter = Map.of(FD.I, v2I, FD.J, v2J, FD.F, v2F, FD.D, v2D);

    protected void checkLocalPrimitiveArray(StackFrame frame, String lName, FD desc, Object expVals) throws Exception {
        String lType = FD2JDIArrType.get(desc);
        Asserts.assertNotNull(lType, "jdi type not found");
        Asserts.assertEQ(EATestCaseBaseTarget.TESTMETHOD_DEFAULT_NAME, frame .location().method().name());
        List<LocalVariable> localVars = frame.visibleVariables();
        msg("Check if the local array variable '" + lName  + "' in " + EATestCaseBaseTarget.TESTMETHOD_DEFAULT_NAME + " has the expected elements: ");
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
        Asserts.assertEQ(EATestCaseBaseTarget.TESTMETHOD_DEFAULT_NAME, frame .location().method().name());
        List<LocalVariable> localVars = frame.visibleVariables();
        msg("Check if the local array variable '" + lName  + "' in " + EATestCaseBaseTarget.TESTMETHOD_DEFAULT_NAME + " has the expected elements: ");
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
        return getLocalRef(frame, EATestCaseBaseTarget.TESTMETHOD_DEFAULT_NAME, lType, lName);
    }

    protected ObjectReference getLocalRef(StackFrame frame, String expectedMethodName, String lType, String lName) throws Exception {
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
                break;
            }
        }
        Asserts.assertNotNull(lRef, "Local variable '" + lName + "' not found");
        msg("OK.");
        return lRef;
    }

    public void setLocal(StackFrame frame, String lName, Value val) throws Exception {
        setLocal(frame, EATestCaseBaseTarget.TESTMETHOD_DEFAULT_NAME, lName, val);
    }

    public void setLocal(StackFrame frame, String expectedMethodName, String lName, Value val) throws Exception {
        Asserts.assertEQ(expectedMethodName, frame.location().method().name());
        List<LocalVariable> localVars = frame.visibleVariables();
        msg("Set local variable '" + lName + "' = " + val + " in " + expectedMethodName);
        for (LocalVariable lv : localVars) {
            if (lv.name().equals(lName)) {
                frame.setValue(lv, val);
                break;
            }
        }
        msg("OK.");
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

    protected void setField(ObjectReference o, String fName, Value val) throws Exception {
        msg("set field " + fName + " = " + val);
        ReferenceType rt = o.referenceType();
        Field fld = rt.fieldByName(fName);
        o.setValue(fld, val);
        msg("ok");
    }

    protected Value getField(ObjectReference o, String fName) throws Exception {
        msg("get field " + fName);
        ReferenceType rt = o.referenceType();
        Field fld = rt.fieldByName(fName);
        Value val = o.getValue(fld);
        msg("result : " + val);
        return val;
    }

    /**
     * Free the memory consumed in the target by {@link EATestCaseBaseTarget#consumedMemory}
     * @throws Exception
     */
    public void freeAllMemory() throws Exception {
        msg("free consumed memory");
        setField(testCase, "consumedMemory", null);
    }
    

    /**
     * @return {@link EATestCaseBaseTarget#targetIsInLoop}. The target must set that field to true as soon as it
     *         enters the endless loop.
     * @throws Exception
     */
    public boolean targetHasEnteredEndlessLoop() throws Exception {
        Value v = getField(testCase, "targetIsInLoop");
        return ((PrimitiveValue) v).booleanValue();
    }

    
    /**
     * Poll {@link EATestCaseBaseTarget#targetIsInLoop} and return if it is found to be true.
     * @throws Exception
     */
    public void waitUntilTargetHasEnteredEndlessLoop() throws Exception {
        while(!targetHasEnteredEndlessLoop()) {
            msg("Target has not yet entered the loop. Sleep 200ms.");
            try { Thread.sleep(200); } catch (InterruptedException e) { /*ignore */ }
        }
    }

    /**
     * Set {@link EATestCaseBaseTarget#loopCount} to 0. This will allow the target to
     * leave the endless loop.
     * @throws Exception
     */
    public void terminateEndlessLoop() throws Exception {
        msg("terminate loop");
        setField(testCase, "doLoop", env.vm().mirrorOf(false));
    }
}

/////////////////////////////////////////////////////////////////////////////
//
// Base class for debuggee side of test cases.
//
/////////////////////////////////////////////////////////////////////////////

abstract class EATestCaseBaseTarget extends EATestCaseBaseShared implements Runnable {

    /**
     * The target must set that field to true as soon as it enters the endless loop.
     */
    public volatile boolean targetIsInLoop;

    /**
     * The number of loop iterations to be performed in a testcase' main test method, i.e. in its
     * version of {@link EATestCaseBaseTarget#dontinline_testMethod()}.
     * To get an endless loop you must set it to something like 1L<<62 in {@link EATestCaseBaseTarget#warmupDone()} 
     */
    public volatile long loopCount;

    /**
     * Used in {@link EATestCaseBaseDebugger#terminateEndlessLoop()} to signal target to leave the endless loop.
     */
    public volatile boolean doLoop;

    public long checkSum;

    public static final String TESTMETHOD_DEFAULT_NAME = "dontinline_testMethod";

    public static final int COMPILE_THRESHOLD = 20000;

    public static final WhiteBox WB = WhiteBox.getWhiteBox();

    // VM flags
    public static final boolean DoEscapeAnalysis     = WB.getBooleanVMFlag("DoEscapeAnalysis");
    public static final boolean EliminateAllocations = DoEscapeAnalysis && WB.getBooleanVMFlag("EliminateAllocations");
    public static final boolean EliminateLocks       = WB.getBooleanVMFlag("EliminateLocks");
    public static final boolean EliminateNestedLocks = WB.getBooleanVMFlag("EliminateNestedLocks");
    public static final boolean UseBiasedLocking     = WB.getBooleanVMFlag("UseBiasedLocking");

    public String testMethodName;
    public int testMethodDepth;

    public int  iResult;
    public long lResult;
    public float  fResult;
    public double dResult;


    public boolean warmupDone;


    // an object with an inflated monitor
    public static PointXY inflatedLock;
    public static Thread  inflatorThread;
    public static boolean inflatedLockIsPermanentlyInflated;

    public static int    NOT_CONST_1I = 1;
    public static long   NOT_CONST_1L = 1L;
    public static float  NOT_CONST_1F = 1.1F;
    public static double NOT_CONST_1D = 1.1D;

    public static          Long NOT_CONST_1_OBJ = Long.valueOf(1);


    public static final    Long CONST_2_OBJ     = Long.valueOf(2);
    public static final    Long CONST_3_OBJ     = Long.valueOf(3);

    public void run() {
        if (shouldSkip()) {
            msg("skipping " + testCaseName);
            return;
        }
        setUp();
        msg(testCaseName + " is up and running.");
        compileTestMethod();
        msg(testCaseName + " warmup done.");
        warmupDone();
        checkCompLevel();
        dontinline_testMethod();
        checkResult();
        msg(testCaseName + " done.");
        testCaseDone();
    }

    public static void staticSetUp() {
        inflatedLock = new PointXY(1, 1);
        synchronized (inflatedLock) {
            inflatorThread = new Thread("Lock Inflator (test thread)") {
                @Override
                public void run() {
                    synchronized (inflatedLock) {
                        inflatedLockIsPermanentlyInflated = true;
                        inflatedLock.notify(); // main thread
                        while (true) {
                            try {
                                // calling wait() on a monitor will cause inflation into a heavy monitor
                                inflatedLock.wait();
                            } catch (InterruptedException e) { /* ignored */ }
                        }
                    }
                }
            };
            inflatorThread.setDaemon(true);
            inflatorThread.start();

            // wait until the lock is permanently inflated by the inflatorThread
            while(!inflatedLockIsPermanentlyInflated) {
                try {
                    inflatedLock.wait(); // until inflated
                } catch (InterruptedException e1) { /* ignored */ }
            }
        }
    }

    public static void staticSetUpDone() {
        // used to sync with debugger
    }

    public void setUp() {
        testMethodDepth = 1;
        testMethodName = TESTMETHOD_DEFAULT_NAME;
    }

    public abstract void dontinline_testMethod();

    public int dontinline_brkpt_iret() {
        dontinline_brkpt();
        return 42;
    }

    public void dontinline_brkpt() {
        // will set breakpoint here after warmup
        if (warmupDone) {
            // check if test method is at expected depth
            StackTraceElement[] frames = Thread.currentThread().getStackTrace();
            int stackTraceDepth = testMethodDepth + 1; // ignore java.lang.Thread.getStackTrace()
            Asserts.assertEQ(testMethodName, frames[stackTraceDepth].getMethodName(),
                    testCaseName + ": test method not found at depth " + testMethodDepth);
            // check if the frame is (not) deoptimized as expected
            if (testFrameShouldBeDeoptimized()) {
                Asserts.assertTrue(WB.isFrameDeoptimized(testMethodDepth+1),
                        testCaseName + ": expected test method frame at depth " + testMethodDepth + " to be deoptimized");
            } else {
                Asserts.assertFalse(WB.isFrameDeoptimized(testMethodDepth+1),
                        testCaseName + ": expected test method frame at depth " + testMethodDepth + " not to be deoptimized");
            }
        }
    }

    public long dontline_endlessLoop() {
        long cs = checkSum;
        doLoop = true;
        while (loopCount-- > 0 && doLoop) {
            targetIsInLoop = true;
            checkSum += checkSum % ++cs;
        }
        loopCount = 3;
        targetIsInLoop = false;
        return checkSum;
    }

    public boolean testFrameShouldBeDeoptimized() {
        return DoEscapeAnalysis;
    }

    public void warmupDone() {
        warmupDone = true;
    }

    public void testCaseDone() {
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
            m = getClass().getMethod(TESTMETHOD_DEFAULT_NAME);
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

    // The object passed will be ArgEscape if it was NoEscape before.
    public final void dontinline_make_arg_escape(PointXY xy) {
    }

    public final void dontinline_call_with_entry_frame(Object receiver, String methodName) {
        Asserts.assertTrue(warmupDone, "We want to take the slow path through jni, so don't call in warmup");

        Class<?> cls = receiver.getClass();
        Class<?>[] none = {};

        java.lang.reflect.Method m;
        try {
            m = cls.getDeclaredMethod(methodName, none);
            m.invoke(receiver);
        } catch (Exception e) {
            Asserts.fail("Call through reflection failed", e);
        }
    }

    static class LinkedList {
        LinkedList l;
        public long[] array;
        public LinkedList(LinkedList l, int size) {
            this.array = new long[size];
            this.l = l;
        }
    }

    public LinkedList consumedMemory;

    public void consumeAllMemory() {
        msg("consume all memory");
        int size = 128 * 1024 * 1024;
        while(size > 0) {
            try {
                while(true) {
                    consumedMemory = new LinkedList(consumedMemory, size);
                }
            } catch(OutOfMemoryError oom) {
            }
            size = size / 2;
        }
    }
}

/////////////////////////////////////////////////////////////////////////////
//
// Test Cases
//
/////////////////////////////////////////////////////////////////////////////

// make sure a compiled frame is not deoptimized if an escaping local is accessed
class EAGetWithoutMaterializeTarget extends EATestCaseBaseTarget {

    public PointXY getAway;

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

    @Override
    public boolean testFrameShouldBeDeoptimized() {
        return false;
    }
}

class EAGetWithoutMaterialize extends EATestCaseBaseDebugger {
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
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
        printStack(bpe.thread());
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
        printStack(bpe.thread());
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
        printStack(bpe.thread());
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
        printStack(bpe.thread());
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
        printStack(bpe.thread());
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
        printStack(bpe.thread());
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
        printStack(bpe.thread());
        ReferenceType clazz = bpe.thread().frame(0).location().declaringType();
        ObjectReference[] expectedVals = {
                (ObjectReference) clazz.getValue(clazz.fieldByName("NOT_CONST_1_OBJ")),
                (ObjectReference) clazz.getValue(clazz.fieldByName("CONST_2_OBJ")),
                (ObjectReference) clazz.getValue(clazz.fieldByName("CONST_3_OBJ"))
        };
        checkLocalObjectArray(bpe.thread().frame(1), EATestCaseBaseTarget.TESTMETHOD_DEFAULT_NAME, "nums", "java.lang.Long[]", expectedVals);
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
        printStack(bpe.thread());
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
        printStack(bpe.thread());
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
        printStack(bpe.thread());
        ObjectReference alias = getLocalRef(bpe.thread().frame(1), "PointXY", "alias");
        setField(alias, "x", env.vm().mirrorOf(42));
    }
}

/////////////////////////////////////////////////////////////////////////////

// Two local variables of the same compiled frame but in different virtual frames reference the same
// object.
// Check if the debugger obtains the same object when reading the two variables
class EAMaterializeObjReferencedBy2LocalsInDifferentVirtFramesTarget extends EATestCaseBaseTarget {

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(2, 3);
        testMethod_inlined(xy);
        iResult += xy.x;
    }

    public void testMethod_inlined(PointXY xy) {
        PointXY alias = xy;
        dontinline_brkpt();
        iResult = alias.x;
    }

    @Override
    public int getExpectedIResult() {
        return 2 + 2;
    }
}

class EAMaterializeObjReferencedBy2LocalsInDifferentVirtFrames extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        ObjectReference xy = getLocalRef(bpe.thread().frame(2), "PointXY", "xy");
        ObjectReference alias = getLocalRef(bpe.thread().frame(1), "testMethod_inlined", "PointXY", "alias");
        Asserts.assertSame(xy, alias, "xy and alias are expected to reference the same object");
    }

}

/////////////////////////////////////////////////////////////////////////////

// Two local variables of the same compiled frame but in different virtual frames reference the same
// object.
// Check if it has the expected effect in the target, if the debugger modifies the object.
class EAMaterializeObjReferencedBy2LocalsInDifferentVirtFramesAndModifyTarget extends EATestCaseBaseTarget {

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(2, 3);
        testMethod_inlined(xy);   // debugger: xy.x = 42
        iResult += xy.x;
    }

    public void testMethod_inlined(PointXY xy) {
        PointXY alias = xy;
        dontinline_brkpt();
        iResult = alias.x;
    }

    @Override
    public int getExpectedIResult() {
        return 42 + 42;
    }
}

class EAMaterializeObjReferencedBy2LocalsInDifferentVirtFramesAndModify extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        ObjectReference alias = getLocalRef(bpe.thread().frame(1), "testMethod_inlined", "PointXY", "alias");
        setField(alias, "x", env.vm().mirrorOf(42));
    }

}

/////////////////////////////////////////////////////////////////////////////

// Test materialization of an object referenced only from expression stack
class EAMaterializeObjReferencedFromOperandStackTarget extends EATestCaseBaseTarget {

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

    public void dontinline_testMethod() {
        @SuppressWarnings("unused")
        PointXY xy1 = new PointXY(2, 3);
        // Debugger breaks in call to dontinline_brkpt_ret_100() and reads
        // the value of the local 'xy1'. This triggers materialization
        // of the object on the operand stack
        iResult = testMethodInlined(new PointXY(4, 2), dontinline_brkpt_ret_100());
    }

    public int testMethodInlined(PointXY xy2, int dontinline_brkpt_ret_100) {
        return xy2.x + dontinline_brkpt_ret_100;
    }

    public int dontinline_brkpt_ret_100() {
        dontinline_brkpt();
        return 100;
    }

    @Override
    public int getExpectedIResult() {
        return 4 + 100;
    }
}

class EAMaterializeObjReferencedFromOperandStack extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        ObjectReference xy1 = getLocalRef(bpe.thread().frame(2), "PointXY", "xy1");
        checkField(xy1, FD.I, "x", 2);
        checkField(xy1, FD.I, "y", 3);
    }

}

/////////////////////////////////////////////////////////////////////////////

/**
 * Tests a regression in the implementation by setting the value of a local int which triggers the
 * creation of a deferred update and then getting the reference to a scalar replaced object.  The
 * issue was that the scalar replaced object was not reallocated. Because of the deferred update it
 * was assumed that the reallocation already happened.
 */
class EAMaterializeLocalVariableUponGetAfterSetInteger extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        setLocal(bpe.thread().frame(1), "i", env.vm().mirrorOf(43));
        ObjectReference o = getLocalRef(bpe.thread().frame(1), "PointXY", "xy");
        checkField(o, FD.I, "x", 4);
        checkField(o, FD.I, "y", 2);
    }
}

class EAMaterializeLocalVariableUponGetAfterSetIntegerTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(4, 2);
        int i = 42;
        dontinline_brkpt();
        iResult = xy.x + xy.y + i;
    }

    @Override
    public int getExpectedIResult() {
        return 4 + 2 + 43;
    }

    @Override
    public boolean testFrameShouldBeDeoptimized() {
        return true; // setting local variable i triggers always deoptimization
    }
}

/////////////////////////////////////////////////////////////////////////////
//
// Locking Tests 
//
/////////////////////////////////////////////////////////////////////////////

class EARelockingSimpleTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY l1 = new PointXY(4, 2);
        synchronized (l1) {
            dontinline_brkpt();
        }
    }
}

class EARelockingSimple extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        @SuppressWarnings("unused")
        ObjectReference o = getLocalRef(bpe.thread().frame(1), "PointXY", "l1");
    }
}

/////////////////////////////////////////////////////////////////////////////

// Test recursive locking
class EARelockingRecursiveTarget extends EATestCaseBaseTarget {

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

    public void dontinline_testMethod() {
        PointXY l1 = new PointXY(4, 2);
        synchronized (l1) {
            testMethod_inlined(l1);
        }
    }

    public void testMethod_inlined(PointXY l2) {
        synchronized (l2) {
            dontinline_brkpt();
        }
    }
}

class EARelockingRecursive extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        @SuppressWarnings("unused")
        ObjectReference o = getLocalRef(bpe.thread().frame(2), "PointXY", "l1");
    }
}

/////////////////////////////////////////////////////////////////////////////

//// Checks if an eliminated nested lock of an inflated monitor can be relocked.
class EARelockingNestedInflatedTarget extends EATestCaseBaseTarget {

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

// TODO: is it really necessary to deoptimize here?
    @Override
    public boolean testFrameShouldBeDeoptimized() {
//        return DoEscapeAnalysis && EliminateLocks;
        return false;
    }

    public void dontinline_testMethod() {
        PointXY l1 = inflatedLock;
        synchronized (l1) {
            testMethod_inlined(l1);
        }
    }

    public void testMethod_inlined(PointXY l2) {
        synchronized (l2) {                 // eliminated nested locking
            dontinline_brkpt();
        }
    }
}

class EARelockingNestedInflated extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        @SuppressWarnings("unused")
        ObjectReference o = getLocalRef(bpe.thread().frame(2), "PointXY", "l1");
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * Like {@link EARelockingNestedInflated} with the difference that there is
 * a scalar replaced object in the scope from which the object with eliminated nested locking
 * is read. This triggers materialization and relocking.
 */
// TODO: remove the test, because it merely tests a property of the implementation.
class EARelockingNestedInflated_02 extends EATestCaseBaseDebugger {
    
    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        @SuppressWarnings("unused")
        ObjectReference o = getLocalRef(bpe.thread().frame(2), "PointXY", "l1");
    }
}

class EARelockingNestedInflated_02Target extends EATestCaseBaseTarget {

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

    public void dontinline_testMethod() {
        @SuppressWarnings("unused")
        PointXY xy = new PointXY(1, 1);     // scalar replaced
        PointXY l1 = inflatedLock;          // read by debugger
        synchronized (l1) {
            testMethod_inlined(l1);
        }
    }

    public void testMethod_inlined(PointXY l2) {
        synchronized (l2) {                 // eliminated nested locking
            dontinline_brkpt();
        }
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * Checks if an eliminated lock of an ArgEscape object l1 can be relocked if
 * l1 is locked in a callee frame.
 */
class EARelockingArgEscapeLWLockedInCalleeFrame extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        @SuppressWarnings("unused")
        ObjectReference o = getLocalRef(bpe.thread().frame(2), "PointXY", "l1");
    }
}

class EARelockingArgEscapeLWLockedInCalleeFrameTarget extends EATestCaseBaseTarget {

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

    public void dontinline_testMethod() {
        PointXY l1 = new PointXY(1, 1);       // ArgEscape
        synchronized (l1) {                   // eliminated
            l1.dontinline_sync_method(this);  // l1 escapes
        }
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * Let xy be NoEscape whose allocation cannot be eliminated (simulated by
 * -XX:-EliminateAllocations). The holding compiled frame has to be deoptimized when debugger
 * accesses xy, because afterwards locking on xy is omitted.
 * Note: there are no EA based optimizations at the escape point.
 */
class EADeoptFrameAfterReadLocalObject_01 extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        @SuppressWarnings("unused")
        ObjectReference xy = getLocalRef(bpe.thread().frame(1), "PointXY", "xy");
        
    }
}

class EADeoptFrameAfterReadLocalObject_01Target extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(1, 1);
        dontinline_brkpt();              // Debugger reads xy, when there are no virtual objects or eliminated locks in scope
        synchronized (xy) {              // Locking is eliminated.
            xy.x++;
            xy.y++;
        }
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * Similar to {@link EADeoptFrameAfterReadLocalObject_01} with the difference that the debugger
 * reads xy from an inlined callee. So xy is NoEscape instead of ArgEscape.
 */
class EADeoptFrameAfterReadLocalObject_01BTarget extends EATestCaseBaseTarget {

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

    public void dontinline_testMethod() {
        PointXY xy  = new PointXY(1, 1);
        callee(xy);                 // Debugger acquires ref to xy from inlined callee
                                    // xy is NoEscape, nevertheless the object is not replaced
                                    // by scalars if running with -XX:-EliminateAllocations.
                                    // In that case there are no EA based optimizations were
                                    // the debugger reads the NoEscape object.
        synchronized (xy) {         // Locking is eliminated.
            xy.x++;
            xy.y++;
        }
    }

    public void callee(PointXY xy) {
        dontinline_brkpt();              // Debugger reads xy.
                                         // There are no virtual objects or eliminated locks.
    }
}

class EADeoptFrameAfterReadLocalObject_01B extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        @SuppressWarnings("unused")
        ObjectReference xy = getLocalRef(bpe.thread().frame(1), "callee", "PointXY", "xy");
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * Let xy be ArgEscape. The frame dontinline_testMethod() has to be deoptimized when debugger
 * acquires xy from dontinline_calee(), because afterwards locking on xy is omitted.
 * Note: there are no EA based optimizations at the escape point.
 */
class EADeoptFrameAfterReadLocalObject_02 extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        @SuppressWarnings("unused")
        ObjectReference xy = getLocalRef(bpe.thread().frame(1), "dontinline_callee", "PointXY", "xy");
    }
}

class EADeoptFrameAfterReadLocalObject_02Target extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy  = new PointXY(1, 1);
        dontinline_callee(xy);      // xy is ArgEscape, debugger acquires ref to xy from callee
        synchronized (xy) {         // Locking is eliminated.
            xy.x++;
            xy.y++;
        }
    }

    public void dontinline_callee(PointXY xy) {
        dontinline_brkpt();              // Debugger reads xy.
                                         // There are no virtual objects or eliminated locks.
    }

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * Similar to {@link EADeoptFrameAfterReadLocalObject_02} there is an ArgEscape object xy, but in
 * contrast it is not in the parameter list of a call when the debugger reads an object.
 * Therefore the frame of the test method should not be deoptimized
 */
class EADeoptFrameAfterReadLocalObject_02C extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        @SuppressWarnings("unused")
        ObjectReference xy = getLocalRef(bpe.thread().frame(1), "dontinline_callee", "PointXY", "xy");
    }
}

class EADeoptFrameAfterReadLocalObject_02CTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy  = new PointXY(1, 1);
        dontinline_make_arg_escape(xy);  // because of this call xy is ArgEscape
        dontinline_callee();             // xy is ArgEscape, but not a parameter of this call
        synchronized (xy) {              // Locking is eliminated.
            xy.x++;
            xy.y++;
        }
    }

    public void dontinline_callee() {
        @SuppressWarnings("unused")
        PointXY xy  = new PointXY(2, 2);
        dontinline_brkpt();              // Debugger reads xy.
                                         // No need to deoptimize the caller frame
    }

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

    @Override
    public boolean testFrameShouldBeDeoptimized() {
        return false;
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * Similar to {@link EADeoptFrameAfterReadLocalObject_02} there is an ArgEscape object xy in
 * dontinline_testMethod() which is being passed as parameter when the debugger accesses a local object.
 * Nevertheless dontinline_testMethod must not be deoptimized, because there is an entry frame
 * between it and the frame accessed by the debugger.
 */
class EADeoptFrameAfterReadLocalObject_02D extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        @SuppressWarnings("unused")
        ObjectReference xy = getLocalRef(bpe.thread().frame(1), "dontinline_callee_accessed_by_debugger", "PointXY", "xy");
    }
}

class EADeoptFrameAfterReadLocalObject_02DTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy  = new PointXY(1, 1);
        dontinline_callee(xy);           // xy is ArgEscape and being passed as parameter
        synchronized (xy) {              // Locking is eliminated.
            xy.x++;
            xy.y++;
        }
    }

    public void dontinline_callee(PointXY xy) {
        if (warmupDone) { // TODO: crashes if doing call during warmup
            dontinline_call_with_entry_frame(this, "dontinline_callee_accessed_by_debugger");
        }
    }

    public void dontinline_callee_accessed_by_debugger() {
        @SuppressWarnings("unused")
        PointXY xy  = new PointXY(2, 2);
        dontinline_brkpt();              // Debugger reads xy.
                                         // No need to deoptimize the caller frame
    }

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 8;
    }

    @Override
    public boolean testFrameShouldBeDeoptimized() {
        return false;
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * Let xy be NoEscape whose allocation cannot be eliminated (e.g. because of
 * -XX:-EliminateAllocations).  The holding compiled frame has to be deoptimized when debugger
 * accesses xy, because the following field accesses get eliminated.  Note: there are no EA based
 * optimizations at the escape point.
 */
class EADeoptFrameAfterReadLocalObject_03 extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        ObjectReference xy = getLocalRef(bpe.thread().frame(1), "PointXY", "xy");
        setField(xy, "x", env.vm().mirrorOf(1));
    }
}

class EADeoptFrameAfterReadLocalObject_03Target extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(0, 1);
        dontinline_brkpt();              // Debugger reads xy, when there are no virtual objects or
                                         // eliminated locks in scope and modifies xy.x
        iResult = xy.x + xy.y;           // Loads are replaced by constants 0 and 1.
    }

    @Override
    public int getExpectedIResult() {
        return 1 + 1;
    }
}

/////////////////////////////////////////////////////////////////////////////
//
// Monitor info tests
//
/////////////////////////////////////////////////////////////////////////////

class EAGetOwnedMonitorsTarget extends EATestCaseBaseTarget {

    public long checkSum;

    public void dontinline_testMethod() {
        PointXY l1 = new PointXY(4, 2);
        synchronized (l1) {
            dontline_endlessLoop();
        }
    }

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
        loopCount = 3;
    }

    public void warmupDone() {
        super.warmupDone();
        msg("enter 'endless' loop by setting loopCount = 1L << 60");
        loopCount = 1L << 60; // endless loop
    }
}

class EAGetOwnedMonitors extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        msg("resume");
        env.targetMainThread.resume();
        waitUntilTargetHasEnteredEndlessLoop();
        // In contrast to JVMTI, JDWP requires a target thread to be suspended, before the owned monitors can be queried
        msg("suspend target");
        env.targetMainThread.suspend();
        msg("Get owned monitors");
        List<ObjectReference> monitors = env.targetMainThread.ownedMonitors();
        Asserts.assertEQ(monitors.size(), 1, "unexpected number of owned monitors");
        terminateEndlessLoop();
    }
}

/////////////////////////////////////////////////////////////////////////////

class EAEntryCountTarget extends EATestCaseBaseTarget {

    public long checkSum;

    public void dontinline_testMethod() {
        PointXY l1 = new PointXY(4, 2);
        synchronized (l1) {
            inline_testMethod2(l1);
        }
    }

    public void inline_testMethod2(PointXY l1) {
        synchronized (l1) {
            dontline_endlessLoop();
        }
    }

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
        loopCount = 3;
    }

    public void warmupDone() {
        super.warmupDone();
        msg("enter 'endless' loop by setting loopCount = 1L << 60");
        loopCount = 1L << 60; // endless loop
    }
}

class EAEntryCount extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        msg("resume");
        env.targetMainThread.resume();
        waitUntilTargetHasEnteredEndlessLoop();
        // In contrast to JVMTI, JDWP requires a target thread to be suspended, before the owned monitors can be queried
        msg("suspend target");
        env.targetMainThread.suspend();
        msg("Get owned monitors");
        List<ObjectReference> monitors = env.targetMainThread.ownedMonitors();
        Asserts.assertEQ(monitors.size(), 1, "unexpected number of owned monitors");
        msg("Get entry count");
        int entryCount = monitors.get(0).entryCount();
        Asserts.assertEQ(entryCount, 2, "wrong entry count");
        terminateEndlessLoop();
    }
}

/////////////////////////////////////////////////////////////////////////////
//
// PopFrame tests
//
/////////////////////////////////////////////////////////////////////////////

/**
 * PopFrame into caller frame with scalar replaced objects.
 */
class EAPopFrameNotInlined extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        printStack(bpe.thread());
        msg("PopFrame");
        bpe.thread().popFrames(bpe.thread().frame(0));
        msg("PopFrame DONE");
    }
}

class EAPopFrameNotInlinedTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(4, 2);
        dontinline_brkpt();
        iResult = xy.x + xy.y;
    }

    @Override
    public boolean testFrameShouldBeDeoptimized() {
        // Test is only performed after the frame pop.
        // Then dontinline_testMethod is interpreted.
        return false;
    }

    @Override
    public int getExpectedIResult() {
        return 4 + 2;
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * Pop frames into {@link EAPopFrameNotInlinedReallocFailureTarget#dontinline_testMethod()} which
 * holds scalar replaced objects. In preparation of the pop frame operations the vm eagerly
 * reallocates scalar replaced objects to avoid failures when actually poping the frames. We provoke
 * a reallocation failures and expect {@link VMOutOfMemoryException}.
 */
class EAPopFrameNotInlinedReallocFailure extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        ThreadReference thread = bpe.thread();
        printStack(thread);
        // frame[0]: EATestCaseBaseTarget.dontinline_brkpt()
        // frame[1]: EAPopFrameNotInlinedReallocFailureTarget.dontinline_consume_all_memory_brkpt()
        // frame[2]: EAPopFrameNotInlinedReallocFailureTarget.dontinline_testMethod()
        // frame[3]: EATestCaseBaseTarget.run()
        // frame[4]: EATestsTarget.main(java.lang.String[])
        msg("PopFrame");
        boolean coughtOom = false;
        try {
            // try to pop dontinline_consume_all_memory_brkpt
            thread.popFrames(thread.frame(1));
        } catch (VMOutOfMemoryException oom) {
            // as expected
            msg("cought OOM");
            coughtOom  = true;
        }
        freeAllMemory();
        // We succeeded to pop just one frame. When we continue, we will call dontinline_brkpt() again.
        Asserts.assertTrue(coughtOom || !env.targetVMOptions.EliminateAllocations, "PopFrame should have triggered an OOM exception in target");
        String expectedTopFrame =
                env.targetVMOptions.EliminateAllocations ? "dontinline_consume_all_memory_brkpt" : "dontinline_testMethod";
        Asserts.assertEQ(expectedTopFrame, thread.frame(0).location().method().name());
        printStack(thread);
    }
}

class EAPopFrameNotInlinedReallocFailureTarget extends EATestCaseBaseTarget {

    public boolean doneAlready;

    public void dontinline_testMethod() {
        long a[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};                // scalar replaced
        Vector10 v = new Vector10(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);  // scalar replaced
        dontinline_consume_all_memory_brkpt();
        lResult = a[0] + a[1] + a[2] + a[3] + a[4] + a[5] + a[6] + a[7] + a[8] + a[9]
               + v.i0 + v.i1 + v.i2 + v.i3 + v.i4 + v.i5 + v.i6 + v.i7 + v.i8 + v.i9;
    }

    public void dontinline_consume_all_memory_brkpt() {
        if (warmupDone && !doneAlready) {
            doneAlready = true;
            consumeAllMemory(); // provoke reallocation failure
            dontinline_brkpt();
        }
    }

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

    @Override
    public long getExpectedLResult() {
        long n = 10;
        return 2*n*(n+1)/2;
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * Pop inlined top frame dropping into method with scalar replaced opjects.
 */
class EAPopInlinedMethodWithScalarReplacedObjectsReallocFailure extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        ThreadReference thread = env.targetMainThread;
        thread.resume();
        waitUntilTargetHasEnteredEndlessLoop();

        thread.suspend();
        printStack(thread);
        // frame[0]: EAPopInlinedMethodWithScalarReplacedObjectsReallocFailureTarget.inlinedCallForcedToReturn()
        // frame[1]: EAPopInlinedMethodWithScalarReplacedObjectsReallocFailureTarget.dontinline_testMethod()
        // frame[2]: EATestCaseBaseTarget.run()

        msg("Pop Frames");
        boolean coughtOom = false;
        try {
            thread.popFrames(thread.frame(0));    // Request pop frame of inlinedCallForcedToReturn()
                                                  // reallocation is triggered here
        } catch (VMOutOfMemoryException oom) {
            // as expected
            msg("cought OOM");
            coughtOom = true;
        }
        printStack(thread);
        // frame[0]: EAPopInlinedMethodWithScalarReplacedObjectsReallocFailureTarget.inlinedCallForcedToReturn()
        // frame[1]: EAPopInlinedMethodWithScalarReplacedObjectsReallocFailureTarget.dontinline_testMethod()
        // frame[2]: EATestCaseBaseTarget.run()

        freeAllMemory();
        setField(testCase, "loopCount", env.vm().mirrorOf(0)); // terminate loop
        Asserts.assertTrue(coughtOom || !env.targetVMOptions.EliminateAllocations, "PopFrame should have triggered an OOM exception in target");
        String expectedTopFrame =
                env.targetVMOptions.EliminateAllocations ? "inlinedCallForcedToReturn" : "dontinline_testMethod";
        Asserts.assertEQ(expectedTopFrame, thread.frame(0).location().method().name());
    }
}

class EAPopInlinedMethodWithScalarReplacedObjectsReallocFailureTarget extends EATestCaseBaseTarget {

    public long checkSum;

    public void dontinline_testMethod() {
        long a[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};                // scalar replaced
        Vector10 v = new Vector10(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);  // scalar replaced
        long l = inlinedCallForcedToReturn();
        lResult = a[0] + a[1] + a[2] + a[3] + a[4] + a[5] + a[6] + a[7] + a[8] + a[9]
               + v.i0 + v.i1 + v.i2 + v.i3 + v.i4 + v.i5 + v.i6 + v.i7 + v.i8 + v.i9;
    }

    public long inlinedCallForcedToReturn() {
        long cs = checkSum;
        dontinline_consumeAllMemory();
        while (loopCount-- > 0) {
            targetIsInLoop = true;
            checkSum += checkSum % ++cs;
        }
        loopCount = 3;
        targetIsInLoop = false;
        return checkSum;
    }

    public void dontinline_consumeAllMemory() {
        if (warmupDone && (loopCount > 3)) {
            consumeAllMemory();
        }
    }

    @Override
    public long getExpectedLResult() {
        long n = 10;
        return 2*n*(n+1)/2;
    }

    @Override
    public void setUp() {
        super.setUp();
        loopCount = 3;
    }

    public void warmupDone() {
        super.warmupDone();
        msg("enter 'endless' loop by setting loopCount = 1L << 60");
        loopCount = 1L << 60; // endless loop
    }
}

/////////////////////////////////////////////////////////////////////////////
//
// ForceEarlyReturn tests
//
/////////////////////////////////////////////////////////////////////////////

/**
 * ForceEarlyReturn into caller frame with scalar replaced objects.
 */
class EAForceEarlyReturnNotInlined extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        BreakpointEvent bpe = env.resumeTo(getTargetTestCaseBaseName(), "dontinline_brkpt", "()V");
        ThreadReference thread = bpe.thread();
        printStack(thread);
        // frame[0]: EATestCaseBaseTarget.dontinline_brkpt()
        // frame[1]: EATestCaseBaseTarget.dontinline_brkpt_iret()
        // frame[2]: EAForceEarlyReturnNotInlinedTarget.dontinline_testMethod()
        // frame[3]: EATestCaseBaseTarget.run()
        // frame[4]: EATestsTarget.main(java.lang.String[])

        msg("Step out");
        env.stepOut(thread);                               // return from dontinline_brkpt
        printStack(thread);
        msg("ForceEarlyReturn");
        thread.forceEarlyReturn(env.vm().mirrorOf(43));    // return from dontinline_brkpt_iret,
                                                           // does not trigger reallocation in contrast to PopFrame
        msg("Step over line");
        env.stepOverLine(thread);                          // reallocation is triggered here
        printStack(thread);
        msg("ForceEarlyReturn DONE");
    }
}

class EAForceEarlyReturnNotInlinedTarget extends EATestCaseBaseTarget {

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(4, 2);
        int i = dontinline_brkpt_iret();
        iResult = xy.x + xy.y + i;
    }

    @Override
    public int getExpectedIResult() {
        return 4 + 2 + 43;
    }

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
    }

    public boolean testFrameShouldBeDeoptimized() {
        return true; // because of stepping
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * ForceEarlyReturn at safepoint in frame with scalar replaced objects.
 */
class EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjects extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        ThreadReference thread = env.targetMainThread;
        thread.resume();
        waitUntilTargetHasEnteredEndlessLoop();

        thread.suspend();
        printStack(thread);
        // frame[0]: EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjectsTarget.inlinedCallForcedToReturn()
        // frame[1]: EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjectsTarget.dontinline_testMethod()
        // frame[2]: EATestCaseBaseTarget.run()

        msg("ForceEarlyReturn");
        thread.forceEarlyReturn(env.vm().mirrorOf(43));    // Request force return 43 from inlinedCallForcedToReturn()
                                                           // reallocation is triggered here
        msg("Step over instruction to do the forced return");
        env.stepOverInstruction(thread);
        printStack(thread);
        msg("ForceEarlyReturn DONE");
    }
}

class EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjectsTarget extends EATestCaseBaseTarget {

    public int checkSum;

    public void dontinline_testMethod() {
        PointXY xy = new PointXY(4, 2);
        int i = inlinedCallForcedToReturn();
        iResult = xy.x + xy.y + i;
    }

    public int inlinedCallForcedToReturn() {               // forced to return 43
        int i = checkSum;
        while (loopCount-- > 0) {
            targetIsInLoop = true;
            checkSum += checkSum % ++i;
        }
        loopCount = 3;
        targetIsInLoop = false;
        return checkSum;
    }

    @Override
    public int getExpectedIResult() {
        return 4 + 2 + 43;
    }

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
        loopCount = 3;
    }

    public void warmupDone() {
        super.warmupDone();
        msg("enter 'endless' loop by setting loopCount = 1L << 60");
        loopCount = 1L << 60; // endless loop
    }

    public boolean testFrameShouldBeDeoptimized() {
        return true; // because of stepping
    }
}

/////////////////////////////////////////////////////////////////////////////

/**
 * ForceEarlyReturn with reallocation failure.
 */
class EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjectsReallocFailure extends EATestCaseBaseDebugger {

    public void runTestCase() throws Exception {
        ThreadReference thread = env.targetMainThread;
        thread.resume();
        waitUntilTargetHasEnteredEndlessLoop();

        thread.suspend();
        printStack(thread);
        // frame[0]: EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjectsReallocFailureTarget.inlinedCallForcedToReturn()
        // frame[1]: EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjectsReallocFailureTarget.dontinline_testMethod()
        // frame[2]: EATestCaseBaseTarget.run()

        msg("ForceEarlyReturn");
        boolean coughtOom = false;
        try {
            thread.forceEarlyReturn(env.vm().mirrorOf(43));    // Request force return 43 from inlinedCallForcedToReturn()
                                                               // reallocation is triggered here
        } catch (VMOutOfMemoryException oom) {
            // as expected
            msg("cought OOM");
            coughtOom   = true;
        }        
        freeAllMemory();
        if (env.targetVMOptions.EliminateAllocations) {
            Asserts.assertTrue(coughtOom, "PopFrame should have triggered an OOM exception in target");
            thread.forceEarlyReturn(env.vm().mirrorOf(43));
        }
        msg("Step over instruction to do the forced return");
        env.stepOverInstruction(thread);
        printStack(thread);
        msg("ForceEarlyReturn DONE");
    }
}

class EAForceEarlyReturnOfInlinedMethodWithScalarReplacedObjectsReallocFailureTarget extends EATestCaseBaseTarget {

    public int checkSum;

    public void dontinline_testMethod() {
        long a[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};                // scalar replaced
        Vector10 v = new Vector10(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);  // scalar replaced
        long l = inlinedCallForcedToReturn();
        lResult = a[0] + a[1] + a[2] + a[3] + a[4] + a[5] + a[6] + a[7] + a[8] + a[9]
               + v.i0 + v.i1 + v.i2 + v.i3 + v.i4 + v.i5 + v.i6 + v.i7 + v.i8 + v.i9 + l;
    }

    public long inlinedCallForcedToReturn() {                      // forced to return 43
        long cs = checkSum;
        dontinline_consumeAllMemory();
        while (loopCount-- > 0) {
            targetIsInLoop = true;
            checkSum += checkSum % ++cs;
        }
        loopCount = 3;
        targetIsInLoop = false;
        return checkSum;
    }

    public void dontinline_consumeAllMemory() {
        if (warmupDone) {
            consumeAllMemory();
        }
    }

    @Override
    public long getExpectedLResult() {
        long n = 10;
        return 2*n*(n+1)/2 + 43;
    }

    @Override
    public void setUp() {
        super.setUp();
        testMethodDepth = 2;
        loopCount = 3;
    }

    public void warmupDone() {
        super.warmupDone();
        msg("enter 'endless' loop by setting loopCount = 1L << 60");
        loopCount = 1L << 60; // endless loop
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

    /**
     * Note that we don't use a sync block here, because javac would generate an synthetic exception
     * handler for the synchronized block that catches Throwable E, unlocks and throws E
     * again. The throw bytecode causes the BCEscapeAnalyzer to set the escape state to GlobalEscape
     * (see comment on exception handlers in BCEscapeAnalyzer::iterate_blocks())
     */
    public synchronized void dontinline_sync_method(EATestCaseBaseTarget target) {
        target.dontinline_brkpt();
    }
}

class Vector10 {
    int i0, i1, i2, i3, i4, i5, i6, i7, i8, i9;
    public Vector10(int j0, int j1, int j2, int j3, int j4, int j5, int j6, int j7, int j8, int j9) {
        i0=j0; i1=j1; i2=j2; i3=j3; i4=j4; i5=j5; i6=j6; i7=j7; i8=j8; i9=j9;
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
