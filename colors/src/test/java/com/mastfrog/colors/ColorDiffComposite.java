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

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

/**
 *
 * @author Tim Boudreau
 */
final class ColorDiffComposite implements Composite, CompositeContext {

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return this;
    }

    @Override
    public void dispose() {
        // do nothing
    }
    int[] WHITE = new int[]{0, 255, 0, 255};

    @Override
    public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
        int w = Math.min(dstIn.getWidth(), src.getWidth());
        int h = Math.min(dstIn.getHeight(), src.getHeight());
        int[] scratchA = new int[4];
        int[] scratchB = new int[4];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                src.getPixel(x, y, scratchA);
                dstIn.getPixel(x, y, scratchB);
                if (!Arrays.equals(scratchA, scratchB)) {
                    for (int i = 0; i < scratchA.length - 1; i++) {
                        int diff = scratchA[i] - scratchB[i];
                        int val = Math.max(0, Math.min(255, 128 + diff));
                        scratchA[i] = val;
                    }
                    scratchA[3] = 255;
                    dstOut.setPixel(x, y, scratchA);
                } else {
                    dstOut.setPixel(x, y, WHITE);
                }
            }
        }
    }

}
