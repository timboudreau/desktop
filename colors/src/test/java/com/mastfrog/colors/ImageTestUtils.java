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

import static com.mastfrog.colors.GradientUtils.colorToString;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author Tim Boudreau
 */
public final class ImageTestUtils {

    private static boolean visualAssert = false;
//            && (Boolean.getBoolean("visualAssert")
//            && !Boolean.getBoolean("java.awt.headless"));

    static boolean visualAssert() {
        return visualAssert && !Boolean.getBoolean("java.awt.headless");
    }

    static void enableVisualAssert() {
        visualAssert = true;
    }

    public static void assertNotEmpty(BufferedImage a) {
        assertNotEmpty(a, new Rectangle(0, 0, a.getWidth(), a.getHeight()));
    }

    public static void assertNotEmpty(BufferedImage a, Rectangle aRect) {
        int expected = -1;
        boolean variationFound = false;
        for (int y = 0; y < aRect.height; y++) {
            for (int x = 0; x < aRect.width; x++) {
                int col = a.getRGB(aRect.x + x, aRect.y + y);
                if (y == 0 && x == 0) {
                    expected = col;
                } else {
                    if (col != expected) {
                        variationFound = true;
                        break;
                    }
                }
            }
        }
        assertTrue(variationFound, "Image pixels are all the same color");
    }

    static boolean defaultCompareColors(Color a, Color b) {
        if (a.getAlpha() <= 1 && b.getAlpha() <= 1) {
            return true;
        }
        return a.getRed() == b.getRed() && a.getGreen() == b.getGreen()
                && a.getBlue() == b.getBlue() && a.getAlpha() == b.getAlpha();
    }

    static BiPredicate<Color, Color> toleranceCompareColors(int tolerance) {
        return (a, b) -> {
            int tol = Math.abs(tolerance);
            if (defaultCompareColors(a, b)) {
                return true;
            }
            int rDiff = Math.abs(a.getRed() - b.getRed());
            int gDiff = Math.abs(a.getGreen() - b.getGreen());
            int bDiff = Math.abs(a.getBlue() - b.getBlue());
            int aDiff = Math.abs(a.getBlue() - b.getBlue());
            return aDiff <= tol && rDiff <= tol
                    && gDiff <= tol && bDiff <= tol;
        };
    }

    public static void assertImages(BufferedImage a, BufferedImage b, int tol) throws Throwable {
        assertImages(a, b, toleranceCompareColors(tol));
    }

    public static void assertImages(BufferedImage a, BufferedImage b) throws Throwable {
        assertImages(a, b, ImageTestUtils::defaultCompareColors);
    }

    public static void assertImages(BufferedImage a, BufferedImage b, BiPredicate<Color, Color> comparer) throws Throwable {
        assertImages(a, new Rectangle(0, 0, a.getWidth(), a.getHeight()), b, new Rectangle(0, 0, b.getWidth(), b.getHeight()), comparer);
    }

    public static void assertImages(BufferedImage a, Rectangle aRect, BufferedImage b, Rectangle bRect) throws Throwable {
        assertImages(a, aRect, b, bRect, ImageTestUtils::defaultCompareColors);
    }

    public static void assertImages(BufferedImage a, Rectangle aRect, BufferedImage b, Rectangle bRect, BiPredicate<Color, Color> comparer) throws Throwable {
        assertNotEmpty(a, aRect);
        assertNotEmpty(b, bRect);
        assert aRect.width == bRect.width : aRect.width + " vs " + bRect.width;
        assert aRect.height == bRect.height : aRect.height + " vs " + bRect.height;
        for (int y = 0; y < aRect.height; y++) {
            for (int x = 0; x < aRect.width; x++) {
                int expect = a.getRGB(aRect.x + x, aRect.y + y);
                int got = b.getRGB(bRect.x + x, bRect.y + y);
                Color expectColor = new Color(expect, true);
                Color gotColor = new Color(got, true);
                String msg = compareColors(expectColor, gotColor, comparer,
                        "Colors differ at " + x + "," + y);
                if (msg != null && visualAssert()) {
                    showDifference(msg, a, aRect, b, bRect);
                }
                if (msg != null) {
                    fail(msg);
                }
            }
        }
    }

    public static BufferedImage sub(BufferedImage a, int x, int y, int w, int h) {
        return sub(a, new Rectangle(x, y, w, h));
    }

    public static BufferedImage sub(BufferedImage a, Rectangle aRect) {
        if (aRect.x == 0 && aRect.y == 0 && aRect.width == a.getWidth() && aRect.height == a.getHeight()) {
            return a;
        }
        int w = aRect.width;
        if (aRect.x + aRect.width > a.getWidth()) {
            fail("Passed " + aRect + " but image width is " + a.getWidth() + " and that would require " + (aRect.x + aRect.width));
            w = a.getWidth() - aRect.x;
        }
        int h = aRect.height;
        if (aRect.x + aRect.height > a.getHeight()) {
            fail("Passed " + aRect + " but image height is " + a.getHeight());
            h = a.getHeight() - aRect.y;
        }
        return a.getSubimage(aRect.x, aRect.y, w, h);
    }

    private static int[] rgbaDiff(Color a, Color b) {
        int[] result = new int[4];
        result[0] = a.getRed() - b.getRed();
        result[1] = a.getGreen() - b.getGreen();
        result[2] = a.getBlue() - b.getBlue();
        result[3] = a.getAlpha() - b.getAlpha();
        return result;
    }

    private static float[] hsbDiff(Color a, Color b) {
        float[] af = new float[3];
        float[] bf = new float[3];
        Color.RGBtoHSB(a.getRed(), a.getGreen(), a.getBlue(), af);
        Color.RGBtoHSB(b.getRed(), b.getGreen(), b.getBlue(), bf);
        for (int i = 0; i < af.length; i++) {
            af[i] -= bf[i];
        }
        return af;
    }

    public static String compareColors(Color expect, Color got, String msg) {
        return compareColors(expect, got, ImageTestUtils::defaultCompareColors, msg);
    }

    public static String compareColors(Color expect, Color got, BiPredicate<Color, Color> comparer, String msg) {
        if (!comparer.test(expect, got)) {
            return msg + ": " + colorToString(got) + " expected " + colorToString(expect)
                    + " rgbaDiff: " + Arrays.toString(rgbaDiff(expect, got))
                    + " hsbDiff: " + Arrays.toString(hsbDiff(expect, got));
        }
        return null;
    }

    public static BufferedImage newImage(int width, int height, Consumer<Graphics2D> c) {
        BufferedImage result = newImage(width, height);
        paintInto(result, c);
        return result;
    }

    public static BufferedImage newImage(int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return result;
    }

    public static Consumer<BufferedImage> showImage() {
        return img -> {
            try {
                ImageTestUtils.showImage(img.getWidth() + "," + img.getHeight() + " cm " + img.getColorModel(), img, 6);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        };
    }

    private static void showDifference(String msg, BufferedImage a, Rectangle aRect, BufferedImage b, Rectangle bRect) throws InterruptedException {
        BufferedImage aSub = sub(a, aRect);
        BufferedImage bSub = sub(b, bRect);
        BufferedImage diff = diff(aSub, aRect, bSub, bRect);
        ImageDialog dlg = new ImageDialog(msg, aSub, bSub, diff);
        CountDownLatch latch = new CountDownLatch(1);
        dlg.display(latch::countDown);
        latch.await();
    }

    public static void showImages(String title, int zoom, BufferedImage... imgs) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        EventQueue.invokeLater(() -> {
//            ImageComponent comp = new ImageComponent(img, title, zoom);
            JFrame jf = new JFrame(title) {
                @Override
                public void removeNotify() {
                    super.removeNotify(); //To change body of generated methods, choose Tools | Templates.
                    latch.countDown();
                }
            };
            jf.setLayout(new FlowLayout());
            for (BufferedImage bi : imgs) {
                ImageComponent comp = new ImageComponent(bi, title, zoom);
                jf.add(comp);
            }
            jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jf.pack();
            jf.setLocation(new Point(100, 100));
            jf.setVisible(true);
        });
        latch.await();

    }

    public static void showImage(String title, BufferedImage img, int zoom) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        EventQueue.invokeLater(() -> {
            ImageComponent comp = new ImageComponent(img, title, zoom);
            JFrame jf = new JFrame(title) {
                @Override
                public void removeNotify() {
                    super.removeNotify(); //To change body of generated methods, choose Tools | Templates.
                    latch.countDown();
                }
            };
            jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jf.setContentPane(comp);
            jf.pack();
            jf.setLocation(new Point(100, 100));
            jf.setVisible(true);
        });
        latch.await();
    }

    public static void paintInto(BufferedImage img, Consumer<Graphics2D> c) {
        Graphics2D g = img.createGraphics();
        prepareGraphics(g);
        try {
            c.accept(g);
        } finally {
            g.dispose();
        }
    }

    static void prepareGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    }

    public static BufferedImage diff(BufferedImage a, Rectangle aRect, BufferedImage b, Rectangle bRect) {
        BufferedImage aSub = sub(a, aRect);
        BufferedImage bSub = sub(b, bRect);
        BufferedImage nue = new BufferedImage(Math.max(aRect.width, bRect.width), Math.max(aRect.height, bRect.height), BufferedImage.TYPE_INT_ARGB);
        paintInto(nue, (g) -> {
            g.drawRenderedImage(aSub, null);
            g.setComposite(new ColorDiffComposite());
            g.drawRenderedImage(bSub, null);
            g.setComposite(AlphaComposite.SrcOver.derive(0.1f));
            g.drawRenderedImage(aSub, null);
            g.drawRenderedImage(bSub, null);
        });
        return nue;
    }

    private ImageTestUtils() {
        throw new AssertionError();
    }
}
