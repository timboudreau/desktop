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

import com.mastfrog.colors.Gradients.DiagonalGradientPainter;
import static com.mastfrog.colors.ImageTestUtils.assertImages;
import static com.mastfrog.colors.ImageTestUtils.newImage;
import static com.mastfrog.colors.ImageTestUtils.sub;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Tim Boudreau
 */
public class GradientsTest {

    private final Color COLOR_A = new Color(0, 0, 255, 255);
    private final Color COLOR_B = new Color(255, 127, 22, 0);
    Gradients gradients;
    Shape shape;

    @BeforeEach
    public void setup() {
        gradients = new Gradients();
        shape = new Ellipse2D.Double(5, 5, 40, 40);
    }

    @Test
    public void testNonCacheable() throws Throwable {
        BufferedImage expected = newImage(80, 80, g -> {
            DiagonalGradientPainter gp = (DiagonalGradientPainter) gradients.linear(g, 10, 15, COLOR_A, 20, 20, COLOR_B);
            GradientUtils.prepareGraphics(g);
            gp.fill(g, new Rectangle(10, 15, 20, 20));
            DiagonalGradientPainter gp2 = (DiagonalGradientPainter) gradients.linear(g, 10, 15, COLOR_A, 20, 20, COLOR_B);
            assertEquals(gp, gp2);
            assertSame(gp, gp2);

            DiagonalGradientPainter gp3 = (DiagonalGradientPainter) gradients.linear(g, 10, 15, COLOR_A, 20, 20, Color.BLACK);
            assertNotEquals(gp, gp3);
            gp3 = (DiagonalGradientPainter) gradients.linear(g, 11, 10, COLOR_A, 20, 20, COLOR_B);
            assertNotEquals(gp, gp3);
            gp3 = (DiagonalGradientPainter) gradients.linear(g, 10, 11, COLOR_A, 20, 20, COLOR_B);
            assertNotEquals(gp, gp3);
            gp3 = (DiagonalGradientPainter) gradients.linear(g, 10, 15, COLOR_B, 20, 20, Color.BLACK);
            assertNotEquals(gp, gp3);
            gp3 = (DiagonalGradientPainter) gradients.linear(g, 10, 15, COLOR_A, 21, 20, COLOR_B);
            assertNotEquals(gp, gp3);
            gp3 = (DiagonalGradientPainter) gradients.linear(g, 10, 15, COLOR_A, 20, 21, COLOR_B);
            assertNotEquals(gp, gp3);
            gp3 = (DiagonalGradientPainter) gradients.linear(g, 20, 20, COLOR_B, 10, 15, COLOR_A);
            assertNotEquals(gp, gp3);

            gp2 = (DiagonalGradientPainter) gradients.linear(g, 10, 15, COLOR_A, 20, 20, COLOR_B);
            assertSame(gp, gp2);

            for (int i = 0; i < Gradients.MAX_CACHED + 1; i++) {
                gradients.linear(g, 15 + i, 10, Color.ORANGE, 20, 20 + i, Color.BLUE);
            }
            gp3 = (DiagonalGradientPainter) gradients.linear(g, 10, 15, COLOR_A, 20, 20, COLOR_B);
            assertEquals(gp, gp3);
            assertNotSame(gp, gp3);
        });
    }

    @Test
    public void basicTestRadial() throws Throwable {
        BufferedImage expected = newImage(80, 80, g -> {
            RadialGradientPaint rgp = new RadialGradientPaint(10, 10, 10, new float[]{0, 1}, new Color[]{COLOR_A, COLOR_B}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
            GradientUtils.prepareGraphics(g);
            g.setPaint(rgp);
            g.fillRect(10, 10, 20, 20);
        });
        BufferedImage got = newImage(80, 80, g -> {
            GradientPainter rad = gradients.radial(g, 0, 0, COLOR_A, COLOR_B, 10);
            rad.fill(g, 10, 10, 20, 20);
        });
        assertImages(expected, got, (a, b) -> {
            if (a.getAlpha() <= 1 && b.getAlpha() <= 1) {
                if (!a.equals(b)) {
//                    System.out.println("SLIGHT DIFFERENCE: "
//                            + colorToString(a) + " vs " + colorToString(b));
                }
                return true;
            }
            return a.equals(b);
        });
    }

    @Test
    public void basicTestRadialShape() throws Throwable {
        if (true) {
            return;
        }
//        ImageTestUtils.enableVisualAssert();
        BufferedImage expected = newImage(80, 80, g -> {
            RadialGradientPaint rgp = new RadialGradientPaint(10, 10, 10, new float[]{0, 1}, new Color[]{COLOR_A, COLOR_B}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
            GradientUtils.prepareGraphics(g);
            g.setPaint(rgp);
            g.fill(shape);
        });
        BufferedImage got = newImage(80, 80, g -> {
            GradientPainter rad = gradients.radial(g, 0, 0, COLOR_A, COLOR_B, 10);
            rad.fillShape(g, shape);
        });
        assertImages(expected, got, (a, b) -> {
            if (a.getAlpha() <= 1 && b.getAlpha() <= 1) {
                if (!a.equals(b)) {
//                    System.out.println("SLIGHT DIFFERENCE: "
//                            + colorToString(a) + " vs " + colorToString(b));
                }
                return true;
            }
            return a.equals(b);
        });
    }

    @Test
    public void offsetTestRadial() throws Throwable {
//        gradients.onImageCreate = showImage();
        BufferedImage expected = newImage(80, 80, g -> {
            RadialGradientPaint rgp = new RadialGradientPaint(10, 10, 10, new float[]{0, 1}, new Color[]{COLOR_A, COLOR_B}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
            GradientUtils.prepareGraphics(g);
            g.setPaint(rgp);
            g.fillRect(10, 10, 20, 20);
        });
        BufferedImage got = newImage(480, 480, g -> {
            GradientPainter rad = gradients.radial(g, 400, 400, COLOR_A, COLOR_B, 10);
            rad.fill(g, 410, 410, 20, 20);
        });
        got = sub(got, 400, 400, 80, 80);
        assertImages(expected, got, 1);
    }

    @Test
    public void opaqueTestRadial() throws Throwable {
        BufferedImage expected = newImage(80, 80, g -> {
            g.clearRect(0, 0, 80, 80);
            RadialGradientPaint rgp = new RadialGradientPaint(10, 10, 10, new float[]{0, 1}, new Color[]{Color.BLUE, Color.ORANGE}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
            GradientUtils.prepareGraphics(g);
            g.setPaint(rgp);
            g.fillRect(10, 10, 20, 20);
        });
        BufferedImage got = newImage(80, 80, g -> {
            g.clearRect(0, 0, 80, 80);
            GradientPainter rad = gradients.radial(g, 0, 0, Color.BLUE, Color.ORANGE, 10);
            rad.fill(g, 10, 10, 20, 20);
        });
        assertImages(expected, got, 1);
    }

    @Test
    public void basicTestVertical() throws Throwable {
        BufferedImage expected = newImage(80, 80, g -> {
            g.clearRect(0, 0, 80, 80);
            GradientPaint gp = new GradientPaint(10, 10, COLOR_A, 10, 20, COLOR_B, false);
            g.setPaint(gp);
            g.fillRect(10, 10, 50, 50);
        });
        BufferedImage img = newImage(80, 80, g -> {
            g.clearRect(0, 0, 80, 80);
            GradientPainter p = gradients.linear(g, 10, 10, COLOR_A, 10, 20, COLOR_B);
            p.fill(g, new Rectangle(10, 10, 50, 50));
        });
        assertImages(expected, img);
    }

    @Test
    public void basicTestShapeVertical() throws Throwable {
        if (true) {
            return;
        }
//        ImageTestUtils.enableVisualAssert();
        BufferedImage expected = newImage(80, 80, g -> {
            g.clearRect(0, 0, 80, 80);
            GradientPaint gp = new GradientPaint(10, 10, COLOR_A, 10, 20, COLOR_B, false);
            g.setPaint(gp);
            g.fill(shape);
        });
        BufferedImage img = newImage(80, 80, g -> {
            g.clearRect(0, 0, 80, 80);
            GradientPainter p = gradients.linear(g, 10, 10, COLOR_A, 10, 20, COLOR_B);
            p.fillShape(g, shape);
        });
        assertImages(expected, img);
    }

    @Test
    public void testInverseKey() throws Throwable {
        LinearKey.normalize(10, 20, COLOR_B, 10, 10, COLOR_A, (nx1, ny1, nTop, nx2, ny2, nBottom, normed) -> {
            assertTrue(normed);
            assertEquals(0, nx1);
            assertEquals(0, ny1);
            assertSame(COLOR_A, nTop);
            assertSame(COLOR_B, nBottom);
            assertEquals(0, nx2);
            assertEquals(10, ny2);
            return null;
        });
    }

    @Test
    public void inverseTestVertical() throws Throwable {
        BufferedImage expected = newImage(80, 80, g -> {
            GradientPaint gp = new GradientPaint(10, 10, COLOR_B, 10, 20, COLOR_A, false);
            g.setPaint(gp);
            g.fillRect(10, 10, 10, 10);
        });
        BufferedImage got = newImage(80, 80, g -> {
            GradientPainter p = gradients.linear(g, 10, 20, COLOR_A, 10, 10, COLOR_B);
            p.fill(g, new Rectangle(10, 10, 10, 10));
        });
        assertImages(expected, got, 2);
    }

    @Test
    public void inverseTestVerticalWithFill() throws Throwable {
//        gradients.onImageCreate = showImage();
        BufferedImage expected = newImage(80, 80, g -> {
            GradientPaint gp = new GradientPaint(10, 10, COLOR_B, 10, 20, COLOR_A, false);
            g.setPaint(gp);
            g.fillRect(10, 10, 50, 50);
        });
        BufferedImage got = newImage(80, 80, g -> {
            GradientPainter p = gradients.linear(g, 10, 20, COLOR_A, 10, 10, COLOR_B);
            p.fill(g, new Rectangle(10, 10, 50, 50));
        });
//        showImage("Expected", expected, 5);
//        showImage("Got", got, 5);
        assertImages(expected, got, 2);
    }

    @Test
    public void inverseTestVerticalWithTopAndBottomFill() throws Throwable {
//        gradients.onImageCreate = showImage();
        BufferedImage expected = newImage(80, 80, g -> {
            GradientPaint gp = new GradientPaint(10, 10, COLOR_B, 10, 20, COLOR_A, false);
            g.setPaint(gp);
            g.fillRect(10, 10, 50, 50);
        });
        BufferedImage got = newImage(80, 80, g -> {
            GradientPainter p = gradients.linear(g, 10, 20, COLOR_A, 10, 10, COLOR_B);
            p.fill(g, new Rectangle(10, 10, 50, 50));
        });
//        showImage("Expected", expected, 5);
//        showImage("Got", got, 5);
        assertImages(expected, got, 2);
    }

    @Test
    public void inverseTestVerticalWithTopFill() throws Throwable {
//        gradients.onImageCreate = showImage();
        BufferedImage expected = newImage(80, 80, g -> {
            g.clearRect(0, 0, 80, 80);
            GradientPaint gp = new GradientPaint(40, 40, Color.ORANGE, 40, 50, Color.BLUE, false);
            g.setPaint(gp);
            g.fillRect(0, 0, 60, 60);
        });
        BufferedImage got = newImage(80, 80, g -> {
            g.clearRect(0, 0, 80, 80);
            GradientPainter p = gradients.linear(g, 40, 50, Color.BLUE, 40, 40, Color.ORANGE);
            p.fill(g, new Rectangle(0, 0, 60, 60));
        });
//        showImage("Expected", expected, 5);
//        showImage("Got", got, 5);
        assertImages(expected, got, 2);
    }

    @Test
    public void inverseTestHorizontalWithFill() throws Throwable {
//        gradients.onImageCreate = showImage();
        BufferedImage expected = newImage(80, 80, g -> {
            GradientPaint gp = new GradientPaint(10, 10, COLOR_B, 20, 10, COLOR_A, false);
            g.setPaint(gp);
            g.fillRect(10, 10, 50, 50);
        });
        BufferedImage got = newImage(80, 80, g -> {
            GradientPainter p = gradients.linear(g, 20, 10, COLOR_A, 10, 10, COLOR_B);
            p.fill(g, new Rectangle(10, 10, 50, 50));
        });
//        showImage("Expected", expected, 5);
//        showImage("Got", got, 5);
        assertImages(expected, got, 2);
    }

    @Test
    public void inverseTestHorizontalWithFillBoth() throws Throwable {
//        gradients.onImageCreate = showImage();
        BufferedImage expected = newImage(80, 80, g -> {
            GradientPaint gp = new GradientPaint(10, 10, COLOR_B, 20, 10, COLOR_A, false);
            g.setPaint(gp);
            g.fillRect(3, 3, 60, 60);
        });
        BufferedImage got = newImage(80, 80, g -> {
            GradientPainter p = gradients.linear(g, 20, 10, COLOR_A, 10, 10, COLOR_B);
            p.fill(g, new Rectangle(3, 3, 60, 60));
        });
//        showImage("Expected", expected, 5);
//        showImage("Got", got, 5);
        assertImages(expected, got, 2);
    }

    @Test
    public void basicTestHorizontal() throws Throwable {
        BufferedImage expected = newImage(80, 80, g -> {
            GradientPaint gp = new GradientPaint(10, 10, COLOR_A, 20, 10, COLOR_B, false);
            g.setPaint(gp);
            g.fillRect(10, 10, 50, 50);
        });
        BufferedImage img = newImage(80, 80, g -> {
            GradientPainter p = gradients.linear(g, 10, 10, COLOR_A, 20, 10, COLOR_B);
            p.fill(g, new Rectangle(10, 10, 50, 50));
        });
        assertImages(expected, img);
    }

    @Test
    public void basicTestHorizontalShape() throws Throwable {
        if (true) {
            return;
        }
//        ImageTestUtils.enableVisualAssert();
        BufferedImage expected = newImage(80, 80, g -> {
            GradientPaint gp = new GradientPaint(10, 10, COLOR_A, 20, 10, COLOR_B, false);
            g.setPaint(gp);
            g.fill(shape);
        });
        BufferedImage img = newImage(80, 80, g -> {
            GradientPainter p = gradients.linear(g, 10, 10, COLOR_A, 20, 10, COLOR_B);
            p.fillShape(g, shape);
        });
        assertImages(expected, img);
    }

    @Test
    public void testHorizontalPartial() throws Throwable {
        BufferedImage expected = newImage(80, 80, g -> {
            GradientPaint gp = new GradientPaint(10, 10, COLOR_A, 20, 10, COLOR_B, false);
            g.setPaint(gp);
            g.fillRect(15, 15, 18, 40);
        });
        BufferedImage img = newImage(80, 80, g -> {
            GradientPainter p = gradients.linear(g, 10, 10, COLOR_A, 20, 10, COLOR_B);
            p.fill(g, new Rectangle(15, 15, 18, 40));
        });
        assertImages(expected, img);
    }

    @Test
    public void testVerticalPartial() throws Throwable {
        BufferedImage expected = newImage(80, 80, g -> {
            GradientPaint gp = new GradientPaint(10, 10, COLOR_A, 10, 20, COLOR_B, false);
            g.setPaint(gp);
            g.fillRect(15, 15, 40, 18);
        });
        BufferedImage img = newImage(80, 80, g -> {
            GradientPainter p = gradients.linear(g, 10, 10, COLOR_A, 10, 20, COLOR_B);
            p.fill(g, new Rectangle(15, 15, 40, 18));
        });
        assertImages(expected, img);
    }

    @Test
    public void testCompletelyOutsideRaster() throws Throwable {
        BufferedImage img = newImage(400, 400, g -> {
            GradientPainter gg = gradients.linear(g, 230, 230, Color.ORANGE, 250, 230, COLOR_B);
            gg.fill(g, 232, 233, 10, 10);
            gg.fill(g, 252, 253, 20, 20);
        });
    }

    @Test
    public void testGradientOffRaster() throws Throwable {
        BufferedImage img = newImage(400, 400, g -> {
            gradients.radial(g, 60, 80, COLOR_A, COLOR_B, 16)
                    .fill(g, 50, 70, 20, 20);

            gradients.radial(g, 100, 100, COLOR_A, Color.RED, 30)
                    .fill(g, 105, 105, 40, 40);
        });
    }
}
