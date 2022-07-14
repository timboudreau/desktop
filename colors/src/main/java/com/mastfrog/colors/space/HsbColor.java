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

import static com.mastfrog.colors.space.ColorRepresentation.ComponentRange.FRACTIONAL;
import com.mastfrog.colors.space.HsbColor.HsbComponents;
import static com.mastfrog.colors.space.HsbColor.HsbComponents.BRIGHTNESS;
import static com.mastfrog.colors.space.HsbColor.HsbComponents.HUE;
import static com.mastfrog.colors.space.HsbColor.HsbComponents.SATURATION;
import java.awt.Color;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

/**
 * A first-class color in HSB color space.
 *
 * @author Tim Boudreau
 * @since 2.8.3.1
 */
public final class HsbColor implements ColorRepresentation<HsbColor, HsbComponents> {

    private final double hue;
    private final double saturation;
    private final double brightness;

    public HsbColor(double hue, double saturation, double brightness) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

    public HsbColor(Color color) {
        this(HUE.valueIn(color), SATURATION.valueIn(color), BRIGHTNESS.valueIn(color));
    }

    @Override
    public XyzColor toXyz() {
        double[] comps = new double[3];
        Conversions.hsbToXyz(hue, saturation, brightness, comps);
        return new XyzColor(comps[0], comps[1], comps[2]);
    }

    @Override
    public double[] components() {
        return new double[]{hue, saturation, brightness};
    }

    @Override
    public Color toColor() {
        return new Color(Conversions.hsbToRgb(hue, saturation, brightness));
    }

    @Override
    public double component(HsbComponents which) {
        switch (which) {
            case HUE:
                return hue;
            case SATURATION:
                return saturation;
            case BRIGHTNESS:
                return brightness;
            default:
                throw new AssertionError(which);
        }
    }

    public HsbColor withHue(double hue) {
        return hue == this.hue ? this : new HsbColor(hue, saturation, brightness);
    }

    public HsbColor withSaturation(double saturation) {
        return saturation == this.saturation ? this : new HsbColor(hue, saturation, brightness);
    }

    public HsbColor withBrightness(double brightness) {
        return brightness == this.brightness ? this : new HsbColor(hue, saturation, brightness);
    }

    public double hue() {
        return hue;
    }

    public double saturation() {
        return saturation;
    }

    public double brightness() {
        return brightness;
    }

    @Override
    public Class<HsbComponents> componentType() {
        return HsbComponents.class;
    }

    @Override
    public HsbColor combine(HsbColor other, DoubleBinaryOperator op) {
        return new HsbColor(
                op.applyAsDouble(hue, other.hue),
                op.applyAsDouble(saturation, other.saturation),
                op.applyAsDouble(brightness, other.brightness));
    }

    @Override
    public HsbColor combine(HsbColor other, Function<HsbComponents, DoubleBinaryOperator> opFactory) {
        return new HsbColor(
                opFactory.apply(HUE).applyAsDouble(hue, other.hue),
                opFactory.apply(SATURATION).applyAsDouble(saturation, other.saturation),
                opFactory.apply(BRIGHTNESS).applyAsDouble(brightness, other.brightness)
        );
    }

    @Override
    public HsbColor withComponent(HsbComponents component, double value) {
        switch (component) {
            case HUE:
                return withHue(value);
            case SATURATION:
                return withSaturation(value);
            case BRIGHTNESS:
                return withBrightness(value);
            default:
                throw new AssertionError(component);
        }
    }

    public enum HsbComponents implements ColorRepresentation.ColorComponent {
        HUE,
        SATURATION,
        BRIGHTNESS;

        @Override
        public ComponentRange range() {
            return FRACTIONAL;
        }

        @Override
        public double valueIn(Color color) {
            int rgbMax = max(color.getRed(), max(color.getGreen(), color.getBlue()));
            int rgbMin = min(color.getRed(), min(color.getGreen(), color.getBlue()));
            switch (this) {
                case BRIGHTNESS:
                    return rgbMax / 255D;
                case SATURATION:
                    if (rgbMax == 0) {
                        return 0;
                    }
                    return ((double) (rgbMax - rgbMin)) / ((float) rgbMax);
                case HUE:
                    double r = color.getRed();
                    double g = color.getGreen();
                    double b = color.getBlue();
                    double redc = (rgbMax - r) / (rgbMax - rgbMin);
                    double greenc = ((double) (rgbMax - g)) / (rgbMax - rgbMin);
                    double bluec = (rgbMax - b) / (rgbMax - rgbMin);
                    double hue;
                    if (r == rgbMax) {
                        hue = bluec - greenc;
                    } else if (g == rgbMax) {
                        hue = 2.0D + redc - bluec;
                    } else {
                        hue = 4.0D + greenc - redc;
                    }
                    hue = hue / 6.0D;
                    if (hue < 0) {
                        hue = hue + 1.0D;
                    }
                    return hue;
                default:
                    throw new AssertionError(this);
            }
        }
    }

    @Override
    public String toString() {
        return hue + "," + saturation + "," + brightness;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.hue) ^ (Double.doubleToLongBits(this.hue) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.saturation) ^ (Double.doubleToLongBits(this.saturation) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.brightness) ^ (Double.doubleToLongBits(this.brightness) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HsbColor other = (HsbColor) obj;
        if (Double.doubleToLongBits(this.hue) != Double.doubleToLongBits(other.hue)) {
            return false;
        }
        if (Double.doubleToLongBits(this.saturation) != Double.doubleToLongBits(other.saturation)) {
            return false;
        }
        return Double.doubleToLongBits(this.brightness) == Double.doubleToLongBits(other.brightness);
    }

}
