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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *
 * @author Tim Boudreau
 */
final class RadialGradientPainter implements GradientPainter {

    final BufferedImage img;
    final int x;
    final int y;
    final Color fillColor;
    final AffineTransform invertTransform;

    RadialGradientPainter(BufferedImage img, int x, int y, Color fillColor, AffineTransform invertTransform) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.fillColor = fillColor;
        this.invertTransform = invertTransform;
    }

    @Override
    public String toString() {
        return "Radial{" + x + "," + y + " fill=" + fillColor + ", imgSize=" + img.getWidth() + "," + img.getHeight() + "}";
    }

    @Override
    public void fill(Graphics2D g, Rectangle bounds) {
        AffineTransform xform = AffineTransform.getTranslateInstance(bounds.x, bounds.y);
        if (Gradients.SCALING_SUPPORT) {
            if (invertTransform != GradientUtils.NO_XFORM) {
                xform.concatenate(invertTransform);
                bounds = invertTransform.createTransformedShape(bounds).getBounds();
            }
        }
        BufferedImage bi = img;
        int imageX = bounds.x - x;
        int imageY = bounds.y - y;
        int imageW = Math.min(bounds.width, bi.getWidth() - imageX);
        int imageH = Math.min(bounds.height, bi.getHeight() - imageY);
        if (imageW > 0 && imageH > 0 && imageX < bi.getWidth() && imageY < bi.getHeight() && imageY >= 0 && imageX >= 0) {
            if (imageX != 0 || imageY != 0 || imageW != bi.getWidth() || imageH != bi.getHeight()) {
                // Get just the subset of the image we can use
                bi = img.getSubimage(imageX, imageY, imageW, imageH);
            }
            g.drawRenderedImage(bi, xform);
        }
        int xRemainder = bounds.width - bi.getWidth();
        int yRemainder = bounds.height - bi.getHeight();
        if (xRemainder > 0) {
            g.setColor(fillColor);
            g.fillRect(bounds.x + bi.getWidth(), bounds.y, xRemainder, bounds.height);
        }
        if (yRemainder > 0) {
            g.setColor(fillColor);
            g.fillRect(bounds.x, bounds.y + bi.getHeight(), bounds.width, yRemainder);
        }
    }
}
