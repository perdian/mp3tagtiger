/*
 * Copyright 2014 Christian Robert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.perdian.apps.tagtiger.fx.panels.selection;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;

public class SelectionKeyEventHandler implements EventHandler<KeyEvent> {

    private Selection selection = null;

    public SelectionKeyEventHandler(Selection selection) {
        this.setSelection(selection);
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.PAGE_UP) {
            this.getSelection().selectedIndexProperty().set(event.isShiftDown() ? Integer.MIN_VALUE : this.getSelection().selectedIndexProperty().get() - 1);
        } else if (event.getCode() == KeyCode.PAGE_DOWN) {
            this.getSelection().selectedIndexProperty().set(event.isShiftDown() ? Integer.MAX_VALUE : this.getSelection().selectedIndexProperty().get() + 1);
        }
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

}