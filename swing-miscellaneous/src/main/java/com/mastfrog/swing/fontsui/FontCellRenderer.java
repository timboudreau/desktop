package com.mastfrog.swing.fontsui;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * A Cell renderer for fonts.
 *
 * @author Tim Boudreau
 */
public final class FontCellRenderer implements ListCellRenderer {

    private final FontView view = new FontView();
    private final JComboBox<Font> box;

    FontCellRenderer(JComboBox<Font> box) {
        this.box = box;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        view.setTargetFont((Font) value);
        view.setPaintFocusIndicator(cellHasFocus && index >= 0);
        view.setEnabled(box.isEnabled());
        view.setToolTipText(((Font) value).getFamily());
        if (isSelected) {
            view.setSelectionColor(list.getSelectionBackground());
        } else {
            view.setSelectionColor(null);
        }
        return view;
    }
}
