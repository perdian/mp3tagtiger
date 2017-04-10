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
package de.perdian.apps.tagtiger.fx.panels.directories;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class DirectoryTreeItem extends TreeItem<Directory> {

    private FileFilter childrenFilter = null;
    private boolean childrenLoaded = false;

    private DirectoryTreeItem(Directory directory, FileFilter fileFilter) {
        super(directory, new ImageView(new Image(DirectoryTreeItem.class.getClassLoader().getResourceAsStream("icons/16/folder.png"))));
        this.setChildrenFilter(fileFilter);
        this.addEventHandler(TreeItem.branchExpandedEvent(), event -> this.handleBranchExpandedEvent(event));
        this.addEventHandler(TreeItem.branchExpandedEvent(), event -> this.setGraphic(new ImageView(new Image(DirectoryTreeItem.class.getClassLoader().getResourceAsStream("icons/16/folder-open.png")))));
        this.addEventHandler(TreeItem.branchCollapsedEvent(), event -> this.setGraphic(new ImageView(new Image(DirectoryTreeItem.class.getClassLoader().getResourceAsStream("icons/16/folder.png")))));
    }

    public static DirectoryTreeItem createRootItem(FileFilter fileFilter) {

        List<DirectoryTreeItem> rootChildren = Arrays.stream(File.listRoots())
            .map(file -> new Directory(file, file.getName().length() <= 0 ? file.getAbsolutePath() : file.getName()))
            .map(directory -> new DirectoryTreeItem(directory, fileFilter))
            .peek(item -> item.loadChildren(1))
            .collect(Collectors.toList());

        DirectoryTreeItem rootItem = new DirectoryTreeItem(null, fileFilter);
        rootItem.setChildrenLoaded(true);
        rootItem.getChildren().setAll(rootChildren);
        return rootItem;

    }

    void reloadChildren() {
        this.setChildrenLoaded(false);
        this.getChildren().clear();
        this.loadChildrenIfNeeded(2, true);
    }

    public DirectoryTreeItem lookupChildForFile(File currentDirectory) {
        this.loadChildrenIfNeeded(2, false);
        for (TreeItem<Directory> childItem : this.getChildren()) {
            if (Objects.equals(currentDirectory, childItem.getValue().getFile())) {
                return (DirectoryTreeItem)childItem;
            }
        }
        return null;
    }

    private void handleBranchExpandedEvent(TreeModificationEvent<?> event) {
        if (this.getValue() != null) {
            this.loadChildrenIfNeeded(2, true);
        }
    }

    private void loadChildrenIfNeeded(int depth, boolean launchNewThread) {
        if (depth > 0) {
            if (this.isChildrenLoaded()) {
                this.getChildren().forEach(child -> ((DirectoryTreeItem)child).loadChildrenIfNeeded(depth - 1, launchNewThread));
            } else {
                if(launchNewThread) {
                    new Thread(() -> {
                        this.loadChildren(depth);
                    }).start();
                } else {
                    this.loadChildren(depth);
                }
            }
        }
    }

    private void loadChildren(int depth) {

        List<DirectoryTreeItem> childItems = this.getValue().loadChildren(this.getChildrenFilter())
            .stream()
            .map(directory -> new DirectoryTreeItem(directory, this.getChildrenFilter()))
            .collect(Collectors.toList());

        if (depth > 1) {
            childItems.forEach(child -> child.loadChildren(depth - 1));
        }
        this.getChildren().setAll(childItems);
        this.setChildrenLoaded(true);

    }

    private FileFilter getChildrenFilter() {
        return this.childrenFilter;
    }
    private void setChildrenFilter(FileFilter childrenFilter) {
        this.childrenFilter = childrenFilter;
    }

    private boolean isChildrenLoaded() {
        return this.childrenLoaded;
    }
    private void setChildrenLoaded(boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }

}