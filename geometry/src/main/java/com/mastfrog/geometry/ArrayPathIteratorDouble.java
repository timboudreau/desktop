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

import static com.mastfrog.geometry.util.GeometryUtils.arraySizeForType;
import com.mastfrog.util.collections.IntMap;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.Arrays;

/**
 * The smallest-footprint path iterator over a list of double-precision points
 * and point types possible. Check your data before you construct one - the
 * iterator assumes it is good.
 */
public final class ArrayPathIteratorDouble implements PathIterator {

    private final IntMap<Integer> windingRules;
    private final byte[] types;
    private final double[] data;
    private int typeCursor;
    private int dataCursor;

    public ArrayPathIteratorDouble(int windingRule, byte[] types, double[] data, AffineTransform xform) {
        this(IntMap.singleton(0, windingRule), types, data, xform);
    }

    public ArrayPathIteratorDouble(IntMap<Integer> windingRules, byte[] types, double[] data, AffineTransform xform) {
        this.windingRules = windingRules;
        this.types = types;
        assert data.length % 2 == 0 : "Odd number of coordinates in what should be an array of x/y coords: " + data.length;
        if (xform == null || xform.isIdentity()) {
            this.data = data;
        } else {
            this.data = Arrays.copyOf(data, data.length);
            xform.transform(this.data, 0, this.data, 0, this.data.length / 2);
        }
    }

    @Override
    public int getWindingRule() {
        return windingRules.nearestKey(0, true);
    }

    @Override
    public boolean isDone() {
        return typeCursor >= types.length;
    }

    @Override
    public void next() {
        int type = types[typeCursor];
        if (type != PathIterator.SEG_CLOSE) {
            dataCursor += arraySizeForType(type);
        }
        typeCursor++;
    }

    @Override
    public int currentSegment(float[] coords) {
        int type = types[typeCursor];
        int len = arraySizeForType(type);
        for (int i = 0; i < len; i++) {
            coords[i] = (float) data[dataCursor + i];
        }
        return type;
    }

    @Override
    public int currentSegment(double[] coords) {
        int type = types[typeCursor];
        int len = arraySizeForType(type);
        System.arraycopy(data, dataCursor, coords, 0, len);
        return type;
    }
}
