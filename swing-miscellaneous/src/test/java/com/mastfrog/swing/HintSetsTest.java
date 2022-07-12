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
package com.mastfrog.swing;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import static java.util.Collections.emptyMap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Tim Boudreau
 */
public class HintSetsTest {

    @Test
    public void testHintSets() {
        assertTrue(true);
        for (HintSets s : HintSets.values()) {
            testOneHints(s);
        }
    }

    public void testOneHints(HintSets set) {
        System.out.println("TEST " + set);
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHints(new RenderingHints(emptyMap()));
        try {
            try {
                Graphics2D g1 = set.apply(g);
                assertSame(g, g1);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Bad key/value pairs in " + set, ex);
            }
            set.contents().forEach((k, v) -> {
                Object o = g.getRenderingHint(k);
                assertEquals(v, o, "Wrong hint value for " + set + ": " + k);
            });
        } finally {
            g.dispose();
        }
    }

}
