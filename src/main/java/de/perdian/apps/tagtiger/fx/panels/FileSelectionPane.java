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
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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
import de.perdian.apps.tagtiger.business.model.DirectoryWrapper;
import de.perdian.apps.tagtiger.business.model.MpFileWrapper;

class FileSelectionPane extends BorderPane {

    static final Logger log = LoggerFactory.getLogger(FileSelectionPane.class);

    FileSelectionPane(TagTiger tagTiger) {

        TextField directoryField = new TextField();
        HBox.setHgrow(directoryField, Priority.ALWAYS);
        HBox directoryFieldWrapper = new HBox(directoryField);
        directoryFieldWrapper.setPadding(new Insets(0, 0, 5, 0));

        TreeItem<DirectoryWrapper> rootTreeItem = new TreeItem<>(null);
        rootTreeItem.getChildren().addAll(FileSelectionDirectoryTreeItem.listRoots());
        rootTreeItem.setExpanded(true);

        TreeView<DirectoryWrapper> directoryTree = new TreeView<>(rootTreeItem);
        directoryTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isExpanded()) {
                newValue.expandedProperty().set(true);
            }
        });
        directoryTree.getSelectionModel().selectedItemProperty().addListener(new FileSelectionDirectoryListener(tagTiger));
        directoryTree.setShowRoot(false);

        ListView<MpFileWrapper> selectedFilesList = new ListView<>();
        selectedFilesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedFilesList.setBorder(null);
        VBox.setVgrow(selectedFilesList, Priority.ALWAYS);
        tagTiger.getSelection().getAvailableFiles().addListener((ListChangeListener.Change<? extends MpFileWrapper> change) -> {
            Platform.runLater(() -> selectedFilesList.getItems().setAll(change.getList()));
        });

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(directoryTree);
        splitPane.getItems().add(selectedFilesList);

        FileSelectionStatusPane statusPane = new FileSelectionStatusPane(tagTiger);
        statusPane.setPadding(new Insets(5, 0, 0, 0));

        this.setTop(directoryFieldWrapper);
        this.setCenter(splitPane);
        this.setBottom(statusPane);
        this.setPadding(new Insets(5, 5, 5, 5));

    }

}