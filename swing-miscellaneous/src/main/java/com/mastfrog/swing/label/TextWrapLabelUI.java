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
package com.mastfrog.swing.label;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.LabelUI;

/**
 * A LabelUI which can wrap text.  No HTML support.
 *
 * @author Tim Boudreau
 */
public class TextWrapLabelUI extends LabelUI {
    
    private static TextWrapLabelUI INSTANCE;
    private static final int WRAP_TRIGGER = 20;
    private static final int WRAP_POINT = 20;
    private final int wrapTrigger;
    private final int wrapPoint;

    public TextWrapLabelUI() {
        this(WRAP_POINT, WRAP_POINT);
    }

    public TextWrapLabelUI(int wrapTriggerAndPoint) {
        this(wrapTriggerAndPoint, wrapTriggerAndPoint);
    }

    public TextWrapLabelUI(int wrapTrigger, int wrapPoint) {
        this.wrapTrigger = wrapTrigger;
        this.wrapPoint = wrapPoint;
    }

    public static JLabel createLabel() {
        JLabel result = new JLabel();
        attach(result);
        return result;
    }

    public static void attach(JLabel label) {
        label.setUI(INSTANCE);
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }
    
    @Override
    public Dimension getPreferredSize(JComponent c) {
        FontMetrics fm = c.getFontMetrics(c.getFont());
        String txt = ((JLabel) c).getText();
        Insets ins = c.getInsets();
        Color fg = c.getForeground();
        Font f = c.getFont();
        return doPaint (null, ins, txt, fm, fg, f, 1.0D);
    }

    @Override
    public int getBaseline(JComponent c, int width, int height) {
        Font f = c.getFont();
        FontMetrics fm = c.getFontMetrics(f);
        Insets ins = c.getInsets();
        if (fm != null) {
            return ins.top + fm.getMaxAscent();
        } else {
            return height / 2;
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        String txt = ((JLabel) c).getText();
        Insets ins = c.getInsets();
        doPaint((Graphics2D) g, ins, txt, null, c.getForeground(), c.getFont(), 1.0D);
    }
    
    public Dimension doPaint(Graphics2D gg, Insets ins, String txt, FontMetrics fm, Paint fg, Font f, double leading) {
        return doPaint(wrapTrigger, gg, ins, txt, fm, fg, f, leading);
    }

    public static Dimension doPaint(int wrapTrigger, Graphics2D gg, Insets ins, String txt, FontMetrics fm, Paint fg, Font f, double leading) {
        String[] words;
        if (txt.length() < wrapTrigger) {
            words = new String[]{txt};
        } else {
            words = txt.split("\\s+");
            if (words.length < 2) {
                words = new String[] { txt };
            }
        }
        int x = ins.left;
        int y = ins.top;
        if (gg != null) {
            gg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gg.setFont(f);
            gg.setPaint(fg);
            fm = gg.getFontMetrics();
        }
        int spaceWidth = fm.stringWidth(" ");
        int top = y + fm.getMaxAscent();
        int charCount = 0;
        int maxX = ins.left + ins.right;
        int maxY = ins.top + ins.bottom + fm.getHeight();
        int lineGap = (int) Math.ceil((double) fm.getHeight() * leading);
        
        for (int i=0; i < words.length; i++) {
            String word = words[i];
            
            int w = fm.stringWidth(word);
            if (gg != null) {
                gg.drawString(word, x, top);
            }
            charCount += word.length() + 1;
            x += w;
            
            maxX = Math.max(maxX, x);
            if (i != words.length - 1) {
                x += spaceWidth;
                maxX = Math.max (maxX, x);
                int nextWordLength = words[i+1].length();
                if (charCount + nextWordLength > wrapTrigger) {
                    x = ins.left;
                    top += lineGap;
                    maxY += lineGap;
                    charCount = 0;
                }
            }
            maxY = Math.max(maxY, y);
        }
        return new Dimension(maxX + ins.left + ins.right, maxY + ins.top + ins.bottom);
    }
}
