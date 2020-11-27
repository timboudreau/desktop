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

import com.mastfrog.function.DoubleQuadConsumer;
import com.mastfrog.function.state.Int;
import com.mastfrog.geometry.util.GeometryUtils;

/**
 * Interface with default implementations for shapes which can count how many
 * times they are intersected by other shapes; used for computing interiority
 * with some shapes.
 *
 * @author Tim Boudreau
 */
public interface Intersectable {

    void visitLines(DoubleQuadConsumer consumer, boolean includeClose);

    default boolean intersectsSegment(Intersectable inter, boolean includeClose) {
        return intersectionCount(inter, true) > 0;
    }

    default boolean intersectsSegment(Intersectable inter) {
        return intersectionCount(inter, true) > 0;
    }

    default int intersectionCount(Intersectable other, boolean includeClose) {
        if (other == this) {
            return -1;
        }
        Int result = Int.create();
        visitLines((ax, ay, bx, by) -> {
            other.visitLines((cx, cy, dx, dy) -> {
                if (GeometryUtils.linesIntersect(ax, ay, bx, by, cx, cy, dx, dy, false)) {
                    result.increment();
                }
            }, includeClose);
        }, includeClose);
        return result.get();
    }
}
