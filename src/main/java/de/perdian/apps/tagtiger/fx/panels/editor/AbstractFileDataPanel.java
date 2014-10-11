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
package de.perdian.apps.tagtiger.fx.panels.editor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.GridPane;
import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;
import de.perdian.apps.tagtiger.business.framework.tagging.FileWithTags;

/**
 * Abstract definition for any panel that displayed information about a single
 * file
 *
 * @author Christian Robert
 */

abstract class AbstractFileDataPanel extends GridPane {

    private Selection selection = null;

    AbstractFileDataPanel(TagTiger tagTiger) {
        tagTiger.getSelection().getSelectedFile().addListener((o, oldValue, newValue) -> this.updateSelectedFile(newValue));
        this.setSelection(tagTiger.getSelection());
    }

    protected final void updateSelectedFile(FileWithTags file) {
        this.updateSelectedFileInternal(file);
    }

    protected abstract void updateSelectedFileInternal(FileWithTags file);

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    protected class UpdateSelectedFileListener<T> implements ChangeListener<T> {

        @Override
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
            AbstractFileDataPanel.this.updateSelectedFile(AbstractFileDataPanel.this.getSelection().getSelectedFile().get());
        }

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

}