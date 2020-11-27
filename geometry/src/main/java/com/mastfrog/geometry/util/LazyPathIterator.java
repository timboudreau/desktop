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
package com.mastfrog.geometry.util;

import java.awt.geom.PathIterator;
import java.util.function.Supplier;

/**
 *
 * @author Tim Boudreau
 */
public class LazyPathIterator implements PathIterator {

    private final Supplier<PathIterator> supplier;

    public LazyPathIterator(Supplier<PathIterator> supplier) {
        this.supplier = supplier;
    }

    @Override
    public int getWindingRule() {
        return supplier.get().getWindingRule();
    }

    @Override
    public boolean isDone() {
        return supplier.get().isDone();
    }

    @Override
    public void next() {
        supplier.get().next();
    }

    @Override
    public int currentSegment(float[] coords) {
        return supplier.get().currentSegment(coords);
    }

    @Override
    public int currentSegment(double[] coords) {
        return supplier.get().currentSegment(coords);
    }
}
