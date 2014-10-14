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
package de.perdian.apps.tagtiger.fx.components.directories;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;

/**
 * Panel in which a tree of directories is displayed
 *
 * @author Christian Robert
 */

public class DirectorySelectionPane extends BorderPane {

    private final ObjectProperty<File> selectedDirectory = new SimpleObjectProperty<>();

    public DirectorySelectionPane(Localization localization) {

        DirectorySelectionTreeItem directoryRootItem = DirectorySelectionTreeItem.createRootItem();
        TreeView<DirectorySelectionBean> directoryTree = new TreeView<>(directoryRootItem);
        directoryTree.setShowRoot(false);
        directoryTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        directoryTree.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> this.handleSelectedItemChange(oldValue, newValue));

        MenuItem reloadMenuItem = new MenuItem(localization.reload(), new ImageView(new Image(DirectorySelectionPane.class.getClassLoader().getResourceAsStream("icons/16/refresh.png"))));
        reloadMenuItem.setOnAction(event -> this.handleSelectedItemReload(directoryTree));
        ContextMenu directoryTreeContextMenu = new ContextMenu();
        directoryTreeContextMenu.getItems().add(reloadMenuItem);
        directoryTree.setContextMenu(directoryTreeContextMenu);

        this.selectedDirectoryProperty().addListener((o, oldValue, newValue) -> this.handleSelectedDirectoryChange(directoryTree, newValue));

        this.setCenter(directoryTree);

    }

    private void handleSelectedItemChange(TreeItem<DirectorySelectionBean> oldValue, TreeItem<DirectorySelectionBean> newValue) {
        if (!Objects.equals(oldValue, newValue)) {

            // First we make sure that the selection is open, so the user only
            // has to click once to expand the current element
            if (newValue != null && !newValue.isExpanded()) {
                Platform.runLater(() -> newValue.setExpanded(true));
            }

            // Now we tell everyone that we've changed the selection
            new Thread(() -> {
                this.setSelectedDirectory(newValue == null || newValue.getValue() == null ? null : newValue.getValue().getFile());
            }).start();

        }
    }

    private void handleSelectedItemReload(TreeView<DirectorySelectionBean> directoryTree) {
        TreeItem<DirectorySelectionBean> selectedItem = directoryTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem instanceof DirectorySelectionTreeItem) {
            ((DirectorySelectionTreeItem)selectedItem).reloadChildren();
        }
        this.setSelectedDirectory(null);
        this.handleSelectedItemChange(null, selectedItem);
    }

    private void handleSelectedDirectoryChange(TreeView<DirectorySelectionBean> treeView, File directory) {

        // If the directory is set from the outside, we need to manually
        // traverse the tree and make sure that the
        TreeItem<DirectorySelectionBean> selectedItem = treeView.getSelectionModel().getSelectedItem();
        DirectorySelectionBean selectedBean = selectedItem == null ? null : selectedItem.getValue();
        File selectedFile = selectedBean == null ? null : selectedBean.getFile();

        // Make sure that the selection actually is different. If it's not, then
        // we won't show anything at all, since we already have the target file
        // under display
        if (!Objects.equals(selectedFile, directory)) {
            this.selectNodeForDirectory(treeView, directory);
        }

    }

    private void selectNodeForDirectory(TreeView<DirectorySelectionBean> directoryTree, File directory) {

        List<File> filesFromRoot = new LinkedList<>();
        for (File currentFile = directory; currentFile != null; currentFile = currentFile.getParentFile()) {
            filesFromRoot.add(0, currentFile);
        }

        TreeItem<DirectorySelectionBean> resolvedItem = directoryTree.getRoot();
        for (File currentFile : filesFromRoot) {
            resolvedItem = this.findChildItem(resolvedItem, currentFile);
            if (resolvedItem == null) {
                break;
            }
        }

        if (resolvedItem != null && !resolvedItem.equals(directoryTree.getRoot())) {
            TreeItem<DirectorySelectionBean> updateItem = resolvedItem;
            Platform.runLater(() -> directoryTree.getSelectionModel().select(updateItem));
        }

    }

    private TreeItem<DirectorySelectionBean> findChildItem(TreeItem<DirectorySelectionBean> parentItem, File file) {
        if (parentItem != null) {
            if (parentItem instanceof DirectorySelectionTreeItem) {
                ((DirectorySelectionTreeItem)parentItem).ensureChildren();
            }
            if (parentItem.getChildren() != null) {
                for (TreeItem<DirectorySelectionBean> directoryItem : parentItem.getChildren()) {
                    if (file.equals(directoryItem.getValue().getFile())) {
                        return directoryItem;
                    }
                }
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObjectProperty<File> selectedDirectoryProperty() {
        return this.selectedDirectory;
    }
    public File getSelectedDirectory() {
        return this.selectedDirectory.get();
    }
    public void setSelectedDirectory(File directory) {
        this.selectedDirectory.set(directory);
    }

}