package com.mastfrog.swing.fontsui;

import java.awt.Component;
import java.awt.Font;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * A Cell renderer for fonts.
 *
 * @author Tim Boudreau
 */
public final class FontCellRenderer implements ListCellRenderer {

    private static Reference<FontCellRenderer> REF;
    private final FontView view = new FontView();

    private FontCellRenderer() {

    }

    public static FontCellRenderer instance() {
        FontCellRenderer result = REF == null ? null : REF.get();
        if (result == null) {
            result = new FontCellRenderer();
            REF = new SoftReference<>(result);
        }
        return result;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        view.setTargetFont((Font) value);
        view.setPaintFocusIndicator(cellHasFocus && index >= 0);
        if (isSelected) {
            view.setSelectionColor(list.getSelectionBackground());
        } else {
            view.setSelectionColor(null);
        }
        return view;
    }
}
