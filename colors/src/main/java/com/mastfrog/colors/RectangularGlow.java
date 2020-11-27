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
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Paints a gradient around the edges of a rectangle, such that the same color
 * is always on the outer edge.
 *
 * @author Tim Boudreau
 */
public final class RectangularGlow implements GradientPainter {

    final Color dark;
    final Color light;
    final Gradients gradients;
    final int glowWidth;

    public RectangularGlow(Color dark, Color light, Gradients gradients, int glowWidth) {
        this.dark = dark;
        this.light = light;
        this.gradients = gradients;
        this.glowWidth = glowWidth;
    }

    @Override
    public void fill(Graphics2D g, Rectangle r) {
        if (r == null || r.width == 0 || r.height == 0) {
            return;
        }
        Rectangle rect = new Rectangle(r.x + glowWidth, r.y, r.width - (glowWidth * 2), glowWidth);
        painter(Region.TOP, rect, g).fill(g, rect);
        rect.translate(0, r.height - (glowWidth));
        painter(Region.BOTTOM, rect, g).fill(g, rect);
        rect.translate(-glowWidth, -(r.height - glowWidth));
        rect.width += glowWidth * 2;
        rect.x = r.x;
        rect.y = r.y + glowWidth;
        rect.width = glowWidth;
        rect.height = r.height - (glowWidth * 2);
        painter(Region.LEFT, rect, g).fill(g, rect);
        rect.translate(r.width - glowWidth, 0);
        painter(Region.RIGHT, rect, g).fill(g, rect);
        rect.x = r.x;
        rect.y = r.y;
        rect.width = glowWidth;
        rect.height = glowWidth;
        painter(Region.TOP_LEFT, rect, g).fill(g, rect);
        rect.translate(r.width - glowWidth, 0);
        painter(Region.TOP_RIGHT, rect, g).fill(g, rect);
        rect.translate(-(r.width - glowWidth), r.height - glowWidth);
        painter(Region.BOTTOM_LEFT, rect, g).fill(g, rect);
        rect.translate(r.width - glowWidth, 0);
        painter(Region.BOTTOM_RIGHT, rect, g).fill(g, rect);
    }

    GradientPainter painter(Region side, Rectangle r, Graphics2D g) {
        int glowSize = Math.min(r.width, r.height) - 1;
        switch (side) {
            case TOP:
                return gradients.linear(g, r.x, r.y + glowSize, dark, r.x, r.y, light);
            case LEFT:
                return gradients.linear(g, r.x, r.y, light, r.x + glowSize, r.y, dark);
            case BOTTOM:
                return gradients.linear(g, r.x, r.y, dark, r.x, r.y + glowSize, light);
            case RIGHT:
                return gradients.linear(g, r.x, r.y, dark, r.x + glowSize, r.y, light);
            case TOP_LEFT:
                return gradients.radial(g, r.x, r.y, dark, light, glowSize);
            case TOP_RIGHT:
                return gradients.radial(g, r.x - glowSize, r.y, dark, light, glowSize);
            case BOTTOM_LEFT:
                return gradients.radial(g, r.x, r.y - glowSize, dark, light, glowSize);
            case BOTTOM_RIGHT:
                return gradients.radial(g, r.x - glowSize, r.y - glowSize, dark, light, glowSize);
            default:
                throw new AssertionError(side);
        }
    }

    private enum Region {
        LEFT, TOP, RIGHT, BOTTOM, TOP_LEFT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_RIGHT
    }

}
