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
package de.perdian.apps.tagtiger.fx.components.files;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

/**
 * Displays a list of selected files that are in use by the user
 *
 * @author Christian Robert
 */

public class FileSelectionPane extends BorderPane {

    private ObservableList<TaggableFile> availableFiles = FXCollections.observableArrayList();
    private ObservableList<TaggableFile> selectedFiles = FXCollections.observableArrayList();

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    ObservableList<TaggableFile> getAvailableFiles() {
        return this.availableFiles;
    }

    ObservableList<TaggableFile> getSelectedFiles() {
        return this.selectedFiles;
    }

}