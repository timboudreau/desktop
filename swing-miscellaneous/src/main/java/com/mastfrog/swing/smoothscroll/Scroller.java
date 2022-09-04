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
package com.mastfrog.swing.smoothscroll;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Implements animated smooth vertical scrolling over any JScrollPane, with
 * convenience methods for JList. Usage:
 * <pre>
 * JList list = someJList;
 * Scroller.get(list).beginScroll(list, itemNumber);
 * </pre> The scroller instance is retained as a client property of the passed
 * component and can either be held or retrieved with Scroller.get() as needed.
 *
 * @author Tim Boudreau
 */
public final class Scroller {

    private final Rectangle target = new Rectangle();
    private final JScrollPane pane;
    private final JComponent comp;
    private final InnerListener listener = new InnerListener();
    private final Timer timer = new Timer(30, listener);
    private int realTargetHeight;

    @SuppressWarnings(value = "LeakingThisInConstructor")
    Scroller(JComponent comp, JScrollPane pane) {
        this.pane = pane;
        this.comp = comp;
        timer.setCoalesce(false);
        comp.putClientProperty(Scroller.class.getName(), this);
    }

    public static Scroller get(JComponent comp) {
        Scroller s = (Scroller) comp.getClientProperty(Scroller.class.getName());
        if (s == null) {
            JScrollPane pane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, comp);
            if (pane == null) {
                throw new IllegalArgumentException("No scroll pane ancestor of " + comp);
            }
            s = new Scroller(comp, pane);
        }
        return s;
    }

    public void beginScroll(JList<?> l, int index) {
        Rectangle r = l.getCellBounds(index, index);
        realTargetHeight = r.height;
        Rectangle viewBounds = SwingUtilities.convertRectangle(
                pane.getViewport(), pane.getViewport().getViewRect(), comp);
        int rCenterY = r.y + (r.height / 2);
        Rectangle targetRect;
        targetRect = new Rectangle(
                r.x, rCenterY - viewBounds.height / 2, r.width,
                viewBounds.height / 2);
        if (targetRect.y < 0) {
            targetRect.height += targetRect.y;
            targetRect.y = 0;
        }
        beginScroll(targetRect);
    }

    public void abortScroll() {
        done();
    }

    public void beginScroll(Rectangle destination) {
        if (timer.isRunning()) {
            abortScroll();
        }
        if (destination.height <= 0) {
            destination.height = 17;
        } else if (destination.height > 17) {
            destination.y += destination.height / 2;
            destination.height = 17;
        }
        if (realTargetHeight == 0) {
            realTargetHeight = destination.height;
        }
        target.setBounds(destination);
        if (!Arrays.asList(comp.getComponentListeners()).contains(listener)) {
            comp.addComponentListener(listener);
            comp.addMouseWheelListener(listener);
            comp.addMouseListener(listener);
            comp.addPropertyChangeListener("ancestor", listener);
        }
        startTimer();
    }

    void startTimer() {
        BoundedRangeModel vmdl = pane.getVerticalScrollBar().getModel();
        int val = vmdl.getValue();
        timer.start();
    }

    class InnerListener extends ComponentAdapter implements
            ActionListener, MouseWheelListener, MouseListener,
            PropertyChangeListener {

        @Override
        public void componentHidden(ComponentEvent e) {
            done();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            done();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mousePressed(MouseEvent e) {
            done();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // do nothing
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Scroller.this.actionPerformed(e);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("ancestor".equals(evt.getPropertyName())) {
                done();
            }
        }
    }

    int step(int distance) {
        distance = Math.abs(distance) / realTargetHeight;
        int result;
        if (distance > 200) {
            result = realTargetHeight * 60;
        } else if (distance > 40) {
            result = realTargetHeight * 20;
        } else if (distance > 40) {
            result = realTargetHeight * 15;
        } else if (distance > 20) {
            result = realTargetHeight * 10;
        } else if (distance > 15) {
            result = realTargetHeight * 6;
        } else if (distance > 10) {
            result = realTargetHeight * 2;
        } else if (distance > 5) {
            result = realTargetHeight * 1;
        } else if (distance > 3) {
            result = Math.max(1, realTargetHeight / 2);
        } else if (distance > 1) {
            result = Math.max(1, realTargetHeight / 4);
        } else {
            result = 2;
        }
        return result;
    }

    void done() {
        realTargetHeight = 0;
        timer.stop();
        comp.removeComponentListener(listener);
        comp.removeMouseWheelListener(listener);
        comp.removeMouseListener(listener);
        comp.removePropertyChangeListener("ancestor", listener);
    }

    void actionPerformed(ActionEvent e) {
        if (!comp.isDisplayable() || !comp.isVisible() || !comp.isShowing()) {
            timer.stop();
            return;
        }
        BoundedRangeModel vmdl = pane.getVerticalScrollBar().getModel();
        int val = vmdl.getValue();
        int ydist = val - target.y;
        int step = step(val > target.y ? val - target.y : target.y - val);
        if (ydist > 0) {
            int newVal = val - step;
            if (newVal < 0) {
                done();
                return;
            }
            if (newVal < target.y) {
                newVal = target.y;
                done();
            }
            vmdl.setValue(newVal);
        } else if (ydist < 0) {
            int newVal = val + step;
            if (newVal > target.y) {
                newVal = target.y;
                done();
            }
            if (newVal > comp.getHeight()) {
                done();
                return;
            }
            vmdl.setValue(newVal);
        } else {
            done();
        }
    }
    /*
    public static void main(String[] args) {
        DefaultListModel<Integer> m = new DefaultListModel<>();
        for (int i = 0; i < 2000; i++) {
            m.addElement(i);
        }
        EventQueue.invokeLater(() -> {
            JPanel outer = new JPanel(new BorderLayout());
            JPanel pnl = new JPanel(new FlowLayout());
            JList<Integer> l = new JList<>(m);
            JTextArea jta = new JTextArea("500");
            outer.add(new JScrollPane(l), BorderLayout.CENTER);
            outer.add(pnl, BorderLayout.EAST);
            pnl.add(jta);
            JButton go = new JButton("Go");
            pnl.add(go);
            go.addActionListener(ae -> {
                String s = jta.getText();
                int ix = Integer.parseInt(s);
                Scroller.get(l).beginScroll(l, ix);
            });
            JButton zero = new JButton("Zero");
            zero.addActionListener(ae -> {
                Scroller.get(l).beginScroll(l, 0);
            });
            pnl.add(zero);
            JButton fh = new JButton("1500");
            fh.addActionListener(ae -> {
                Scroller.get(l).beginScroll(l, 1500);
            });
            pnl.add(fh);
            JFrame jf = new JFrame();
            jf.setMinimumSize(new Dimension(500, 900));
            jf.setContentPane(outer);
            jf.pack();
            jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            jf.setVisible(true);
        });
    }
     */
}
