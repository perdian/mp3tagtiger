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
package de.perdian.apps.tagtiger.fx.panels.file;

import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.scene.layout.GridPane;
import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

/**
 * Abstract definition for any panel that displayed information about a single
 * file
 *
 * @author Christian Robert
 */

public abstract class FileDataPane extends GridPane {

    private TaggableFile currentFile = null;
    private List<FileProperty> fileProperties = null;

    protected FileDataPane(TagTiger tagTiger) {

        FilePropertyControlFactory controlFactory = new FilePropertyControlFactory(tagTiger.getSelection(), this::getCurrentFile);
        this.initializePane(controlFactory, tagTiger);
        this.setFileProperties(controlFactory.getProperties());
        this.setDisable(true);

        tagTiger.getSelection().getSelectedFile().addListener((o, oldValue, newValue) -> this.updateCurrentFile(newValue));

    }

    private synchronized void updateCurrentFile(TaggableFile file) {

        // Make sure the currently active file has all listeners removed, so
        // that no further events will be sent if data from that file is updated
        if (this.getCurrentFile() != null) {
            this.getFileProperties().forEach(property -> property.removeListeners(this.getCurrentFile()));
        }

        // Now that the old listeners have been removed, we add the new
        // listeners to the file that is to be displayed now
        Platform.runLater(() -> this.setDisable(file == null));
        this.setCurrentFile(file);
        if (file != null) {
            this.getFileProperties().forEach(property -> property.addListeners(file));
        }
        this.getFileProperties().forEach(property -> this.updateCurrentFileProperty(file, property));

    }

    private void updateCurrentFileProperty(TaggableFile file, FileProperty editorProperty) {
        Property<String> property = file == null ? null : editorProperty.getPropertyFunction().apply(file);
        String propertyValue = property == null ? "" : property.getValue();
        editorProperty.getPropertyChangedListener().changed(property, null, propertyValue);
    }

    protected abstract void initializePane(FilePropertyControlFactory propertyFactory, TagTiger tagTiger);

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private TaggableFile getCurrentFile() {
        return this.currentFile;
    }
    private void setCurrentFile(TaggableFile currentFile) {
        this.currentFile = currentFile;
    }

    private List<FileProperty> getFileProperties() {
        return this.fileProperties;
    }
    private void setFileProperties(List<FileProperty> fileProperties) {
        this.fileProperties = fileProperties;
    }

}