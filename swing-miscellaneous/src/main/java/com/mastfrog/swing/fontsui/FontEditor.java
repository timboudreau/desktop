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

import static com.mastfrog.swing.fontsui.FontEditor.FontComponent.FAMILY;
import com.mastfrog.swing.slider.PopupSliderUI;
import static com.mastfrog.util.preconditions.Checks.notNull;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A "virtual panel" for editing fonts, with methods to construct or add
 * components to actual panels.
 *
 * @author Tim Boudreau
 */
public final class FontEditor {

    private final JComboBox<Font> fonts = FontComboBoxModel.newFontComboBox();
    private final JCheckBox bold = new JCheckBox("Bold");
    private final JCheckBox italic = new JCheckBox("Italic");
    private final JSlider size = new JSlider(JSlider.VERTICAL, 4, 148, 13);
    private final Set<ChangeListener> listeners = new LinkedHashSet<>();
    private boolean isChanging;
    Font last;

    public FontEditor() {
        this(UIManager.getFont("Label.font"));
    }

    public FontEditor(Font font) {
        if (font != null) {
            setFont(font);
        }
        size.setUI(new PopupSliderUI());
        fonts.addActionListener(this::onAction);
        bold.addActionListener(this::onAction);
        italic.addActionListener(this::onAction);
        size.addChangeListener(this::onAction);
        last = getFont();
    }

    public FontEditor setSize(int size) {
        this.size.setValue(size);
        return this;
    }

    public FontEditor setBold(boolean bold) {
        this.bold.setSelected(bold);
        return this;
    }

    public FontEditor setItalic(boolean italic) {
        this.italic.setSelected(italic);
        return this;
    }

    public boolean isItalic() {
        return italic.isSelected();
    }

    public boolean isBold() {
        return bold.isSelected();
    }

    public int getSize() {
        return size.getValue();
    }

    public FontEditor addChangeListener(ChangeListener cl) {
        listeners.add(cl);
        return this;
    }

    public FontEditor removeChangeListener(ChangeListener cl) {
        listeners.remove(cl);
        return this;
    }

    public JPanel createPanel() {
        JPanel result = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
        attach((en, comp) -> {
            result.add(comp);
        });
        return result;
    }

    public JLabel createDemoLabel(String txt) {
        JLabel lbl = new JLabel(txt);
        lbl.setHorizontalTextPosition(SwingConstants.CENTER);
        addChangeListener(ce -> {
            lbl.setFont(getFont());
        });
        return lbl;
    }

    public void attach(Container container, GridBagConstraints constraints, FontComponent... comps) {
        if (comps.length == 0) {
            return;
        }
        constraints.gridy++;
        constraints.gridx = 0;
        for (FontComponent c : comps) {
            container.add(component(c), constraints);
            constraints.gridx++;
        }
    }

    public void attach(BiConsumer<FontComponent, JComponent> visitor) {
        for (FontComponent f : FontComponent.values()) {
            visitor.accept(f, component(f));
        }
    }

    public JComponent component(FontComponent comp) {
        switch (notNull("comp", comp)) {
            case FAMILY:
                return fonts;
            case BOLD:
                return bold;
            case ITALIC:
                return italic;
            case SIZE:
                return size;
            default:
                throw new AssertionError(comp);
        }
    }

    public enum FontComponent {
        FAMILY,
        BOLD,
        ITALIC,
        SIZE
    }

    private void onAction(ActionEvent evt) {
        onChange(last);
    }

    private void onAction(ChangeEvent evt) {
        onChange(last);
    }

    public Font getFont() {
        Font f = (Font) fonts.getSelectedItem();
        Font result = f.deriveFont((float) size.getValue());
        if (bold.isSelected()) {
            result = result.deriveFont(Font.BOLD);
        }
        if (italic.isSelected()) {
            result = result.deriveFont(Font.ITALIC);
        }
        return last = result;
    }

    private void onChange(Font old) {
        if (isChanging) {
            return;
        }
        Font nue = getFont();
        if (!fontsEqual(old, nue)) {
            for (ChangeListener cl : listeners) {
                cl.stateChanged(new ChangeEvent(this));
            }
        }
    }

    private boolean fontsEqual(Font old, Font nue) {
        return old.getFamily().equals(nue.getFamily())
                && old.getSize() == nue.getSize()
                && old.getStyle() == nue.getStyle();
    }

    private void whileAdjusting(BooleanSupplier r) {
        Font old = getFont();
        isChanging = true;
        boolean changed;
        try {
            changed = r.getAsBoolean();
        } finally {
            isChanging = false;
        }
        if (changed) {
            onChange(old);
        }
    }

    public FontEditor setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null passed for font");
        }
        whileAdjusting(() -> {
            Font target = find(f -> {
                return f.getFamily().equals(font.getFamily());
            }).orElseGet(() -> font.deriveFont(1F));
            fonts.setSelectedItem(target);
            size.setValue(font.getSize());
            bold.setSelected((font.getStyle() & Font.BOLD) != 0);
            italic.setSelected((font.getStyle() & Font.ITALIC) != 0);
            return true;
        });
        return this;
    }

    public Optional<Font> find(Predicate<Font> test) {
        for (int i = 0; i < fonts.getModel().getSize(); i++) {
            Font f = (Font) fonts.getModel().getElementAt(i);
            if (f == null) {
                System.out.println("NULL AT " + i);
                continue;
            }
            if (test.test(f)) {
                return Optional.of(f);
            }
        }
        return Optional.empty();
    }
}
