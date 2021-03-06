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

import com.mastfrog.geometry.util.GeometryStrings;
import com.mastfrog.geometry.util.GeometryUtils;
import java.awt.geom.Line2D;
import static java.lang.Math.atan2;
import static java.lang.Math.ceil;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import java.util.function.Consumer;

/**
 * Safe type for a normalized angle in degrees, assuming 0\u00b0 is 12 o'clock.
 *
 * @author Tim Boudreau
 */
public final strictfp class Angle implements Comparable<Angle> {

    private final double degrees;
    public static final Angle ZERO = new Angle(0);

    Angle(double degrees) {
        assert degrees >= 0 && degrees < 360;
        this.degrees = degrees;
    }

    public boolean isAxial() {
        return degrees == 0 || degrees == 180 || degrees == 90 || degrees == 270;
    }

    public boolean isAxial(double tolerance) {
        return axis(tolerance) != null;
    }

    public Axis axis(double tolerance) {
        if (GeometryUtils.isSameCoordinate(0, degrees, tolerance) || GeometryUtils.isSameCoordinate(180, degrees, tolerance)) {
            return Axis.VERTICAL;
        } else if (GeometryUtils.isSameCoordinate(90, degrees, tolerance) || GeometryUtils.isSameCoordinate(270, degrees, tolerance)) {
            return Axis.HORIZONTAL;
        }
        return null;
    }

    public Axis axis() {
        if (degrees == 0 || degrees == 180) {
            return Axis.VERTICAL;
        } else if (degrees == 90 || degrees == 270) {
            return Axis.HORIZONTAL;
        }
        return null;
    }

    public Angle translatedTo(Quadrant quadrant) {
        Quadrant curr = quadrant();
        if (quadrant == curr) {
            return this;
        }
        return ofDegrees(quadrant.translate(curr, degrees));
    }

    public static boolean isSameHemisphere(double a, double b) {
        return Hemisphere.forAngle(a) == Hemisphere.forAngle(b);
    }

    public boolean isSameHemisphere(Angle other) {
        return isSameHemisphere(this.degrees, other.degrees);
    }

    public boolean isSameHemisphere(double other) {
        return isSameHemisphere(degrees, other);
    }

    public static Angle maxValue() {
        return new Angle(359D + 0.999999999999971);
    }

    public boolean equals(double degrees, double tolerance) {
        return GeometryUtils.isSameCoordinate(degrees, this.degrees, tolerance);
    }

    public boolean equals(Angle other, double tolerance) {
        return other == this ? true : equals(other.degrees, tolerance);
    }

    public static Angle ofDegrees(double degrees) {
        if (!Double.isFinite(degrees)) {
            return ZERO;
        }
        if (degrees == 0.0 || degrees == -0.0 || degrees == 360.0 || degrees == -360.0) {
            return ZERO;
        }
        return new Angle(normalize(degrees));
    }

    /**
     * Create an angle equivalent to this one, but with an angle less than 180.
     *
     * @return this or an equivalent angle
     */
    public Angle toCanonicalAngle() {
        if (degrees >= 180) {
            return ofDegrees(degrees - 180);
        }
        return this;
    }

    /**
     * Normalize an angle in degrees, then subtract 180 if it is greater than
     * 180.
     *
     * @param angle An angle
     * @return An angle between 0 and 180
     */
    public static double canonicalize(double angle) {
        angle = normalize(angle);
        if (angle > 180) {
            angle -= 180;
        }
        return angle;
    }

    public static Angle zero() {
        return ZERO;
    }

    /**
     * Convert an angle to a fraction between 0 and 1 which can be multiplied by
     * 360.
     *
     * @param degrees An angle in degrees
     * @return A fraction
     */
    public static double asFraction(double degrees) {
        return normalize(degrees) / 360;
    }

    public static boolean isSameHemisphere(double a, double b, Axis axis) {
        Quadrant qa = Quadrant.forAngle(a);
        Quadrant qb = Quadrant.forAngle(b);
        if (qa == qb) {
            return true;
        }
        if (qa == qb.opposite()) {
            return false;
        }
        return qa.trailingAxis() == axis ? qb.leadingAxis() == axis
                : qb.trailingAxis() == axis;
    }

    /**
     * Normalize an angle into the range 0 to 360.
     *
     * @param degrees An angle in degrees
     * @return The same angle, normalized
     */
    public static double normalize(double degrees) {
        if (degrees == 0 || degrees == -0.0 || degrees == 360 || degrees == -360) {
            return 0;
        }
        if (degrees >= 0 && degrees < 360) {
            return degrees;
        }
        if (degrees < 0) {
            long integralPortion = (long) Math.ceil(degrees);
            double fractionalPortion = integralPortion - degrees;
            integralPortion %= 360;
            if (integralPortion == 0) {
                return fractionalPortion == 0 ? 0 : 360 - fractionalPortion;
            } else if (fractionalPortion == 0) {
                return integralPortion == 0 ? 0 : 360 + integralPortion;
            }
            degrees = 360D + (integralPortion - fractionalPortion);
        } else {
            long integralPortion = (long) Math.floor(degrees);
            double frationalPortion = degrees - integralPortion;
            integralPortion %= 360;
            degrees = integralPortion + frationalPortion;
        }
        return degrees;
    }

    public static double complement(double degrees) {
        Quadrant quad = Quadrant.forAngle(degrees);
        return quad.next().translate(quad, degrees);
    }

    public Angle complement() {
        return Angle.ofDegrees(complement(degrees));
    }

    public boolean isReflex() {
        return degrees >= 180;
    }

    /**
     * Return the angle 180 degrees opposite to the passed one.
     *
     * @param angle An angle in degrees
     * @return The opposite angle in degrees between 0 and 360.
     */
    public static double opposite(double angle) {
        if (!Double.isFinite(angle)) {
            return angle;
        }
        if (angle < 0) {
            angle = 360D + angle;
        }
        if (angle > 360) {
            angle = 360D % angle;
        }
        if (angle == 0D || angle == 360D) {
            return 180D;
        } else if (angle == 180D) {
            return 0;
        } else if (angle == 90D) {
            return 270D;
        }
        if (angle >= 360 || angle < 0) {
            angle = normalize(angle);
        }
        if (angle > 180D) {
            return angle - 180D;
        } else {
            return angle + 180D;
        }
    }

    public static Angle forLine(Line2D line) {
        return forLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public static Angle forLine(double x1, double y1, double x2, double y2) {
        return Angle.ofDegrees(ofLine(x1, y1, x2, y2));
    }

    public static double ofLine(double x1, double y1, double x2, double y2) {
        return normalize(Circle.angleOf(x1, y1, x2, y2));
    }

    public static void angles(int count, Consumer<Angle> c) {
        double curr = 0;
        double step = 360 / count;
        while (curr < 360) {
            c.accept(new Angle(curr));
            curr += step;
        }
    }

    public static void angles(double start, int count, Consumer<Angle> c) {
        double curr = normalize(start);
        double step = 360 / count;
        while (curr < 360) {
            c.accept(ofDegrees(curr));
            curr += step;
        }
    }

    public Angle ceiling() {
        return ofDegrees(ceil(degrees));
    }

    public Angle floor() {
        return ofDegrees(Math.floor(degrees));
    }

    public Angle round() {
        return ofDegrees((int) Math.round(degrees));
    }

    public Angle times(double multiplier) {
        return ofDegrees(degrees * multiplier);
    }

    public Angle dividedBy(double divideBy) {
        return ofDegrees(degrees / divideBy);
    }

    public Angle degreesTo(Angle other) {
        double diff = other.degrees - degrees;
        if (diff == 0) {
            return ZERO;
        }
        if (diff < 0) {
            diff = opposite(-diff);
        }
        return new Angle(diff);
    }

    public Angle degreesFrom(Angle other) {
        double diff = degrees - other.degrees;
        if (diff == 0) {
            return ZERO;
        }
        if (diff < 0) {
            diff = opposite(-diff);
        }
        return new Angle(diff);
    }

    public Angle gapWith(Angle ang, boolean leading) {
        double a = min(degrees, ang.degrees);
        double b = max(degrees, ang.degrees);
        if (a == b) {
            return ZERO;
        }
        double result = a + ((b - a) / 2D);
        return ofDegrees(result);
    }

    public static double angleBetween(double angleA, double angleB) {
        if (angleA == angleB) {
            return angleA;
        }
        angleA = normalize(angleA);
        angleB = normalize(angleB);
        double a = min(angleA, angleB);
        double b = max(angleA, angleB);
        return a + ((b - a) / 2D);
    }

    public static double addAngles(double angleA, double angleB) {
        return normalize(angleA + angleB);
    }

    public static double subtractAngles(double from, double subtract) {
        return normalize(from - subtract);
    }

    public static double averageAngle(double a, double b) {
        double ra = toRadians(a);
        double rb = toRadians(b);
        double x = cos(ra) + cos(rb);
        double y = sin(ra) + sin(rb);
        double result = toDegrees(atan2(y, x));
        return result == 0 ? 0 : (result + 360D) % 360D;
    }

    public static double averageAngle(double a, double b, double c) {
        double ra = toRadians(a);
        double rb = toRadians(b);
        double rc = toRadians(c);
        double x = cos(ra) + cos(rb) + cos(rc);
        double y = sin(ra) + sin(rb) + sin(rc);
        double result = toDegrees(atan2(y, x));
        return result == 0 ? 0 : (result + 360D) % 360D;
    }

    public static double average(double... angles) {
        double x = 0;
        double y = 0;
        for (int i = 0; i < angles.length; i++) {
            double rad = toRadians(angles[i]);
            x += cos(rad);
            y += sin(rad);
        }
        double result = toDegrees(atan2(x, y));
        return result == 0 ? 0 : (result + 360D) % 360D;
    }

    public static Angle ofRadians(double radians) {
        return ofDegrees(toRadians(radians));
    }

    public static double perpendicularClockwise(double angle) {
        if (!Double.isFinite(angle)) {
            return angle;
        }
        angle = normalize(angle);
        if (angle + 90D > 360) {
            angle = (angle + 90D) - 360;
        } else {
            angle += 90D;
        }
        return normalize(angle);
    }

    public static double perpendicularCounterclockwise(double angle) {
        if (!Double.isFinite(angle)) {
            return angle;
        }
        angle = normalize(angle);
        if (angle - 90D < 0) {
            angle = 360D + (angle - 90D);
        } else {
            angle -= 90D;
        }
        return normalize(angle);
    }

    public Angle minus(double degrees) {
        return new Angle(normalize(this.degrees - degrees));
    }

    public Angle plus(double degrees) {
        return new Angle(normalize(this.degrees + degrees));
    }

    public Angle plus(Angle other) {
        return new Angle(normalize(degrees + other.degrees));
    }

    public double degrees() {
        return degrees;
    }

    public double radians() {
        return toRadians(degrees);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof Angle) {
            Angle a = (Angle) o;
            return degrees == a.degrees
                    || degrees + 0.0 == a.degrees + 0.0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        long hash = Double.doubleToLongBits(degrees + 0.0);
        return (int) (hash ^ hash >> 32);
    }

    @Override
    public String toString() {
        return GeometryStrings.toString(degrees) + "\u00B0";
    }

    @Override
    public int compareTo(Angle o) {
        return Double.compare(degrees, o.degrees);
    }

    public Quadrant quadrant() {
        return Quadrant.forAngle(degrees);
    }
}
