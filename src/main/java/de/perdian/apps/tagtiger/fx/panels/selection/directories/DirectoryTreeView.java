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
package de.perdian.apps.tagtiger.fx.panels.selection.directories;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A specialized {@code TreeView} implementation that is designed to display the
 * directory structure of the local file system. Since populating the tree
 * completely at startup would require a lot of resources (the whole filesystem
 * tree would have to be walked) the nodes are created on an as-needed base.
 *
 * @author Christian Robert
 */

public class DirectoryTreeView extends TreeView<Directory> {

    private final Property<File> selectedDirectory = new SimpleObjectProperty<>();

    public DirectoryTreeView(Localization localization) {
        this(new DirectoryTreeDefaultFileFilter(), localization);
    }

    /**
     * Creates a new view
     *
     * @param fileFilter
     *     the filter that is used to determine which directories should be
     *     shown
     * @param localization
     *     localization object to enable language specific values
     */
    public DirectoryTreeView(FileFilter fileFilter, Localization localization) {

        this.setShowRoot(false);
        this.setRoot(DirectoryTreeItem.createRootItem(fileFilter));
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            Directory selectedDirectory = newValue == null ? null : newValue.getValue();
            File selectedFile = selectedDirectory == null ? null : selectedDirectory.getFile();
            File currentlySelectedFile = this.selectedDirectoryProperty().getValue();
            if (!Objects.equals(currentlySelectedFile, selectedFile)) {
                this.selectedDirectoryProperty().setValue(selectedFile);
            }
        });

        MenuItem reloadMenuItem = new MenuItem(localization.reload(), new ImageView(new Image(DirectoryTreeView.class.getClassLoader().getResourceAsStream("icons/16/refresh.png"))));
        reloadMenuItem.setOnAction(event -> this.handleSelectedItemReload());
        ContextMenu directoryTreeContextMenu = new ContextMenu();
        directoryTreeContextMenu.getItems().add(reloadMenuItem);
        this.setContextMenu(directoryTreeContextMenu);

    }

    /**
     * Selects a new directory within the current tree
     *
     * @param newDirectory
     *     the new directory to be selected
     */
    public boolean selectDirectory(File newDirectory) {
        if (!Objects.equals(this.selectedDirectoryProperty().getValue(), newDirectory)) {

            List<File> flattenedTreeFromRoot = new LinkedList<>();
            for (File file = newDirectory; file != null; file = file.getParentFile()) {
                flattenedTreeFromRoot.add(0, file);
            }

            DirectoryTreeItem parentTreeItem = (DirectoryTreeItem)this.getRoot();
            for (File currentDirectory : flattenedTreeFromRoot) {
                parentTreeItem = parentTreeItem.lookupChildForFile(currentDirectory);
                if (parentTreeItem == null) {

                    // We cannot find a match in the current file hierarchy, so
                    // there is nothing we can do and simply return right away
                    return false;

                }
            }

            List<DirectoryTreeItem> flattenedTreeItemsFromRoot = new LinkedList<>();
            for (DirectoryTreeItem treeItem = parentTreeItem; treeItem != null; treeItem = (DirectoryTreeItem)treeItem.getParent()) {
                flattenedTreeItemsFromRoot.add(0, treeItem);
            }

            for (DirectoryTreeItem treeItem : flattenedTreeItemsFromRoot) {
                treeItem.setExpanded(true);
            }
            this.getSelectionModel().select(parentTreeItem);
            return true;

        } else {
            return true;
        }
    }

    private void handleSelectedItemReload() {

        DirectoryTreeItem selectedItem = (DirectoryTreeItem)this.getSelectionModel().getSelectedItem();
        this.getSelectionModel().clearSelection();

        selectedItem.reloadChildren();
        this.getSelectionModel().select(selectedItem);

    }

    public Property<File> selectedDirectoryProperty() {
        return this.selectedDirectory;
    }

}