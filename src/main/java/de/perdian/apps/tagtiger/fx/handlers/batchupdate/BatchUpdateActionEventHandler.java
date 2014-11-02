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
package de.perdian.apps.tagtiger.fx.handlers.batchupdate;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;

/**
 * Describes any kind of action that performs an update upon a list of selected
 * files
 *
 * @author Christian Robert
 */

public abstract class BatchUpdateActionEventHandler implements EventHandler<ActionEvent> {

    private ObservableList<TaggableFile> otherFiles = null;
    private Property<TaggableFile> currentFileProperty = null;

    public BatchUpdateActionEventHandler(Property<TaggableFile> currentFileProperty, ObservableList<TaggableFile> otherFiles) {
        this.setCurrentFileProperty(currentFileProperty);
        this.setOtherFiles(otherFiles);
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    protected Property<TaggableFile> getCurrentFileProperty() {
        return this.currentFileProperty;
    }
    private void setCurrentFileProperty(Property<TaggableFile> currentFileProperty) {
        this.currentFileProperty = currentFileProperty;
    }

    protected ObservableList<TaggableFile> getOtherFiles() {
        return this.otherFiles;
    }
    private void setOtherFiles(ObservableList<TaggableFile> otherFiles) {
        this.otherFiles = otherFiles;
    }

}