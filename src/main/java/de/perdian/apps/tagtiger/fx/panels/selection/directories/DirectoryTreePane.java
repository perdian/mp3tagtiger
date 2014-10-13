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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import de.perdian.apps.tagtiger.business.framework.TagTiger;

public class DirectoryTreePane extends BorderPane {

    public DirectoryTreePane(TagTiger tagTiger) {

        TreeItem<DirectoryTreeFile> rootTreeItem = new TreeItem<>(null);
        rootTreeItem.getChildren().addAll(DirectoryTreeFileItem.listRoots());
        rootTreeItem.setExpanded(true);

        TreeView<DirectoryTreeFile> directoryTree = new TreeView<>(rootTreeItem);
        directoryTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isExpanded()) {
                Platform.runLater(() -> newValue.setExpanded(true));
            }
        });
        directoryTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            File selectedFile = newValue == null || newValue.getValue() == null ? null : newValue.getValue().getFile();
            if (selectedFile != null && !Arrays.asList(File.listRoots()).contains(selectedFile)) {
                new Thread(() -> tagTiger.getSelection().updateDirectory(selectedFile)).start();
            }
        });
        directoryTree.setShowRoot(false);
        directoryTree.getSelectionModel().select(rootTreeItem);

        MenuItem reloadMenuItem = new MenuItem(tagTiger.getLocalization().reload(), new ImageView(new Image(DirectoryTreePane.class.getClassLoader().getResourceAsStream("icons/16/refresh.png"))));
        reloadMenuItem.setOnAction(event -> this.reloadSelectedNode(directoryTree));
        ContextMenu directoryTreeContextMenu = new ContextMenu();
        directoryTreeContextMenu.getItems().add(reloadMenuItem);
        directoryTree.setContextMenu(directoryTreeContextMenu);

        tagTiger.getSelection().getSelectedDirectory().addListener((observable, oldValue, newValue) -> this.selectNodeForDirectory(directoryTree, newValue));

        this.setCenter(directoryTree);

    }

    private void selectNodeForDirectory(TreeView<DirectoryTreeFile> directoryTree, File directory) {

        List<File> filesFromRoot = new LinkedList<>();
        for (File currentFile = directory; currentFile != null; currentFile = currentFile.getParentFile()) {
            filesFromRoot.add(0, currentFile);
        }

        TreeItem<DirectoryTreeFile> resolvedItem = directoryTree.getRoot();
        for (File currentFile : filesFromRoot) {
            resolvedItem = this.findChildItem(resolvedItem, currentFile);
        }
        if (resolvedItem != null) {
            TreeItem<DirectoryTreeFile> updateItem = resolvedItem;
            Platform.runLater(() -> {
                directoryTree.getSelectionModel().select(updateItem);
            });
        }

    }

    private TreeItem<DirectoryTreeFile> findChildItem(TreeItem<DirectoryTreeFile> parentItem, File file) {
        if (parentItem instanceof DirectoryTreeFileItem) {
            DirectoryTreeFileItem parentFileItem = (DirectoryTreeFileItem)parentItem;
            parentFileItem.ensureChildren();
        }
        if (parentItem != null && parentItem.getChildren() != null) {
            for (TreeItem<DirectoryTreeFile> directoryItem : parentItem.getChildren()) {
                if (file.equals(directoryItem.getValue().getFile())) {
                    return directoryItem;
                }
            }
        }
        return null;
    }

    private void reloadSelectedNode(TreeView<DirectoryTreeFile> directoryTree) {
        DirectoryTreeFileItem item = (DirectoryTreeFileItem)directoryTree.getSelectionModel().getSelectedItem();
        if (item != null) {
            item.reloadChildren();
        }
    }

}