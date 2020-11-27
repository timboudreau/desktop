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

import com.mastfrog.geometry.Sector;
import com.mastfrog.geometry.Sector;

/**
 *
 * @author Tim Boudreau
 */
public class SA<S extends Sector> extends AssertionFacade<S> {

    public SA(S ca) {
        super(ca);
    }

    public SA assertExtent(double val) {
        return assertExtent(null, val);
    }

    public SA assertExtent(String msg, double val) {
        assertSaneDegrees(ca.extent(), msg);
        assertDouble(val, ca.extent(), msg("Wrong extent", msg));
        return this;
    }

    public SA assertStartingAngle(double val) {
        return assertStartingAngle(null, val);
    }

    public SA assertStartingAngle(String msg, double val) {
        assertSaneDegrees(ca.start(), msg);
        assertDouble(val, ca.start(), msg("Wrong starting angle in '" + ca + "' " + ca.start() + " expected " + val, msg));
        return this;
    }

    public SA assertMinAngle(double val) {
        return assertMinAngle(null, val);
    }

    public SA assertMinAngle(String msg, double val) {
        assertSaneDegrees(ca.minDegrees(), msg);
        assertDouble(val, ca.minDegrees(), msg("Wrong min angle in '" + ca + "'", msg));
        return this;
    }

    public SA assertMaxAngle(double val) {
        return assertMaxAngle(null, val);
    }

    public SA assertMaxAngle(String msg, double val) {
        assertSaneDegrees(ca.maxDegrees(), msg);
        assertDouble(val, ca.maxDegrees(), msg("Wrong max angle in '" + ca + "'", msg));
        return this;
    }

}
