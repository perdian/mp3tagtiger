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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class DirectoryTreeFileItem extends TreeItem<DirectoryTreeFile> {

    private boolean childrenLoaded = false;

    private DirectoryTreeFileItem(DirectoryTreeFile path, boolean virtualItem) {
        super(path, new ImageView(new Image(DirectoryTreeFileItem.class.getClassLoader().getResourceAsStream("icons/16/folder.png"))));
        if (!virtualItem) {
            this.addEventHandler(TreeItem.branchExpandedEvent(), event -> this.handleBranchExpandedEvent(event));
            this.addEventHandler(TreeItem.branchExpandedEvent(), event -> this.setGraphic(new ImageView(new Image(DirectoryTreeFileItem.class.getClassLoader().getResourceAsStream("icons/16/folder-open.png")))));
            this.addEventHandler(TreeItem.branchCollapsedEvent(), event -> this.setGraphic(new ImageView(new Image(DirectoryTreeFileItem.class.getClassLoader().getResourceAsStream("icons/16/folder.png")))));
            this.getChildren().add(new DirectoryTreeFileItem(new DirectoryTreeFile(null, "..."), true));
        }
    }

    static List<DirectoryTreeFileItem> listRoots() {
        return DirectoryTreeFile.listRoots().stream()
            .map(path -> new DirectoryTreeFileItem(path, false))
            .peek(item -> item.populateChildrenToDepth(1))
            .collect(Collectors.toList());
    }

    void ensureChildren() {
        this.populateChildrenToDepth(1);
    }

    void reloadChildren() {
        List<DirectoryTreeFileItem> loadingChildren = new ArrayList<>();
        loadingChildren.add(new DirectoryTreeFileItem(new DirectoryTreeFile(null, "..."), true));
        this.getChildren().setAll(loadingChildren);
        new Thread(() -> {
            List<DirectoryTreeFileItem> children = this.createChildrenList();
            children.forEach(child -> child.populateChildrenToDepth(0));
            Platform.runLater(() -> this.getChildren().setAll(children));
        }).start();
    }

    private void handleBranchExpandedEvent(TreeModificationEvent<Object> event) {
        new Thread(() -> this.populateChildrenToDepth(2)).start();;
    }

    private void populateChildrenToDepth(int depth) {
        if (!this.isChildrenLoaded()) {
            this.setChildrenLoaded(true);
            this.getChildren().setAll(this.createChildrenList());
        }
        if (depth > 0) {
            this.getChildren().forEach(child -> ((DirectoryTreeFileItem)child).populateChildrenToDepth(depth - 1));
        }
    }

    private List<DirectoryTreeFileItem> createChildrenList() {
        return this.getValue().listChildren().stream().map(path -> new DirectoryTreeFileItem(path, false)).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    boolean isChildrenLoaded() {
        return this.childrenLoaded;
    }
    private void setChildrenLoaded(boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }

}