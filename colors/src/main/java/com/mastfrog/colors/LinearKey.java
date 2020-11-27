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
package com.mastfrog.colors;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;

/**
 *
 * @author Tim Boudreau
 */
final class LinearKey {

    private final int[] values = new int[4];

    LinearKey(boolean vertical, int dist, Color topColor, Color bottomColor) {
        this(vertical, dist, topColor.getRGB(), bottomColor.getRGB());
    }

    LinearKey(boolean vertical, int dist, int topColor, int bottomColor) {
        this.values[0] = vertical ? -dist : dist;
        this.values[1] = topColor;
        this.values[2] = bottomColor;
        this.values[3] = 7 * Arrays.hashCode(values);
    }

    public boolean isVertical() {
        return values[0] < 0;
    }

    public Rectangle bounds(int dim) {
        if (isVertical()) {
            return new Rectangle(0, 0, dim, -values[0]);
        } else {
            return new Rectangle(0, 0, values[0], dim);
        }
    }

    public interface GradientReceiver<T> {

        T withParams(int nx1, int ny1, Color nTop, int nx2, int ny2, Color nBottom, boolean wasNormalized);
    }

    public static <T> T normalize(int x1, int y1, Color top, int x2, int y2, Color bottom, GradientReceiver<T> r) {
        boolean vert = isVertical(x1, y1, x2, y2);
        if (!vert && !isHorizontal(x1, y1, x2, y2)) {
            return r.withParams(x1, y1, top, x2, y2, bottom, false);
        }
        boolean reversed = vert ? y2 < y1 : x2 < x1;
        int nx1 = reversed ? x2 : x1;
        int nx2 = reversed ? x1 : x2;
        int ny1 = reversed ? y2 : y1;
        int ny2 = reversed ? y1 : y2;
        Color nTop = reversed ? bottom : top;
        Color nBottom = reversed ? top : bottom;
        // normalize
        if (vert) {
            // Vertical - y differs, x the same
            if (nx1 > 0) {
                nx1 = 0;
                nx2 = 0;
            }
            if (ny1 > 0) {
                ny2 -= ny1;
                ny1 = 0;
            }
        } else {
            if (ny1 > 0) {
                ny1 = 0;
                ny2 = 0;
            }
            if (nx1 > 0) {
                nx2 -= nx1;
                nx1 = 0;
            }
        }
        return r.withParams(nx1, ny1, nTop, nx2, ny2, nBottom, true);
    }

    public static LinearKey forGradientSpec(int x1, int y1, Color top, int x2, int y2, Color bottom) {
        return normalize(x1, y1, top, x2, y2, bottom, (cx1, cy1, a, cx2, cy2, b, norm) -> {
            boolean vert = isVertical(x1, y1, x2, y2);
            return norm ? new LinearKey(vert, distance(cx1, cy1, cx2, cy2), a, b) : null;
        });
    }

    public Color topColor() {
        return new Color(values[1], true);
    }

    public Color bottomColor() {
        return new Color(values[2], true);
    }

    static boolean isCacheable(int x1, int y1, int x2, int y2) {
        return isVertical(x1, y1, x2, y2) || isHorizontal(x1, y1, x2, y2);
    }

    static boolean isVertical(int x1, int y1, int x2, int y2) {
        return x1 == x2;
    }

    static boolean isHorizontal(int x1, int y1, int x2, int y2) {
        return y1 == y2;
    }

    static boolean isEmpty(int x1, int y1, int x2, int y2) {
        return isVertical(x1, y1, x2, y2) && isHorizontal(x1, y1, x2, y2);
    }

    static int distance(int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            return Math.abs(y2 - y1);
        } else {
            return Math.abs(x2 - x1);
        }
    }

    @Override
    public int hashCode() {
        return values[3];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof LinearKey) {
            LinearKey gk = (LinearKey) obj;
            return Arrays.equals(values, gk.values);
        }
        return false;
    }

    @Override
    public String toString() {
        return "GradientKey{" + "vertical=" + (values[0] < 0)
                + ", dist=" + Math.abs(values[0])
                + ", topColor=" + GradientUtils.colorToString(new Color(values[1], true))
                + ", bottomColor=" + GradientUtils.colorToString(new Color(values[2], true))
                + '}';
    }
}
