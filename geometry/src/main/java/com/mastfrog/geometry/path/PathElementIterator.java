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

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 *
 * @author Tim Boudreau
 */
final class PathElementIterator implements Iterator<PathElement> {

    private final PathIterator iter;
    private final FlyweightPathElement el = new FlyweightPathElement();

    private PathElementIterator(PathIterator iter) {
        this.iter = iter;
    }

    static Iterator<PathElement> iterator(PathIterator iter) {
        return new PathElementIterator(iter);
    }

    static Iterable<PathElement> pathIterable(Supplier<PathIterator> supp) {
        return new IterableWrapper(supp);
    }

    static Iterable<PathElement> pathIterable(Shape shape) {
        return pathIterable(() -> shape.getPathIterator(null));
    }

    @Override
    public boolean hasNext() {
        return !iter.isDone();
    }

    @Override
    public PathElement next() {
        if (iter.isDone()) {
            throw new NoSuchElementException();
        }
        el.update(iter);
        if (!iter.isDone()) {
            iter.next();
        }
        return el;
    }

    static class IterableWrapper implements Iterable<PathElement> {

        private final Supplier<PathIterator> supp;

        public IterableWrapper(Supplier<PathIterator> supp) {
            this.supp = supp;
        }

        @Override
        public Iterator<PathElement> iterator() {
            return new PathElementIterator(supp.get());
        }
    }
}
