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
package com.mastfrog.geometry;

import com.mastfrog.geometry.util.GeometryUtils;
import static com.mastfrog.geometry.util.GeometryUtils.arraySizeForType;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import static java.awt.geom.PathIterator.SEG_CLOSE;
import static java.awt.geom.PathIterator.SEG_CUBICTO;
import static java.awt.geom.PathIterator.SEG_LINETO;
import static java.awt.geom.PathIterator.SEG_MOVETO;
import static java.awt.geom.PathIterator.WIND_NON_ZERO;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * For indicating angled segments of leading circle; Arc2D is not quite right
 * for this, since it is not angle-oriented but bounds-oriented, and can be
 * irregular, so it is of little help if you are starting with an angle and
 * leading distance.
 * <p>
 * <b>Negative angles and extents:</b> Angles and extents may be negative; a
 * negative angle is converted to its positive equivalent via Angle.normalize()
 * (so an angle of -10 degrees is equivalent to 350 degrees). A negative extent
 * indicates that the sweep of degrees covered is the inverted, starting at
 * <code>angle + extent</code> (which will be less than <code>angle</code>) and
 * extending through <code>angle</code>.
 * </p><p>
 * Values for extent and angle &gt;360 or &lt;-360 will be normalized.
 * </p>
 *
 * @author Tim Boudreau
 */
public class PieWedge extends AbstractShape implements Sector {

    private final Circle circ;
    private double angle;
    boolean pie = true;
    private double extent;

    public PieWedge() {
        this(0, 0, 1, 0, 0);
    }

    public PieWedge(double cx, double cy, double radius, double angle, double extent) {
        this(cx, cy, radius, angle, extent, true);
    }

    public PieWedge(double cx, double cy, double radius, double angle, double extent, boolean pie) {
        circ = new Circle(cx, cy, radius);
        setAngleAndExtent(angle, extent);
        this.pie = pie;
    }

    public PieWedge translate(double x, double y) {
        circ.translate(x, y);
        return this;
    }

    @Override
    public String toString() {
        return "Angle(" + circ.centerX + "," + circ.centerY
                + " r" + circ.radius()
                + " a" + angle + "\u00B0"
                + " to " + (angle + extent) + "\u00B0"
                + " = \u0394" + extent + "\u00B0"
                + ")";
    }

    public boolean isLinesIncluded() {
        return pie;
    }

    public double radius() {
        return circ.radius;
    }

    public double angle() {
        return angle;
    }

    @Override
    public double extent() {
        return extent;
    }

    public PieWedge add(double degrees) {
        angle = Angle.normalize(angle + degrees);
        return this;
    }

    public PieWedge setAngle(double angle) {
        this.angle = Angle.normalize(angle);
        return this;
    }

    public PieWedge setAngleAndExtent(double angle, double ext) {
        if (ext < 0) {
            while (ext < 0) { // XXX
                ext += 360;
            }
            this.extent = Angle.normalize(ext);
            this.angle = Angle.normalize(angle);
        } else {
            this.angle = Angle.normalize(angle);
            this.extent = Angle.normalize(ext);
        }
        return this;
    }

    public PieWedge setAngleAndExtent(Sector sector) {
        return setAngleAndExtent(sector.start(), sector.extent());
    }

    public PieWedge setFrom(LineVector vect) {
        setCenter(vect.apexX(), vect.apexY());
        setRadius(Math.max(vect.trailingLineLength(), vect.leadingLineLength()));
        return setAngleAndExtent(vect.corner());
    }

    public PieWedge setExtent(double ext) {
        while (ext < 0) {
            ext += 360;
        }
        this.extent = ext;
        return this;
    }

    public PieWedge setCenter(double centerX, double centerY) {
        circ.setCenter(centerX, centerY);
        return this;
    }

    public PieWedge setCenterX(double centerX) {
        circ.setCenter(centerX, circ.centerY());
        return this;
    }

    public PieWedge setCenterY(double centerY) {
        circ.setCenter(circ.centerX(), centerY);
        return this;
    }

    public double getCenterX() {
        return circ.centerX();
    }

    public double getCenterY() {
        return circ.centerY();
    }

    public EqPointDouble getCenter() {
        return new EqPointDouble(circ.centerX(), circ.centerY());
    }

    public PieWedge setRadius(double radius) {
        circ.setRadius(radius);
        return this;
    }

    @Override
    public <T extends Rectangle2D> T addToBounds(T into) {
        if (into.isEmpty()) {
            into.setFrame(circ.centerX, circ.centerY, 1, 1);
        } else {
            into.add(circ.centerX, circ.centerY);
        }
        if (extent > 270 || extent < -270) {
            into.add(circ.getBounds2D());
            return into;
        }
        double[] pt1 = circ.positionOf(angle);
        double[] pt2 = circ.positionOf(angle + extent);
        double[] pt3 = circ.positionOf(angle + (extent / 2));
        into.add(pt1[0], pt1[1]);
        into.add(pt2[0], pt2[1]);
        into.add(pt3[0], pt3[1]);
        for (int i = 0; i < extent; i += 45) {
            double[] pt4 = circ.positionOf(angle + i);
            into.add(pt4[0], pt4[1]);
        }
        return into;
    }

    @Override
    public boolean contains(double x, double y) {
        if (!circ.contains(x, y)) {
            return false;
        }
        double[] a = circ.positionOf(angle);
        double[] b = circ.positionOf(angle + extent);
        return GeometryUtils.triangleContains(circ.centerX, circ.centerY, a[0], a[1], b[0], b[1], x, y);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        if (circ.intersects(x, y, w, h)) {
            double[] a = circ.positionOf(angle);
            double[] b = circ.positionOf(angle + extent);
            Triangle2D t2 = new Triangle2D(circ.centerX, circ.centerY,
                    a[0], a[1], b[0], b[1]);
            return t2.intersects(x, y, w, h);
        }
        return false;
    }

    public int quadrantSpan() {
        return (int) Math.abs(extent / 90) + 1;
    }

    public Quadrant startingQuadrant() {
        return Quadrant.forAngle(angle);
    }

    public Quadrant endingQuadrant() {
        return Quadrant.forAngle(angle + extent);
    }

    public void setLinesIncluded(boolean val) {
        this.pie = val;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        if (extent == 0) {
            double[] ps = circ.positionOf(angle);
            return new Line2D.Double(circ.centerX, circ.centerY, ps[0], ps[1]).getPathIterator(at);
        }
        // XXX add in painting arrowheads

        double angle = this.angle;
        double extent = this.extent;
        if (extent < 0) {
            CornerAngle ca = new CornerAngle(angle, angle + extent).inverse();
            angle = ca.toSector().start();
            extent = ca.toSector().extent();
        }

        int bezierPointsRequired = (int) (Math.abs(extent) / 15D) + 1;

        int numIterEntries = bezierPointsRequired + (pie ? 3 : 1); // span + initial moveto + closepath

        byte[] types = new byte[numIterEntries];
        Arrays.fill(types, (byte) SEG_CUBICTO);
        types[0] = SEG_MOVETO;
        if (pie) {
            types[1] = SEG_LINETO;
            types[types.length - 1] = SEG_CLOSE;
        }

        int base = pie ? 4 : 2;

        int steps = (bezierPointsRequired + 1);

        double[] startPt = circ.positionOf(angle);
        int nCoordinates = base + (6 * steps); // initial moveto / lineto

        double[] data = new double[nCoordinates];
        if (pie) {
            data[0] = circ.centerX;
            data[1] = circ.centerY;
        } else {
            data[0] = startPt[0];
            data[1] = startPt[1];
        }
        System.arraycopy(startPt, 0, data, 2, 2);

        double step = extent / bezierPointsRequired;
        double currAngle = angle + step;
        for (int i = base; i < nCoordinates; i += 6) {
            if (i + 5 > nCoordinates) {
                throw new IllegalStateException("Array to small: Trying to write to "
                        + " index " + (i + 5) + " of " + data.length
                        + " nCoordinates " + nCoordinates + " at " + i);
            }
            double[] pt = circ.positionOf(currAngle);
            data[i + 4] = pt[0];
            data[i + 5] = pt[1];

            double radiusPerStep = (Math.abs(extent) / steps) * radius();

            double magic1 = 2 * 0.005;
            double magic2 = 1 * 0.005;

            EqLine firstTangent = circ.tangent(currAngle, radiusPerStep * magic1);
            EqLine secondTangent = circ.tangent(currAngle, radiusPerStep * magic2);
            data[i] = firstTangent.x1;
            data[i + 1] = firstTangent.y1;
            data[i + 2] = secondTangent.x1;
            data[i + 3] = secondTangent.y1;
            currAngle += step;
        }
        return new ArrayPathIteratorDouble(WIND_NON_ZERO, types, data, at);
    }

    public static void main(String... ignored) {
        PieWedge ang = new PieWedge(300, 300, 250, 180, 90);
        AngleComp angC = new AngleComp(ang);
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jf.setContentPane(angC);
        jf.pack();
        jf.setVisible(true);
    }

    private static final class AngleComp extends JComponent {

        private final double scale = 1;
        private PieWedge ang;
        private final int margin = 10;
        int tick = 0;
        Timer timer = new Timer(50, ae -> {
            ang.setAngle(Angle.normalize(ang.angle() + 5));
            if (tick++ % 180 == 0 && tick != 1) {
                ang.setExtent((ang.extent + 5) % 360);
                if (ang.extent == 0) {
                    ang.extent = 5;
                }
            }
            repaint();
        });

        AngleComp(PieWedge ang) {
            this.ang = ang;
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }

            });
        }

        @Override
        public void addNotify() {
            super.addNotify();
//            timer.start();
        }

        @Override
        public void removeNotify() {
            timer.stop();
            super.addNotify();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = ang.getBounds().getSize();
            d.width += margin;
            d.height += margin;
            d.width *= scale;
            d.height *= scale;
            if (d.width < 1_200) {
                d.width = 1_200;
            }
            if (d.height < 900) {
                d.height = 900;
            }
            return d;
        }

        @Override
        public void paintComponent(Graphics g) {
            paintComponent((Graphics2D) g);
        }

        public void paintComponent(Graphics2D g) {
            try {
                int w = getWidth();
                int h = getHeight();
                g.setColor(Color.GRAY);
                g.fillRect(0, 0, w, h);

                AffineTransform af = AffineTransform.getTranslateInstance(margin, margin);
                af.concatenate(AffineTransform.getScaleInstance(scale, scale));
                g.transform(af);
                g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

                g.setColor(new Color(120, 120, 255, 220));
                g.fill(ang.getBounds2D());

                g.setColor(new Color(240, 240, 0, 100));
                c.setCenterAndRadius(ang.circ.centerX, ang.circ.centerY, ang.radius());
                g.fill(c);
                g.setColor(Color.BLACK);
                g.draw(c);
                c.setRadius(5);

                g.drawString(
                        Double.toString(ang.angle)
                        + " / e" + Double.toString(ang.extent), 50, 50);

//                Rectangle r = ang.addToBounds();
//                Color dk = new Color(0, 0, 0, 200);
//                Color bt = new Color(255, 255, 255, 200);
//                for (int x = r.x; x < r.x + r.width; x++) {
//                    for (int y = r.y; y < r.y + r.height; y++) {
//                        if (ang.contains(x, y)) {
//                            g.setColor(dk);
//                            g.fillRect(x, y, 1, 1);
//                        } else {
//                            g.setColor(bt);
//                            g.fillRect(x, y, 1, 1);
//                        }
//                    }
//                }
                PathIterator pi = ang.getPathIterator(null);
                double[] d = new double[6];
                int[] pti = new int[1];
                while (!pi.isDone()) {
                    int type = pi.currentSegment(d);
                    String txt = "";
                    int sz = arraySizeForType(type);
                    if (type == SEG_CLOSE) {
                        break;
                    }
                    for (int i = 0; i < sz; i += 2) {
                        c.setCenter(d[i], d[i + 1]);
                        if (i == sz - 2) {
                            switch (type) {
                                case SEG_MOVETO:
                                    txt = pti[0] + ". move ";
//                                    System.out.println("moveTo " + d[i] + ", " + d[i + 1]);
                                    g.setColor(new Color(0, 255, 255, 128));
                                    break;
                                case SEG_LINETO:
                                    txt = pti[0] + ". line ";
//                                    System.out.println("lineTo " + d[i] + ", " + d[i + 1]);
                                    g.setColor(new Color(255, 120, 120, 128));
                                    break;
                                case SEG_CUBICTO:
                                    txt = pti[0] + ". cub ";
//                                    System.out.println("cubicTo " + d[i] + ", " + d[i + 1]);
                                    g.setColor(new Color(255, 255, 255, 128));
                                    break;
                            }
                        } else {
                            txt = pti[0] + " - " + (i / 2) + " ctrl ";
//                            System.out.println("ctrlPoint " + d[i] + ", " + d[i + 1]);
                            g.setColor(new Color(0, 0, 0, 128));
                        }
                        g.draw(c);
//                        g.setColor(new Color(255, 255, 255, 128));
//                        float cx = (float) c.centerX + 7;
//                        float cy = (float) c.centerY + 7;
//                        g.drawString(txt, cx, cy);
//                        int hh = g.getFontMetrics().getHeight();
//                        g.drawString(d[i] + ", " + d[i + 1], cx + 20, cy + hh + 2);
                    }
                    pti[0]++;
                    pi.next();
                }
            } catch (Exception ex) {
                Logger.getLogger(PieWedge.class.getName()).log(Level.SEVERE, null, ex);
            }
            g.setColor(new Color(128, 128, 255, 180));
            g.fill(ang);
            g.setColor(new Color(255, 255, 0, 180));
            g.draw(ang);
//            System.out.println(ang);
        }

        Circle c = new Circle(0, 0, 5);
    }

    @Override
    public double start() {
        return this.angle;
    }

    @Override
    public PieWedge rotatedBy(double degrees) {
        double deg = Angle.normalize(degrees + angle);
        return new PieWedge(circ.centerX, circ.centerY, circ.radius, deg, extent, pie);
    }
}
