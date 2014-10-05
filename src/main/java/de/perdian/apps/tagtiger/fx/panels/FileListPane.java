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
package de.perdian.apps.tagtiger.fx.panels;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.business.framework.tagging.FileWithTags;

class FileListPane extends BorderPane {

    FileListPane(TagTiger tagTiger) {

        ListView<FileWithTags> selectedFilesList = new ListView<>();
        selectedFilesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedFilesList.setBorder(null);
        VBox.setVgrow(selectedFilesList, Priority.ALWAYS);
        tagTiger.getSelection().getAvailableFiles().addListener((ListChangeListener.Change<? extends FileWithTags> change) -> {
            Platform.runLater(() -> selectedFilesList.getItems().setAll(change.getList()));
        });

        this.setCenter(selectedFilesList);

    }

}