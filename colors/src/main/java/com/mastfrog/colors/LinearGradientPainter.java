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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

/**
 *
 * @author Tim Boudreau
 */
final class LinearGradientPainter implements GradientPainter {

    final BufferedImage img;
    final int x;
    final int y;
    final boolean vertical;
    final Color before;
    final Color after;
    final AffineTransform inverseTransform;

    LinearGradientPainter(BufferedImage img, int x, int y, boolean vertical, Color before, Color after, AffineTransform invertTransform) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.vertical = vertical;
        this.before = before;
        this.after = after;
        this.inverseTransform = invertTransform;
    }

    double[] centerXY() {
        Rectangle2D.Double r = new Rectangle2D.Double(x, y, img.getWidth(), img.getHeight());
        return new double[]{r.getCenterX(), r.getCenterY()};
    }

    @Override
    public String toString() {
        return (vertical ? "Vert{" : "Horiz{") + "{" + x + "," + y + " before=" + before + " after=" + after + ", imgSize=" + img.getWidth() + "," + img.getHeight() + "}";
    }

    @Override
    public void fill(Graphics2D g, Rectangle bounds) {
//        System.out.println("FILL " + bounds
//            + " xlation " + g.getTransform().getTranslateX()
//            + ", " + g.getTransform().getTranslateY());
        boolean verticalTiling = !this.vertical;
        bounds = new Rectangle(bounds);
        int x = this.x;
        int y = this.y;
        BufferedImage bi = img;
        int imageX = Math.max(0, bounds.x - x); // questionable
        int imageY = Math.max(0, bounds.y - y);
        int imageW = Math.min(bounds.width, bi.getWidth() - imageX);
        int imageH = Math.min(bounds.height, bi.getHeight() - imageY);
        if (bounds.x < x) {
            if (verticalTiling) {
                g.setColor(before);
                g.fillRect(bounds.x, bounds.y, x, bounds.height);
                bounds.width -= x - bounds.x;
                bounds.x = x;
            }
        }
        if (bounds.y < y) {
            if (!verticalTiling) {
                g.setColor(before);
                g.fillRect(bounds.x, bounds.y, bounds.width, y);
                bounds.height -= y;
                bounds.y = y;
            }
        }
        if (bounds.width > bi.getWidth()) {
            if (!verticalTiling) {
                g.setColor(after);
                int imageBottom = bounds.y + bi.getHeight();
                int fillHeight = bounds.y + bounds.height - imageBottom;
                g.fillRect(bounds.x, imageBottom, bounds.width, fillHeight);
            }
        }
        if (bounds.height > bi.getHeight()) {
            if (verticalTiling) {
                int imageRight = bounds.x + bi.getWidth();
                int fillWidth = (bounds.x + bounds.width) - imageRight;
                g.setColor(after);
                g.fillRect(imageRight, bounds.y, fillWidth, bounds.height);
            }
        }
        if (imageX >= bi.getWidth() || imageY >= bi.getHeight() || imageX < 0 || imageY < 0 || imageH <= 0 || imageW <= 0) {
            // Fill was everything, we aren't painting within the bounds the
            // gradient would fill
            return;
        }
        if (imageX != 0 || imageY != 0 || imageW < img.getWidth() || imageH < img.getHeight()) {
            // Get just the subset of the image we can use
            try {
                bi = bi.getSubimage(imageX, imageY, imageW, imageH);
            } catch (RasterFormatException rfe) {
                throw new IllegalStateException("Creating subimage of image " + bi.getWidth() + "x" + bi.getHeight() + " with bounds " + imageX + "," + imageY + "," + imageW + "," + imageH, rfe);
            }
        }
        int position = verticalTiling ? bounds.y : bounds.x;
        int max = verticalTiling ? bounds.y + bounds.height : bounds.x + bounds.width;
        // Tile the image along the dimension in question
        while (position < max) {
            // Translate to the bounds position
            AffineTransform xform = verticalTiling ? AffineTransform.getTranslateInstance(bounds.x, position) : AffineTransform.getTranslateInstance(position, bounds.y);
            // See if we are going to tile past the rectangle bounds,
            // and trim the image if need be
            int imageDim = (int) (verticalTiling ? bi.getHeight() : bi.getWidth());
            if (position + imageDim > max) {
                // last round - snip off any of the image we don't want to
                // paint, which would go outside the rectangle
                int diff = (position + imageDim) - (max);
                // Get a subimage of just the remainder
                bi = verticalTiling ? bi.getSubimage(0, 0, bi.getWidth(), bi.getHeight() - diff) : bi.getSubimage(0, 0, bi.getWidth() - diff, bi.getHeight());
            }
            g.drawRenderedImage(bi, xform);
            position += verticalTiling ? bi.getHeight() : bi.getWidth();
        }
    }
}
