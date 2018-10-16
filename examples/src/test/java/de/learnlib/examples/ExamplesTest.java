/* Copyright (C) 2013-2018 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.learnlib.examples;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import javax.swing.SwingUtilities;

import de.learnlib.datastructure.observationtable.OTUtils;
import de.learnlib.datastructure.observationtable.ObservationTable;
import mockit.Mock;
import mockit.MockUp;
import net.automatalib.commons.util.system.JVMUtil;
import net.automatalib.modelcheckers.ltsmin.LTSminUtil;
import net.automatalib.words.Word;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author frohme
 */
public class ExamplesTest {

    @BeforeClass
    public void setupAutoClose() {
        // As soon as we observe an event that indicates a new window, close it to prevent blocking the tests.
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            final WindowEvent windowEvent = (WindowEvent) event;
            final Window w = windowEvent.getWindow();
            w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
        }, AWTEvent.WINDOW_FOCUS_EVENT_MASK);
    }

    @Test
    public void testBBCExample1() {
        checkLTSminAvailability();
        de.learnlib.examples.bbc.example1.Example.main(new String[0]);
    }

    @Test
    public void testBBCExample2() {
        checkLTSminAvailability();
        de.learnlib.examples.bbc.example2.Example.main(new String[0]);
    }

    @Test
    public void testBBCExample3() {
        checkLTSminAvailability();
        de.learnlib.examples.bbc.example3.Example.main(new String[0]);
    }

    @Test
    public void testExample1() throws Exception {
        checkJVMCompatibility();

        // Mock OTUtils class, so we don't actually open a browser during the test
        new MockUp<OTUtils>() {

            @Mock
            public <I, D> void displayHTMLInBrowser(ObservationTable<I, D> table,
                                                    Function<? super Word<? extends I>, ? extends String> wordToString,
                                                    Function<? super D, ? extends String> outputToString) {
                // do nothing
            }
        };

        SwingUtilities.invokeAndWait(() -> {
            try {
                de.learnlib.examples.example1.Example.main(new String[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testExample2() throws InvocationTargetException, InterruptedException {
        checkJVMCompatibility();
        SwingUtilities.invokeAndWait(() -> {
            try {
                de.learnlib.examples.example2.Example.main(new String[0]);
            } catch (IOException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testExample3() throws InvocationTargetException, InterruptedException {
        checkJVMCompatibility();
        SwingUtilities.invokeAndWait(() -> de.learnlib.examples.example3.Example.main(new String[0]));
    }

    private static void checkJVMCompatibility() {
        if (JVMUtil.getCanonicalSpecVersion() > 8) {
            throw new SkipException("The headless AWT environment currently only works with Java 8 and below");
        }
    }

    private static void checkLTSminAvailability() {
        if (!LTSminUtil.checkUsable()) {
            throw new SkipException("LTSmin is not installed");
        }
    }

}