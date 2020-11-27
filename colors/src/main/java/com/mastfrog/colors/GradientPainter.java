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

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.function.DoubleFunction;

/**
 * Paints a gradient or similar, which may be cached as a BufferedImage for
 * performance; the GradientPaint and similar interfaces in the JDK allocate
 * large pixel buffers; for linear and radial gradients, a single cached image
 * can be reused, resulting an considerably better performance (since modern
 * graphics pipelines are optimized for working with images, not pushing pixels)
 * and memory footprint.
 *
 * @author Tim Boudreau
 */
public interface GradientPainter {

    /**
     * Fill a rectangle with the gradient
     *
     * @param g the graphics context
     * @param bounds The rectangle
     */
    void fill(Graphics2D g, Rectangle bounds);

    /**
     * Fill a rectangle with the gradient
     *
     * @param g the graphics context
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param w the width
     * @param h the height
     * @param bounds The rectangle
     */
    default void fill(Graphics2D g, int x, int y, int w, int h) {
        fill(g, new Rectangle(x, y, w, h));
    }

    /**
     * Fill a shape with the gradient. Note that for some implementations this
     * will be accomplished by setting the Graphics2D's <code>clip</code> to the
     * shape, which can result in un-antialiased edges.
     *
     * @param g A graphics
     * @param bounds The rectangle
     */
    default void fillShape(Graphics2D g, Shape shape) {
        Shape oldClip = g.getClip();
        g.setClip(shape);
        fill(g, shape.getBounds());
        g.setClip(oldClip);
    }

    /**
     * Combine this painter with another one, painting both when calls to fill
     * or fill the shape are made. Note that the second gradient must use alpha
     * for this to do anything useful.
     *
     * @param next Another gradient painter
     * @return A gradient painter that wraps both this and the passed one
     */
    default GradientPainter and(GradientPainter next) {
        return new GradientPainter() {
            @Override
            public void fill(Graphics2D g, int x, int y, int w, int h) {
                GradientPainter.this.fill(g, x, y, w, h);
                next.fill(g, x, y, w, h);
            }

            @Override
            public void fill(Graphics2D g, Rectangle bounds) {
                GradientPainter.this.fill(g, bounds);
                next.fill(g, bounds);
            }

            @Override
            public void fillShape(Graphics2D g, Shape shape) {
                GradientPainter.this.fillShape(g, shape);
                next.fillShape(g, shape);
            }
        };
    }

    /**
     * For a gradient that fades into place, returns a function which returns a
     * gradient with varying alpha based on the current step (passed to the
     * function) of the total steps (passed to this method).
     *
     * @param steps The number of steps
     * @return A function which will produce a GradientPainter of varying alpha
     * values based on its input
     */
    default DoubleFunction<GradientPainter> animateTransparency(float steps) {
        if (steps == 0D) {
            throw new IllegalArgumentException("Cannot have 0 steps "
                    + "- division by zero");
        }
        return step -> {
            double amt = Math.min(1D, Math.max(0D, step / steps));
            AlphaComposite comp = AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, (float) amt);
            return withComposite(comp);
        };
    }

    /**
     * Returns a gradient painter which sets the passed composite on the
     * graphics before painting with this one.
     *
     * @param composite A composite
     * @return A gradient painter
     */
    default GradientPainter withComposite(AlphaComposite composite) {
        return new GradientPainter() {

            private void withComposite(Graphics2D g, Runnable r) {
                Composite old = g.getComposite();
                g.setComposite(composite);
                try {
                    r.run();
                } finally {
                    g.setComposite(old);
                }
            }

            @Override
            public void fill(Graphics2D g, Rectangle bounds) {
                withComposite(g, () -> {
                    GradientPainter.this.fill(g, bounds);
                });
            }

            @Override
            public void fill(Graphics2D g, int x, int y, int w, int h) {
                withComposite(g, () -> {
                    GradientPainter.this.fill(g, x, y, w, h);
                });
            }

            @Override
            public void fillShape(Graphics2D g, Shape shape) {
                withComposite(g, () -> {
                    GradientPainter.this.fillShape(g, shape);
                });
            }
        };
    }
}
