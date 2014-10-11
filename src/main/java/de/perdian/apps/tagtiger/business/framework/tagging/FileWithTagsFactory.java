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
package de.perdian.apps.tagtiger.business.framework.tagging;

import java.io.File;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;

public class FileWithTagsFactory {

    private Selection targetSelection = null;

    public FileWithTagsFactory(Selection targetSelection) {
        this.setTargetSelection(targetSelection);
    }

    public FileWithTags createFileWrapper(File file) {

        int extensionSeparator = file.getName().lastIndexOf(".");

        FileWithTags fileWrapper = new FileWithTags(file);
        fileWrapper.getFileName().set(extensionSeparator < 0 ? file.getName() : file.getName().substring(0, extensionSeparator));
        fileWrapper.getFileExtension().set(extensionSeparator < 0 || extensionSeparator >= file.getName().length() - 1 ? null : file.getName().substring(extensionSeparator + 1));

        // Add listener to get notified when something has changed
        fileWrapper.getFileExtension().addListener(new UpdateTargetPropertyWithNewValueListener<>(fileWrapper.getChanged()));
        fileWrapper.getFileName().addListener(new UpdateTargetPropertyWithNewValueListener<>(fileWrapper.getChanged()));
        fileWrapper.getChanged().addListener(new UpdateChangedFilesInTargetSelectionListener(fileWrapper));

        return fileWrapper;

    }

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    class UpdateChangedFilesInTargetSelectionListener implements ChangeListener<Boolean> {

        private FileWithTags file = null;

        UpdateChangedFilesInTargetSelectionListener(FileWithTags file) {
            this.setFile(file);
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue()) {
                FileWithTagsFactory.this.getTargetSelection().getChangedFiles().add(this.getFile());
            } else {
                FileWithTagsFactory.this.getTargetSelection().getChangedFiles().remove(this.getFile());
            }
        }

        // ---------------------------------------------------------------------
        // --- Property access methods -----------------------------------------
        // ---------------------------------------------------------------------

        private FileWithTags getFile() {
            return this.file;
        }
        private void setFile(FileWithTags file) {
            this.file = file;
        }

    }

    class UpdateTargetPropertyWithNewValueListener<T> implements ChangeListener<T> {

        private BooleanProperty targetProperty = null;

        UpdateTargetPropertyWithNewValueListener(BooleanProperty targetProperty) {
            this.setTargetProperty(targetProperty);
        }

        @Override
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
            if (oldValue == null && newValue == null) {
                // Do nothing - no change
            } else if (oldValue == newValue) {
                // Do nothing - no change
            } else if (oldValue == null || !oldValue.equals(newValue)) {
                this.getTargetProperty().set(true);
            }
        }

        // -------------------------------------------------------------------------
        // --- Property access methods ---------------------------------------------
        // -------------------------------------------------------------------------

        private BooleanProperty getTargetProperty() {
            return this.targetProperty;
        }
        private void setTargetProperty(BooleanProperty targetProperty) {
            this.targetProperty = targetProperty;
        }

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    Selection getTargetSelection() {
        return this.targetSelection;
    }
    private void setTargetSelection(Selection targetSelection) {
        this.targetSelection = targetSelection;
    }

}