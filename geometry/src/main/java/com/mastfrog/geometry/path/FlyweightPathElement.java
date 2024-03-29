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
import java.awt.geom.PathIterator;
import java.util.Arrays;

/**
 * A flyweight implementation of PathElement
 *
 * @author Tim Boudreau
 */
public final class FlyweightPathElement implements PathElement {

    private int type = -1;
    private double[] data = new double[6];

    FlyweightPathElement() {

    }

    FlyweightPathElement(int type, double[] data) {
        this.data = Arrays.copyOf(data, 6);
        this.type = type;
    }

    @Override
    public PathElement copy() {
        return new SimplePathElement(type, Arrays.copyOf(data, Math.min(data.length, 6)));
    }

    @Override
    public PathElementKind kind() {
        if (type < 0) {
            return null;
        }
        return PathElement.super.kind();
    }

    @Override
    public int type() {
        return type;
    }

    @Override
    public double[] points() {
        return data;
    }

    public boolean update(PathIterator iter) {
        if (iter.isDone()) {
            return false;
        }
        type = iter.currentSegment(data);
        return true;
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
        int type = type();
        if (this.type != other.type()) {
            return false;
        }
        return Arrays.equals(this.pointData(), other.pointData());
    }

}
