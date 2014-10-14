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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class DirectorySelectionTreeItem extends TreeItem<DirectorySelectionBean> {

    private boolean childrenLoaded = false;

    private DirectorySelectionTreeItem(DirectorySelectionBean path, boolean virtualItem) {
        super(path, new ImageView(new Image(DirectorySelectionTreeItem.class.getClassLoader().getResourceAsStream("icons/16/folder.png"))));
        if (!virtualItem) {
            this.addEventHandler(TreeItem.branchExpandedEvent(), event -> this.handleBranchExpandedEvent(event));
            this.addEventHandler(TreeItem.branchExpandedEvent(), event -> this.setGraphic(new ImageView(new Image(DirectorySelectionTreeItem.class.getClassLoader().getResourceAsStream("icons/16/folder-open.png")))));
            this.addEventHandler(TreeItem.branchCollapsedEvent(), event -> this.setGraphic(new ImageView(new Image(DirectorySelectionTreeItem.class.getClassLoader().getResourceAsStream("icons/16/folder.png")))));
            this.getChildren().add(new DirectorySelectionTreeItem(new DirectorySelectionBean(null, "..."), true));
        }
    }

    /**
     * Creates the root item that can be used in a tree and contains the
     * complete file system directory tree
     */
    static DirectorySelectionTreeItem createRootItem() {

        List<DirectorySelectionTreeItem> rootChildren = DirectorySelectionBean.listRoots().stream()
            .map(path -> new DirectorySelectionTreeItem(path, false))
            .peek(item -> item.populateChildrenToDepth(1))
            .collect(Collectors.toList());

        DirectorySelectionTreeItem rootItem = new DirectorySelectionTreeItem(null, true);
        rootItem.getChildren().setAll(rootChildren);
        return rootItem;

    }

    void ensureChildren() {
        this.populateChildrenToDepth(1);
    }

    void reloadChildren() {

        List<DirectorySelectionTreeItem> loadingChildren = new ArrayList<>();
        loadingChildren.add(new DirectorySelectionTreeItem(new DirectorySelectionBean(null, "..."), true));
        this.getChildren().setAll(loadingChildren);

        new Thread(() -> {
            List<DirectorySelectionTreeItem> children = this.createChildrenList();
            children.forEach(child -> child.populateChildrenToDepth(0));
            Platform.runLater(() -> this.getChildren().setAll(children));
        }).start();
    }

    private void handleBranchExpandedEvent(TreeModificationEvent<Object> event) {
        new Thread(() -> {
            this.populateChildrenToDepth(2);
        }).start();
    }

    private void populateChildrenToDepth(int depth) {
        if (this.getValue() != null) {
            if (!this.isChildrenLoaded()) {
                this.setChildrenLoaded(true);
                this.getChildren().setAll(this.createChildrenList());
                if (depth > 0) {
                    this.getChildren().forEach(child -> ((DirectorySelectionTreeItem)child).populateChildrenToDepth(depth - 1));
                }
            }
        }
    }

    private List<DirectorySelectionTreeItem> createChildrenList() {
        if (this.getValue() == null) {
            return Collections.emptyList();
        } else {
            return this.getValue().listChildren().stream().map(path -> new DirectorySelectionTreeItem(path, false)).collect(Collectors.toList());
        }
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private boolean isChildrenLoaded() {
        return this.childrenLoaded;
    }
    private void setChildrenLoaded(boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }

}