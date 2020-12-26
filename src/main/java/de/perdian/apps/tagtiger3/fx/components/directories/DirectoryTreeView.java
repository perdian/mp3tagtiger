/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.directories;

import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeView;

public class DirectoryTreeView extends TreeView<Directory> {

    private static final Logger log = LoggerFactory.getLogger(DirectoryTreeView.class);
    private ObjectProperty<Directory> selectedDirectory = new SimpleObjectProperty<>();

    public DirectoryTreeView() {
        this.setRoot(DirectoryTreeItem.createRootTreeItem());
        this.setShowRoot(false);
        this.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> this.getSelectedDirectory().setValue(newValue == null ? null : newValue.getValue()));
    }

    public void selectDirectory(Path directory) {
        if (directory != null && !Objects.equals(directory, this.getSelectedDirectory().getValue() == null ? null : this.getSelectedDirectory().getValue().getPath())) {

            // First we make sure that all the nodes leading up to the selected directory are visible and have their
            // children loaded so they're correctly displayed in the UI
            DirectoryTreeItem rootDirectoryItem = (DirectoryTreeItem)this.getRoot().getChildren().stream().filter(r -> Objects.equals(r.getValue().getPath(), directory.getRoot())).findAny().get();
            DirectoryTreeItem directoryItem = rootDirectoryItem.ensureChildrenLoadedUntil(directory);
            if (directoryItem != null) {

                // Now we can open the tree until we have found or target item
                log.debug("Opening tree until item: " + directoryItem.getValue().getPath());
                directoryItem.computePathFromRoot().stream().forEach(item -> item.setExpanded(true));
                this.getSelectionModel().select(directoryItem);

            }

        }
    }

    public ObjectProperty<Directory> getSelectedDirectory() {
        return this.selectedDirectory;
    }
    void setSelectedDirectory(ObjectProperty<Directory> selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

}
