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
package com.mastfrog.geometry.util;

import java.awt.geom.AffineTransform;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Tim Boudreau
 */
public class PooledTransformTest {

    private int doSomething() {
        AffineTransform xf1 = PooledTransform.getQuadrantRotateInstance(32);
        return System.identityHashCode(xf1);
    }

    @Test
    public void testPool() throws InterruptedException {

        Thing t1 = new Thing();

        AffineTransform xf1 = PooledTransform.getQuadrantRotateInstance(2, t1);
        AffineTransform xf2 = PooledTransform.getRotateInstance(0.5, t1);

        int hash1 = System.identityHashCode(xf1);
        int hash2 = System.identityHashCode(xf2);

        t1 = null;
        for (int i = 0; i < 150; i++) {
            System.gc();
            System.runFinalization();
            Thread.sleep(10);
//            System.out.println("  phants sz now " + PooledTransform.phants.size());
        }

        Thing t2 = new Thing();
        AffineTransform xf3 = PooledTransform.getTranslateInstance(3, 3, t2);
        AffineTransform xf4 = PooledTransform.getTranslateInstance(5, 5, t2);

        System.out.println("phants " + PooledTransform.POOL);

        int hash3 = System.identityHashCode(xf3);
        int hash4 = System.identityHashCode(xf4);

        assertTrue(hash1 == hash3 || hash1 == hash4, "First item should have been recycled");
        assertTrue(hash2 == hash3 || hash2 == hash4, "First item should have been recycled");

        System.out.println("Hashes " + hash1 + " / " + hash2 + " / " + hash3 + " / " + hash4);
    }

    static class Thing {

        private static int ids;
        private final int id = ids++;

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + this.id;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Thing other = (Thing) obj;
            if (this.id != other.id) {
                return false;
            }
            return true;
        }

        public String toString() {
            return Integer.toString(id);
        }
    }
}
