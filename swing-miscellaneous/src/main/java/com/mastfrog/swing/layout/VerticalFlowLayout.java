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
package com.mastfrog.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.function.Function;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * A simple layout that lays components out vertically from top to bottom.
 * Components that should not be stretched vertically can set their client
 * property "noVStretch" to Boolean.TRUE.
 *
 * @author Tim Boudreau
 */
public final class VerticalFlowLayout implements LayoutManager {

    private final int gap;
    private final boolean useMinimumSizeWhenTooSmall;

    public VerticalFlowLayout() {
        this(-1);
    }

    public VerticalFlowLayout(int gap, boolean useMinimumSizeWhenTooSmall) {
        this.gap = gap;
        this.useMinimumSizeWhenTooSmall = useMinimumSizeWhenTooSmall;
    }

    public VerticalFlowLayout(int gap) {
        this(gap, false);
    }

    private int gap(Container c) {
        if (gap < 0) {
            return defaultGap(c);
        }
        return gap;
    }

    public static int defaultGap(Component c) {
        FontMetrics fm = c.getFontMetrics(c.getFont());
        return (fm.getHeight() / 4) * 3;
    }

    public static Border topGapBorder() {
        return TopGapBorder.INSTANCE;
    }

    private static final class TopGapBorder implements Border {

        static final TopGapBorder INSTANCE = new TopGapBorder();

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            // do nothing
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(defaultGap(c), 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private int expectedLineHeight(Container c) {
        FontMetrics fm = c.getFontMetrics(c.getFont());
        return fm.getMaxAscent() + fm.getMaxDescent() + 4;
    }

    private Dimension computeSize(Container parent, Function<Component, Dimension> f) {
        Insets ins = parent.getInsets();
        Dimension result = new Dimension();
        Component[] comps = parent.getComponents();
        int gap = gap(parent);
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (!c.isVisible()) {
                continue;
            }
            Dimension ps = f.apply(c);
            result.width = Math.max(result.width, ps.width);
            if (i != comps.length - 1) {
                result.height += ps.height + gap;
            } else {
                result.height += ps.height;
            }
        }
        result.width += ins.left + ins.right;
        result.height += ins.top + ins.bottom + gap;
        return result;
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return computeSize(parent, Component::getPreferredSize);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return computeSize(parent, Component::getMinimumSize);
    }

    @Override
    public void layoutContainer(Container parent) {
        Dimension sz = parent.getSize();
        Dimension pref = preferredLayoutSize(parent);
        Component[] comps = parent.getComponents();
        // If everything won't fit at preferred size,
        // use minimum size for everything and pray
        boolean useMinimum = false;
        if (useMinimumSizeWhenTooSmall && sz.height < pref.height) {
            useMinimum = true;
        }
        Insets ins = parent.getInsets();
        int x = ins.left;
        int y = ins.top;
        int bottom = sz.height - ins.bottom;
        int lh = expectedLineHeight(parent);
        int gap = gap(parent);
        for (int i = 0; i < comps.length; i++) {
            Component c = comps[i];
            if (!c.isVisible()) {
                continue;
            }
            Dimension ps = useMinimum ? c.getMinimumSize() : c.getPreferredSize();
            if (x + ps.width < sz.width - ins.right) {
                ps.width = (sz.width - (ins.right)) - x;
            }
            // Give any extra space to the last component
            if (i == comps.length - 1 && y + ps.height < bottom 
                    && isStretchableBottomComponent(comps[i])) {
                c.setBounds(x, y, ps.width, bottom - y);
                break;
            }
            int workingGap = gap;
            int anticipatedBottom = y + lh;
            if (y + ps.height < anticipatedBottom) {
                workingGap += anticipatedBottom - (y + ps.height);
            }
            c.setBounds(x, y, ps.width, ps.height);
            y += ps.height + workingGap;
        }
    }

    private boolean isStretchableBottomComponent(Component c) {
        if (c instanceof JComboBox) {
            return false;
        }
        if (c instanceof JComponent) {
            return Boolean.TRUE.equals(((JComponent) c).getClientProperty("noVStretch"));
        }
        return true;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        // do nothing
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        // do nothing
    }
}
