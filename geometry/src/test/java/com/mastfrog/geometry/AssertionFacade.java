/* 
 * The MIT License
 *
 * Copyright 2020 Tim Boudreau.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mastfrog.geometry;

import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author Tim Boudreau
 */
public abstract class AssertionFacade<T> {

    static final double TOL = 0.0000000001;

    protected T ca;

    AssertionFacade(T ca) {
        this.ca = ca;
        Assertions.assertNotNull(ca);
    }

    @Override
    public String toString() {
        return ca.toString();
    }

    protected static String msgs(String a, String b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return a + "; " + b;
        }
    }

    protected Supplier<String> msg(String message) {
        return msg(ca, message);
    }

    protected Supplier<String> msg(String msgA, String msgB) {
        return msg(msgs(msgA, msgB));
    }

    protected void assertSaneDegrees(double val, String msg) {
        Assertions.assertTrue(val >= 0);
        Assertions.assertTrue(val < 360, msg("Degrees out of range 0-360: " + val, msg));
    }

    protected void assertDouble(double expected, double val) {
        Assertions.assertEquals(expected, val, TOL);
    }

    protected void assertDouble(double expected, double val, String msg) {
        Assertions.assertEquals(expected, val, TOL, msg);
    }

    protected void assertDouble(double expected, double val, Supplier<String> msg) {
        Assertions.assertEquals(expected, val, TOL, msg);
    }

    protected void assertNotDouble(double expected, double val) {
        Assertions.assertNotEquals(expected, val, TOL);
    }

    protected void assertNotDouble(double expected, double val, String msg) {
        Assertions.assertEquals(expected, val, TOL, msg);
    }

    protected void assertNotDouble(double expected, double val, Supplier<String> msg) {
        Assertions.assertEquals(expected, val, TOL, msg);
    }

    static Supplier<String> msg(Object ca, String message) {
        return () -> {
            if (message == null) {
                return ca.toString();
            }
            return message + " in " + ca;
        };
    }
}
