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
package de.perdian.apps.tagtiger3.fx.components.selection.directories;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.TreeItem;

public class DirectoryTreeItem extends TreeItem<Directory> {

    private static final Logger log = LoggerFactory.getLogger(DirectoryTreeItem.class);
    private boolean childrenLoaded = false;

    public DirectoryTreeItem(Directory directory) {
        this.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER));
        this.setValue(directory);
        this.addEventHandler(TreeItem.branchExpandedEvent(), event -> this.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN)));
        this.addEventHandler(TreeItem.branchCollapsedEvent(), event -> this.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER)));
        this.addEventHandler(TreeItem.branchExpandedEvent(), event -> this.loadChildren(2));
    }

    public static DirectoryTreeItem createRootTreeItem() {
        DirectoryTreeItem rootItem = new DirectoryTreeItem(null);
        Arrays.stream(File.listRoots()).map(File::toPath).forEach(rootPath -> rootItem.getChildren().add(DirectoryTreeItem.createRootTreeItem(rootPath)));
        return rootItem;
    }

    private static DirectoryTreeItem createRootTreeItem(Path rootPath) {
        Directory rootDirectory = new Directory(rootPath.toString(), rootPath);
        DirectoryTreeItem rootTreeItem = new DirectoryTreeItem(rootDirectory);
        rootTreeItem.loadChildren(2);
        rootTreeItem.setExpanded(true);
        return rootTreeItem;
    }

    private synchronized void loadChildren(int depth) {
        if (this.getValue() != null && depth > 0) {
            if (!this.isChildrenLoaded()) {
                log.debug("Loading children for path: {}", this.getValue().getPath());
                try {
                    List<Directory> childDirectories = this.getValue().loadChildren();
                    List<DirectoryTreeItem> childItems = new ArrayList<>(childDirectories.size());
                    for (Directory childDirectory : childDirectories) {
                        DirectoryTreeItem childItem = new DirectoryTreeItem(childDirectory);
                        childItem.loadChildren(depth - 1);
                        childItems.add(childItem);
                    }
                    this.getChildren().setAll(childItems);
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
                    for (TreeItem<Directory> childItem : this.getChildren()) {
                        if (childItem.getValue().getPath().equals(needle)) {
                            return ((DirectoryTreeItem)childItem).ensureChildrenLoadedUntil(directory);
                        }
                    }
                }
            }
            return null;
        }
    }

    private boolean isChildrenLoaded() {
        return this.childrenLoaded;
    }
    private void setChildrenLoaded(boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }

    List<DirectoryTreeItem> computePathFromRoot() {
        List<DirectoryTreeItem> resultList = new LinkedList<>();
        for (DirectoryTreeItem currentItem = this; currentItem != null; currentItem = (DirectoryTreeItem)currentItem.getParent()) {
            resultList.add(0, currentItem);
        }
        return resultList;
    }

}
