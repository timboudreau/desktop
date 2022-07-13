package com.mastfrog.swing.fontsui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * A combo box model for all installed fonts.
 *
 * @author Tim Boudreau
 */
public final class FontComboBoxModel implements ComboBoxModel<Font> {

    private static final List<String> fontNames = new ArrayList<>();
    private static String lastSelected;
    private String selectedName;

    public static void main(String[] args) throws Exception {
        System.setProperty("awt.useSystemAAFontSettings", "lcd_hrgb");
//        System.setProperty("sun.java2d.dpiaware", "false");
        
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        JFrame jf = new JFrame();
        JPanel p = new JPanel(new FlowLayout());
        p.add(new JLabel("Font"));
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        JComboBox<Font> box = newFontComboBox();
        p.add(box);
        JCheckBox cb = new JCheckBox("Enabled");
        cb.setSelected(true);
        cb.addActionListener(ae -> {
            box.setEnabled(cb.isSelected());
        });
        p.add(cb);

        JButton jb = new JButton("Change L&F");
        jb.addActionListener(ae -> {
            try {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(FontComboBoxModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        p.add(jb);

        jf.setContentPane(p);
        jf.pack();
        jf.setVisible(true);
    }

    public FontComboBoxModel() {
        selectedName = lastSelected;
        if (selectedName == null) {
            selectedName = fontNames().get(0);
        }
    }

    /**
     * Create a new JComboBox with all fonts and a cell renderer that renders
     * them in their own typeface.
     *
     * @return A combo box
     */
    @SuppressWarnings("unchecked")
    public static JComboBox<Font> newFontComboBox() {
        JComboBox<Font> result = new JComboBox<>(new FontComboBoxModel());
        result.setRenderer(new FontCellRenderer(result));
        return result;
    }

    private static List<String> fontNames() {
        if (fontNames.isEmpty()) {
            String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            Arrays.sort(names, (a, b) -> {
                return a.compareToIgnoreCase(b);
            });
            fontNames.addAll(Arrays.asList(names));
        }
        return fontNames;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem instanceof String) {
            selectedName = (String) anItem;
            lastSelected = selectedName;
            if (!fontNames.isEmpty()) {
                fontNames.remove(selectedName);
                fontNames.add(0, selectedName);
            }
        } else if (anItem instanceof Font) {
            selectedName = ((Font) anItem).getFamily();
            lastSelected = selectedName;
            if (!fontNames.isEmpty()) {
                fontNames.remove(selectedName);
                fontNames.add(0, selectedName);
            }
        } else if (anItem == null) {
            selectedName = null;
        }
    }

    @Override
    public Object getSelectedItem() {
        if (selectedName == null) {
            return null;
        }
        return new Font(selectedName, Font.PLAIN, 14);
    }

    @Override
    public int getSize() {
        return fontNames().size();
    }

    @Override
    public Font getElementAt(int index) {
        String name = fontNames().get(index);
        return new Font(name, Font.PLAIN, 14);
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
