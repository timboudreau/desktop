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
import java.util.Arrays;

/**
 *
 * @author Tim Boudreau
 */
final class DiagonalKey implements Comparable<DiagonalKey> {

    private final int[] values;
    private final boolean cyclic;
    private long touched = System.nanoTime();

    DiagonalKey(int x1, int x2, int y1, int y2, Color top, Color bottom, boolean cyclic) {
        this(x1, x2, y1, y2, top.getRGB(), bottom.getRGB(), cyclic);
    }

    DiagonalKey(int x1, int x2, int y1, int y2, int top, int bottom, boolean cyclic) {
        values = new int[]{x1, x2, y1, y2, top, bottom};
        this.cyclic = cyclic;
    }

    void touch() {
        touched = System.nanoTime();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(Integer.toHexString(values[i]));
            if (i != values.length - 1) {
                sb.append('-');
            }
            sb.append(cyclic ? 'c' : 'n');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Arrays.hashCode(this.values) + (cyclic ? 73 : 0);
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
        if (!(obj instanceof DiagonalKey)) {
            return false;
        }
        final DiagonalKey other = (DiagonalKey) obj;
        return Arrays.equals(this.values, other.values);
    }

    @Override
    public int compareTo(DiagonalKey o) {
        return Long.compare(touched, o.touched);
    }

}
