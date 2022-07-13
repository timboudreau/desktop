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

import com.mastfrog.util.preconditions.Exceptions;
import static java.awt.EventQueue.invokeLater;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;

/**
 * Utilities for working with swing documents.
 *
 * @author Tim Boudreau
 */
public final class Documents {

    /**
     * Invoke the passed runnable whenever the document changes, on the thread
     * it was changed on (may be called while holding a document lock).
     *
     * @param doc A document
     * @param run A runnable
     * @return A runnable which when called, will stop listening to the document
     */
    public static Runnable onChange(Document doc, Runnable run) {
        DL dl = new DL(doc, run, false);
        doc.addDocumentListener(dl);
        return dl::detach;
    }

    /**
     * Invoke the passed runnable whenever the document changes, on the event
     * thread (will not be called while holding a document lock).
     *
     * @param doc A document
     * @param run A runnable
     * @return A runnable which when called, will stop listening to the document
     */
    public static Runnable onChangeAsync(Document doc, Runnable run) {
        DL dl = new DL(doc, run, false);
        doc.addDocumentListener(dl);
        return dl::detach;
    }

    /**
     * Invoke the passed runnable when <code>delay</code> amount of time has
     * elapsed since the last document modification.
     *
     * @param doc A document
     * @param delay How long to (re)schedule the timer for after a document
     * change
     * @param run A runnable
     * @return A runnable which when called, will stop listening to the document
     */
    public static Runnable onCessationOfChange(Document doc, Duration delay, Runnable run) {
        DelayedL dl = new DelayedL(doc, delay, run);
        doc.addDocumentListener(dl);
        return dl::detach;
    }

    public static void withText(Document doc, Consumer<CharSequence> consumer) {
        Segment seg = new Segment();
        doc.render(() -> {
            try {
                doc.getText(0, doc.getLength(), seg);
            } catch (BadLocationException ex) {
                Exceptions.chuck(ex);
            }
        });
        consumer.accept(seg);
    }

    public static <T> T withText(Document doc, Function<CharSequence, T> consumer) {
        Segment seg = new Segment();
        doc.render(() -> {
            try {
                doc.getText(0, doc.getLength(), seg);
            } catch (BadLocationException ex) {
                Exceptions.chuck(ex);
            }
        });
        return consumer.apply(seg);
    }

    private static final class DelayedL implements DocumentListener, ActionListener {

        private final Timer timer;
        private final Reference<Document> docRef;
        private final Runnable toRun;
        private boolean detached;

        DelayedL(Document doc, Duration delay, Runnable r) {
            docRef = new WeakReference<>(doc);
            toRun = r;
            long millis = delay.toMillis();
            if (millis <= 0 || millis > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Duration " + millis + " out of range");
            }
            timer = new Timer((int) delay.toMillis(), this);
            timer.setRepeats(false);
            timer.setCoalesce(true);
        }

        private void reset() {
            if (timer.isRunning()) {
                timer.stop();
            }
            timer.start();
        }

        void detach() {
            detached = true;
            Document doc = docRef.get();
            if (doc != null) {
                doc.removeDocumentListener(this);
            }
        }

        private void onChange() {
            if (detached) {
                return;
            }
            reset();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            onChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            onChange();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // do nothing
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            toRun.run();
        }
    }

    private static final class DL implements Runnable, DocumentListener {

        private final Reference<Document> docRef;
        private final Runnable toRun;
        private final boolean async;
        private AtomicBoolean enqueued = new AtomicBoolean();

        DL(Document doc, Runnable toRun, boolean async) {
            this.toRun = toRun;
            this.docRef = new WeakReference<>(doc);
            this.async = async;
        }

        private void onChange() {
            if (async) {
                if (enqueued.compareAndSet(false, true)) {
                    invokeLater(this);
                }
            } else {
                run();
            }
        }

        void detach() {
            Document doc = docRef.get();
            if (doc != null) {
                doc.removeDocumentListener(this);
            }
        }

        @Override
        public void run() {
            toRun.run();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            onChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            onChange();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // do nothing
        }
    }

    private Documents() {
        throw new AssertionError();
    }
}
