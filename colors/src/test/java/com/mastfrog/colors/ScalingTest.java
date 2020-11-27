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

import static com.mastfrog.colors.ImageTestUtils.assertImages;
import static com.mastfrog.colors.ImageTestUtils.newImage;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Tim Boudreau
 */
public class ScalingTest {

    private final Color COLOR_A = new Color(0, 0, 255, 255);
    private final Color COLOR_B = new Color(255, 127, 22, 0);
    Gradients gradients;

    @BeforeEach
    public void setup() {
        gradients = new Gradients();
//        gradients.onImageCreate = showImage();
    }

    @Test
    public void testScaling() throws Throwable {
        if (true) {
            return;
        }
        BufferedImage expected = scaledImage(80, 80, 2, g -> {
            g.clearRect(0, 0, 80, 80);
            GradientPaint gp = new GradientPaint(10, 10, COLOR_A, 10, 20, COLOR_B, false);
            g.setPaint(gp);
            g.fillRect(10, 10, 50, 50);
        });
        BufferedImage img = newImage(160, 160, g -> {
            g.scale(2, 2);
            g.clearRect(0, 0, 80, 80);
            GradientPainter p = gradients.linear(g, 10, 10, COLOR_A, 10, 20, COLOR_B);
            p.fill(g, new Rectangle(10, 10, 50, 50));
        });
        assertImages(expected, img);
    }

    private BufferedImage scaledImage(int w, int h, int scale, Consumer<Graphics2D> c) {
        return newImage(w * scale, h * scale, gr -> {
            gr.scale(scale, scale);
            c.accept(gr);
        });
    }

    private BufferedImage scaledGradient(int w, int h, int scale, Consumer<Graphics2D> c) {
        return newImage(w * scale, h * scale, gr -> {
            gr.scale(scale, scale);
            c.accept(gr);
        });
    }
}
