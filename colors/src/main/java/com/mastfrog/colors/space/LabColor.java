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

import com.mastfrog.colors.space.LabColor.LabComponents;
import static com.mastfrog.colors.space.LabColor.LabComponents.*;
import static com.mastfrog.colors.space.Conversions.*;
import java.awt.Color;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

/**
 * Implementation of L*A*B color.
 *
 * @author Tim Boudreau
 * @since 2.8.3.1
 */
public final class LabColor implements ColorRepresentation<LabColor, LabComponents> {

    /**
     * Component type for L*A*B color - note that the value returned by range()
     * is the total value range that can be produced by all possible values of
     * java.awt.Color - it is legal to produce instances outside the range -
     * they just may not translate as expected to RGB.
     */
    public enum LabComponents implements ColorRepresentation.ColorComponent {
        // These magic values are the maximum and minimum values
        // encountered when traversing the entire integer RGB color space
        // used by java.awt.Color
        L(0, 100),
        A(-85.88539046866318, 98.60839787805526),
        B(-108.79669359538693, 93.99151723968109);

        private final ComponentRange range;

        private LabComponents(double low, double high) {
            this.range = new ComponentRange(low, high);
        }

        @Override
        public ComponentRange range() {
            return range;
        }

        @Override
        public double valueIn(Color color) {
            double[] arr = new double[3];
            rgbToLab(color.getRed(), color.getGreen(), color.getBlue(), arr);
            return arr[ordinal()];
        }
    }

    public final double l;
    public final double a;
    public final double b;

    public final Illuminant illuminant;
    public final Standard standard;

    public LabColor(double l, double a, double b) {
        this.l = l;
        this.a = a;
        this.b = b;
        this.illuminant = null;
        this.standard = Standard.CIE_1964;
    }

    public LabColor(double l, double a, double b, Illuminant illuminant) {
        this(l, a, b, illuminant, Standard.CIE_1964);
    }

    public LabColor(double l, double a, double b, Illuminant illuminant, Standard standard) {
        this.l = l;
        this.a = a;
        this.b = b;
        this.illuminant = illuminant;
        this.standard = standard;
    }

    public LabColor(Color color) {
        double[] xyz = new double[3];
        rgbToXyz(color.getRed(), color.getGreen(), color.getBlue(), xyz);
        xyzToLab(xyz[0], xyz[1], xyz[2], xyz);
        l = xyz[0];
        a = xyz[1];
        b = xyz[2];
        this.illuminant = null;
        this.standard = Standard.CIE_1964;
    }

    public LabColor(Color color, Illuminant illuminant) {
        this(color, illuminant, Standard.CIE_1964);
    }

    public LabColor(Color color, Illuminant illuminant, Standard standard) {
        double[] xyz = new double[3];
        rgbToXyz(color.getRed(), color.getGreen(), color.getBlue(), xyz);
        xyzToLab(xyz[0], xyz[1], xyz[2], xyz);
        l = xyz[0];
        a = xyz[1];
        b = xyz[2];
        this.illuminant = illuminant;
        this.standard = standard;
    }

    public double l() {
        return l;
    }

    public double a() {
        return a;
    }

    public double b() {
        return b;
    }

    public LabColor withStandard(Standard standard) {
        if (standard == this.standard) {
            return this;
        }
        return new LabColor(l, a, b, illuminant, standard);
    }

    public LabColor withIlluminant(Illuminant illuminant) {
        return new LabColor(l, a, b, illuminant, standard);
    }

    public LabColor withIlluminantAndStandard(Illuminant illuminant, Standard standard) {
        return new LabColor(l, a, b, illuminant, standard);
    }

    @Override
    public double component(LabComponents which) {
        switch (which) {
            case L:
                return l;
            case A:
                return a;
            case B:
                return b;
            default:
                throw new AssertionError(which);
        }
    }

    @Override
    public LabColor withComponent(LabComponents component, double value) {
        switch (component) {
            case L:
                return new LabColor(value, a, b, illuminant, standard);
            case A:
                return new LabColor(l, value, b, illuminant, standard);
            case B:
                return new LabColor(l, a, value, illuminant, standard);
            default:
                throw new AssertionError(component);
        }
    }

    @Override
    public Class<LabComponents> componentType() {
        return LabComponents.class;
    }

    public Illuminant illuminant() {
        return illuminant == null ? Illuminant.getDefault() : illuminant;
    }

    public Standard standard() {
        return standard == null ? Standard.CIE_1964 : standard;
    }

    public LabColor withL(double newL) {
        return new LabColor(newL, a, b);
    }

    public LabColor withA(double newA) {
        return new LabColor(l, newA, b);
    }

    public LabColor withB(double newB) {
        return new LabColor(l, a, newB);
    }

    @Override
    public LabColor combine(LabColor other, DoubleBinaryOperator op) {
        return new LabColor(
                op.applyAsDouble(l, other.l),
                op.applyAsDouble(a, other.a),
                op.applyAsDouble(b, other.b),
                illuminant,
                standard);
    }

    @Override
    public LabColor combine(LabColor other, Function<LabComponents, DoubleBinaryOperator> opFactory) {
        return new LabColor(
                opFactory.apply(L).applyAsDouble(l, other.l),
                opFactory.apply(A).applyAsDouble(a, other.a),
                opFactory.apply(B).applyAsDouble(b, other.b),
                illuminant,
                standard
        );
    }

    public LabColor inverse() {
        return new LabColor(L.inverse(l), A.inverse(a), B.inverse(b), illuminant, standard);
    }

    public XyzColor toXyz() {
        double[] xyz = new double[3];
        labToXyz(l, a, b, xyz, illuminant == null
                ? Illuminant.getDefault()
                : illuminant, standard == null
                        ? Standard.CIE_1964
                        : standard);
        return new XyzColor(xyz[0], xyz[1], xyz[2]);
    }

    @Override
    public Color toColor() {
        int[] rgb = new int[3];
        Conversions.labToRgb(l, a, b, rgb,
                illuminant == null
                        ? Illuminant.getDefault()
                        : illuminant,
                standard == null
                        ? Standard.CIE_1964
                        : standard);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    @Override
    public String toString() {
        return "LAB(" + l + ", " + a + ": " + b + ")";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.l)
                ^ (Double.doubleToLongBits(this.l) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.a)
                ^ (Double.doubleToLongBits(this.a) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.b)
                ^ (Double.doubleToLongBits(this.b) >>> 32));
        hash = 83 * hash + illuminant().hashCode();
        hash = 83 * hash + standard().hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || obj.getClass() != LabColor.class) {
            return false;
        }
        final LabColor other = (LabColor) obj;
        if (Double.doubleToLongBits(this.l) != Double.doubleToLongBits(other.l)) {
            return false;
        }
        if (Double.doubleToLongBits(this.a) != Double.doubleToLongBits(other.a)) {
            return false;
        }
        if (Double.doubleToLongBits(this.b) != Double.doubleToLongBits(other.b)) {
            return false;
        }
        if (!this.illuminant().equals(other.illuminant())) {
            return false;
        }
        return this.standard() == other.standard();
    }

    @Override
    public double[] components() {
        return new double[]{l, a, b};
    }
}
