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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Tim Boudreau
 */
final class GradientUtils {

    public static final AffineTransform NO_XFORM = AffineTransform.getTranslateInstance(0, 0);

    public static String transparencyToString(int xpar) {
        switch (xpar) {
            case Transparency.BITMASK:
                return "Bitmask";
            case Transparency.OPAQUE:
                return "Opaque";
            case Transparency.TRANSLUCENT:
                return "Translucent";
            default:
                return "Unknown-" + xpar;
        }
    }

    public static boolean isInvertableTransform(AffineTransform xform) {
        return xform.getDeterminant() != 0;
    }

    public static boolean isNoTransform(AffineTransform xform) {
        return xform == null || NO_XFORM.equals(xform);
    }

    public static String colorToString(Color c) {
        return c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "," + c.getAlpha();
    }

    public static String paintToString(Paint p) {
        if (p instanceof Color) {
            return "Color{" + GradientUtils.colorToString((Color) p) + "}";
        } else if (p instanceof RadialGradientPaint) {
            RadialGradientPaint rgp = (RadialGradientPaint) p;
            StringBuilder sb = new StringBuilder("RadialGradientPaint{");
            float[] fracs = rgp.getFractions();
            Color[] colors = rgp.getColors();
            sb.append(rgp.getRadius());
            sb.append(" @ ").append((int) rgp.getCenterPoint().getX())
                    .append(',').append((int) rgp.getCenterPoint().getY())
                    .append(":");
            for (int i = 0; i < fracs.length; i++) {
                sb.append(fracs[i]).append('=').append(GradientUtils.colorToString(colors[i]));
                if (i != fracs.length - 1) {
                    sb.append(", ");
                }
            }
            return sb.append('}').toString();
        } else if (p instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) p;
            return "GradientPaint{" + (int) gp.getPoint1().getX() + "," + (int) gp.getPoint1().getY()
                    + ":" + GradientUtils.colorToString(gp.getColor1()) + " -> " + (int) gp.getPoint2().getX() + ","
                    + (int) gp.getPoint2().getY() + "=" + GradientUtils.colorToString(gp.getColor2()) + "}";
        }
        return p.toString();
    }

    public static void prepareGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    }

    private GradientUtils() {
    }
}
