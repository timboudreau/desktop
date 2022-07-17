package com.mastfrog.geometry;

import static com.mastfrog.geometry.Axis.HORIZONTAL;
import com.mastfrog.geometry.util.GeometryStrings;
import com.mastfrog.geometry.util.GeometryUtils;
import static com.mastfrog.util.preconditions.Checks.notNull;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;
import java.util.function.DoubleBinaryOperator;

/**
 * A double precision Dimension.
 *
 * @author Tim Boudreau
 */
public class DimensionDouble extends Dimension2D {

    public double width;
    public double height;

    public DimensionDouble() {

    }

    public DimensionDouble(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public DimensionDouble times(double factor) {
        return new DimensionDouble(width * factor, height * factor);
    }

    public static DimensionDouble of(RectangularShape shape) {
        return new DimensionDouble(notNull("shape", shape).getWidth(),
                shape.getHeight());
    }

    public static DimensionDouble copyOf(Dimension2D d2d) {
        return new DimensionDouble(notNull("d2d", d2d).getWidth(),
                d2d.getHeight());
    }

    public static DimensionDouble of(Dimension2D d2d) {
        if (notNull("d2d", d2d) instanceof DimensionDouble) {
            return ((DimensionDouble) d2d);
        }
        return new DimensionDouble(d2d.getWidth(), d2d.getHeight());
    }

    public DimensionDouble copy() {
        return new DimensionDouble(width, height);
    }

    public boolean isNegative() {
        return width < 0 || height < 0;
    }

    public void add(Dimension2D other) {
        width += other.getWidth();
        height += other.getHeight();
    }

    public boolean isInfinite() {
        return !Double.isFinite(getWidth()) || !Double.isFinite(getHeight());
    }

    public void clear() {
        width = height = 0;
    }

    /**
     * Returns a new DimensionDouble which adds the width of this and another
     * dimension.
     *
     * @param other
     * @return
     */
    public DimensionDouble plus(Dimension2D other) {
        return new DimensionDouble(width + other.getWidth(),
                height + other.getHeight());
    }

    /**
     * Get the area of this dimension.
     *
     * @return An area
     */
    public double area() {
        return width * height;
    }

    /**
     * Get the square root of the area of this dimension.
     *
     * @return The square root of the area
     */
    public double areaRoot() {
        return sqrt(area());
    }

    /**
     * Determine if this dimension is <i>exactly</i> the same as another in
     * area; if you are doing math on dimensions, you may want the variant that
     * takes a tolerance to account for floating point rounding errors.
     *
     * @param other Another dimension
     * @return True if the areas are the same.
     */
    public boolean isSameArea(Dimension2D other) {
        if (other == this) {
            return true;
        }
        double a = other.getWidth() * other.getHeight();
        return GeometryUtils.isSameCoordinate(a, area());
    }

    /**
     * Determine if this dimension has the same area as another within the
     * specified tolerance.
     *
     * @param other Another dimension
     * @param tolerance A tolerance value
     * @return true if <code>abs(area(this) - area(other)) &lt; tolerance</code>
     */
    public boolean isSameArea(Dimension2D other, double tolerance) {
        double a = other.getWidth() * other.getHeight();
        return GeometryUtils.isSameCoordinate(a, area(), tolerance);
    }

    /**
     * Create a new dimension with the same area as this one, but with the same
     * width and height.
     *
     * @return A new dimension
     */
    public DimensionDouble asSquare() {
        double a = areaRoot();
        return new DimensionDouble(a, a);
    }

    /**
     * Determine if the area of this dimension is greater than that of another.
     *
     * @param other
     * @return
     */
    public boolean isGreaterThan(Dimension2D other) {
        return area() > other.getWidth() * other.getHeight();
    }

    /**
     * Get the square root of the distance in area subtracting the area of this
     * dimension from that of the other.
     *
     * @param other Another
     * @return A difference
     */
    public double areaDifferenceRoot(Dimension2D other) {
        return sqrt(area() - other.getWidth() * other.getHeight());
    }

    public double areaDifference(DimensionDouble other) {
        return area() - other.area();
    }

    public double areaFactor(DimensionDouble other) {
        return area() / other.area();
    }

    public DimensionDouble lesserOf(DimensionDouble other) {
        double mine = area();
        double theirs = other.area();
        if (mine <= theirs) {
            return this;
        } else {
            return other;
        }
    }

    public DimensionDouble(Dimension2D dim) {
        this.width = dim.getWidth();
        this.height = dim.getHeight();
    }

    public static DimensionDouble of(Rectangle2D rect) {
        return new DimensionDouble(rect.getWidth(), rect.getHeight());
    }

    public static DimensionDouble square(double value) {
        return new DimensionDouble(value, value);
    }

    public boolean isEmpty() {
        return width <= 0 || height <= 0;
    }

    public DimensionDouble maximumWith(Dimension2D other) {
        double newW = max(other.getWidth(), getWidth());
        double newH = max(other.getHeight(), getHeight());
        return new DimensionDouble(newW, newH);
    }

    /**
     * Combine with another dimension using the specified operators.
     *
     * @param other The other dimension
     * @param width how to combine widths
     * @param height how to combine heights
     * @return A new combined dimension
     */
    public DimensionDouble combineWith(Dimension2D other, DoubleBinaryOperator width, DoubleBinaryOperator height) {
        double w = width.applyAsDouble(this.width, other.getWidth());
        double h = height.applyAsDouble(this.height, other.getHeight());
        return new DimensionDouble(w, h);
    }

    /**
     * Handle common cases of laying out a stack or row of shapes and computing
     * the maximum on one axis and adding on the other.
     *
     * @param other Another dimension
     * @param maximize The dimension to maximize
     * @return A new dimension
     */
    public DimensionDouble add(Dimension2D other, Axis maximize) {
        DoubleBinaryOperator add = (a, b) -> a + b;
        DoubleBinaryOperator max = Math::max;
        return combineWith(other, maximize == HORIZONTAL ? max : add, maximize == HORIZONTAL ? add : max);
    }

    /**
     * Handle common cases of laying out a stack or row of shapes and computing
     * the maximum on one axis and adding on the other, altering this dimension
     * in-place.
     *
     * @param other Another dimension
     * @param maximize The dimension to maximize
     */
    public void addInPlace(Dimension2D other, Axis maximize) {
        DoubleBinaryOperator add = (a, b) -> a + b;
        DoubleBinaryOperator max = Math::max;
        DoubleBinaryOperator w = maximize == HORIZONTAL ? max : add;
        DoubleBinaryOperator h = maximize == HORIZONTAL ? add : max;
        width = w.applyAsDouble(width, other.getWidth());
        height = h.applyAsDouble(height, other.getHeight());
    }

    public double value(Axis axis) {
        switch (axis) {
            case HORIZONTAL:
                return width;
            case VERTICAL:
                return height;
            default:
                throw new AssertionError(axis);
        }
    }

    public void flip() {
        double hold = width;
        width = height;
        height = hold;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return GeometryStrings.toString(width) + " x "
                + GeometryStrings.toString(height);
    }

    public Dimension toDimension() {
        return new Dimension((int) ceil(width), (int) ceil(height));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || !(o instanceof Dimension2D)) {
            return false;
        }
        Dimension2D other = (Dimension2D) o;
        return Double.doubleToLongBits(other.getWidth())
                == Double.doubleToLongBits(getWidth())
                && Double.doubleToLongBits(other.getHeight())
                == Double.doubleToLongBits(getHeight());
    }

    @Override
    public int hashCode() {
        double hash = width + ((width * 71) + (height * 101999D));
        long l = Double.doubleToLongBits(hash);
        return (int) (l ^ (l >>> 32));
    }
}
