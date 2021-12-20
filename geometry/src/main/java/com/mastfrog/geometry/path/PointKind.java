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
package com.mastfrog.geometry.path;

/**
 * Enumeration of the kinds of points that can be part of a Java2D shape.
 *
 * @author Tim Boudreau
 */
public enum PointKind {

    QUADRATIC_FIRST_CONTROL_POINT,
    QUADRATIC_DESTINATION_POINT,
    CUBIC_FIRST_CONTROL_POINT,
    CUBIC_SECOND_CONTROL_POINT,
    CUBIC_DESTINATION_POINT,
    LINE_DESTINATION_POINT,
    MOVE_DESTINATION_POINT,
    /**
     * Some uses of this library involve point types that are not part of
     * the traditional Java2D panoply, so this is a placeholder value for
     * those.
     */
    OTHER;

    public int pointIndex() {
        switch (this) {
            case LINE_DESTINATION_POINT:
            case MOVE_DESTINATION_POINT:
            case CUBIC_FIRST_CONTROL_POINT:
            case QUADRATIC_FIRST_CONTROL_POINT:
                return 0;
            case CUBIC_SECOND_CONTROL_POINT:
            case QUADRATIC_DESTINATION_POINT:
                return 2;
            case CUBIC_DESTINATION_POINT:
                return 3;
            case OTHER :
                return -1;
            default:
                throw new AssertionError(this);
        }
    }

    public boolean isCurvePoint() {
        switch(this) {
            case LINE_DESTINATION_POINT :
            case MOVE_DESTINATION_POINT :
            case OTHER :
                return false;
            default :
                return true;
        }
    }

    public int arrayPositionOffset() {
        if (this == OTHER) {
            return -1;
        }
        return pointIndex() * 2;
    }

    public PathElementKind elementKind() {
        switch (this) {
            case QUADRATIC_FIRST_CONTROL_POINT:
            case QUADRATIC_DESTINATION_POINT:
                return PathElementKind.QUADRATIC;
            case CUBIC_FIRST_CONTROL_POINT:
            case CUBIC_SECOND_CONTROL_POINT:
            case CUBIC_DESTINATION_POINT:
                return PathElementKind.CUBIC;
            case LINE_DESTINATION_POINT:
                return PathElementKind.LINE;
            case MOVE_DESTINATION_POINT:
                return PathElementKind.MOVE;
            case OTHER :
                return null;
            default:
                throw new AssertionError(this);
        }
    }

    public boolean isDestination() {
        switch (this) {
            case CUBIC_DESTINATION_POINT:
            case LINE_DESTINATION_POINT:
            case QUADRATIC_DESTINATION_POINT:
            case MOVE_DESTINATION_POINT:
            case OTHER :
                return true;
            default:
                return false;
        }
    }
}
