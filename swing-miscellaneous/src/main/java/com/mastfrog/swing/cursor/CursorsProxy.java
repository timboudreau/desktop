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
package com.mastfrog.swing.cursor;

import com.mastfrog.geometry.Quadrant;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;

/**
 *
 * @author Tim Boudreau
 */
final class CursorsProxy implements Cursors, PropertyChangeListener {

    private final JComponent component;

    CursorsProxy(JComponent comp) {
        this.component = comp;
        comp.addPropertyChangeListener("graphicsConfiguration", this);
    }

    private CursorsImpl cached;

    private Cursors get() {
        if (cached != null) {
            return cached;
        }
        cached = CursorsImpl.rawCursorsForComponent(component);
        return cached;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        cached = null;
    }

    @Override
    public Cursor star() {
        return get().star();
    }

    @Override
    public Cursor barbell() {
        return get().barbell();
    }

    @Override
    public Cursor x() {
        return get().x();
    }

    @Override
    public Cursor hin() {
        return get().hin();
    }

    @Override
    public Cursor no() {
        return get().no();
    }

    @Override
    public Cursor horizontal() {
        return get().horizontal();
    }

    @Override
    public Cursor vertical() {
        return get().vertical();
    }

    @Override
    public Cursor southWestNorthEast() {
        return get().southWestNorthEast();
    }

    @Override
    public Cursor southEastNorthWest() {
        return get().southEastNorthWest();
    }

    @Override
    public Cursor rhombus() {
        return get().rhombus();
    }

    @Override
    public Cursor rhombusFilled() {
        return get().rhombusFilled();
    }

    @Override
    public Cursor triangleDown() {
        return get().triangleDown();
    }

    @Override
    public Cursor triangleDownFilled() {
        return get().triangleDownFilled();
    }

    @Override
    public Cursor triangleRight() {
        return get().triangleRight();
    }

    @Override
    public Cursor triangleRightFilled() {
        return get().triangleRightFilled();
    }

    @Override
    public Cursor triangleLeft() {
        return get().triangleLeft();
    }

    @Override
    public Cursor triangleLeftFilled() {
        return get().triangleLeftFilled();
    }

    @Override
    public Cursor arrowsCrossed() {
        return get().arrowsCrossed();
    }

    @Override
    public Cursor multiMove() {
        return get().multiMove();
    }

    @Override
    public Cursor rotate() {
        return get().rotate();
    }

    @Override
    public Cursor rotateMany() {
        return get().rotateMany();
    }

    @Override
    public Cursor dottedRect() {
        return get().dottedRect();
    }

    @Override
    public Cursor arrowPlus() {
        return get().arrowPlus();
    }

    @Override
    public Cursor shortArrow() {
        return get().shortArrow();
    }

    @Override
    public Cursor closeShape() {
        return get().closeShape();
    }

    @Override
    public Cursor arrowTilde() {
        return get().arrowTilde();
    }

    @Override
    public Cursor cursorPerpendicularTo(double angle) {
        return get().cursorPerpendicularTo(angle);
    }

    @Override
    public Cursor cursorPerpendicularToQuadrant(Quadrant quad) {
        return get().cursorPerpendicularToQuadrant(quad);
    }
}
