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
package com.mastfrog.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataListener;

/**
 * Factory for combo boxen over enums.
 *
 * @author Tim Boudreau
 */
public final class EnumComboBoxModel<T extends Enum<T>> implements ComboBoxModel<T> {

    private final List<T> values = new ArrayList<>();
    private T selectedItem;
    private final Class<T> type;

    EnumComboBoxModel(T selectedItem, EnumSortMode mode) {
        this(selectedItem.getDeclaringClass(), mode, selectedItem);
    }

    EnumComboBoxModel(Class<T> type, EnumSortMode mode) {
        this(type, mode, null);
    }

    EnumComboBoxModel(Class<T> type, EnumSortMode mode, T selectedItem) {
        this.type = type;
        values.addAll(new ArrayList<>(Arrays.asList(type.getEnumConstants())));
        mode.sort(values);
        this.selectedItem = selectedItem == null ? values.isEmpty() ? null
                : values.get(0) : selectedItem;
    }

    /**
     * Ways the contents of an enum combo box may be sorted.
     */
    public enum EnumSortMode {
        /**
         * Use the order of enum constants.
         */
        NATURAL,
        /**
         * Sort on the name() of enum constants.
         */
        NAME,
        /**
         * Sort case-sensitively on the toString() value of enum constants.
         */
        TO_STRING_CASE_SENSITIVE,
        /**
         * Sort case-insensitively on the toString() value of enum constants.
         */
        TO_STRING_CASE_INSENSITIVE;

        static EnumSortMode defaultMode() {
            return TO_STRING_CASE_SENSITIVE;
        }

        <E extends Enum<E>> void sort(List<E> items) {
            switch (this) {
                case NATURAL:
                    return;
                case NAME:
                    items.sort((a, b) -> {
                        return a.name().compareTo(b.name());
                    });
                    break;
                case TO_STRING_CASE_INSENSITIVE:
                    items.sort((a, b) -> {
                        return a.toString().compareToIgnoreCase(b.toString());
                    });
                    break;
                case TO_STRING_CASE_SENSITIVE:
                    items.sort((a, b) -> {
                        return a.toString().compareTo(b.toString());
                    });
            }
        }
    }

    public static <T extends Enum<T>> ComboBoxModel<T> newModel(Class<T> type) {
        return new EnumComboBoxModel<>(type, EnumSortMode.defaultMode());
    }

    public static <T extends Enum<T>> ComboBoxModel<T> newModel(Class<T> type, T selected) {
        assert selected == null || type.isInstance(selected);
        return new EnumComboBoxModel<>(type, EnumSortMode.defaultMode(), selected);
    }

    public static <T extends Enum<T>> ComboBoxModel<T> newModel(T selected) {
        return new EnumComboBoxModel<>(selected, EnumSortMode.defaultMode());
    }

    public static <T extends Enum<T>> JComboBox<T> newComboBox(Class<T> type) {
        return new JComboBox<>(new EnumComboBoxModel<>(type, EnumSortMode.defaultMode()));
    }

    public static <T extends Enum<T>> JComboBox<T> newComboBox(Class<T> type, T item) {
        assert item == null || type.isInstance(item);
        return new JComboBox<>(new EnumComboBoxModel<>(type, EnumSortMode.defaultMode(), item));
    }

    public static <T extends Enum<T>> JComboBox<T> newComboBox(T item) {
        assert item != null : "Null item - cannot determine type";
        return new JComboBox<>(new EnumComboBoxModel<>(item, EnumSortMode.defaultMode()));
    }

    public static <T extends Enum<T>> ComboBoxModel<T> newModel(Class<T> type, EnumSortMode mode) {
        return new EnumComboBoxModel<>(type, mode);
    }

    public static <T extends Enum<T>> ComboBoxModel<T> newModel(Class<T> type, T selected, EnumSortMode mode) {
        assert selected == null || type.isInstance(selected);
        return new EnumComboBoxModel<>(type, mode, selected);
    }

    public static <T extends Enum<T>> ComboBoxModel<T> newModel(T selected, EnumSortMode mode) {
        return new EnumComboBoxModel<>(selected, mode);
    }

    public static <T extends Enum<T>> JComboBox<T> newComboBox(Class<T> type, EnumSortMode mode) {
        return new JComboBox<>(new EnumComboBoxModel<>(type, mode));
    }

    public static <T extends Enum<T>> JComboBox<T> newComboBox(Class<T> type, T item, EnumSortMode mode) {
        assert item == null || type.isInstance(item);
        return new JComboBox<>(new EnumComboBoxModel<>(type, mode, item));
    }

    public static <T extends Enum<T>> JComboBox<T> newComboBox(T item, EnumSortMode mode) {
        assert item != null : "Null item - cannot determine type";
        return new JComboBox<>(new EnumComboBoxModel<>(item, mode));
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem instanceof String) {
            String s = (String) anItem;
            for (T val : values) {
                if (s.equals(val.name())) {
                    doSetSelectedItem(val);
                    break;
                } else if (s.equals(val.toString())) {
                    doSetSelectedItem(val);
                    break;
                }
            }
        } else if (type.isInstance(anItem)) {
            doSetSelectedItem(type.cast(anItem));
        } else if (anItem == null) {
            selectedItem = null;
        }
    }

    private void doSetSelectedItem(T item) {
        values.remove(item);
        values.add(0, item);
        selectedItem = item;
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }

    @Override
    public int getSize() {
        return values.size();
    }

    @Override
    public T getElementAt(int index) {
        return values.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        // do nothing
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        // do nothing
    }
}
