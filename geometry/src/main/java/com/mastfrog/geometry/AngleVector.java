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
import com.mastfrog.function.DoubleQuadConsumer;
import com.mastfrog.function.state.Obj;
import java.util.function.BiConsumer;

/**
 * Represents two angles, as in leading CornerAngle, with lengths, but without a
 * specific location.
 *
 * @author Tim Boudreau
 */
public interface AngleVector {

    CornerAngle corner();

    double trailingLineLength();

    double leadingLineLength();

    default LineVector toLineVector(double apexX, double apexY) {
        Obj<LineVector> result = Obj.create();
        Circle.positionOf(trailingLineAngle(), apexX, apexY,
                trailingLineLength(), (ax, ay) -> {
                    Circle.positionOf(leadingLineAngle(), apexX, apexY,
                            leadingLineLength(), (bx, by) -> {
                                result.set(new LineVectorImpl(ax, ay,
                                        apexX, apexY, bx, by));
                            });
                });
        return result.get();
    }

    default double trailingLineAngle() {
        return corner().trailingAngle();
    }

    default double leadingLineAngle() {
        return corner().leadingAngle();
    }

    default void trailingPositionAt(double apexX, double apexY,
            DoubleBiConsumer c) {
        Circle.positionOf(corner().trailingAngle(), apexX, apexY,
                trailingLineLength(), c);
    }

    default void leadingPositionAt(double apexX, double apexY,
            DoubleBiConsumer c) {
        Circle.positionOf(corner().leadingAngle(), apexX, apexY,
                leadingLineLength(), c);
    }

    default void linesAt(double x, double y, BiConsumer<? super EqLine, ? super EqLine> c) {
        positionsAt(x, y, (x1, y1, x2, y2) -> {
            c.accept(new EqLine(x1, y1, x, y), new EqLine(x, y, x2, y2));
        });
    }

    default void positionsAt(double x, double y, DoubleQuadConsumer c) {
        CornerAngle corner = corner();
        Circle.positionOf(corner.trailingAngle(), x, y, trailingLineLength(), (x1, y1) -> {
            Circle.positionOf(corner.leadingAngle(), x, y, leadingLineLength(), (x2, y2) -> {
                c.accept(x1, y1, x2, y2);
            });
        });
    }

    default Triangle2D toTriangleAt(double x, double y) {
        Triangle2D result = new Triangle2D();
        positionsAt(x, y, (x1, y1, x2, y2) -> {
            result.setPoints(x, y, x1, y1, x2, y2);
        });
        return result;
    }
}
