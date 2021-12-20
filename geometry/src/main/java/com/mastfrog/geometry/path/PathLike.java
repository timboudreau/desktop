/*
 * The MIT License
 *
 * Copyright 2021 Mastfrog Technologies.
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
package com.mastfrog.geometry.path;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * Abstraction for a Path2D which is an interface.
 *
 * @author Tim Boudreau
 */
public interface PathLike {

    public static PathLike of(Path2D path) {
        return new PathLikeAdapter(path);
    }

    void moveTo(double x, double y);

    void lineTo(double x, double y);

    void quadTo(double x1, double y1, double x2, double y2);

    void curveTo(double x1, double y1, double x2, double y2, double x3, double y3);

    void closePath();

    Rectangle2D getBounds2D();

    default void moveTo(float x, float y) {
        this.moveTo((double) x, (double) y);
    }

    default void lineTo(float x, float y) {
        this.lineTo((double) x, (double) y);
    }

    default void quadTo(float x1, float y1, float x2, float y2) {
        this.quadTo((double) x1, (double) y1, (double) x2, (double) y2);
    }

    default void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.curveTo((double) x1, (double) y1, (double) x2, (double) y2, (double) x3, (double) y3);
    }
}
