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

import com.mastfrog.geometry.util.PooledTransform;
import com.mastfrog.swing.HintSets;
import java.awt.AlphaComposite;
import static java.awt.AlphaComposite.SRC_OVER;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.HierarchyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 * Cell renderer component for fonts, which overrides performance-impacting
 * methods that should not be called on a component that is never actually
 * realized on-screen.
 */
class FontView extends JComponent {

    private FontImages fontImages = new FontImages();
    private static final AlphaComposite DISABLED_COMPOSITE
            = AlphaComposite.getInstance(SRC_OVER, 0.5F);

    private synchronized FontImages images() {
        if (fontImages.isOutOfSync()) {
            fontImages = new FontImages();
        }
        return fontImages;
    }

    private Color background() {
        Color result = UIManager.getColor("List.background");
        if (result == null) {
            result = UIManager.getColor("ComboBox.background");
        }
        if (result == null) {
            result = UIManager.getColor("control");
        }
        if (result == null) {
            result = Color.WHITE;
        }
        return result;
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    @Override
    public String getToolTipText() {
        return targetFont == null ? null : targetFont.getFamily();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D gg = (Graphics2D) g;
        HintSets.DISPLAY.apply(gg);
        g.setColor(background());
        g.fillRect(0, 0, getWidth(), getHeight());
        FontImages images = images();
        Font f = targetFont;
        if (f != null) {
            BufferedImage image = images.imageFor(f);
            if (image != null) {
                AffineTransform xform = null;
                if (image.getHeight() < getHeight()) {
                    xform = PooledTransform.getTranslateInstance(0, (getHeight() - image.getHeight()) / 2, null);
                }
                if (selectionBackground != null) {
                    g.setColor(selectionBackground);
                    g.fillRect(0, 0, images.maxWidth + 5, images.maxHeight + 5);
                    if (paintFocus) {
                        g.drawRect(1, 1, images.maxWidth - 2, images.maxHeight - 2);
                    }
                }
                boolean ena = isEnabled();
                if (ena) {
                    gg.drawRenderedImage(image, xform);
                } else {
                    Composite old = gg.getComposite();
                    try {
                        // No universal way to do this
                        gg.setComposite(DISABLED_COMPOSITE);
                        gg.drawRenderedImage(image, xform);
                    } finally {
                        gg.setComposite(old);
                    }
                }
                
                if (xform != null) {
                    PooledTransform.returnToPool(xform);
                }
            }
        }
    }
    private Color selectionBackground;
    private boolean paintFocus;

    void setSelectionColor(Color selectionBackground) {
        this.selectionBackground = selectionBackground;
    }

    void setPaintFocusIndicator(boolean val) {
        paintFocus = val;
    }
    private Font targetFont;

    void setTargetFont(Font font) {
        targetFont = font;
    }

    @Override
    public Dimension getPreferredSize() {
        FontImages images = images();
        return new Dimension(images.maxWidth, images.maxHeight);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void layout() {
        // do nothing
    }

    @Override
    public void invalidate() {
        // do nothing
    }

    @Override
    public void revalidate() {
        // do nothing
    }

    @Override
    public void repaint() {
        // do nothing
    }

    @Override
    public void repaint(long tm) {
        // do nothing
    }

    @Override
    public void repaint(int x, int y, int width, int height) {
        // do nothing
    }

    @Override
    public void repaint(Rectangle r) {
        // do nothing
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        // do nothing
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        // do nothing
    }

    @Override
    public void addHierarchyListener(HierarchyListener l) {
        // do nothing
    }

    @Override
    public void addContainerListener(ContainerListener l) {
        // do nothing
    }

    @Override
    public synchronized void addComponentListener(ComponentListener l) {
        // do nothing
    }
}
