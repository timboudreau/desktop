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
package com.mastfrog.swing.activity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * A small balloon-like component which, which triggered, starts in a point,
 * expands out to the bounds of the component and fades away as it does - used
 * to indicate background parsing or similar regeneration of something that
 * happens periodically, where it's useful to indicate to the user that something
 * happened, and do so less intrusively than a cycling progress bar.
 *
 * @author Tim Boudreau
 */
public final class ActivityIndicator extends JComponent implements ActionListener {

    private final Dimension size;
    private final Timer timer = new Timer(35, this);
    private static final int MAX_TICKS = 120;
    private int tick;
    private final int[] rgb = new int[3];

    public ActivityIndicator(int size) {
        this.size = new Dimension(size, size);
    }

    public ActivityIndicator() {
        this(32);
        setOpaque(true);
        setBackground(UIManager.getColor("control"));
        setForeground(UIManager.getColor("textText"));
        timer.setCoalesce(false);
        timer.setRepeats(true);
        setFocusable(false);
    }

//
//    @Override
//    public void doLayout() {
//        // do nothing
//    }
//
//    @Override
//    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
//        // do nothing
//    }

    private float tickValue() {
        float max = MAX_TICKS;
        float t = tick;
        return (max - t) / max;
    }

    private float revTickValue() {
        float max = MAX_TICKS;
        float t = tick;
        float res = t / max;
//        return res + (res * (res / 3F));
        return res + (res / 1.5F);
    }

    Color color() {
        Color fg = getForeground();
        if (tick == 0 || !EventQueue.isDispatchThread()) {
            return fg;
        }
        Color bg = getBackground();
        float tv = tickValue();
        int alph = Math.max(0, (int) (tv * 255));
        for (int i = 0; i < 3; i++) {
            int fgc, bgc;
            switch (i) {
                case 0:
                    fgc = fg.getRed();
                    bgc = bg.getRed();
                    break;
                case 1:
                    fgc = fg.getGreen();
                    bgc = bg.getGreen();
                    break;
                case 2:
                    fgc = fg.getBlue();
                    bgc = bg.getBlue();
                    break;
                default:
                    throw new AssertionError();
            }
            int diff = fgc - bgc;
            int adj = (int) ((float) diff * tv);
            rgb[i] = Math.max(0, Math.min(255, bgc + adj));
        }
        return new Color(rgb[0], rgb[1], rgb[2], alph);
    }

    private static final Map<?, ?> HINTS
            = Collections.singletonMap(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    public void paint(Graphics g) {
        if (timer.isRunning()) {
            int sz = (int) Math.min(size.width, (size.width * revTickValue()));
            ((Graphics2D) g).addRenderingHints(HINTS);
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(color());
            int s2 = sz / 2;
            int xy = ((size.width / 2) - s2);
            g.fillRoundRect(xy, xy, sz, sz, size.width, size.width);
            g.drawRoundRect(xy, xy, sz, sz, size.width, size.width);
        } else {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void done() {
        timer.stop();
        tick = 0;
    }

    public void trigger() {
        tick = 0;
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    @Override
    public void removeNotify() {
        timer.stop();
        tick = 0;
        super.removeNotify();
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
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
    public void actionPerformed(ActionEvent e) {
        if (tick++ > MAX_TICKS) {
            done();
        }
        paintImmediately(0, 0, getWidth(), getHeight());
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            JPanel pnl = new JPanel(new FlowLayout());
            ActivityIndicator ind = new ActivityIndicator(24);
            JButton button = new JButton("Trigger");
            pnl.add(ind);
            pnl.add(button);
            button.addActionListener(ae -> {
                ind.trigger();
            });
            JFrame jf = new JFrame("Indicator demo");
            jf.setContentPane(pnl);
            jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            jf.pack();
            jf.setVisible(true);
        });
    }
}
