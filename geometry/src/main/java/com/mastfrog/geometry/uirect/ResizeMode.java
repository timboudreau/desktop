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
package com.mastfrog.geometry.uirect;

import com.mastfrog.geometry.EqPointDouble;
import java.awt.geom.Point2D;

/**
 *
 * @author Tim Boudreau
 */
public enum ResizeMode {
    TOP_EDGE, LEFT_EDGE, RIGHT_EDGE, BOTTOM_EDGE, NORTHWEST, NORTHEAST, SOUTHEAST, SOUTHWEST;

    public static ResizeMode forCornerConst(int c) {
        switch (c) {
            case MutableRectangle2D.NW:
                return NORTHWEST;
            case MutableRectangle2D.SE:
                return SOUTHEAST;
            case MutableRectangle2D.SW:
                return SOUTHWEST;
            default:
                return NORTHEAST;
        }
    }

    public int cornerConst() {
        switch (this) {
            case NORTHEAST:
                return MutableRectangle2D.NE;
            case NORTHWEST:
                return MutableRectangle2D.NW;
            case SOUTHEAST:
                return MutableRectangle2D.SE;
            case SOUTHWEST:
                return MutableRectangle2D.SW;
            default:
                return MutableRectangle2D.ANY;
        }
    }

    public ResizeMode apply(double x, double y, MutableRectangle2D rect) {
        switch (this) {
            case NORTHWEST:
            case SOUTHEAST:
            case SOUTHWEST:
            case NORTHEAST:
                int cc = cornerConst();
                EqPointDouble pt = new EqPointDouble(x, y);
                rect.setPoint(pt, cc);
                return forCornerConst(rect.nearestCorner(pt));
            case LEFT_EDGE:
                double off = x - rect.x;
                rect.x = x;
                rect.width -= off;
                if (rect.width < 0) {
                    rect.x += rect.width;
                    rect.width = -rect.width;
                    return RIGHT_EDGE;
                }
                break;
            case RIGHT_EDGE:
                double off1 = (rect.x + rect.width) - x;
                rect.width -= off1;
                if (rect.width < 0) {
                    rect.x += rect.width;
                    rect.width = -rect.width;
                    return LEFT_EDGE;
                }
                break;
            case TOP_EDGE:
                double off2 = y - rect.y;
                rect.y = y;
                rect.height -= off2;
                if (rect.height < 0) {
                    rect.y += rect.height;
                    rect.height = -rect.height;
                    return BOTTOM_EDGE;
                }
                break;
            case BOTTOM_EDGE:
                double off3 = (rect.y + rect.height) - y;
                rect.height -= off3;
                if (rect.height < 0) {
                    rect.y += rect.height;
                    rect.height = -rect.height;
                    return TOP_EDGE;
                }
                break;
            default:
        }
        return this;
    }

    public static ResizeMode forRect(double x, double y, double hitRadius, MutableRectangle2D rect) {
        int corn = rect.nearestCorner(x, y);
        hitRadius *= 2;
        switch (corn) {
            case MutableRectangle2D.ANY:
            case MutableRectangle2D.NONE:
                break;
            default:
                Point2D pt = rect.getPoint(corn);
                double dist = pt.distance(x, y);
                if (dist <= hitRadius) {
                    switch (corn) {
                        case MutableRectangle2D.NE:
                            return NORTHEAST;
                        case MutableRectangle2D.NW:
                            return NORTHWEST;
                        case MutableRectangle2D.SE:
                            return SOUTHEAST;
                        case MutableRectangle2D.SW:
                            return SOUTHWEST;
                        default:
                            throw new AssertionError("Corner " + corn);
                    }
                }
        }
        if (Math.abs(x - rect.x) < hitRadius) {
            return LEFT_EDGE;
        } else if (Math.abs(x - (rect.x + rect.width)) < hitRadius) {
            return RIGHT_EDGE;
        } else if (Math.abs(y - rect.y) < hitRadius) {
            return TOP_EDGE;
        } else if (Math.abs(y - (rect.y + rect.height)) < hitRadius) {
            return BOTTOM_EDGE;
        }
        return null;
    }

}
