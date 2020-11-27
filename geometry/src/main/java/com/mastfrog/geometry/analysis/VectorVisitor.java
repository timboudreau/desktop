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
package com.mastfrog.geometry.analysis;

import com.mastfrog.geometry.LineVector;
import com.mastfrog.geometry.Polygon2D;
import com.mastfrog.geometry.RotationDirection;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * For analyzing the angles within potentially complex paths.
 *
 * @author Tim Boudreau
 */
@FunctionalInterface
public interface VectorVisitor {

    /**
     * Called once for (at a minimum) each point which begins the third of three
     * consecutive straight lines within a shape, such that there is vector
     * which constitutes a straight-line angle.
     *
     * @param pointIndex The <i>absolute</i> index of the point within the shape
     * (including any preceding cubic or quadratic control points)
     * @param vect The line vector at this point, with the angle correctly
     * normalized
     * @param subpathIndex The index of this subpath within the shape, if it
     * contains multiple paths, in the order encountered in its PathIterator
     * @param subpathRotationDirection The overall rotation direction of this
     * sub-path - the majority of angles turn clockwise or counterclockwise?
     * @param approximate A polygon which *approximates* the entire shape, for
     * hit-testing and similar - reliably implements contains(x,y) which some
     * shapes don't, but contains minimal detail for cubic and quadratic curves.
     */
    void visit(int pointIndex, LineVector vect, int subpathIndex, RotationDirection subpathRotationDirection, Polygon2D approximate,
            int prevPointIndex, int nextPointIndex);

    default RotationDirection analyze(Shape shape) {
        return analyze(shape, (AffineTransform) null);
    }

    default RotationDirection analyze(Shape shape, AffineTransform xform) {
        AnglesAnalyzer ana = new AnglesAnalyzer();
        RotationDirection result = ana.analyzeShape(shape, xform);
        ana.visitAll(this);
        return result;
    }

    default RotationDirection analyze(PathIterator iter) {
        AnglesAnalyzer ana = new AnglesAnalyzer();
        RotationDirection result = ana.analyze(iter);
        ana.visitAll(this);
        return result;
    }

    public static RotationDirection analyze(Shape shape, VectorVisitor vv) {
        return analyze(shape, null, vv);
    }

    public static RotationDirection analyze(Shape shape, AffineTransform xform, VectorVisitor vv) {
        return vv.analyze(shape, xform);
    }

    public static RotationDirection analyze(PathIterator it, VectorVisitor vv) {
        return vv.analyze(it);
    }
}
