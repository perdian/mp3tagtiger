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

import java.io.File;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;

class DirectoryTreeView extends TreeView<File> {

    private static final Logger log = LoggerFactory.getLogger(DirectoryTreeView.class);

    public DirectoryTreeView() {

        MenuItem reloadMenuItem = new MenuItem("Reload", new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        reloadMenuItem.disableProperty().bind(this.getSelectionModel().selectedItemProperty().isNull());
        reloadMenuItem.setOnAction(event -> ((DirectoryTreeItem)this.getSelectionModel().selectedItemProperty().getValue()).reloadChildren());
        ContextMenu contextMenu = new ContextMenu(reloadMenuItem);

        this.setRoot(DirectoryTreeItem.createRootTreeItem());
        this.setShowRoot(false);
        this.setCellFactory(TextFieldTreeCell.forTreeView(new DirectoryStringConverter()));
        this.setContextMenu(contextMenu);
    }

    void selectDirectory(File directory) {
        File currentlySelectedDirectory = this.getSelectionModel().getSelectedItem() == null ? null : this.getSelectionModel().getSelectedItem().getValue();
        if (directory != null && !Objects.equals(directory, currentlySelectedDirectory)) {

            // First we make sure that all the nodes leading up to the selected directory are visible and have their
            // children loaded so they're correctly displayed in the UI
            DirectoryTreeItem rootDirectoryItem = (DirectoryTreeItem)this.getRoot().getChildren().stream().filter(r -> Objects.equals(r.getValue(), directory.toPath().getRoot().toFile())).findAny().get();
            DirectoryTreeItem directoryItem = rootDirectoryItem.ensureChildrenLoadedUntil(directory);
            if (directoryItem != null) {

                // Now we can open the tree until we have found or target item
                log.debug("Opening tree until item: " + directoryItem.getValue());
                directoryItem.computePathFromRoot().stream().forEach(item -> item.setExpanded(true));
                this.getSelectionModel().select(directoryItem);

            }
        }
    }

    private class DirectoryStringConverter extends StringConverter<File> {

        @Override
        public String toString(File file) {
            return StringUtils.defaultIfEmpty(file.getName(), "/");
        }

        @Override
        public File fromString(String string) {
            throw new UnsupportedOperationException();
        }

    }

}
