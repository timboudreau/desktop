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
import com.mastfrog.function.DoubleSextaConsumer;
import com.mastfrog.geometry.util.GeometryStrings;
import com.mastfrog.geometry.util.GeometryUtils;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Extends rectangle with decoration methods and a few other things;
 * particularly, an empty rectangle can have shapes added to its bounds without
 * remaining anchored at 0,0 if it started as 0,0,0,0.
 *
 * @author Tim Boudreau
 */
public class EnhRectangle2D extends Rectangle2D.Double implements EnhancedShape, Tesselable {

    public EnhRectangle2D() {
    }

    public EnhRectangle2D(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    public EnhRectangle2D(EnhRectangle2D other) {
        super(other.x, other.y, other.width, other.height);
    }

    public EnhRectangle2D(Rectangle2D other) {
        super(other.getX(), other.getY(), other.getWidth(), other.getHeight());
    }

    public EnhRectangle2D translate(double dx, double dy) {
        x += dx;
        y += dy;
        return this;
    }

    public EnhRectangle2D copy() {
        return new EnhRectangle2D(this);
    }

    public EnhRectangle2D grownBy(double val) {
        EnhRectangle2D result = copy();
        result.grow(val);
        return result;
    }

    public void clear() {
        x = y = width = height = 0;
    }

    /**
     * Get the intersection of a line and this rectangle, if any.
     * Returns null if there are not two different intersection points.
     * The intersection points must be on the line as specified, without
     * requiring the line to be extended.
     *
     * @param ln A line
     * @return A line or null
     */
    public EqLine intersection(EqLine ln) {
        EqPointDouble start = null;
        EqPointDouble end = null;

        for (EqLine l : new EqLine[] {leftEdge(), topEdge(), rightEdge(), bottomEdge()}) {
            EqPointDouble pt = l.intersectionPoint(ln);
            if (pt != null) {
                if (start == null) {
                    start = pt;
                } else {
                    end = pt;
                    break;
                }
            }
        }

        if (start != null && end != null) {
            return new EqLine(start, end);
        }
        return null;
    }

    /**
     * Multiply the width and height by the passed factor, without
     * changing the x and y coordinates (unlike grow, which grows the
     * rectangle in all directions).
     *
     * @param by The multiplier
     * @return this
     */
    public EnhRectangle2D multiplySize(double by) {
        width *= by;
        height *= by;
        return this;
    }

    public EnhRectangle2D multiplySizeMovingOrigin(double by) {
        double newWidth = width * by;
        double newHeight = height * by;
        x -= newWidth - width;
        y -= newHeight - height;
        width = newWidth;
        height = newHeight;
        return this;
    }

    /**
     * Divide this rectangle into four equal ones.
     *
     * @return topLeft, topRight, bottomLeft, bottomRight
     */
    public EnhRectangle2D[] toQuadrants() {
        double w2 = width / 2D;
        double h2 = height / 2D;
        return new EnhRectangle2D[]{
            rect(x, y, w2, h2),
            rect(x + w2, y, w2, h2),
            rect(x, y + h2, w2, h2),
            rect(x + w2, y + h2, w2, h2)
        };
    }

    public EnhRectangle2D[] toSquareQuadrants() {
        double size = min(width / 2, height / 2);
        return new EnhRectangle2D[] {
            rect(x, y, size, size),
            rect(right() - size, y, size, size),
            rect(x, bottom() - size, size, size),
            rect(right() - size, bottom() - size, size, size)
        };
    }

    public static EnhRectangle2D rect(double x, double y, double w, double h) {
        return new EnhRectangle2D(x, y, w, h);
    }

    public EnhRectangle2D growHorizontal(double by) {
        double halved = by / 2;
        x -= halved;
        width += by;
        return this;
    }

    public EnhRectangle2D growVertical(double by) {
        double halved = by / 2;
        y -= halved;
        width += by;
        return this;
    }

    public EnhRectangle2D minSquare() {
        width = height = min(width, height);
        return this;
    }

    public EnhRectangle2D maxSquare() {
        width = height = max(width, height);
        return this;
    }

    public EnhRectangle2D centerOn(Rectangle2D rect) {
        if (rect == this) {
            return this;
        }
        return centerOn(rect.getCenterX(), rect.getCenterY());
    }

    public EnhRectangle2D centerOn(Point2D pt) {
        return centerOn(pt.getX(), pt.getY());
    }

    public EnhRectangle2D centerOn(double cx, double cy) {
        double myCx = getCenterX();
        double myCy = getCenterY();
        if (myCx != cx) {
            x -= myCx - cx;
        }
        if (myCy != cy) {
            y -= myCy - cy;
        }
        return this;
    }

    public EqPointDouble centerLeft() {
        return new EqPointDouble(x, getCenterY());
    }

    public EqPointDouble centerRight() {
        return new EqPointDouble(right(), getCenterY());
    }

    public EqLine diagonalTopLeft() {
        return new EqLine(x, y, right(), bottom());
    }

    public EqLine diagonalBottomRight() {
        return new EqLine(x, bottom(), right(), y);
    }

    public static EnhRectangle2D of(RectangularShape shape) {
        return shape instanceof EnhRectangle2D ? (EnhRectangle2D) shape
                : new EnhRectangle2D(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
    }

    public EnhRectangle2D setCenter(double x, double y) {
        this.x = x - (width / 2);
        this.y = y - (height / 2);
        return this;
    }

    public void setLocation(Point2D loc) {
        this.x = loc.getX();
        this.y = loc.getY();
    }

    @Override
    public Triangle2D[] tesselate() {
        if (isEmpty()) {
            return new Triangle2D[0];
        }
        double cx = getCenterX();
        double cy = getCenterY();
        return new Triangle2D[]{
            new Triangle2D(cx, cy, x, y, x + width, y),
            new Triangle2D(cx, cy, x + width, y, x + width, y + height),
            new Triangle2D(cx, cy, x + width, y + height, x, y + height),
            new Triangle2D(cx, cy, x, y + height, x, y)
        };
    }

    @Override
    public int indexOfTopLeftmostPoint() {
        return 0;
    }

    @Override
    public EnhRectangle2D getBounds2D() {
        return copy();
    }

    public double diagonalLength() {
        return Point2D.distance(x, y, x + width, y + height);
    }

    @Override
    public EnhRectangle2D getFrame() {
        return getBounds2D();
    }

    public EqPointDouble getLocation() {
        return new EqPointDouble(x, y);
    }

    @Override
    public EnhRectangle2D createUnion(Rectangle2D r) {
        return of(super.createUnion(r));
    }

    @Override
    public EnhRectangle2D createIntersection(Rectangle2D r) {
        return of(super.createIntersection(r));
    }

    @Override
    public boolean normalize() {
        return false;
    }

    public EqPointDouble center() {
        return new EqPointDouble(getCenterX(), getCenterY());
    }

    @Override
    public void visitPoints(DoubleBiConsumer consumer) {
        consumer.accept(x, y);
        if (!isEmpty()) {
            consumer.accept(x + width, y);
            consumer.accept(x + width, y + height);
            consumer.accept(x, y + height);
        }
    }

    @Override
    public void visitLines(DoubleQuadConsumer consumer) {
        if (isEmpty()) {
            return;
        }
        consumer.accept(x, y, x + width, y);
        consumer.accept(x, y + width, x + width, y + height);
        consumer.accept(x + width, y + width, x, y + height);
        consumer.accept(x, y + height, x, y);
    }

    @Override
    public void visitAdjoiningLines(DoubleSextaConsumer consumer) {
        consumer.accept(x, y, x + width, y, x + width, y + height);
        consumer.accept(x + width, y, x + width, y + height, x, y + height);
        consumer.accept(x + width, y + height, x, y + height, x, y);
        consumer.accept(x, y + height, x, y, x + width, y);
    }

    @Override
    public boolean isClosed() {
        return true;
    }

    @Override
    public boolean isNormalized() {
        return true;
    }

    @Override
    public boolean selfIntersects() {
        return !isEmpty();
    }

    @Override
    public Point2D topLeftPoint() {
        return new EqPointDouble(x, y);
    }

    @Override
    public Point2D point(int index) {
        switch (index) {
            case 0:
                return new EqPointDouble(x, y);
            case 1:
                return new EqPointDouble(x + width, y);
            case 2:
                return new EqPointDouble(x + width, y + height);
            case 3:
                return new EqPointDouble(x, y + height);
            default:
                throw new IndexOutOfBoundsException("" + index);
        }
    }

    @Override
    public int pointCount() {
        return isEmpty() ? 1 : 4;
    }

    public boolean isSquare() {
        return GeometryUtils.isSameCoordinate(width, height);
    }

    @Override
    public String toString() {
        return GeometryStrings.toString(this);
    }

    public EqLine leftEdge() {
        return new EqLine(x, y, x, bottom());
    }

    public EqLine rightEdge() {
        return new EqLine(right(), y, right(), bottom());
    }

    public EqLine topEdge() {
        return new EqLine(x, y, right(), y);
    }

    public EqLine bottomEdge() {
        return new EqLine(x, bottom(), right(), bottom());
    }

    public void bottomLeft(DoubleBiConsumer c) {
        c.accept(x, y + height);
    }

    public EqPointDouble bottomLeft() {
        return new EqPointDouble(x, y + height);
    }

    public void bottomRight(DoubleBiConsumer c) {
        c.accept(x + width, y + height);
    }

    public EqPointDouble bottomRight() {
        return new EqPointDouble(x + width, y + height);
    }

    public void bottomCenter(DoubleBiConsumer c) {
        c.accept(x + width / 2, y + height);
    }

    public EqPointDouble bottomCenter() {
        return new EqPointDouble(x + width / 2, y + height);
    }

    public void topLeft(DoubleBiConsumer c) {
        c.accept(x, y);
    }

    public EqPointDouble topLeft() {
        return new EqPointDouble(x, y);
    }

    public void topRight(DoubleBiConsumer c) {
        c.accept(x + width, y);
    }

    public EqPointDouble topRight() {
        return new EqPointDouble(x + width, y);
    }

    public void topCenter(DoubleBiConsumer c) {
        c.accept(x + width / 2, y);
    }

    public EqPointDouble topCenter() {
        return new EqPointDouble(x + width / 2, y);
    }

    public EnhRectangle2D growVertically(double by) {
        double half = by / 2;
        y += half;
        height -= by;
        return this;
    }

    public EnhRectangle2D growHorizontally(double by) {
        double half = by / 2;
        x += half;
        width -= by;
        return this;
    }

    public EnhRectangle2D setBottom(double bottom) {
        double newHeight = Math.abs(bottom - y);
        height = newHeight;
        return this;
    }

    public EnhRectangle2D setRight(double right) {
        double newWidth = Math.abs(right - x);
        width = newWidth;
        return this;
    }

    public double bottom() {
        return y + height;
    }

    public double right() {
        return x + width;
    }

    public EnhRectangle2D add(Shape shapeForBounds) {
        add(shapeForBounds.getBounds2D());
        return this;
    }

    public EnhRectangle2D add(Shape shapeForBounds, BasicStroke stroke) {
        return add(shapeForBounds, stroke == null ? 0 : stroke.getLineWidth());
    }

    public EnhRectangle2D grow(double by) {
        double half = by / 2;
        x -= half;
        y -= half;
        width += by;
        height += by;
        if (width < 0) {
            x += width;
            width = -width;
        }
        if (height < 0) {
            y += height;
            height = -height;
        }
        return this;
    }

    public EnhRectangle2D add(Shape shapeForBounds, double stroke) {
        Rectangle2D bds = shapeForBounds.getBounds2D();
        if (shapeForBounds instanceof Line2D) {
            if (bds.getWidth() <= 0 && bds.getHeight() != 0) {
                bds.setFrame(bds.getX() - stroke, bds.getY(), stroke * 2, bds.getHeight());
            } else if (bds.getHeight() == 0 && bds.getWidth() != 0) {
                bds.setFrame(bds.getX(), bds.getY() - stroke, bds.getWidth(), stroke * 2);
            }
        }
        if (bds.isEmpty()) {
            return this;
        }
        if (stroke <= 0) {
            add(bds);
        } else {
            if (isEmpty()) {
                setFrameFromDiagonal(bds.getMinX() - stroke, bds.getMinY() - stroke,
                        bds.getMaxX() + stroke, bds.getMaxY() + stroke);
            } else {
                add(bds.getMinX() - stroke, bds.getMinY() - stroke);
                add(bds.getMaxX() + stroke, bds.getMaxY() + stroke);
            }
        }
        return this;
    }

    public void add(double newX, double newY, double radius) {
        add(newX - radius, newY - radius);
        add(newX + radius, newY + radius);
    }

    @Override
    public void add(double newX, double newY) {
        if (isEmpty()) {
            setFrame(newX - 0.5, newY - 0.5, 1, 1);
        } else {
            double x1 = Math.min(getMinX(), newX);
            double x2 = Math.max(getMaxX(), newX);
            double y1 = Math.min(getMinY(), newY);
            double y2 = Math.max(getMaxY(), newY);
            setRect(x1, y1, x2 - x1, y2 - y1);
        }
    }

    @Override
    public void add(Rectangle2D r) {
        if (isEmpty()) {
            setFrame(r);
        } else {
            super.add(r);
        }
    }

    public Dimension2D getSize() {
        return size();
    }

    public DimensionDouble size() {
        return new DimensionDouble(width, height);
    }
}
