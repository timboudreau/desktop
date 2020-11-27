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
package com.mastfrog.swing.cursor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author Tim Boudreau
 */
interface CursorProperties {

    Color shadow();

    Color primary();

    Color attention();

    Color warning();

    default Color darkerMainColor() {
        return CursorUtils.isDarker(shadow(), primary()) ? shadow() : primary();
    }

    default Color lighterMainColor() {
        return !CursorUtils.isDarker(shadow(), primary()) ? shadow() : primary();
    }

    int width();

    int height();

    default BasicStroke shadowStroke() {
        return new BasicStroke((width() / 8F) + 1);
    }

    default BasicStroke shadowStrokeThin() {
        return new BasicStroke(mainStroke().getLineWidth() * 1.5F);
    }

    default BasicStroke mainStroke() {
        return new BasicStroke(width() / 16F);
    }

    default BasicStroke wideShadowStroke() {
        return new BasicStroke((width() / 8F) + 2);
    }

    default BasicStroke wideMainStroke() {
        return new BasicStroke(height() / 16F);
    }

    default int centerX() {
        return width() / 2;
    }

    default int centerY() {
        return height() / 2;
    }

    int edgeOffset();

    int cornerOffset();

    int minimumHollow();

    CursorProperties scaled(double by);

    CursorProperties scaled(double by, GraphicsConfiguration config);

    boolean isDarkBackground();

    Graphics2D hint(Graphics2D g);

    default Cursor createCursor(String name, int hitX, int hitY, BiConsumer<Graphics2D, CursorProperties> c) {
        Cursor result = createCursor(name, hitX, hitY, g -> {
            c.accept(g, this);
        });
        return result;
    }

    default Cursor createCursor(String name, int hitX, int hitY, Consumer<Graphics2D> c) {
        BufferedImage img = createCursorImage(c);
        return Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(hitX, hitY), name);
    }

    default CursorProperties warningVariant() {
        Color newColor = Color.RED;
        return CursorUtils.isDarker(primary(), shadow()) ? withColors(newColor, shadow()) : withColors(primary(), newColor);
    }

    default CursorProperties attentionVariant() {
        Color newColor = Color.BLUE;
        return CursorUtils.isDarker(primary(), shadow()) ? withColors(newColor, shadow()) : withColors(primary(), newColor);
    }

    default CursorProperties creationVariant() {
        Color newColor = new Color(0, 128, 0);
        return CursorUtils.isDarker(primary(), shadow()) ? withColors(newColor, shadow()) : withColors(primary(), newColor);
    }

    default CursorProperties withColors(Color primary, Color shadow) {
        return this;
    }

    CursorProperties withSize(int w, int h);

    BufferedImage createCursorImage(Consumer<Graphics2D> c);

}
