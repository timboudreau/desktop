/*
 * The MIT License
 *
 * Copyright 2022 Mastfrog Technologies.
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
package com.mastfrog.swing.fontsui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import javax.swing.UIManager;

/**
 *
 * @author Tim Boudreau
 */
final class FontImages {

    private final int gap = 5;
    private final String[] names;
    private final int[] positions;
    private final int[] heights;
    private final BufferedImage image;
    private final String lookAndFeelId;
    final int maxWidth;
    final int maxHeight;

    BufferedImage imageFor(Font font) {
        int ix = Arrays.binarySearch(names, font.getName(), (a, b) -> {
            return a.compareToIgnoreCase(b);
        });
        if (ix < 0) {
            return null; // XXX
        }
        int top = positions[ix];
        int height = Math.max(1, heights[ix]);
        return image.getSubimage(0, top, Math.max(1, maxWidth), height);
    }

    static Font[] allFonts() {
        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Font[] result = new Font[names.length];
        for (int i = 0; i < names.length; i++) {
            Font f = new Font(names[i], Font.PLAIN, fontSize());
            result[i] = f;
        }
        return result;
    }
    static Integer FONT_SIZE = null;

    static int fontSize() {
        if (FONT_SIZE != null) {
            return FONT_SIZE;
        }
        String o = System.getProperty("uiFontSize");
        int result = 13;
        if (o != null) {
            try {
                result = Integer.parseInt(o);
                return FONT_SIZE = result;
            } catch (NumberFormatException nfe) {
            }
        }
        Font f = UIManager.getFont("controlFont");
        if (f == null) {
            f = UIManager.getFont("Label.font");
        }
        if (f != null) {
            result = f.getSize();
        }
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("lookAndFeel".equals(evt.getPropertyName())) {
                    FONT_SIZE = null;
                    UIManager.removePropertyChangeListener(this);
                }
            }
        });
        return FONT_SIZE = result;
    }

    private Color fg;

    Color foreground() {
        if (fg == null) {
            fg = UIManager.getColor("ComboBox.foreground");
            if (fg == null) {
                fg = UIManager.getColor("List.foreground");
            }
            if (fg == null) {
                fg = UIManager.getColor("controlText");
            }
            if (fg == null) {
                fg = Color.BLACK;
            }
        }
        return fg;
    }

    FontImages() {
        names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        lookAndFeelId = UIManager.getLookAndFeel().getID();
        Arrays.sort(names, (a, b) -> {
            return a.compareToIgnoreCase(b);
        });
        Font[] fonts = new Font[names.length];
        heights = new int[fonts.length];
        positions = new int[fonts.length];
        int[] widths = new int[fonts.length];
        int[] ascents = new int[fonts.length];
        int minHeight;
        int minWidth;
        int maxHeight;
        int maxWidth;
        minHeight = minWidth = Integer.MAX_VALUE;
        maxHeight = maxWidth = Integer.MIN_VALUE;
        int maxAscent = Integer.MIN_VALUE;
        BufferedImage img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1, 1, Transparency.OPAQUE);
        // Ensure if we are using high dpi scaling, that gets applied to the image's graphics - don't
        // assume BufferedImage.createGraphics() will do the right thing - on Mac OS it will result in
        // 2x scaling
        Graphics2D g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(img);
        try {
            for (int i = 0; i < fonts.length; i++) {
                Font f = new Font(names[i], Font.PLAIN, fontSize());
                fonts[i] = f;
                g.setFont(f);
                FontMetrics fm = g.getFontMetrics(f);
                ascents[i] = fm.getAscent();
                heights[i] = fm.getHeight();
                widths[i] = fm.stringWidth(names[i]);
                maxAscent = Math.max(maxAscent, ascents[i]);
                minWidth = Math.min(widths[i], minWidth);
                maxWidth = Math.max(widths[i], maxWidth);
                minHeight = Math.min(heights[i], minHeight);
                maxHeight = Math.max(heights[i], maxHeight);
            }
        } finally {
            g.dispose();
            img.flush();
        }
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        int imageHeight = (maxHeight + gap) * fonts.length;
        img = image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(2 + maxWidth + 2, imageHeight, Transparency.TRANSLUCENT);
        // Ensure if we are using high dpi scaling, that gets applied to the image's graphics - don't
        // assume BufferedImage.createGraphics() will do the right thing - on Mac OS it will result in
        // 2x scaling
        g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(img);
        int y = 0;
        try {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setColor(foreground());
            for (int i = 0; i < fonts.length; i++) {
                positions[i] = y;
                boolean problematic = widths[i] < 5;
                for (int j = 0; j < names[i].length(); j++) {
                    char c = names[i].charAt(j);
                    if (!fonts[i].canDisplay(c)) {
                        problematic = true;
                        break;
                    }
                }
                if (problematic) {
                    g.setFont(new Font("Times New Roman", Font.ITALIC, fontSize()));
                    FontMetrics fm = g.getFontMetrics();
                    widths[i] = fm.stringWidth(names[i]);
                    heights[i] = fm.getHeight();
                    ascents[i] = fm.getAscent();
                    minWidth = Math.min(minWidth, widths[i]);
                    minHeight = Math.min(minHeight, heights[i]);
                    maxWidth = Math.max(maxWidth, widths[i]);
                    maxHeight = Math.max(maxHeight, heights[i]);
                } else {
                    g.setFont(fonts[i]);
                }
                if (heights[i] < maxHeight) {
                    Font f = g.getFont();
                    double scale = (double) maxHeight / (double) heights[i];
                    f = f.deriveFont(AffineTransform.getScaleInstance(scale, scale));
                    g.setFont(f);
                    ascents[i] = g.getFontMetrics().getAscent();
                    heights[i] = g.getFontMetrics().getHeight();
                }
                int fy = y + ascents[i] + 1;
                g.drawString(names[i], gap, fy);
                y += heights[i] + gap;
            }
        } finally {
            g.dispose();
        }
    }

    public boolean isOutOfSync() {
        return !UIManager.getLookAndFeel().getID().equals(lookAndFeelId);
    }

}
