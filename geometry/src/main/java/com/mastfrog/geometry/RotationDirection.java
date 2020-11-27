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

/**
 * Direction of motion around a circle.
 *
 * @author Tim Boudreau
 */
public enum RotationDirection {

    /**
     * Clockwise rotation.
     */
    CLOCKWISE,
    /**
     * Counter clockwise rotation.
     */
    COUNTER_CLOCKWISE,
    /**
     * Rotation cannot be computed (used when, say, returning the difference
     * between two angles that are the same).
     */
    NONE;

    @Override
    public String toString() {
        switch (this) {
            case CLOCKWISE:
                return "cw";
            case COUNTER_CLOCKWISE:
                return "ccw";
            case NONE:
                return "none";
            default:
                throw new AssertionError(this);
        }
    }

    /**
     * Adjust an angle by the passed amount in this direction.
     *
     * @param angle The angle
     * @param byDegrees The degrees
     * @return Another angle
     */
    public double adjustAngle(double angle, double byDegrees) {
        switch (this) {
            case CLOCKWISE:
                return Angle.addAngles(angle, byDegrees);
            case COUNTER_CLOCKWISE:
                return Angle.subtractAngles(angle, byDegrees);
            case NONE:
                return angle;
            default:
                throw new AssertionError(this);
        }
    }

    /**
     * Get the opposite direction of rotation.
     *
     * @return The opposite
     */
    public RotationDirection opposite() {
        switch (this) {
            case CLOCKWISE:
                return COUNTER_CLOCKWISE;
            case COUNTER_CLOCKWISE:
                return CLOCKWISE;
            default:
                return this;
        }
    }
}
