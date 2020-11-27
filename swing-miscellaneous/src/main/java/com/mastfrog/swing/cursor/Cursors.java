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
import javax.swing.JComponent;

/**
 * Provides a set of custom cursors which are dynamically created and stored in
 * in-memory images. Takes care of some level of hidpi support.  Light and dark
 * background optimized implementations can be obtained from static methods.
 *
 * @author Tim Boudreau
 */
public interface Cursors {

    /**
     * Get the set of cursors for dark UIs.
     *
     * @return A cursor set
     */
    public static Cursors forDarkBackgrounds() {
        return CursorsImpl.darkBackgroundCursors();
    }

    /**
     * Get the set of cursors for light-background UIs.
     *
     * @return A cursor set
     */
    public static Cursors forBrightBackgrounds() {
        return CursorsImpl.brightBackgroundCursors();
    }

    /**
     * Get the cursors instance for this component based on its background
     * colors (which may be stored in a client property from previous use).
     *
     * @param comp A component
     * @return A set of cursors
     */
    public static Cursors forComponent(JComponent comp) {
        return CursorsImpl.cursorsForComponent(comp);
    }

    /**
     * A star cursor.
     *
     * @return A cursor
     */
    public Cursor star();

    /**
     * A barbell cursor.
     *
     * @return A cursor
     */
    public Cursor barbell();

    /**
     * An X cursor.
     *
     * @return A cursor
     */
    public Cursor x();

    /**
     * It's difficult to describe. Or name.
     *
     * @return A cursor
     */
    public Cursor hin();

    /**
     * A circle-with-line-through-it cursor.
     *
     * @return A cursor
     */
    public Cursor no();

    /**
     * Left-right arrows.
     *
     * @return A cursor
     */
    public Cursor horizontal();

    /**
     * Up/down arrows.
     *
     * @return
     */
    public Cursor vertical();

    /**
     * Diagonal 45 degree arrows.
     *
     * @return A cursor
     */
    public Cursor southWestNorthEast();

    /**
     * Diagonal 135 degree arrows.
     *
     * @return A cursor
     */
    public Cursor southEastNorthWest();

    /**
     * A hollow diamond or rhombus cursor.
     *
     * @return A cursor
     */
    public Cursor rhombus();

    /**
     * A filled diamond or rhombus cursor.
     *
     * @return A cursor
     */
    public Cursor rhombusFilled();

    /**
     * A hollow triangle pointing down cursor.
     *
     * @return A cursor
     */
    public Cursor triangleDown();

    /**
     * A filled triangle pointing down cursor.
     *
     * @return A cursor
     */
    public Cursor triangleDownFilled();

    /**
     * A hollow triangle pointing right cursor.
     *
     * @return A cursor
     */
    public Cursor triangleRight();

    /**
     * A filled triangle pointing right cursor.
     *
     * @return A cursor
     */
    public Cursor triangleRightFilled();

    /**
     * A hollow triangle pointing left cursor.
     *
     * @return A cursor
     */
    public Cursor triangleLeft();

    /**
     * A filled triangle pointing left cursor.
     *
     * @return A cursor
     */
    public Cursor triangleLeftFilled();

    /**
     * A X-crossed diagonal arrows cursor.
     *
     * @return A cursor
     */
    public Cursor arrowsCrossed();

    /**
     * A multi-arrow cursor with a second multi-arrow image indicating moving
     * multiple objects.
     *
     * @return A cursor
     */
    public Cursor multiMove();

    /**
     * Rotation cursor.
     *
     * @return A cursor
     */
    public Cursor rotate();

    /**
     * Multi-rotation cursor.
     *
     * @return A cursor
     */
    public Cursor rotateMany();

    /**
     * Dotted rectangle cursor, as in rectangular selecting.
     *
     * @return A cursor
     */
    public Cursor dottedRect();

    /**
     * An arrow with a plus sign.
     *
     * @return A cursor
     */
    public Cursor arrowPlus();

    /**
     * A shorter arrow cursor.
     *
     * @return A cursor
     */
    public Cursor shortArrow();

    /**
     * A cursor indicating to connect to points.
     *
     * @return A cursor
     */
    public Cursor closeShape();

    /**
     * An arrow cursor with a tilde.
     *
     * @return A cursor
     */
    public Cursor arrowTilde();

    /**
     * Get a cursor which is perpendicular to the passed angle (12:00 == 0).
     *
     * @param angle Angle in a coordinate space where 12 o;clock is 0 degrees
     * @return A cursor
     */
    public Cursor cursorPerpendicularTo(double angle);

    /**
     * Get a cursor which is perpendicular to a quadrant of a circle.
     *
     * @param angle One of the arrow cursors,d epending on the angle.
     * @return A cursor
     */
    public Cursor cursorPerpendicularToQuadrant(Quadrant quad);
}
