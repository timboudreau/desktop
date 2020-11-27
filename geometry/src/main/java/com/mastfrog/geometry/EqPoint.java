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
package com.mastfrog.geometry;

import com.mastfrog.function.DoubleBiConsumer;
import com.mastfrog.geometry.util.GeometryStrings;
import com.mastfrog.geometry.util.GeometryUtils;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * A Point2D.Float which provides a reasonable (tolerance based) implementation
 * of equals() and hashCode().
 *
 * @author Tim Boudreau
 */
public class EqPoint extends Point2D.Float implements Comparable<Point2D>, DoubleBiConsumer {

    public EqPoint() {
    }

    public EqPoint(float x, float y) {
        super(x, y);
    }

    public EqPoint(double x, double y) {
        super((float) x, (float) y);
    }

    public EqPoint(Point2D p) {
        this(p.getX(), p.getY());
    }

    public static EqPoint of(Point2D p) {
        if (p == null) {
            return null;
        }
        if (p instanceof EqPoint) {
            return (EqPoint) p;
        }
        return new EqPoint(p);
    }

    public Point toPoint() {
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o == this) {
            return true;
        } else if (o instanceof Point2D) {
            Point2D p = (Point2D) o;
            return GeometryUtils.isSamePoint(p, this);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return GeometryUtils.pointHashCode(x, y);
    }

    @Override
    public String toString() {
        return GeometryStrings.toString(x, y);
    }

    @Override
    public int compareTo(Point2D o) {
        int result = java.lang.Double.compare(y, o.getY());
        if (result == 0) {
            result = java.lang.Double.compare(x, o.getX());
        }
        return result;
    }

    @Override
    public void accept(double a, double b) {
        setLocation(a, b);
    }
}
