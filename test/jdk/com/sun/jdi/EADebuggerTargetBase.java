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

/*
 * Base class for tests exercising debugging with active escape analysis.
 */

import compiler.testlibrary.CompilerUtils;
import jdk.test.lib.Asserts;
import sun.hotspot.WhiteBox;

public abstract class EADebuggerTargetBase {

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
        msg(getName() + " is exiting.");
    }

    public abstract void dontinline_testMethod();

    public void dontinline_brkpt() {
        // will set breakpoint here
    }

    public String getName() {
        return getClass().getName();
    }

    private void warmupDone() {
        msg(getName() + " warmup done.");
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

