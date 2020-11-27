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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Tim Boudreau
 */
final class ImageComponent extends JPanel {

    ImageComponent(BufferedImage img, String title, int zoom) {
        setLayout(new BorderLayout());
        JLabel ttl = new JLabel(title);
        ttl.setHorizontalAlignment(SwingConstants.CENTER);
        add(ttl, BorderLayout.NORTH);
        ImageView view = new ImageView(img, zoom);
        view.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        add(view, BorderLayout.CENTER);
    }

    static class ImageView extends JComponent {

        private final BufferedImage img;
        private final float zoom;

        public ImageView(BufferedImage img, float zoom) {
            this.img = img;
            this.zoom = zoom;
            setToolTipText("Image");
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            Insets ins = getInsets();
            int x = (int) ((event.getX() - ins.left) / zoom);
            int y = (int) ((event.getY() - ins.right) / zoom);
            if (x >= 0 && x < img.getWidth() && y >= 0 && y < img.getHeight()) {
                Color px = new Color(img.getRGB(x, y), true);
                return x + "," + y + ": " + colorToString(px);
            }
            return "(off image)";
        }

        public Dimension getPreferredSize() {
            int w = (int) Math.ceil(img.getWidth() * zoom);
            int h = (int) Math.ceil(img.getHeight() * zoom);
            Insets ins = getInsets();
            if (ins != null) {
                w += ins.left + ins.right;
                h += ins.top + ins.bottom;
            }
            return new Dimension(w, h);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        private AffineTransform insetsTransform() {
            Insets ins = getInsets();
            if (ins != null) {
                return AffineTransform.getTranslateInstance(ins.left, ins.top);
            }
            return AffineTransform.getTranslateInstance(0, 0);
        }

        private AffineTransform imageTransform() {
            AffineTransform xform = insetsTransform();
            xform.concatenate(AffineTransform.getScaleInstance(zoom, zoom));
            return xform;
        }

        public void paint(Graphics gr) {
            Graphics2D g = (Graphics2D) gr;
            //                prepareGraphics(g);
            g.drawRenderedImage(img, imageTransform());
            javax.swing.border.Border b = getBorder();
            if (b != null) {
                b.paintBorder(this, gr, 0, 0, getWidth(), getHeight());
            }
        }
    }

}
