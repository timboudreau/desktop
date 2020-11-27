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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Boudreau
 */
final class TransformedGradientPainter implements GradientPainter {

    private final double y2;
    private final GradientPainter orig;
    private final double x1;
    private final double y1;
    private final double x2;

    TransformedGradientPainter(double x1, double y1, double x2, double y2,
            GradientPainter orig) {
        this.orig = orig;
        this.y2 = y2;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
    }

    @Override
    public void fill(Graphics2D g, Rectangle bounds) {
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            Graphics2D sub = (Graphics2D) g.create();
            double[] cd = centerAndDegrees();

            LinearGradientPainter lin = (LinearGradientPainter) orig;

            /*
             * Okay, what we need to achieve here to be able to paint non-linear
             * gradients by transforming the graphics rather than creating new
             * gradients:

            Create a transform that will:

            Rotate the graphics context around the CENTER of the IMAGE
            being painted.

            Translate the graphics context so that the corner of the
            rectangle is in the same relative position it would be
            relative to the gradient image being painted if we were
            doing straight painting.

             */

            double[] cen = lin.centerXY();
            AffineTransform rot = AffineTransform.getRotateInstance(cd[3], cen[0], cen[1]);

            double[] pts = new double[] {lin.x - bounds.getX(), lin.y - bounds.getY(), bounds.getX() + bounds.getWidth(),
                bounds.getY() + bounds.getHeight()};
            rot.transform(pts, 0, pts, 0, 1);

            AffineTransform shift = AffineTransform.getTranslateInstance(-pts[0], -pts[1]);
            rot.concatenate(shift);
            
            /*
            AffineTransform rot;
//            rot = AffineTransform.getTranslateInstance(bounds.x + lin.x, bounds.y + lin.y);
            rot = AffineTransform.getTranslateInstance(0, 0);
//            rot.concatenate(AffineTransform.getRotateInstance(cd[3], cen[0], cen[1]));
            rot.concatenate(AffineTransform.getRotateInstance(cd[3], cen[0], cen[1]));

            double[] pts = new double[]{bounds.x, bounds.y};

//            rot.transform(pts, 0, pts, 0, 1);
            System.out.println("RECT CENT " + cd[0] + ", " + cd[1] + " li cx " + cen[0] + "," + cen[1]
                + " pts " + pts[0] + "," + pts[1]);
//            rot.preConcatenate(AffineTransform.getTranslateInstance(pts[0], pts[1]));

            System.out.println("xform to " + pts[0] + ", " + pts[1]);
*/
            
            sub.transform(rot);
            orig.fillShape(sub, rot.createInverse().createTransformedShape(bounds));
            sub.dispose();
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(TransformedGradientPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    double[] centerAndDegrees() {
        double[] center = center(x1, y1, x2, y2);
        double theta = Math.atan2(x2 - center[0], y2 - center[1]);
        double deg = Math.toDegrees(theta);
        double[] result = new double[]{center[0], center[1],
            theta, Math.toRadians(deg + 90)};
        return result;
    }

    static double[] center(double x1, double y1, double x2, double y2) {
        Rectangle2D.Double dbl = new Rectangle2D.Double();
        dbl.setFrameFromDiagonal(x1, y1, x2, y2);
        return new double[]{dbl.getCenterX(), dbl.getCenterY()};
    }
}
