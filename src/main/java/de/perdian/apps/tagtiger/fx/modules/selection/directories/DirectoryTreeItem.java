/*
 * Copyright 2014-2019 Christian Robert
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class DirectoryTreeItem extends TreeItem<DirectoryPathWrapper> {

    private static final Logger log = LoggerFactory.getLogger(DirectoryTreeItem.class);

    private boolean childrenLoaded = false;

    DirectoryTreeItem(Path path, int loadChildrenDepth) {
        super(new DirectoryPathWrapper(path), new ImageView(new Image(DirectoryTreeItem.class.getClassLoader().getResourceAsStream("icons/16/folder.png"))));
        this.addEventHandler(TreeItem.branchExpandedEvent(), event -> this.setGraphic(new ImageView(new Image(DirectoryTreeItem.class.getClassLoader().getResourceAsStream("icons/16/folder-open.png")))));
        this.addEventHandler(TreeItem.branchCollapsedEvent(), event -> this.setGraphic(new ImageView(new Image(DirectoryTreeItem.class.getClassLoader().getResourceAsStream("icons/16/folder.png")))));
        this.addEventHandler(TreeItem.branchExpandedEvent(), event -> this.handleBranchExpandedEvent(event));

        this.loadChildren(loadChildrenDepth);
    }

    private synchronized void loadChildren(int depth) {
        if (depth > 0) {
            if (!this.isChildrenLoaded()) {
                log.debug("Loading children for path: {}", this.getValue().getPath());
                try {
                    StreamSupport.stream(Files.newDirectoryStream(this.getValue().getPath(), path -> Files.isDirectory(path) && !Files.isSymbolicLink(path) && !Files.isHidden(path) && !path.getFileName().toString().startsWith(".")).spliterator(), false)
                        .sorted((p1, p2) -> p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString()))
                        .forEach(p -> this.getChildren().add(new DirectoryTreeItem(p, depth - 1)));
                } catch (IOException e) {
                    log.warn("Cannot load children of directory: {}", this.getValue().getPath());
                } finally {
                    this.setChildrenLoaded(true);
                }
            }
            if (depth > 1 && this.getChildren() != null) {
                this.getChildren().stream()
                    .filter(item -> item instanceof DirectoryTreeItem)
                    .forEach(item -> ((DirectoryTreeItem)item).loadChildren(depth - 1));
            }
        }
    }

    synchronized DirectoryTreeItem ensureChildrenLoadedUntil(Path directory) {
        if (this.getValue().getPath().equals(directory)) {
            this.loadChildren(2);
            return this;
        } else {
            for (Path needle = directory; needle != null; needle = needle.getParent()) {
                if (this.getValue().getPath().equals(needle.getParent())) {
                    this.loadChildren(2);
                    for (TreeItem<DirectoryPathWrapper> childItem : this.getChildren()) {
                        if (childItem.getValue().getPath().equals(needle)) {
                            return ((DirectoryTreeItem)childItem).ensureChildrenLoadedUntil(directory);
                        }
                    }
                }
            }
            return null;
        }
    }

    synchronized void reloadChildren() {
        this.setChildrenLoaded(false);
        this.getChildren().clear();
        this.loadChildren(2);
    }

    private void handleBranchExpandedEvent(TreeModificationEvent<Object> event) {
        this.loadChildren(2);
    }

    private boolean isChildrenLoaded() {
        return this.childrenLoaded;
    }
    private void setChildrenLoaded(boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }

}
