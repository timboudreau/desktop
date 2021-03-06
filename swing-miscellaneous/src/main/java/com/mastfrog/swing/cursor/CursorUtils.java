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
package com.mastfrog.swing.cursor;

import static com.mastfrog.geometry.util.PooledTransform.withQuadrantRotateInstance;
import java.awt.Color;
import static java.awt.Color.RGBtoHSB;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import static java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_COLOR_RENDERING;
import static java.awt.RenderingHints.KEY_DITHERING;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.KEY_STROKE_CONTROL;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_OFF;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_DITHER_ENABLE;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_STROKE_NORMALIZE;
import java.awt.Toolkit;
import static java.awt.Transparency.TRANSLUCENT;
import java.awt.image.BufferedImage;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JComponent;

/**
 *
 * @author Tim Boudreau
 */
final class CursorUtils {

    static final boolean MAC
            = System.getProperty("os.name", "").toLowerCase().contains("mac os")
            || System.getProperty("os.name", "").toLowerCase().contains("darwin")
            || System.getProperty("mrj.version") != null;

    static boolean isDarkBackground(JComponent comp) {
        Color bg = comp.getBackground();
        Color fg = comp.getForeground();
        float bri1 = brightnessOf(fg);
        float bri2 = brightnessOf(bg);
        boolean result;
        if (abs(bri1 - bri2) > 0.1) {
            result = bri1 > bri2;
        } else {
            result = min(bri1, bri2) < 0.45F;
        }
        return result;
    }

    static BufferedImage createCursorImage(GraphicsConfiguration config, int w, int h, Consumer<Graphics2D> c) {
        BufferedImage result = createCursorImage(config, w, h);
        Graphics2D g = result.createGraphics();
        try {
            applyRenderingHints(g);
            c.accept(g);
        } finally {
            g.dispose();
        }
        return result;
    }

    static BufferedImage createCursorImage(GraphicsConfiguration config, int w, int h) {
        BufferedImage result;
        if (config == null) {
            result = getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration()
                    .createCompatibleImage(w, h, TRANSLUCENT);
        } else {
            result = config.createCompatibleImage(w, h, TRANSLUCENT);
        }
        return result;
    }

    static void applyRenderingHints(Graphics2D g) {
        if (MAC) {
            Map hints = (Map) (Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints"));
            if (hints != null) {
                g.setRenderingHints(hints);
            }

            g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
//            g.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
//            g.setRenderingHint(KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

//            g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
//            g.setRenderingHint(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY);
//            g.setRenderingHint(KEY_STROKE_CONTROL, VALUE_STROKE_PURE);
//            g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
//            g.setRenderingHint(KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
//            g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
//            g.setRenderingHint(RenderingHints.KEY_RESOLUTION_VARIANT, RenderingHints.VALUE_RESOLUTION_VARIANT_DPI_FIT);
            return;
        }
        g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
        g.setRenderingHint(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(KEY_STROKE_CONTROL, VALUE_STROKE_NORMALIZE);
        g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(KEY_DITHERING, VALUE_DITHER_ENABLE);
    }

    static GraphicsConfiguration configFor(Component comp) {
        GraphicsConfiguration config = comp.getGraphicsConfiguration();
        if (config == null) {
            Frame[] fr = Frame.getFrames();
            if (fr != null && fr.length > 0) {
                config = fr[0].getGraphicsConfiguration();
            }
        }
        if (config == null) {
            config = getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .getDefaultConfiguration();
        }
        return config;
    }

    static BufferedImage rotated(BufferedImage img, int quadrants) {
        BufferedImage nue = new BufferedImage(img.getWidth(), img.getHeight(),
                img.getType());
        withQuadrantRotateInstance(quadrants, img.getWidth() / 2D, img.getHeight() / 2D, xform -> {
            Graphics2D g = (Graphics2D) nue.getGraphics();
            applyRenderingHints(g);
            try {
                g.drawImage(img, xform, null);
            } finally {
                g.dispose();
            }
        });
        return nue;
    }

    static boolean isDarker(Color a, Color b) {
        return brightnessOf(a) < brightnessOf(b);
    }

    static float brightnessOf(Color c) {
        float[] hsb = new float[4];
        RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
        return hsb[2];
    }

    private CursorUtils() {
        throw new AssertionError();
    }
}
