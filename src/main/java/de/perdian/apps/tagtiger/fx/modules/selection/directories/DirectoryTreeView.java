/*
 * Copyright 2014-2019 Christian Seifert
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
package de.perdian.apps.tagtiger.fx.modules.selection.directories;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DirectoryTreeView extends TreeView<DirectoryPathWrapper> {

    private static final Logger log = LoggerFactory.getLogger(DirectoryTreeView.class);

    private ObjectProperty<Path> selectedDirectoryProperty = null;
    private Map<Path, DirectoryTreeItem> rootItems = null;

    public DirectoryTreeView(Localization localization) {
        super(new TreeItem<>());

        Map<Path, DirectoryTreeItem> rootItems = DirectoryTreeHelper.computeRootItems();
        rootItems.entrySet().forEach(entry -> this.getRoot().getChildren().add(entry.getValue()));
        this.setRootItems(rootItems);

        ObjectProperty<Path> selectedDirectoryProperty = new SimpleObjectProperty<>();
        this.setSelectedDirectoryProperty(selectedDirectoryProperty);

        this.setShowRoot(false);
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            Path newSelectedPath = newValue == null ? null : newValue.getValue().getPath();
            Path currentlySelectedPath = this.selectedDirectory().getValue();
            if (!Objects.equals(currentlySelectedPath, newSelectedPath)) {
                this.getSelectedDirectoryProperty().setValue(newSelectedPath);
            }
        });

        MenuItem reloadMenuItem = new MenuItem(localization.reload(), new ImageView(new Image(DirectoryTreeView.class.getClassLoader().getResourceAsStream("icons/16/refresh.png"))));
        reloadMenuItem.setOnAction(event -> this.handleSelectedItemReload());
        ContextMenu directoryTreeContextMenu = new ContextMenu();
        directoryTreeContextMenu.getItems().add(reloadMenuItem);
        this.setContextMenu(directoryTreeContextMenu);

    }

    public void selectDirectory(Path directory) {
        if (directory != null) {

            // First we make sure that all the nodes leading up to the selected directory are visible and have their
            // children loaded so they're correctly displayed in the UI
            DirectoryTreeItem rootItem = this.getRootItems().get(directory.getRoot());
            DirectoryTreeItem directoryItem = rootItem == null ? null : rootItem.ensureChildrenLoadedUntil(directory);
            if (directoryItem != null) {

                // Now we can open the tree until we have found or target item
                log.debug("Opening tree until item: " + directoryItem.getValue().getPath());
                DirectoryTreeHelper.computeItemsFromRoot(directoryItem).stream().forEach(item -> item.setExpanded(true));
                this.getSelectionModel().select(directoryItem);

            }

        }
    }

    private void handleSelectedItemReload() {

        DirectoryTreeItem selectedItem = (DirectoryTreeItem)this.getSelectionModel().getSelectedItem();
        this.getSelectionModel().clearSelection();

        selectedItem.reloadChildren();
        this.getSelectionModel().select(selectedItem);

    }

    public Property<Path> selectedDirectory() {
        return this.selectedDirectoryProperty;
    }

    private ObjectProperty<Path> getSelectedDirectoryProperty() {
        return this.selectedDirectoryProperty;
    }
    private void setSelectedDirectoryProperty(ObjectProperty<Path> selectedDirectoryProperty) {
        this.selectedDirectoryProperty = selectedDirectoryProperty;
    }

    private Map<Path, DirectoryTreeItem> getRootItems() {
        return this.rootItems;
    }
    private void setRootItems(Map<Path, DirectoryTreeItem> rootItems) {
        this.rootItems = rootItems;
    }

}
