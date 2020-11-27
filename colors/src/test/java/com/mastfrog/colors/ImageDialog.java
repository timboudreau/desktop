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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *
 * @author Tim Boudreau
 */
final class ImageDialog extends JDialog {

    @SuppressWarnings(value = "OverridableMethodCallInConstructor")
    ImageDialog(String msg, BufferedImage expect, BufferedImage got, BufferedImage diff) {
        setLayout(new BorderLayout());
        JPanel beforeAfter = new JPanel(new GridLayout(1, 2));
        beforeAfter.add(new ImageComponent(expect, "Expected", 6));
        beforeAfter.add(new ImageComponent(got, "Got", 6));
        add(beforeAfter, BorderLayout.NORTH);
        add(new ImageComponent(diff, "Difference", 6), BorderLayout.CENTER);
        setTitle(msg);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (onDone != null) {
            onDone.run();
            onDone = null;
        }
    }
    Runnable onDone;

    void display(Runnable onDone) {
        this.onDone = onDone;
        EventQueue.invokeLater(() -> {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            pack();
            setLocation(new Point(40, 40));
            setVisible(true);
        });
    }
}
