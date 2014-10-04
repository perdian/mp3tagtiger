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

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.TagTiger;
import de.perdian.apps.tagtiger.business.model.SelectedDirectory;
import de.perdian.apps.tagtiger.business.model.SelectedFile;

class FileSelectionPane extends BorderPane {

    static final Logger log = LoggerFactory.getLogger(FileSelectionPane.class);

    FileSelectionPane(TagTiger tagTiger) {

        TextField directoryField = new TextField();
        HBox.setHgrow(directoryField, Priority.ALWAYS);
        HBox directoryFieldWrapper = new HBox(directoryField);
        directoryFieldWrapper.setPadding(new Insets(0, 0, 5, 0));

        TreeItem<SelectedDirectory> rootTreeItem = new TreeItem<>(null);
        rootTreeItem.getChildren().addAll(FileSelectionDirectoryTreeItem.listRoots());
        rootTreeItem.setExpanded(true);

        TreeView<SelectedDirectory> directoryTree = new TreeView<>(rootTreeItem);
        directoryTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isExpanded()) {
                newValue.expandedProperty().set(true);
            }
        });
        directoryTree.getSelectionModel().selectedItemProperty().addListener(new FileSelectionDirectoryListener(tagTiger));
        directoryTree.setShowRoot(false);

        ListView<SelectedFile> selectedFilesList = new ListView<>();
        selectedFilesList.setBorder(null);
        VBox.setVgrow(selectedFilesList, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(directoryTree);
        splitPane.getItems().add(selectedFilesList);

        Label statusLabel = new Label(tagTiger.getLocalization().noFilesSelectedYet());
        statusLabel.setPadding(new Insets(5, 5, 0, 5));

        this.setTop(directoryFieldWrapper);
        this.setCenter(splitPane);
        this.setBottom(statusLabel);
        this.setPadding(new Insets(5, 5, 5, 5));

    }

}