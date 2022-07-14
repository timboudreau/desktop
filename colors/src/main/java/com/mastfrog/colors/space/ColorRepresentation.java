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

import com.mastfrog.colors.space.ColorRepresentation.ColorComponent;
import java.awt.Color;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

/**
 * Interface for objects which represent a single color in various color-spaces.
 *
 * @author Tim Boudreau
 * @param <C> The type of this
 * @param <Components> The component type
 * @since 2.8.3.1
 */
public interface ColorRepresentation<C extends ColorRepresentation<C, Components>, Components extends Enum<Components> & ColorComponent> {

    /**
     * Removes the specified fraction of the value of component a, and adds the
     * same fraction to the value of b.
     *
     * @param amount A fraction
     * @param from The first component
     * @param to The second component
     * @return A new instance
     */
    @SuppressWarnings("unchecked")
    default C redistributeValues(double amount, Components from, Components to) {
        if (from == to) {
            return (C) this;
        }
        double newA = from.range().clamp(component(from) - from.fraction(amount));
        double newB = to.range().clamp(component(to) + to.fraction(amount));
        return withComponent(from, newA).withComponent(to, newB);
    }

    /**
     * Get the value of a component as a fraction from zero to one.
     *
     * @param comp A component
     * @return A fractional value between 0.0 and 1.0
     */
    default double fraction(Components comp) {
        return comp.fraction(component(comp));
    }

    /**
     * Get the set of components as an array.
     *
     * @return Some components
     */
    double[] components();

    /**
     * Convert to a {@link java.awt.Color}.
     *
     * @return A color
     */
    Color toColor();

    /**
     * Get a particular color component.
     *
     * @param which The component to retrieve
     * @return A double value for that compoennt
     */
    double component(Components which);

    /**
     * Create a copy of this color representation, replacing the given component
     * with the passed value.
     *
     * @param component A component
     * @param value A new value
     * @return a copy of this, or optionally, this, if the values are the same
     */
    C withComponent(Components component, double value);

    /**
     * Get the enum type for components of this color.
     *
     * @return a type
     */
    Class<Components> componentType();

    /**
     * Combine this color and another, using the passed operator for all
     * components.
     *
     * @param other Another color
     * @param op An operator to combine components
     * @return A new color
     */
    C combine(C other, DoubleBinaryOperator op);

    /**
     * Combine two colors using per-component conversion.
     *
     * @param other Another color
     * @param opFactory A function which produces the conversion function for
     * each component
     * @return A color
     */
    C combine(C other, Function<Components, DoubleBinaryOperator> opFactory);

    /**
     * Invert all of the component values in this color - the exact effect of
     * this depends on the color space.
     *
     * @return A color
     */
    @SuppressWarnings("unchecked")
    default C inverse() {
        Components[] all = componentType().getEnumConstants();
        double[] comps = components();
        assert comps.length == all.length;
        C result = (C) this;
        for (int i = 0; i < comps.length; i++) {
            double val = all[i].inverse(comps[i]);
            result = withComponent(all[i], val);
        }
        return result;
    }

    /**
     * Convert to the CIE-XYZ intermediate color space.
     *
     * @return An XyzColor
     */
    default XyzColor toXyz() {
        return new XyzColor(toColor());
    }

    /**
     * A color component.
     */
    public interface ColorComponent {

        /**
         * The ordinal and component position of this component in a component
         * array.
         *
         * @return an ordinal
         */
        int ordinal();

        /**
         * Get the value of this component in a color.
         *
         * @param color A color
         * @return a value
         */
        double valueIn(Color color);

        default double inverse(double value) {
            return convert(1D - fraction(value));
        }

        /**
         * Get the expected range of values for this component; values outside
         * the range may not represent hard-failure, simply once that may
         * convert in unexpected ways or are not representable in all color
         * spaces.
         *
         * @return A component range
         */
        default ComponentRange range() {
            return ComponentRange.UNBOUNDED;
        }

        /**
         * Get the middle value in the range of possible values.
         *
         * @return A value
         */
        default double middle() {
            ComponentRange rng = range();
            if (rng == ComponentRange.UNBOUNDED) {
                return 0;
            }
            return rng.middle();
        }

        /**
         * Get the fraction between minimum and maximum values of this component
         * that the passed value represents.
         *
         * @param val A value
         * @return A fraction
         */
        default double fraction(double val) {
            ComponentRange rng = range();
            if (rng == ComponentRange.UNBOUNDED) {
                return 1;
            }
            return rng.fraction(val);
        }

        /**
         * Convert a fractional value between 0 and 1 to a value representing
         * that proportion of distance across the range of this component.
         *
         * @param fraction A fraction
         * @return A value
         */
        default double convert(double fraction) {
            ComponentRange rng = range();
            if (rng == ComponentRange.UNBOUNDED) {
                throw new UnsupportedOperationException();
            }
            fraction = max(0, min(1, fraction));
            return rng.proportion(fraction);
        }
    }

    /**
     * A range of possible values for a color component.
     */
    public static final class ComponentRange {

        public static final ComponentRange UNBOUNDED
                = new ComponentRange(Double.MIN_VALUE, Double.MAX_VALUE);
        public static final ComponentRange FRACTIONAL
                = new ComponentRange(0, 1);
        public final double low;
        public final double high;

        public ComponentRange(double low, double high) {
            assert low < high;
            this.low = low;
            this.high = high;
        }

        public double clamp(double val) {
            if (val < low) {
                val = low;
            } else if (val > high) {
                return high;
            }
            return val;
        }

        public double fraction(double val) {
            val += low;
            return val / size();
        }

        public double middle() {
            return (size() / 2) + low;
        }

        public double proportion(double fraction) {
            return low + (size() * fraction);
        }

        public boolean contains(double value) {
            return value >= low && value <= high;
        }

        public double size() {
            return high - low;
        }

        public String toString() {
            return low + ":" + high;
        }
    }
}
