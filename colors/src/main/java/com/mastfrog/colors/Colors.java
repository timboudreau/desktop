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
package com.mastfrog.colors;

import static com.mastfrog.colors.GradientUtils.colorToString;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * Supports creating <code>Supplier&lt;Color&gt;</code> instances that can be
 * chained to perform complex transforms on colors. Also supports using
 * UIManagaer colors as a basis for those transforms. A
 * <code>ColorSupplier</code> has various default methods on it that allow you
 * to perform these transforms.
 *
 * @author Tim Boudreau
 */
public final class Colors {

    private Colors() {
        throw new AssertionError();
    }

    /**
     * Create a ColorSupplier for a fixed color.
     *
     * @param color A color
     * @return A color supplier
     */
    public static ColorSupplier fixed(Color color) {
        return new FixedColorSupplier(color);
    }

    /**
     * Create a Color supplier which will always return a non-null color, using
     * a fallback if none is present in UIManager.
     *
     * @param fallback The fallback color if no color exists in UIManager for
     * any of the names.
     *
     * @param names Color names in order of preference - since different look
     * and feels may have different names, more than one may be needed.
     * @return A color supplier
     */
    public static ColorSupplier fromUIManager(Color fallback, String... names) {
        return new UIManagerColor(names, fallback).cache();
    }

    /**
     * Fetch a color, preferentially returning a value from UIManager if
     * present, and none is present, using the first color passed if the boolean
     * supplier returns true, and the second if it returns false. This allows
     * one to implement cases such as "If the text color is near-black, use this
     * light fallback background color, otherwise use this other dark
     * background".
     *
     * @param ifTruefFallabck The color to use if the test returns true and no
     * named color was found in UIManager
     * @param supp A test
     * @param ifFalseFallback The color to use if the test returns false and no
     * named color was found in UIManager
     * @param names Names of colors to look for in UIManager, which should be
     * used in preference to either of the passed colors if present
     * @return A color supplier
     */
    public static ColorSupplier fromUIManager(Color ifTruefFallabck, BooleanSupplier supp, Color ifFalseFallback, String... names) {
        ColorSupplier fallback = Colors.fixed(ifTruefFallabck).unless(supp, Colors.fixed(ifFalseFallback));
        return new UIManagerColor(names, fallback).cache();
    }

    /**
     * Create a Color supplier which will always return a non-null color, using
     * a fallback if none is present in UIManager.
     *
     * @param fallback The fallback color if no color exists in UIManager for
     * any of the names.
     *
     * @param names Color names in order of preference - since different look
     * and feels may have different names, more than one may be needed.
     * @return A color supplier
     */
    public static ColorSupplier fromUIManager(Supplier<Color> fallback, String... names) {
        return new UIManagerColor(names, fallback).cache();
    }

    /**
     * Use one or another color supplier based on the passed test, but
     * preferring colors in the passed array of names, from UIManager, if such
     * exist.
     *
     * @param ifTrue The fallback color to use if the test returns true
     * @param test The test
     * @param ifFalse The fallback color to use if the test returns false
     * @param names A list of color key names to look for in
     * UIManager.getColor()
     * @return A color supplier
     */
    public static ColorSupplier choiceOf(Color ifTrue, BooleanSupplier test, Color ifFalse, String... names) {
        ColorSupplier fallback = Colors.fixed(ifTrue).unless(test, Colors.fixed(ifFalse));
        return new MostSaturatedOf(fallback, names, 1);
    }

    /**
     * Get the most saturated of a set of colors that may or may not be
     * available from UIManager.get(), using the fallback color if none are
     * found.
     *
     * @param fallback A fallback color
     * @param names UIManager color keys
     * @return A color supplier
     */
    public static ColorSupplier mostSaturatedOf(Color fallback, String... names) {
        return new MostSaturatedOf(fallback, names, 1);
    }

    /**
     * Use one or another color supplier based on the passed test, but
     * preferring the brightest of the colors in the passed array of names, from
     * UIManager, if such exist.
     *
     * @param ifTrue The fallback color to use if the test returns true
     * @param test The test
     * @param ifFalse The fallback color to use if the test returns false
     * @param names A list of color key names to look for in
     * UIManager.getColor()
     * @return A color supplier
     */
    public static ColorSupplier brightestOf(Color ifTrue, BooleanSupplier test, Color ifFalse, String... names) {
        ColorSupplier fallback = Colors.fixed(ifTrue).unless(test, Colors.fixed(ifFalse));
        return new MostSaturatedOf(fallback, names, 0);
    }

    /**
     * Pick the brightest color of a list of colors to retrieve from UIManager,
     * using the fallback if none are present.
     *
     * @param fallback The falback color
     * @param names A list of UIManager color keys
     * @return A color supplier
     */
    public static ColorSupplier brightestOf(Color fallback, String... names) {
        return new MostSaturatedOf(fallback, names, 0);
    }

    /**
     * Pick the brightest or darkest color of a list of colors to retrieve from
     * UIManager, using the fallback if none are present.
     *
     * @param fallback The falback color
     * @param darkLight A boolean supplier which determines whether the
     * brightest (true) or darkest (false) should be chosen
     * @param names A list of UIManager color keys
     * @return A color supplier
     */
    public static ColorSupplier brightestOrDarkestOf(Color fallback,
            BooleanSupplier darkLight, String... names) {
        return new MostSaturatedOf(new FixedColorSupplier(fallback), names, 0, darkLight);
    }

    /**
     * Take a Supplier&lt;Color&gt; and, if it is not already one, return a
     * <code>ColorSupplier</code> instance that wraps it.
     *
     * @param supp A supplier
     * @return A color supplier
     */
    public static ColorSupplier toUIColorSupplier(Supplier<Color> supp) {
        return Colors.supplier(supp);
    }

    /**
     * For animating color transforms, synthesize a color at some point between
     * the two passed colors, using the tick and of values to compute a
     * percentage for how much of each color is used.
     *
     * @param a The first color
     * @param b The second color
     * @param tick The current animation tick
     * @param of The total number of animation ticks
     * @return A color
     */
    public static Color between(Color a, Color b, float tick, float of) {
        float pct = tick / of;
        return between(a, b, pct);
    }

    static class Midpoint implements ColorSupplier {

        private final ColorSupplier a;
        private final ColorSupplier b;

        Midpoint(ColorSupplier a, ColorSupplier b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public Color get() {
            return between(a.get(), b.get(), 0.5f, 1f);
        }
    }

    /**
     * Synthesize a color that is the passed percentage (float between 0 and 1)
     * of each.
     *
     * @param a The first color
     * @param b The second color
     * @param percentage The percentage
     * @return The color
     */
    public static Color between(Color a, Color b, float percentage) {
        if (percentage <= 0F) {
            return a;
        } else if (percentage >= 1F) {
            return b;
        }
        int ar = a.getRed();
        int ag = a.getGreen();
        int ab = a.getBlue();
        int aa = a.getAlpha();
        int br = b.getRed();
        int bg = b.getGreen();
        int bb = b.getBlue();
        int ba = b.getAlpha();
        return new Color(spread(ar, br, percentage), spread(ag, bg, percentage),
                spread(ab, bb, percentage), spread(aa, ba, percentage));
    }

    private static int spread(int aVal, int bVal, float percentage) {
        if (aVal == bVal) {
            return aVal;
        }
        int dist = (int) ((bVal - aVal) * percentage);
        return Math.max(0, Math.min(255, aVal + dist));
    }

    interface FloatSupplier {

        public float getAsFloat();
    }

    static class NC extends Color implements Comparable<NC> {

        private final String nm;

        NC(String nm, Color c) {
            super(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            this.nm = nm;
        }

        @Override
        public String toString() {
            return nm + ": \t" + GradientUtils.colorToString(this) + " sat " + saturation()
                    + " bri " + brightness();
        }

        private float saturation() {
            float[] f = new float[3];
            Color.RGBtoHSB(getRed(), getGreen(), getBlue(), f);
            return f[1];
        }

        private float brightness() {
            float[] f = new float[3];
            Color.RGBtoHSB(getRed(), getGreen(), getBlue(), f);
            return f[2];
        }

        @Override
        public int compareTo(NC o) {
            float a = saturation();
            float b = o.saturation();
            if (a == b) {
                return nm.compareToIgnoreCase(o.nm);
            }
            return a > b ? -1 : a < b ? 1 : 0;
        }
    }

    /**
     * Returns a boolean supplier which will determine (as best it can, but it's
     * fairly reliable) whether the current UIManager look and feel is a dark or
     * light theme, using the look and feel id and/or comparing text and text
     * background and control and control background colors.
     *
     * @return A boolean supplier
     */
    public static BooleanSupplier darkTheme() {
        return new DarkThemeDetector();
    }

    /**
     * Returns a supplier the returns the inverse of <code>darkTheme()</code>.
     *
     * @return A boolean supplier
     */
    public static BooleanSupplier lightTheme() {
        BooleanSupplier supp = darkTheme();
        return () -> !supp.getAsBoolean();
    }

    static class DarkThemeDetector implements BooleanSupplier {

        int value;

        @Override
        public boolean getAsBoolean() {
            if (value != 0) {
                return value == 1;
            }

            if ("Darcula".equals(UIManager.getLookAndFeel().getID())) {
                value = 1;
                return true;
            }

            Color col = UIManager.getColor("textText");
            if (col != null && Color.BLACK.equals(col)) {
                value = -1;
                return false;
            } else if (col != null && Color.WHITE.equals(col)) {
                value = 1;
                return true;
            }
            ColorSupplier a = Colors.fromUIManager(Color.RED, "textText", "Label.text", "activeCaption", "Menu.foreground");
            ColorSupplier b = Colors.fromUIManager(Color.RED, "text", "textBackground", "Table.textForeground");

            Color aCol = a.get();
            Color bCol = b.get();
            if (!Color.RED.equals(aCol) && !Color.RED.equals(bCol) && !aCol.equals(bCol)) {
                if (isBrighter(aCol, bCol)) {
                    value = -1;
                } else {
                    value = 1;
                }
                return value == 1;
            }
            Color fallback = new JTextField().getBackground();
            if (fallback != null && !Color.GRAY.equals(fallback)) {
                value = Colors.isBrighter(fallback, Color.GRAY) ? 1 : -1;
                return value == 1;
            }
            return false;
        }
    }

    static final class MostSaturatedOf implements ColorSupplier, Comparator<Color> {

        private final Supplier<Color> fallback;
        private final String[] names;
        private int component;
        private final BooleanSupplier supp;

        MostSaturatedOf(Color fallback, String[] names, int component) {
            this(new FixedColorSupplier(fallback), names, component);
            this.component = component;
        }

        MostSaturatedOf(Supplier<Color> fallback, String[] names, int component, BooleanSupplier supp) {
            this.fallback = fallback;
            this.names = names;
            this.component = component;
            this.supp = supp;

        }

        MostSaturatedOf(Supplier<Color> fallback, String[] names, int component) {
            this(fallback, names, component, null);
        }

        @Override
        public Color get() {
            List<Color> all = new ArrayList<>(names.length);
            for (String nm : names) {
                Color c = UIManager.getColor(nm);
                if (c != null) {
                    all.add(c);
                }
            }
            if (all.isEmpty()) {
                return fallback.get();
            }
            Collections.sort(all, this);
            if (supp == null || !supp.getAsBoolean()) {
                return all.get(0);
            } else {
                return all.get(all.size() - 1);
            }
        }

        @Override
        public int compare(Color o1, Color o2) {
            float[] ahsb = new float[3];
            float[] bhsb = new float[3];
            Color.RGBtoHSB(o1.getRed(), o1.getGreen(), o1.getBlue(), ahsb);
            Color.RGBtoHSB(o2.getRed(), o2.getGreen(), o2.getBlue(), bhsb);
            if (Arrays.equals(ahsb, bhsb)) {
                return 0;
            }
            float[] best;
            if (ahsb[component] > bhsb[component]) {
                // a is best
                if (ahsb[0] < 0.25f && bhsb[0] >= 0.25f) {
                    if (bhsb[component] > 0.1f) {
                        best = bhsb;
                    } else {
                        best = ahsb;
                    }
                } else {
                    best = ahsb;
                }
            } else {
                if (bhsb[0] < 0.25f && ahsb[0] >= 0.25f) {
                    if (ahsb[component] > 0.1f) {
                        best = ahsb;
                    } else {
                        best = bhsb;
                    }
                } else {
                    best = ahsb;
                }
            }
//            System.out.println("Best of " + o1 + " " + o2 + " is " + (best == ahsb ? o1 : o2));
            return best == ahsb ? -1 : 1;
        }
    }

    static final class HSBChanger implements ColorSupplier {

        private final ColorSupplier orig;
        private final FloatSupplier val;
        private final int component;

        HSBChanger(ColorSupplier orig, FloatSupplier val, int component) {
            this.orig = orig;
            this.val = val;
            this.component = component;
        }

        HSBChanger(ColorSupplier orig, float val, int component) {
            this(orig, () -> val, component);
        }

        @Override
        public Color get() {
            Color value = orig.get();
            float[] hsb = new float[3];
            Color.RGBtoHSB(value.getRed(), value.getGreen(), value.getBlue(), hsb);
            hsb[component] = this.val.getAsFloat();
            Color result = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
            if (value.getAlpha() != 255) {
                result = new Color(result.getRed(), result.getGreen(), result.getBlue(), value.getAlpha());
            }
            return result;
        }
    }

    static final class Tinter implements ColorSupplier {

        private final ColorSupplier orig;
        private final Supplier<Color> tintSupplier;
        private final FloatSupplier percentage;

        Tinter(ColorSupplier orig, Supplier<Color> tintSupplier, float pct) {
            this(orig, tintSupplier, () -> pct);
        }

        Tinter(ColorSupplier orig, Supplier<Color> tintSupplier, FloatSupplier percentage) {
            this.orig = orig;
            this.tintSupplier = tintSupplier;
            this.percentage = percentage;
        }

        @Override
        public Color get() {
            Color c = orig.get();
            Color tint = tintSupplier.get();
            float[] cHsb = new float[3];
            float[] tintHsb = new float[3];
            Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), cHsb);
            Color.RGBtoHSB(tint.getRed(), tint.getGreen(), tint.getBlue(), tintHsb);
            float pct = percentage.getAsFloat();
            cHsb[0] = tintHsb[0];
            float sat = tintHsb[1] * pct;
            if (cHsb[1] < sat) {
                cHsb[1] = sat;
            }
            Color result = new Color(Color.HSBtoRGB(cHsb[0], cHsb[1], cHsb[2]));
            if (c.getAlpha() != 255) {
                result = new Color(result.getRed(), result.getGreen(), result.getBlue(), c.getAlpha());
            }
            return result;
        }
    }

    static final class HSBAdjuster implements ColorSupplier {

        private final ColorSupplier orig;
        private final FloatSupplier val;
        private final int component;

        HSBAdjuster(ColorSupplier orig, FloatSupplier val, int component) {
            this.orig = orig;
            this.val = val;
            this.component = component;
        }

        HSBAdjuster(ColorSupplier orig, float val, int component) {
            this(orig, () -> val, component);
        }

        @Override
        public Color get() {
            Color value = orig.get();
            float[] hsb = new float[3];
            Color.RGBtoHSB(value.getRed(), value.getGreen(), value.getBlue(), hsb);
            hsb[component] = Math.max(0f, Math.min(1f, hsb[component] + this.val.getAsFloat()));
            Color result = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
            if (value.getAlpha() != 255) {
                result = new Color(result.getRed(), result.getGreen(), result.getBlue(), value.getAlpha());
            }
            return result;
        }
    }

    static final class HSBLimit implements ColorSupplier {

        private final ColorSupplier orig;
        private final FloatSupplier val;
        private final int component;
        private final boolean noLessThan;

        HSBLimit(ColorSupplier orig, FloatSupplier val, int component, boolean noLessThan) {
            this.orig = orig;
            this.val = val;
            this.component = component;
            this.noLessThan = noLessThan;
        }

        HSBLimit(ColorSupplier orig, float val, int component, boolean noLessThan) {
            this(orig, () -> val, component, noLessThan);
        }

        @Override
        public Color get() {
            Color value = orig.get();
            float[] hsb = new float[3];
            Color.RGBtoHSB(value.getRed(), value.getGreen(), value.getBlue(), hsb);
            float v = val.getAsFloat();
            boolean changed = false;
            if (noLessThan) {
                if (hsb[component] < v) {
                    hsb[component] = v;
                    changed = true;
                }
            } else {
                if (hsb[component] > v) {
                    hsb[component] = v;
                    changed = true;
                }
            }
            if (!changed) {
                return value;
            }
//            hsb[component] = Math.max(0f, Math.min(1f, hsb[component] + this.val.getAsFloat()));
            Color result = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
            if (value.getAlpha() != 255) {
                result = new Color(result.getRed(), result.getGreen(), result.getBlue(), value.getAlpha());
            }
            return result;
        }
    }

    static final class WithRGB implements ColorSupplier {

        private final int component;
        private final Supplier<Color> orig;
        private final int value;

        WithRGB(int component, Supplier<Color> orig, int value) {
            assert component >= 0 && component < 4;
            this.component = component;
            this.orig = orig;
            this.value = value;
        }

        @Override
        public Color get() {
            Color c = orig.get();
            switch (component) {
                case 0:
                    return new Color(value, c.getGreen(), c.getBlue(), c.getAlpha());
                case 1:
                    return new Color(c.getRed(), value, c.getBlue(), c.getAlpha());
                case 2:
                    return new Color(c.getRed(), c.getGreen(), value, c.getAlpha());
                case 3:
                    return new Color(c.getRed(), c.getGreen(), c.getBlue(), value);
                default:
                    throw new AssertionError(component);
            }
        }
    }

    static final class UIManagerColor implements ColorSupplier {

        private final String[] names;
        private final Supplier<Color> fallback;

        UIManagerColor(String[] names, Color fallback) {
            this(names, new FixedColorSupplier(fallback));
        }

        UIManagerColor(String[] names, Supplier<Color> fallback) {
            this.names = names;
            this.fallback = fallback;
        }

        @Override
        public Color get() {
            for (String n : names) {
                Color result = UIManager.getColor(n);
                if (result != null) {
                    return result;
                }
            }
            return fallback.get();
        }
    }

    static final class CachingColorSupplier implements ColorSupplier {

        private Supplier<Color> color;
        private Color value;

        CachingColorSupplier(Supplier<Color> color) {
            this.color = color;
        }

        @Override
        public Color get() {
            if (value == null) {
                value = color.get();
                if (value != null) {
                    color = null;
                }
            }
            return value;
        }

        @Override
        public String toString() {
            return colorToString(get()) + " sat=" + saturation() + " bri " + brightness();
        }
    }

    static final class HsbExtractor implements FloatSupplier {

        private final Supplier<Color> orig;
        private final int component;

        HsbExtractor(Supplier<Color> orig, int component) {
            this.orig = orig;
            this.component = component;
        }

        @Override
        public float getAsFloat() {
            Color c = orig.get();
            float[] hsb = new float[3];
            Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
            return hsb[component];
        }
    }

    static final class FixedColorSupplier implements ColorSupplier {

        private final Color color;

        FixedColorSupplier(Color color) {
            this.color = color;
        }

        @Override
        public Color get() {
            return color;
        }
    }

    static final class ContrastingColorSupplier implements ColorSupplier {

        private final Supplier<Color> orig;

        ContrastingColorSupplier(Supplier<Color> orig) {
            this.orig = orig;
        }

        @Override
        public Color get() {
            Color c = orig.get();
            float[] hsb = new float[3];
            Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
            float bri = hsb[2];
            if (bri > 0.45f && bri < 0.575f) {
                if (bri > 0.45f) {
                    hsb[2] = 0.05f;
                } else {
                    hsb[2] = 0.95f;
                }
                hsb[0] = hsb[0] < 0.5f ? 1f - hsb[0] : 0.5f - hsb[0];
            } else {
                hsb[2] = hsb[2] < 0.5f ? 1f - hsb[2] : 0.5f - hsb[2];
            }
            Color result = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
            if (c.getAlpha() != 255) {
                result = new Color(result.getRed(), result.getGreen(), result.getBlue(), c.getAlpha());
            }
            return result;
        }

    }

    /**
     * Determine if the first color is brighter than the second.
     *
     * @param toTest The first color
     * @param than The color to compare it with
     * @return true or false
     */
    public static boolean isBrighter(Color toTest, Color than) {
        HsbExtractor ae = new HsbExtractor(new FixedColorSupplier(than), 2);
        HsbExtractor be = new HsbExtractor(new FixedColorSupplier(toTest), 2);
        return ae.getAsFloat() > be.getAsFloat();
    }

    /**
     * Determine if the first color less saturated than the second.
     *
     * @param toTest The first color
     * @param than The color to compare it with
     * @return true or false
     */
    public static boolean isLessSaturated(Color toTest, Color than) {
        return !isMoreSaturated(toTest, than);
    }

    /**
     * Determine if the first color is more saturatedthan the second.
     *
     * @param toTest The first color
     * @param than The color to compare it with
     * @return true or false
     */
    public static boolean isMoreSaturated(Color toTest, Color than) {
        HsbExtractor ae = new HsbExtractor(new FixedColorSupplier(than), 1);
        HsbExtractor be = new HsbExtractor(new FixedColorSupplier(toTest), 1);
        return ae.getAsFloat() > be.getAsFloat();
    }

    /**
     * Determine if the first color is darker than the second.
     *
     * @param toTest The first color
     * @param than The color to compare it with
     * @return true or false
     */
    public static boolean isDarker(Color toTest, Color than) {
        return !isBrighter(toTest, than);
    }

    static final class RotateHueSupplier implements ColorSupplier {

        private final Supplier<Color> orig;
        private final float by;

        RotateHueSupplier(Supplier<Color> orig, float by) {
            this.orig = orig;
            this.by = by;
        }

        @Override
        public Color get() {
            Color c = orig.get();
            float[] hsb = new float[3];
            Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
            hsb[0] = (hsb[0] + by);
            if (hsb[0] > 1f) {
                hsb[0] -= 1f;
            }
            Color result = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
            if (c.getAlpha() != 255) {
                result = new Color(result.getRed(), result.getGreen(), result.getBlue(), c.getAlpha());
            }
            return result;
        }
    }

    static ColorSupplier supplier(Supplier<Color> supp) {
        return supp instanceof ColorSupplier ? (ColorSupplier) supp : supp::get;
    }

    static class InvertedSupplier implements ColorSupplier {

        private final ColorSupplier delegate;
        private final boolean invertAlpha;

        InvertedSupplier(boolean invertAlpha, ColorSupplier delegate) {
            this.invertAlpha = invertAlpha;
            this.delegate = delegate;
        }

        @Override
        public Color get() {
            Color result = delegate.get();
            return new Color(255 - result.getRed(), 255 - result.getGreen(), 255 - result.getBlue(),
                    invertAlpha ? 255 - result.getAlpha() : result.getAlpha());
        }

        @Override
        public ColorSupplier invertRGB() {
            if (!invertAlpha) {
                return delegate;
            }
            return ColorSupplier.super.invertRGB(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ColorSupplier invertRGB(boolean invertAlpha) {
            if (invertAlpha == this.invertAlpha) {
                return delegate;
            }
            return ColorSupplier.super.invertRGB(invertAlpha);
        }
    }

    /**
     * An extension to the JDK's <code>Supplier&lt;Color&gt;</code> with default
     * methods for performing extensive transforms on a color to match it with
     * other colors, or otherwise alter it.
     */
    public interface ColorSupplier extends Supplier<Color> {

        /**
         * Simply returns a supplier where the red, green and blue values are
         * subtracted from 255.
         *
         * @param invertAlpha If true, also invert the alpha value
         * @return A color supplier
         */
        default ColorSupplier invertRGB(boolean invertAlpha) {
            return new InvertedSupplier(invertAlpha, this);
        }

        /**
         * Simply returns a supplier where the red, green and blue values are
         * subtracted from 255. Alpha is the same as the original color
         * supplier's value.
         *
         * @return A color supplier
         */
        default ColorSupplier invertRGB() {
            return new InvertedSupplier(false, this);
        }

        /**
         * Find the midpoint color between two colors.
         *
         * @param other A supplier of another color
         * @return A color supplier
         */
        default ColorSupplier midpoint(ColorSupplier other) {
            if (other == this) {
                return this;
            }
            return new Midpoint(this, other);
        }

        /**
         * Returns a color whose saturation and brightness match this color, but
         * whose hue is transformed by that of the passed supplier.
         *
         * @param tintSupplier Supplier of another color
         * @param percentageEffect The amount by which the tint from the other
         * color should be applied to this one
         * @return A color supplier
         */
        default ColorSupplier withTintFrom(Supplier<Color> tintSupplier, float percentageEffect) {
            return new Tinter(this, tintSupplier, percentageEffect);
        }

        /**
         * Constrain the original color to a color whose brightness may be
         * brighter than that of the passed one, but not less bright than it.
         *
         * @param other Another color
         * @return A color supplier
         */
        default ColorSupplier withBrightnessNoLessThanThatOf(Supplier<Color> other) {
            // HSBLimit
            ColorSupplier supp = supplier(other);
            return new HSBLimit(this, supp::brightness, 2, true);
        }

        /**
         * Constrain the original color to a color whose brightness may be less
         * than that of the passed one, but not greater than it.
         *
         * @param other Another color
         * @return A color supplier
         */
        default ColorSupplier withBrightnessNoGreaterThanThatOf(Supplier<Color> other) {
            // HSBLimit
            ColorSupplier supp = supplier(other);
            return new HSBLimit(this, supp::brightness, 2, false);
        }

        /**
         * Constrain the brightness to be no greater than that of the passed
         * float value between 0 and 1.
         *
         * @param val A float between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier withBrightnessNoGreaterThan(float val) {
            return new HSBLimit(this, val, 2, false);
        }

        /**
         * Constrain the brightness to be greater or equal to than that of the
         * passed float value between 0 and 1.
         *
         * @param val A float between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier withBrightnesNoLessThan(float val) {
            return new HSBLimit(this, val, 2, true);
        }

        /**
         * Constrain the saturation to be no greater than that of the passed
         * float value between 0 and 1.
         *
         * @param val A float between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier withSaturationNoGreaterThan(float val) {
            return new HSBLimit(this, val, 1, false);
        }

        /**
         * Constrain the saturation to be greater or equal to than that of the
         * passed float value between 0 and 1.
         *
         * @param val A float between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier withSaturationNoLessThan(float val) {
            return new HSBLimit(this, val, 1, true);
        }

        /**
         * Get an HSB value from this color - 0 = hue, 1 = saturation, 2 =
         * brightness.
         *
         * @param which Which value to retrieve
         * @return A float between 0 and 1
         */
        default float component(int which) {
            Color c = get();
            float[] vals = new float[3];
            Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), vals);
            return vals[which];
        }

        /**
         * Get the HSB brightness.
         *
         * @return A float between 0 and 1
         */
        default float brightness() {
            return component(2);
        }

        /**
         * Get the HSB hue.
         *
         * @return A float between 0 and 1
         */
        default float hue() {
            return component(0);
        }

        /**
         * Get the HSB saturation.
         *
         * @return A float between 0 and 1
         */
        default float saturation() {
            return component(1);
        }

        /**
         * Darken or lighten the color by some amount, darkening or lightening
         * based on the value from the passed BooleanSupplier at the time it is
         * requested.
         *
         * @param amount The amount to change the brightness by
         * @param supp A supplier that determines whether the brightness should
         * be increased or decreased
         * @return A color supplier
         */
        default ColorSupplier darkenOrLighten(float amount, BooleanSupplier supp) {
            FloatSupplier fs = () -> {
                return supp.getAsBoolean() ? -amount : amount;
            };
            return new HSBAdjuster(this, fs, 2);
        }

        /**
         * Adjust the saturation by the passed amount, increasing or decreasing
         * it based on the value from the passed BooleanSupplier at the time it
         * is requested.
         *
         * @param amount The amount to adjust by (if the result is &lt; 0 or
         * &gt; 1 it will be pinned to 0 or 1.
         * @param supp A test for whether to increase or decrease the saturation
         * @return A color supplier
         */
        default ColorSupplier adjustSaturation(float amount, BooleanSupplier supp) {
            FloatSupplier fs = () -> {
                return supp.getAsBoolean() ? -amount : amount;
            };
            return new HSBAdjuster(this, fs, 1);
        }

        /**
         * Darken the color by the passed amount.
         *
         * @param amt An amount between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier darkenBy(float amt) {
            return new HSBAdjuster(this, -amt, 2);
        }

        /**
         * Brighten the color by the passed amount.
         *
         * @param amt An amount between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier brightenBy(float amt) {
            return new HSBAdjuster(this, amt, 2);
        }

        /**
         * Raise the saturation by the passed amount.
         *
         * @param amt An amount between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier increaseSaturationBy(float amt) {
            return new HSBAdjuster(this, -amt, 1);
        }

        /**
         * lower the saturation by the passed amount.
         *
         * @param amt An amount between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier decreaseSaturationBy(float amt) {
            return new HSBAdjuster(this, amt, 1);
        }

        /**
         * Returns a color supplier that returns this supplier's color or
         * another color supplier's color depending on the result of the passed
         * BooleanSupplier.
         *
         * @param supp A test to decide which color to use
         * @param other Another color supplier
         * @return A color supplier
         */
        default ColorSupplier unless(BooleanSupplier supp, Supplier<Color> other) {
            if (other == this) {
                return this;
            }
            return () -> {
                boolean val = supp.getAsBoolean();
                if (val) {
                    return other.get();
                }
                return ColorSupplier.this.get();
            };
        }

        /**
         * Rotate the hue by some amount.
         *
         * @param val An amount between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier rotatingHueBy(float val) {
            if (val == 0F) {
                return this;
            }
            if (val < -1 || val > 1) {
                throw new IllegalArgumentException("Rotate must be >=-1 and <=1");
            }
            return new RotateHueSupplier(this, val);
        }

        /**
         * Generically create a contrasting color, for purposes of painting text
         * against a background.
         *
         * @return A color supplier
         */
        default ColorSupplier contrasting() {
            return new ContrastingColorSupplier(this);
        }

        /**
         * Returns a BooleanSupplier that answers whether this color is brighter
         * than the passed one.
         *
         * @param than The color to compare with
         * @return A boolean supplier
         */
        default BooleanSupplier isBrighter(Supplier<Color> than) {
            if (than == this) {
                return () -> false;
            }
            int[] result = new int[1];
            return () -> {
                if (result[0] != 0) {
                    return result[0] == 1;
                }
                boolean res = Colors.isBrighter(get(), than.get());
                result[0] = res ? 1 : -1;
                return res;
            };
        }

        /**
         * Returns a BooleanSupplier that answers whether this color is brighter
         * than the passed one.
         *
         * @param than The color to compare with
         * @return A boolean supplier
         */
        default BooleanSupplier isDarker(Supplier<Color> than) {
            int[] result = new int[1];
            return () -> {
                if (result[0] != 0) {
                    return result[0] == 1;
                }
                boolean res = !Colors.isBrighter(get(), than.get());
                result[0] = res ? 1 : -1;
                return res;
            };
        }

        /**
         * Returns a ColorSupplier that returns whichever color is darker, this
         * one or the passed one.
         *
         * @param other Another color supplier
         * @return A color supplier
         */
        default ColorSupplier darkerOf(Supplier<Color> other) {
            return () -> {
                Color a = get();
                Color b = other.get();
                if (Colors.isDarker(a, b)) {
                    return a;
                } else {
                    return b;
                }
            };
        }

        /**
         * Returns a ColorSupplier that returns whichever color is brighter,
         * this one or the passed one.
         *
         * @param other Another color supplier
         * @return A color supplier
         */
        default ColorSupplier brighterOf(Supplier<Color> other) {
            return () -> {
                Color a = get();
                Color b = other.get();
                if (Colors.isBrighter(a, b)) {
                    return a;
                } else {
                    return b;
                }
            };
        }

        /**
         * Returns a ColorSupplier that returns whichever color is less
         * saturated, this one or the passed one.
         *
         * @param other Another color supplier
         * @return A color supplier
         */
        default ColorSupplier leastSaturatedOf(Supplier<Color> other) {
            return () -> {
                Color a = get();
                Color b = other.get();
                if (Colors.isLessSaturated(a, b)) {
                    return a;
                } else {
                    return b;
                }
            };
        }

        /**
         * Returns a ColorSupplier that returns whichever color is more
         * saturated, this one or the passed one.
         *
         * @param other Another color supplier
         * @return A color supplier
         */
        default ColorSupplier mostSaturatedOf(Supplier<Color> other) {
            return () -> {
                Color a = get();
                Color b = other.get();
                if (Colors.isLessSaturated(a, b)) {
                    return a;
                } else {
                    return b;
                }
            };
        }

        /**
         * Returns a ColorSupplier that caches the value returned by this one
         * (forever!), so that the computation required to compute the color
         * value is only done once. Use this only for colors which really, once
         * initialized, will never change for the lifetime of this
         * ColorSupplier. The resulting ColorSupplier also throws away the chain
         * of ColorSuppliers that created it, reducing memory consumption.
         *
         * @return A ColorSupplier
         */
        default ColorSupplier cache() {
            if (this instanceof CachingColorSupplier || this instanceof FixedColorSupplier) {
                return this;
            }
            return new CachingColorSupplier(this);
        }

        /**
         * Returns a ColorSupplier which obtains its hue from the passed one,
         * it's saturation and brightness from this one.
         *
         * @param color Another color
         * @return A color supplier
         */
        default ColorSupplier withHueFrom(Supplier<Color> color) {
            HsbExtractor ext = new HsbExtractor(color, 0);
            return new HSBChanger(this, ext, 0);
        }

        /**
         * Returns a ColorSupplier which obtains its saturation from the passed
         * one, it's hue and brightness from this one.
         *
         * @param color Another color
         * @return A color supplier
         */
        default ColorSupplier withSaturationFrom(Supplier<Color> color) {
            HsbExtractor ext = new HsbExtractor(color, 1);
            return new HSBChanger(this, ext, 1);
        }

        /**
         * Returns a ColorSupplier which obtains its hue from the passed one,
         * it's saturation and brightness from this one.
         *
         * @param color Another color
         * @return A color supplier
         */
        default ColorSupplier withBrightnessFrom(Supplier<Color> color) {
            HsbExtractor ext = new HsbExtractor(color, 2);
            return new HSBChanger(this, ext, 2);
        }

        /**
         * Returns a ColorSupplier which applies the passed alpha value to this
         * color.
         *
         * @param fixedAlpha A fixed alpha value from 0 to 255.
         * @return A color supplier
         */
        default ColorSupplier withAlpha(int fixedAlpha) {
            return new WithRGB(3, this, fixedAlpha);
        }

        /**
         * Returns a ColorSupplier which applies the passed hue to this color.
         *
         * @param hue A hue value between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier withHue(float hue) {
            return new HSBChanger(this, hue, 0);
        }

        /**
         * Returns a ColorSupplier which applies the passed saturation to this
         * color.
         *
         * @param saturation A hue value between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier withSaturation(float saturation) {
            return new HSBChanger(this, saturation, 1);
        }

        /**
         * Returns a ColorSupplier which applies the passed brightness to this
         * color.
         *
         * @param bri A brightness value between 0 and 1
         * @return A color supplier
         */
        default ColorSupplier withBrightness(float bri) {
            return new HSBChanger(this, bri, 2);
        }

        /**
         * Returns a ColorSupplier which applies the passed hue to this color,
         * integer variant.
         *
         * @param hue A hue value between 0 and 255
         * @return A color supplier
         */
        default ColorSupplier withHue(int hue) {
            float f = hue;
            f /= 255f;
            return new HSBChanger(this, f, 0);
        }

        /**
         * Returns a ColorSupplier which applies the passed saturation to this
         * color, integer variant.
         *
         * @param saturation A saturation value between 0 and 255
         * @return A color supplier
         */
        default ColorSupplier withSaturation(int saturation) {
            float f = saturation;
            f /= 255f;
            return new HSBChanger(this, f, 1);
        }

        /**
         * Returns a ColorSupplier which applies the passed brightness to this
         * color, integer variant.
         *
         * @param bri A brightness value between 0 and 255
         * @return A color supplier
         */
        default ColorSupplier withBrightness(int bri) {
            float f = bri;
            f /= 255f;
            return new HSBChanger(this, f, 2);
        }

        /**
         * Returns a ColorSupplier which uses the passed red channel value.
         *
         * @param red The new red channel value
         * @return A color supplier
         */
        default ColorSupplier withRed(int red) {
            return new WithRGB(0, this, red);
        }

        /**
         * Returns a ColorSupplier which uses the passed green channel value.
         *
         * @param green The new green channel value
         * @return A color supplier
         */
        default ColorSupplier withGreen(int green) {
            return new WithRGB(1, this, green);
        }

        /**
         * Returns a ColorSupplier which uses the passed blue channel value.
         *
         * @param blue The new blue channel value
         * @return A color supplier
         */
        default ColorSupplier withBlue(int blue) {
            return new WithRGB(2, this, blue);
        }
    }
}
