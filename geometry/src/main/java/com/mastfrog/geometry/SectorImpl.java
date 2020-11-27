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

/**
 *
 * @author Tim Boudreau
 */
final class SectorImpl implements Sector {

    private final double startingAngle;
    private final double extent;

    SectorImpl(double startingAngle, double extent) {
        this.startingAngle = Angle.normalize(startingAngle);
        this.extent = Math.min(360, Math.abs(extent));
    }

    @Override
    public double start() {
        return startingAngle;
    }

    @Override
    public double extent() {
        return extent;
    }

    @Override
    public String toString() {
        return GeometryStrings.toDegreesString(startingAngle)
                + " / " + GeometryStrings.toDegreesString(Angle.normalize(startingAngle + extent))
                + " (" + GeometryStrings.toDegreesString(extent) + ")";
    }

    @Override
    public PieWedge toShape(double x, double y, double radius) {
        System.out.println("CREATE SHAPE FOR " + start() + " / " + extent());
        return new PieWedge(x, y, radius, start(), extent());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof Sector) {
            Sector s = (Sector) o;
            return GeometryUtils.isSameCoordinate(startingAngle, s.start())
                    && GeometryUtils.isSameCoordinate(extent, s.extent());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (isEmpty()) {
            return 0;
        }
        long hash = 51 * Double.doubleToLongBits(startingAngle + 0.0);
        hash = 51 * hash
                + Double.doubleToLongBits(extent + 0.0);
        return (int) (hash ^ (hash >> 32));
    }
}
