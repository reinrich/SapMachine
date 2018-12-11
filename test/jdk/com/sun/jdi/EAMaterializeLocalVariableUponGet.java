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
 * @author Richard Reingruber richard DOT reingruber AT sap DOT com
 *
 * @library /test/lib /test/hotspot/jtreg
 *
 * @run build TestScaffold VMConnection TargetListener TargetAdapter sun.hotspot.WhiteBox
 * @run main jdk.test.lib.FileInstaller compilerDirectives.json compilerDirectives.json
 * @run driver ClassFileInstaller sun.hotspot.WhiteBox
 *                                sun.hotspot.WhiteBox$WhiteBoxPermission
 * @run compile -g EAMaterializeLocalVariableUponGet.java
 * @run driver EAMaterializeLocalVariableUponGet
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

import jdk.test.lib.Asserts;


// Manual execution:
// export CLS_PATH="-cp /priv/d038402/git/reinrich/SapMachine/eclipse_java_projs/test.jdk/bin:/priv/d038402/git/reinrich/SapMachine/eclipse_java_projs/test.lib/bin"
// ./images/jdk/bin/java -Dtest.jdk=/priv/d038402/builds/SapMachine_lu0486_64_slowdebug/images/jdk $CLS_PATH EAMaterializeLocalVariableUponGet $CLS_PATH -XX:+TraceDeoptimization -XX:+PrintCompilation -XX:+PrintInlining -XX:-TieredCompilation -Xbatch -XX:-PrintOptoAssembly -XX:CompilerDirectivesFile=compilerDirectives.json -XX:CICompilerCount=1

// TODO: remove trace options like '-XX:+PrintCompilation -XX:+PrintInlining' to avoid deadlock as in https://bugs.openjdk.java.net/browse/JDK-8213902

/********** target program **********/

class EAMaterializeLocalVariableUponGetTarget extends EADebuggerTargetBase {

    public static void main(String[] args) {
        new EAMaterializeLocalVariableUponGetTarget().run();
    }

    public int dontinline_testMethod() {
        PointXY xy = new PointXY(4, 2);
        dontinline_brkpt();
        return xy.x + xy.y;
    }
}

 /********** test program **********/

public class EAMaterializeLocalVariableUponGet extends TestScaffold {
    ReferenceType targetClass;
    ThreadReference mainThread;

    EAMaterializeLocalVariableUponGet (String args[]) {
        super(args);
    }

    public static void main(String[] args) throws Exception {
        new EAMaterializeLocalVariableUponGet (args).startTests();
    }

    /********** test core **********/

    protected void runTests() throws Exception {
        /*
         * Get to the top of main() to determine targetClass and mainThread
         */
        String targetProgName = EAMaterializeLocalVariableUponGetTarget.class.getName();
        String targetBaseName = EADebuggerTargetBase.class.getName();
        String testName = getClass().getSimpleName();
        BreakpointEvent bpe = startToMain(targetProgName);
        targetClass = bpe.location().declaringType();
        mainThread = bpe.thread();

        resumeTo(targetBaseName, "warmupDone", "()V");
        bpe = resumeTo(targetBaseName, "dontinline_brkpt", "()V");

        // print stack
        msg("Debuggee Stack:");
        List<StackFrame> stack_frames = mainThread.frames();
        int i = 0;
        for (StackFrame ff : stack_frames) {
            System.out.println("frame[" + i++ +"]: " + ff.location().method());
           
        }
        
        // retrieve scalar replaced object
        StackFrame frame = bpe.thread().frame(1);
        Asserts.assertEQ("dontinline_testMethod", frame.location().method().name());
        List<LocalVariable> localVars = frame.visibleVariables();
        msg("Check if the local variable xy in dontinline_testMethod() has the expected value: ");
        boolean found = false;
        for (LocalVariable lv : localVars) {
            if (lv.name().equals("xy")) {
                found  = true;
                Value xy = frame.getValue(lv);
                Asserts.assertNotNull(xy);
                Asserts.assertEQ(xy.type().name(), "PointXY");
                ObjectReference xyObj = (ObjectReference) xy;
                // now check the fields
                ReferenceType rt = xyObj.referenceType();
                Field xFd = rt.fieldByName("x");
                Value xVal = xyObj.getValue(xFd);
                Asserts.assertEQ(((PrimitiveValue)xVal).intValue(), 4);
                Field yFd = rt.fieldByName("y");
                Value yVal = xyObj.getValue(yFd);
                Asserts.assertEQ(((PrimitiveValue)yVal).intValue(), 2);
            }
        }
        Asserts.assertTrue(found);
        msg("OK.");
        
        msg("debugee pid: " + vm().process().pid());
//        waitForInput(); // TODO

        // resume the target listening for events
        listenUntilVMDisconnect();

        /*
         * deal with results of test if anything has called failure("foo")
         * testFailed will be true
         */
        if (!testFailed) {
            println(testName + ": passed");
        } else {
            throw new Exception(testName + ": failed");
        }
    }
    
    private static void msg(String m) {
        System.out.println();
        System.out.println("### " + m);
        System.out.println();
    }
}
