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

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 *
 * @author Tim Boudreau
 */
final class BufferedImagePaint implements Paint {

    private final BufferedImage img;
    private final boolean vertical;

    BufferedImagePaint(BufferedImage img, boolean vertical) {
        this.img = img;
        this.vertical = vertical;
    }

    @Override
    public int getTransparency() {
        return Transparency.TRANSLUCENT;
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        return new PC(cm, deviceBounds, userBounds, xform, hints);
    }

    class PC implements PaintContext {

        private final ColorModel cm;
        private final Rectangle deviceBounds;
        private final Rectangle2D userBounds;
        private final AffineTransform xform;
        private final RenderingHints hints;

        private PC(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
            this.cm = cm;
            this.deviceBounds = deviceBounds;
            this.userBounds = userBounds;
            this.xform = xform;
            this.hints = hints;
        }

        @Override
        public void dispose() {
        }

        @Override
        public ColorModel getColorModel() {
            return img.getColorModel();
        }

        @Override
        public Raster getRaster(int x, int y, int w, int h) {
            // This works, but for the wrong reason - we
            // are being called once for every scan line.
            // Why???
            Raster orig = img.getRaster();
            WritableRaster r = getColorModel()
                    .createCompatibleWritableRaster(w, h);
            int filled = 0;
            orig = orig.createTranslatedChild(-x, -y);
            while (filled < (!vertical ? h : w)) {
                int fx = !vertical ? 0 : x + filled;
                int fy = !vertical ? y + filled : 0;
                r.setRect(fx, fy, orig);
                filled += !vertical ? orig.getHeight() : orig.getWidth();
            }
            return r;
        }
    }
}
