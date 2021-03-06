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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 *
 * @author Tim Boudreau
 */
public class GeometryStrings {

    static final DecimalFormat DEGREES_FMT = new DecimalFormat("######################0.0#################################\u00b0");
    static final DecimalFormat FMT_SHORT = new DecimalFormat("######################0.00");
    static final String DEFAULT_COORD_DELIMITER = ", ";
    static final String DEFAULT_WIDTH_HEIGHT_DELIMITER = " * ";
    static final String DEFAULT_PAIR_DELIMITER = " / ";
    static final DecimalFormat DEGREES_FMT_2PLACE = new DecimalFormat("######################0.00\u00b0");
    static final DecimalFormat FMT = new DecimalFormat("######################0.0#################################");

    /**
     * Format a decimal number to two decimal places
     *
     * @param value A number
     * @return A string representation of the number, rounded
     */
    public static String toShortString(double value) {
        String result = FMT_SHORT.format(value);
        if (result.endsWith(".00")) {
            result = result.substring(0, result.length() - 3);
        }
        return result;
    }

    /**
     * Format a pair of coordinates to two decimal places
     *
     * @param value A number
     * @return A string representation of the number, rounded
     */
    public static String toShortString(double x, double y) {
        return toShortString(x) + DEFAULT_COORD_DELIMITER + toShortString(y);
    }

    public static StringBuilder toStringCoordinates(StringBuilder into, double... coords) {
        return toStringCoordinates(FMT, DEFAULT_COORD_DELIMITER, DEFAULT_PAIR_DELIMITER, into, coords);
    }

    public static String toStringCoordinates(double... coords) {
        int count = (9 * coords.length) + (DEFAULT_PAIR_DELIMITER.length() * (coords.length - 1)) + (DEFAULT_COORD_DELIMITER.length() * coords.length);
        return toStringCoordinates(FMT, DEFAULT_COORD_DELIMITER, DEFAULT_PAIR_DELIMITER, new StringBuilder(count), coords).toString();
    }

    public static StringBuilder toStringCoordinates(String pairDelimiter, StringBuilder into, double... coords) {
        return toStringCoordinates(FMT, DEFAULT_COORD_DELIMITER, pairDelimiter, into, coords);
    }

    public static StringBuilder toStringCoordinates(String coordDelimiter, String pairDelimiter, StringBuilder into, double... coords) {
        return toStringCoordinates(FMT, coordDelimiter, pairDelimiter, into, coords);
    }

    public static StringBuilder toStringCoordinates(DecimalFormat fmt, String coordDelimiter, String pairDelimiter, StringBuilder into, double... coords) {
        assert coords.length % 2 == 0;
        for (int i = 0; i < coords.length; i += 2) {
            into.append(fmt.format(coords[i]));
            into.append(coordDelimiter);
            into.append(fmt.format(coords[i + 1]));
            if (i != coords.length - 2) {
                into.append(pairDelimiter);
            }
        }
        return into;
    }

    public static String toString(Rectangle r) {
        return r.x + DEFAULT_COORD_DELIMITER + r.y + " " + r.width + DEFAULT_WIDTH_HEIGHT_DELIMITER + r.height;
    }

    /**
     * Format a decimal number as a string in long decimal format (no
     * exponential notation).
     *
     * @param value A number
     * @return A string representation of the number, rounded
     */
    public static String toString(double value) {
        return FMT.format(value);
    }

    public static String toString(Rectangle2D r) {
        return toString(r.getX(), r.getY()) + " " + toString(DEFAULT_WIDTH_HEIGHT_DELIMITER, r.getWidth(), r.getHeight());
    }

    public static String toCoordinatesString(Rectangle2D r) {
        return toString(r.getX(), r.getY())
                + " / " + toString(r.getX() + r.getWidth(), r.getY() + r.getHeight());
    }

    /**
     * Convert an array of doubles to a comma-delimited, long-format string.
     *
     * @param dbls An array of doubles
     * @return A string
     */
    public static StringBuilder toString(double... dbls) {
        return toString(new StringBuilder(dbls.length * 8), dbls);
    }

    public static StringBuilder toString(StringBuilder sb, double... dbls) {
        return toString(sb, DEFAULT_COORD_DELIMITER, dbls);
    }

    public static String toString(Point pt) {
        if (pt == null) {
            return "<null>";
        }
        return pt.x + DEFAULT_COORD_DELIMITER + pt.y;
    }

    public static String toString(Point2D pt) {
        if (pt == null) {
            return "<null>";
        }
        return toString(pt.getX(), pt.getY());
    }

    public static StringBuilder toString(StringBuilder sb, String delim, double... dbls) {
        for (int i = 0; i < dbls.length; i++) {
            sb.append(FMT.format(dbls[i]));
            if (i != dbls.length) {
                sb.append(delim);
            }
        }
        return sb;
    }

    public static String toString(String delim, double... dbls) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dbls.length; i++) {
            sb.append(FMT.format(dbls[i]));
            if (i != dbls.length) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    public static String toString(double a, double b) {
        return toString(DEFAULT_COORD_DELIMITER, a, b);
    }

    public static String toString(String delim, double a, double b) {
        return FMT.format(a) + delim + FMT.format(b);
    }

    /**
     * Format an angle in degrees to two decimal places.
     *
     * @param degrees An angle
     * @return A string representation of the angle, rounded to two decimal
     * places
     */
    public static String toDegreesStringShort(double degrees) {
        return DEGREES_FMT_2PLACE.format(degrees);
    }

    /**
     * Provides a standard string representation for a line.
     *
     * @param x1 The first x coordinate
     * @param y1 The first y coordinate
     * @param x2 The second x coordinate
     * @param y2 The second y coordinate
     * @return A string representation of the line
     */
    public static String lineToString(double x1, double y1, double x2, double y2) {
        return "<" + toString(x1, y1) + " : " + toString(x2, y2) + ">";
    }

    public static StringBuilder toStringCoordinatesShort(StringBuilder into, double... coords) {
        return toStringCoordinates(FMT_SHORT, DEFAULT_COORD_DELIMITER, DEFAULT_PAIR_DELIMITER, into, coords);
    }

    public static String toStringCoordinatesShort(double... coords) {
        int count = (4 * coords.length) + (DEFAULT_PAIR_DELIMITER.length() * (coords.length - 1)) + (DEFAULT_COORD_DELIMITER.length() * coords.length);
        return toStringCoordinates(FMT_SHORT, DEFAULT_COORD_DELIMITER, DEFAULT_PAIR_DELIMITER, new StringBuilder(count), coords).toString();
    }

    public static StringBuilder toStringCoordinatesShort(String pairDelimiter, StringBuilder into, double... coords) {
        return toStringCoordinates(FMT_SHORT, DEFAULT_COORD_DELIMITER, pairDelimiter, into, coords);
    }

    public static StringBuilder toStringCoordinatesShort(String coordDelimiter, String pairDelimiter, StringBuilder into, double... coords) {
        return toStringCoordinates(FMT_SHORT, coordDelimiter, pairDelimiter, into, coords);
    }

    /**
     * Format an angle in degrees in long decimal format.
     *
     * @param degrees An angle
     * @return A string representation of the angle
     */
    public static String toDegreesString(double degrees) {
        return DEGREES_FMT.format(degrees);
    }

    public static String transformToString(AffineTransform xform) {
        if (xform == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("(");
        sb.append(typeString(xform.getType())).append(' ');
        double[] mx = new double[6];
        xform.getMatrix(mx);
        doubleArrayToString(mx, sb);
        return sb.append(')').toString();
    }

    private static StringBuilder doubleArrayToString(double[] dbls, StringBuilder into) {
        for (int i = 0; i < dbls.length; i++) {
            into.append(toString(dbls[i]));
            if (i != dbls.length) {
                into.append(", ");
            }
        }
        return into;
    }

    private static String typeString(int type) {
        switch (type) {
            case AffineTransform.TYPE_FLIP:
                return "Flip";
            case AffineTransform.TYPE_GENERAL_ROTATION:
                return "General Rotation";
            case AffineTransform.TYPE_GENERAL_SCALE:
                return "General Scale";
            case AffineTransform.TYPE_GENERAL_TRANSFORM:
                return "General Transform";
            case AffineTransform.TYPE_IDENTITY:
                return "Identity";
            case AffineTransform.TYPE_QUADRANT_ROTATION:
                return "Quadrant Rotation";
            case AffineTransform.TYPE_TRANSLATION:
                return "Translation";
            case AffineTransform.TYPE_UNIFORM_SCALE:
                return "Uniform Scale";
            case AffineTransform.TYPE_MASK_ROTATION:
                return "Rotation";
            case AffineTransform.TYPE_MASK_SCALE:
                return "Scale";
            default:
                return "Unknown";
        }
    }

    private GeometryStrings() {
        throw new AssertionError();
    }
}
