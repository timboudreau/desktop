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
package com.mastfrog.swing;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.util.HashMap;
import java.util.Map;
import static java.awt.RenderingHints.*;
import java.util.function.Function;

/**
 * Sets of rendering hints commonly used.
 *
 * @author Tim Boudreau
 */
public enum HintSets implements Function<Graphics2D, Graphics2D> {

    /**
     * Makes no changes in hints.
     */
    DEFAULTS(),
    /**
     * Basic antialiasing values for display, without undue slowness.
     */
    DISPLAY(
            pair(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON),
            pair(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON),
            pair(KEY_TEXT_ANTIALIASING, displayTextAntialiasingValue()),
            pair(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY),
            pair(KEY_STROKE_CONTROL, VALUE_STROKE_PURE)
    ),
    /**
     * Fastest, no anti-aliasing, all settings to performance values.
     */
    LOW(
            pair(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF),
            pair(KEY_RENDERING, VALUE_RENDER_SPEED),
            pair(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_OFF),
            pair(KEY_STROKE_CONTROL, VALUE_STROKE_NORMALIZE),
            pair(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_SPEED),
            pair(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_SPEED),
            pair(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_OFF)
    ),
    /**
     * Basic antialiasing for when not drawing text.
     */
    ANTIALIASED(
            pair(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON),
            pair(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY),
            pair(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_SPEED)
    ),
    /**
     * Basic antialiasing with stroke control pure.
     */
    ANTIALIASED_WITH_STROKES(
            pair(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON),
            pair(KEY_STROKE_CONTROL, VALUE_STROKE_PURE),
            pair(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY),
            pair(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_SPEED)
    ),
    /**
     * Text antialiasing.
     */
    DISPLAY_TEXT_ANTIALIASED(
            pair(KEY_TEXT_ANTIALIASING, displayTextAntialiasingValue()),
            pair(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON)),
    /**
     * Image text antialiasing.
     */
    IMAGE_TEXT_ANTIALIASED(
            pair(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON),
            pair(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON)),
    /**
     * Image antialiasing with text antialiasing GASP.
     */
    IMAGE_ANTIALIASED(
            pair(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_GASP),
            pair(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON),
            pair(KEY_STROKE_CONTROL, VALUE_STROKE_PURE),
            pair(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY),
            pair(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC)
    ),
    /**
     * Max quality (slow) with non-lcd antialiasing settings.
     */
    IMAGE_MAX_QUALITY(
            pair(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON),
            pair(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON),
            pair(KEY_RENDERING, VALUE_RENDER_QUALITY),
            pair(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY),
            pair(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY),
            pair(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC),
            pair(KEY_STROKE_CONTROL, VALUE_STROKE_PURE)
    ),
    /**
     * Max quality (slow) with lcd antialiasing settings.
     */
    DISPLAY_MAX_QUALITY(
            pair(KEY_TEXT_ANTIALIASING, displayTextAntialiasingValue()),
            pair(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON),
            pair(KEY_RENDERING, VALUE_RENDER_QUALITY),
            pair(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY),
            pair(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY),
            pair(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC),
            pair(KEY_STROKE_CONTROL, VALUE_STROKE_PURE)
    );

    private final HintPair[] pairs;
    private RenderingHints instance;

    private HintSets(HintPair... pairs) {
        this.pairs = pairs;
    }

    @Override
    public String toString() {
        return name().toLowerCase().replace('_', '-');
    }

    @Override
    public Graphics2D apply(Graphics2D g) {
        for (HintPair p : pairs) {
            p.apply(g);
        }
        return g;
    }

    public Graphics2D replace(Graphics2D g) {
        g.setRenderingHints(hints());
        return g;
    }

    public Map<RenderingHints.Key, Object> contents() {
        Map<RenderingHints.Key, Object> result = new HashMap<>();
        for (HintPair p : pairs) {
            p.apply(result);
        }
        return result;
    }

    public RenderingHints hints() {
        if (instance == null) {
            instance = new RenderingHints(contents());
        }
        return (RenderingHints) instance.clone();
    }

    private static HintPair pair(RenderingHints.Key key, Object val) {
        return new HintPair(key, val);
    }

    private static class HintPair {

        private final RenderingHints.Key key;
        private final Object value;

        public HintPair(RenderingHints.Key key, Object value) {
            this.key = key;
            this.value = value;
        }

        void apply(Map<? super Key, ? super Object> map) {
            map.put(key, value);
        }

        void apply(Graphics2D g) {
            g.setRenderingHint(key, value);
        }
    }

    static Object displayTextAntialiasingValue() {
        // awt.useSystemAAFontSettings=lcd_hrgb
        String sysprop = System.getProperty("awt.useSystemAAFontSettings");
        if (sysprop == null) {
            sysprop = System.getProperty("displayAAs");
        }
        if (sysprop != null) {
            switch (sysprop) {
                case "lcd_hrgb":
                    return VALUE_TEXT_ANTIALIAS_LCD_HRGB;
                case "lcd_vrgb":
                    return VALUE_TEXT_ANTIALIAS_LCD_VRGB;
                case "lcd_hbgr":
                    return VALUE_TEXT_ANTIALIAS_LCD_HBGR;
                case "lcd_vbgr":
                    return VALUE_TEXT_ANTIALIAS_LCD_VBGR;
                case "gasp":
                    return VALUE_TEXT_ANTIALIAS_GASP;
                case "on":
                    return VALUE_TEXT_ANTIALIAS_ON;
                case "off":
                    return VALUE_TEXT_ANTIALIAS_OFF;
            }
        }
        return VALUE_TEXT_ANTIALIAS_LCD_HRGB;
    }
}
