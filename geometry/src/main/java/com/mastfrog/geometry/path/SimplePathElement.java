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
package com.mastfrog.geometry.path;

import com.mastfrog.geometry.util.GeometryStrings;
import java.util.Arrays;

/**
 *
 * @author Tim Boudreau
 */
final class SimplePathElement implements PathElement {

    private final int type;
    private final double[] points;

    public SimplePathElement(int type, double[] points) {
        this.type = type;
        this.points = points;
    }

    public SimplePathElement(int type) {
        this(type, null);
    }

    @Override
    public int type() {
        return type;
    }

    @Override
    public double[] points() {
        return points == null ? new double[0] : points;
    }

    @Override
    public String toString() {
        String typeName = kind().toString();
        StringBuilder sb = new StringBuilder(typeName.length() + 5 * pointCount());
        int count = pointCount();
        for (int i = 0; i < count; i++) {
            int ix = i;
            point(i, (x, y) -> {
                GeometryStrings.toString(sb, x, y);
                if (ix != count - 1) {
                    sb.append(",");
                }
            });
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.type;
        hash = 59 * hash + Arrays.hashCode(this.pointData());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PathElement other = (PathElement) obj;
        if (this.type != other.type()) {
            return false;
        }
        if (!Arrays.equals(this.pointData(), other.pointData())) {
            return false;
        }
        return true;
    }
}
