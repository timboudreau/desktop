/*
 * The MIT License
 *
 * Copyright 2020 Mastfrog Technologies.
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

import static com.mastfrog.colors.space.Conversions.*;
import com.mastfrog.colors.space.XyzColor.XyzComponents;
import static com.mastfrog.colors.space.XyzColor.XyzComponents.X;
import static com.mastfrog.colors.space.XyzColor.XyzComponents.Y;
import static com.mastfrog.colors.space.XyzColor.XyzComponents.Z;
import java.awt.Color;
import static java.lang.Math.max;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

/**
 * A color in CieXyz color space.
 *
 * @author Tim Boudreau
 * @since 2.8.3.1
 */
public final class XyzColor implements ColorRepresentation<XyzColor, XyzComponents> {

    /**
     * Component type for XyzColor.
     */
    public enum XyzComponents implements ColorComponent {
        // These magic values are the values encountered when traversing
        // the entire integer RGB color space of java.awt.Color.
        X(0, 95.05),
        Y(0, 100),
        Z(0, 108.9);

        private final ComponentRange range;

        private XyzComponents(double lo, double hi) {
            this.range = new ComponentRange(lo, hi);
        }

        @Override
        public double valueIn(Color color) {
            double[] arr = new double[3];
            rgbToXyz(color.getRed(), color.getGreen(), color.getBlue(), arr);
            return arr[ordinal()];
        }

        /**
         * Ranges returned here are the minimum and maximum possible values
         * across all possible values of java.awt.Color, not necessarily all
         * values that can be represented by an XyzColor.
         *
         * @return A component range
         */
        @Override
        public ComponentRange range() {
            return range;
        }
    }

    public final double x;
    public final double y;
    public final double z;

    public XyzColor(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public XyzColor(Color color) {
        double[] xyz = new double[3];
        rgbToXyz(color.getRed(), color.getGreen(), color.getBlue(), xyz);
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    public XyzColor withX(double x) {
        return new XyzColor(x, y, z);
    }

    public XyzColor withY(double y) {
        return new XyzColor(x, y, z);
    }

    public XyzColor withZ(double z) {
        return new XyzColor(x, y, z);
    }

    @Override
    public XyzColor toXyz() {
        return this;
    }

    public XyzColor invert() {
        double xx = 1D - x;
        double yy = 1D - y;
        double zz = 1D - z;
        return new XyzColor(max(0, xx), max(0, yy), max(0, zz));
    }

    @Override
    public double[] components() {
        return new double[]{x, y, z};
    }

    @Override
    public double component(XyzComponents which) {
        switch (which) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
            default:
                throw new AssertionError(which);
        }
    }

    @Override
    public XyzColor withComponent(XyzComponents component, double value) {
        switch (component) {
            case X:
                return withX(value);
            case Y:
                return withY(value);
            case Z:
                return withZ(value);
            default:
                throw new AssertionError(component);
        }
    }

    public LabColor toLab() {
        double[] lab = new double[3];
        Conversions.xyzToLab(x, y, z, lab);
        return new LabColor(lab[0], lab[1], lab[2]);
    }

    public LabColor toLab(Illuminant ill, Standard standard) {
        double[] lab = new double[3];
        Conversions.xyzToLab(x, y, z, lab);
        return new LabColor(lab[0], lab[1], lab[2], ill == null ? Illuminant.getDefault() : ill,
                standard == null ? Standard.CIE_1964 : standard);
    }

    @Override
    public Class<XyzComponents> componentType() {
        return XyzComponents.class;
    }

    @Override
    public XyzColor combine(XyzColor other, DoubleBinaryOperator op) {
        return new XyzColor(
                op.applyAsDouble(x, other.x),
                op.applyAsDouble(y, other.y),
                op.applyAsDouble(z, other.z));
    }

    @Override
    public XyzColor combine(XyzColor other, Function<XyzComponents, DoubleBinaryOperator> opFactory) {
        return new XyzColor(
                opFactory.apply(X).applyAsDouble(x, other.x),
                opFactory.apply(Y).applyAsDouble(y, other.y),
                opFactory.apply(Z).applyAsDouble(z, other.z)
        );
    }

    @Override
    public Color toColor() {
        int[] rgb = new int[3];
        xyzToRgb(x, y, z, rgb);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    @Override
    public String toString() {
        return "XYZ(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.x)
                ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.y)
                ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.z)
                ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != XyzColor.class) {
            return false;
        }
        final XyzColor other = (XyzColor) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z);
    }
}
