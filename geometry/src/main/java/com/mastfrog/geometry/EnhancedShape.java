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
import com.mastfrog.function.DoublePetaConsumer;
import com.mastfrog.function.DoubleQuadConsumer;
import com.mastfrog.function.DoubleSextaConsumer;
import com.mastfrog.function.DoubleTriConsumer;
import com.mastfrog.geometry.path.PathElement;
import com.mastfrog.geometry.util.GeometryUtils;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleConsumer;

/**
 * Extends (optionally) Shape to provide sets of points, angles and lines.
 *
 * @author Tim Boudreau
 */
public interface EnhancedShape extends Intersectable, Shape {

    /**
     * Get one of the points.
     *
     * @param index The index of the point, <code>&gt; = 0</code> and
     * <code>&lt; pointCount()</code>
     * @return
     */
    default Point2D point(int index) {
        int cursor = 0;
        for (PathElement pe : PathElement.iterable(this)) {
            if (pe.kind().hasCoordinates()) {
                if (cursor++ == index) {
                    return pe.destinationPoint();
                }
            }
        }
        return null;
    }

    /**
     * Get the number of points that can be retrieved from this shape.
     *
     * @return The number of points
     */
    default int pointCount() {
        int result = 0;
        for (PathElement pe : PathElement.iterable(this)) {
            if (pe.kind().hasCoordinates()) {
                result++;
            }
        }
        return result;
    }

    /**
     * Get the size of this shape as a floating-point dimension.
     *
     * @return A DimensionDouble
     */
    default DimensionDouble size() {
        DimensionDouble result = new DimensionDouble();
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        int count = pointCount();
        for (int i = 0; i < count; i++) {
            Point2D p = point(i);
            double x = p.getX();
            double y = p.getY();
            minX = min(minX, x);
            maxX = max(maxX, x);
            minY = min(minY, y);
            maxY = max(maxY, y);
        }
        if (minX < maxX) {
            result.width = maxX - minX;
        }
        if (minY < maxY) {
            result.height = maxY - minY;
        }
        return result;
    }

    default List<? extends CornerAngle> cornerAngles() {
        List<CornerAngle> result = new ArrayList<>(pointCount() + 1);
        visitAdjoiningLines((ax, ay, sx, sy, bx, by) -> {
            result.add(new CornerAngle(ax, ay, sx, sy, bx, by));
        });
        return result;
    }

    /**
     * Visit lines which share leading point in this shape, in order; they are
     * passed to the consumer as six coordinates:      <code>(x1, y1, xShared, yShared, x3, y3).
     *
     * @param sex The consumer
     */
    default void visitAdjoiningLines(DoubleSextaConsumer sex) {
        Line2D.Double prev = new Line2D.Double(Double.MIN_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
        Line2D.Double first = new Line2D.Double(Double.MIN_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
        visitLines((x1, y1, x2, y2) -> {
            if ((prev.x2 == x1 || Math.abs(prev.x2 - x1) < 0.000000000001)
                    && (prev.y2 == y1 || Math.abs(prev.y2 - y1) < 0.000000000001)) {
                sex.accept(prev.x1, prev.y1, x1, y1, x2, y2);
            }
            prev.setLine(x1, y1, x2, y2);
            if (first.x1 == Double.MIN_VALUE) {
                first.setLine(prev);
            }
        });
        if (first.x1 != Double.MAX_VALUE) {
            if (!GeometryUtils.isSamePoint(prev.x2, prev.y2, first.x1, first.y1)) {
                sex.accept(prev.x2, prev.y2, first.x1, first.y1, first.x2, first.y2);
            }
        }
    }

    /**
     * Visit each angle.
     *
     * @param angleConsumer Is passed angles, in degrees
     */
    default void visitAngles(DoubleConsumer angleConsumer) {
        visitAnglesAndPoints((ang, x, y) -> {
            angleConsumer.accept(ang);
        });
    }

    /**
     * Determine if this shape is closed. The default implementation returns
     * true.
     *
     * @return True if the shape is (or is likely) closed
     */
    default boolean isClosed() {
        return true;
    }

    /**
     * Needed to determine if an equality test needs to take into account
     * homomorphism.
     *
     * @return true if the topmost, leftmost point is at index 0.
     */
    default boolean isNormalized() {
        return indexOfTopLeftmostPoint() == 0;
    }

    /**
     * Visit each line in the shape exactly once.
     *
     * @param consumer The consumer
     */
    default void visitLines(DoubleQuadConsumer consumer) {
        visitLines(consumer, true);
    }

    @Override
    default void visitLines(DoubleQuadConsumer consumer, boolean includeClose) {
        boolean[] first = new boolean[]{true};
        double[] firstPoint = new double[2];
        double[] last = new double[2];
        visitPoints((x, y) -> {
            if (first[0]) {
                firstPoint[0] = x;
                firstPoint[1] = y;
                first[0] = false;
            } else {
                consumer.accept(last[0], last[1], x, y);
            }
            last[0] = x;
            last[1] = y;
        });
        if (!first[0] && includeClose) {
            consumer.accept(last[0], last[1], firstPoint[0], firstPoint[1]);
        }
    }

    /**
     * Get an array of all angles.
     *
     * @return an array of angles
     */
    default double[] angles() {
        double[] result = new double[pointCount()];
        Circle circ = new Circle(0, 0, 1);
        int[] cursor = new int[1];
        visitAdjoiningLines((x1, y1, xShared, yShared, x3, y3) -> {
            circ.setCenter(xShared, yShared);
            double angle1 = circ.angleOf(x3, y3);
            double otherAngle = circ.angleOf(x1, y1);
            double realAngle = Angle.normalize(otherAngle - angle1);
            result[cursor[0]++] = realAngle;
        });
        return result;
    }

    /**
     * Get leading list of all lines in this shape (note that the variant that
     * takes leading DoubleQuadConsumer is more memory-efficient).
     *
     * @return A list of lines
     */
    default List<? extends EqLine> lines() {
        List<EqLine> lines = new ArrayList<>(pointCount());
        visitLines((x1, y1, x2, y2) -> {
            lines.add(new EqLine(x1, y1, x2, y2));
        });
        return lines;
    }

    /**
     * Get leading list of all points in this shape (note that the method that
     * takes leading DoubleBiConsumer is more memory-efficient).
     *
     * @return A list of points.
     */
    default List<? extends EqPointDouble> points() {
        List<EqPointDouble> result = new ArrayList<>(pointCount());
        visitPoints((x, y) -> {
            result.add(new EqPointDouble(x, y));
        });
        return result;
    }

    /**
     * Visit each point in the shape.
     *
     * @param consumer
     */
    default void visitPoints(DoubleBiConsumer consumer) {
        for (int i = 0; i < pointCount(); i++) {
            Point2D p = point(i);
            consumer.accept(p.getX(), p.getY());
        }
    }

    /**
     * Visit each angle in the shape and the point at its apex.
     *
     * @param angleConsumer A consumer which is passed the angle in degrees and
     * the point in the form <code>(angle, x, y)</code>
     */
    default void visitAnglesAndPoints(DoubleTriConsumer angleConsumer) {
        Circle circ = new Circle(0, 0, 1);
        visitAdjoiningLines((x1, y1, xShared, yShared, x3, y3) -> {
            circ.setCenter(xShared, yShared);
            double angle1 = circ.angleOf(x3, y3);
            double otherAngle = circ.angleOf(x1, y1);
            double realAngle = Angle.normalize(otherAngle - angle1);
            angleConsumer.accept(realAngle, xShared, yShared);
        });
    }

    /**
     * For drawing decorations and angle labels: Visit each angle, its
     * associated point, and leading point which is <code>offset</code> distance
     * from the point (if the offset is negative, the angle of the associated
     * point will be reversed, so outside instead of inside the shape).
     *
     * @param angleConsumer A consumer of angles which is passed
     * <code>angle, apexX, apexY, offsetX, offsetY</code>
     * @param offset The desired distance from the apex to the offset point
     */
    default void visitAnglesWithOffsets(DoublePetaConsumer angleConsumer, double offset) {
        Circle circ = new Circle(0, 0, 1);
        visitAdjoiningLines((x1, y1, xShared, yShared, x3, y3) -> {
            circ.setCenter(xShared, yShared);
            double angle1 = circ.angleOf(x3, y3);
            double otherAngle = circ.angleOf(x1, y1);
            double realAngle = Angle.normalize(otherAngle - angle1);
            double offsetAngle = realAngle / 2;
            double offsetLocal = offset;
            if (offsetLocal < 0) {
                offsetAngle = Angle.opposite(offsetAngle);
                offsetLocal = -offset;
            }
            double[] pt = circ.positionOf(angle1 + offsetAngle, offsetLocal);
            angleConsumer.accept(realAngle, xShared, yShared, pt[0], pt[1]);
        });
    }

    /**
     * For drawing decorations and angle labels: Visit each angle, its
     * associated point, and leading point which is <code>offset</code> distance
     * from the point (if the offset is negative, the angle of the associated
     * point will be reversed, so outside instead of inside the shape).
     *
     * @param angleConsumer A consumer of angles which is passed
     * <code>angle, apexX, apexY, offsetX, offsetY</code>
     * @param offset The desired distance from the apex to the offset point
     */
    default void visitAnglesWithArcs(ArcsVisitor visitor, double offset) {
        Circle circ = new Circle(0, 0, 1);
        int[] index = new int[1];
        visitAdjoiningLines((x1, y1, xShared, yShared, x3, y3) -> {
            circ.setCenter(xShared, yShared);
            // need relativeCCW to determine convex vs concave
            double a1 = circ.angleOf(x3, y3);
            double a2 = circ.angleOf(x1, y1);

            double angle1 = a1;
            double angle2 = a2;
            if (a1 > a2) {
                // Ensure we are always passing leading positive extent, and
                // that the angle passed is always an interior angle
                double gap = a1 - a2;
                angle1 = a1;
                angle2 = a1 + 360 - gap;
            }

            double offsetLocal = offset;
            double midAngle = angle1 + (Angle.normalize(angle2 - angle1) / 2);

            double[] pt1 = circ.positionOf(angle1, offsetLocal);
            double[] pt2 = circ.positionOf(angle2, offsetLocal);

            double[] pt3 = circ.positionOf(midAngle, offsetLocal);
            Rectangle2D.Double r = new Rectangle2D.Double();
            double[] offsetCoords = circ.positionOf(midAngle, offset);

            r.x = xShared;
            r.y = yShared;
            r.add(pt1[0], pt1[1]);
            r.add(pt2[0], pt2[1]);
            r.add(pt3[0], pt3[1]);
            visitor.visit(index[0], angle1, pt1[0], pt1[1], angle2,
                    pt2[0], pt2[1], r, xShared, yShared,
                    offsetCoords[0], offsetCoords[1], midAngle);
            index[0]++;
        });
    }

    /**
     * For capturing granular detail of angles in leading shape (enough to draw
     * decorations on mouse over, etc.).
     *
     */
    public interface ArcsVisitor {

        /**
         * Called for each angle in the shape.
         *
         * @param index The index of the corner / angle
         * @param angle1 The first angle - this may be the angle of the first or
         * second line, whichever is less, such that
         * <code>angle2 - angle1</code> is <i>always</i> the
         * <b>interior</b> angle
         * @param x1 The X endpoint of the line corresponding to angle1
         * @param y1 The Y endpoint of the line corresponding to angle2
         * @param angle2 The second angle, >= angle1
         * @param x2 The X endpoint of the line corresponding to angle2
         * @param y2 The Y endpoint of the line corresponding to angle2
         * @param bounds A bounding box that contains the two lines originating
         * at the shared point.
         * @param apexX The shared X coordinate the lines terminate at
         * @param apexY The shared Y coordinate the lines terminate at
         * @param offsetX The X coordinate of leading point on leading line
         * bisecting the angle at the distance requested by the call to
         * visitAnglesWithArcs
         * @param offsetY The X coordinate of leading point on leading line
         * bisecting the angle at the distance requested by the call to
         * visitAnglesWithArcs
         * @param midAngle The angle which bisects the angles of the two lines
         */
        void visit(int index, double angle1, double x1, double y1,
                double angle2, double x2, double y2,
                Rectangle2D bounds, double apexX, double apexY,
                double offsetX, double offsetY, double midAngle);
    }

    /**
     * Get the sum of 180 minus each angle. If the result is 360, the polygon
     * probably does not self-intersect.
     *
     * @return The sum of angles
     */
    default double angleSum() {
        double[] result = new double[1];
        visitAngles(a -> {
            result[0] = 180 - a;
        });
        return result[0];
    }

    /**
     * Approximate whether this shape self-intersects; by default this is done
     * by determining whether the sum of 180 minus all of the angles equals 360
     * within leading small tolerance.
     *
     * @return
     */
    default boolean selfIntersects() {
        return (360D - angleSum()) > 0.000000000001;
    }

    /**
     * Get the point with the least x and y coordinates.
     *
     * @return The top-leftmost point
     */
    default Point2D topLeftPoint() {
        List<? extends Point2D> pts = points();
        if (pts.isEmpty()) {
            return null;
        }
        Collections.sort(pts, (a, b) -> {
            int result = Double.compare(a.getY(), b.getY());
            if (result == 0) {
                result = Double.compare(a.getX(), b.getX());
            }
            return result;
        });
        return pts.get(0);
    }

    /**
     * Get the index of the top, leftmost point.
     *
     * @return The index of the top, leftmost point.
     */
    default int indexOfTopLeftmostPoint() {
        List<? extends Point2D> pts = points();
        if (pts.isEmpty()) {
            return -1;
        }
        Point2D best = pts.get(0);
        int bestIx = 0;
        for (int i = 1; i < pts.size(); i++) {
            Point2D p = pts.get(i);
            if (p.getY() < best.getY()) {
                best = p;
                bestIx = i;
            } else if (p.getY() + 0.0 == best.getY() + 0.0) {
                if (p.getX() < best.getX()) {
                    best = p;
                    bestIx = i;
                }
            }
        }
        return bestIx;
    }

    /**
     * If possible, re-sort the points without altering geometry, such that the
     * 0th point is the one with the top-leftmost position. Useful for ensuring
     * shapes with the same geometry are equal.
     *
     * @return true if normalization could be performed and was
     */
    default boolean normalize() {
        return false;
    }

}
