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
package com.mastfrog.swing.cell;

import java.awt.Rectangle;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import javax.swing.event.AncestorListener;

/**
 * Just overrides a bunch of stuff to do nothing for performance as a cell
 * renderer.  Use inside a ListCellRenderer or whatever.
 *
 * @author Tim Boudreau
 */
public class TextCellCellRenderer extends TextCellLabel {

    protected final boolean isCellRenderer() {
        return true;
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    @Override
    public void doLayout() {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    @Override
    public void invalidate() {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    @Override
    public void revalidate() {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    @Override
    public void repaint(Rectangle r) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    @Override
    public void repaint(int x, int y, int width, int height) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    @Override
    public void repaint(long tm) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    @Override
    public void repaint() {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addAncestorListener(AncestorListener l) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addComponentListener(ComponentListener l) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addContainerListener(ContainerListener l) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addHierarchyListener(HierarchyListener l) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addHierarchyBoundsListener(HierarchyBoundsListener l) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addInputMethodListener(InputMethodListener l) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addFocusListener(FocusListener fl) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addMouseListener(MouseListener ml) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addMouseWheelListener(MouseWheelListener ml) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addMouseMotionListener(MouseMotionListener ml) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons
     */
    public @Override
    void addVetoableChangeListener(VetoableChangeListener vl) {
        // do nothing
    }

    /**
     * Overridden to do nothing for performance reasons, unless using standard
     * swing rendering
     */
    public @Override
    void addPropertyChangeListener(String s, PropertyChangeListener l) {
        // do nothing
    }

    public @Override
    void addPropertyChangeListener(PropertyChangeListener l) {
        // do nothing
    }

    @Override
    protected void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        // do nothing
    }

    @Override
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        // do nothing
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // do nothing
    }

    @Override
    public synchronized void addKeyListener(KeyListener l) {
        // do nothing
    }
}
