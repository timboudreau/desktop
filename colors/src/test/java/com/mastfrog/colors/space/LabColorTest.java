/*
 * The MIT License
 *
 * Copyright 2022 Mastfrog Technologies.
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
package com.mastfrog.colors.space;

import java.awt.Color;
import static java.lang.Math.abs;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author timb
 */
public class LabColorTest {

    @Test
    public void testNothing() {
        // here so there is some test and the build doesn't fail
        // the tests below are too slow to run on every build,
        // and what they test is unlikely to change
    }

//    @Test
    public void testHsbConversionIsAccurate() {
        eachColor(c -> {
            HsbColor hsb = new HsbColor(c);
            Color converted = hsb.toColor();
            assertColor(c, converted, hsb);
        });
    }

//    @Test
    public void testLabConversionIsAccurate() {
        eachColor(c -> {
            LabColor lab = new LabColor(c, Illuminant.EqualEnergy);
            Color converted = lab.toColor();
            assertColor(c, converted, lab);
        });
    }

//    @Test
    public void testXyzConversionIsAccurate() {
        eachColor(c -> {
            XyzColor xyz = new XyzColor(c);
            Color converted = xyz.toColor();
            assertColor(c, converted, xyz);
        });
    }

    private <T extends ColorRepresentation> void assertColor(Color expect, Color got, T thru) {
        assertCloseEnough(expect.getRed(), got.getRed(), "red via " + thru
                + " for " + expect + " got " + got);
        assertCloseEnough(expect.getGreen(), got.getGreen(), "red via "
                + thru + " for " + expect + " got " + got);
        assertCloseEnough(expect.getBlue(), got.getBlue(), "red via "
                + thru + " for " + expect + " got " + got);
    }

    private void assertCloseEnough(int a, int b, String what) {
        assertTrue(abs(a - b) <= 1, what + " values differ");
    }

    private void eachColor(Consumer<Color> c) {
        for (int g = 0; g < 256; g++) {
            for (int b = 0; b < 256; b++) {
                for (int r = 0; r < 256; r++) {
                    c.accept(new Color(r, g, b));
                }
            }
        }
    }
}
