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
 * A Point2D.Double which provides a reasonable (tolerance based) implementation
 * of equals() and hashCode().
 *
 * @author Tim Boudreau
 */
public final class EqPointDouble extends Point2D.Double implements Comparable<Point2D>, DoubleBiConsumer {

    public EqPointDouble() {
    }

    public EqPointDouble(double[] coords) {
        this(coords[0], coords[1]);
    }

    public EqPointDouble(int offset, double[] coords) {
        this(coords[offset], coords[offset + 1]);
    }

    public EqPointDouble(double x, double y) {
        super(x, y);
    }

    public EqPointDouble(Point2D p) {
        this(p.getX(), p.getY());
    }

    public EqPoint toFloat() {
        return new EqPoint(getX(), getY());
    }

    public EqPointDouble copy() {
        return new EqPointDouble(this);
    }

    public static EqPointDouble of(Point2D p) {
        if (p == null) {
            return null;
        }
        if (p instanceof EqPointDouble) {
            return (EqPointDouble) p;
        }
        return new EqPointDouble(p);
    }

    public void copyInto(double[] pts, int at) {
        pts[at] = x;
        pts[at + 1] = y;
    }

    /**
     * Exact equality test, with no within-tolerance skew.
     *
     * @param other Another point
     * @return true if x == other.x && y == other.y
     */
    public boolean exactlyEqual(Point2D other) {
        double ox = other.getX();
        double oy = other.getY();
        return ox == x && oy == y;
    }

    public void translate(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public Point toPoint() {
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    public boolean equals(Point2D pt, double tolerance) {
        if (pt == this) {
            return true;
        }
        return GeometryUtils.isSamePoint(x, y, pt.getX(), pt.getY());
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
