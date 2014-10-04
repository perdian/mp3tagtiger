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
package de.perdian.apps.tagtiger.fx.panels;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import de.perdian.apps.tagtiger.business.model.SelectedDirectory;

class FileSelectionDirectoryTreeItem extends TreeItem<SelectedDirectory> {

    private boolean childrenLoaded = false;

    private FileSelectionDirectoryTreeItem(SelectedDirectory path) {
        super(path);
    }

    static List<FileSelectionDirectoryTreeItem> listRoots() {
        return StreamSupport.stream(FileSystems.getDefault().getRootDirectories().spliterator(), false)
                .map(path -> new SelectedDirectory(path, path.toString()))
                .map(FileSelectionDirectoryTreeItem::new).collect(Collectors.toList());
    }

    @Override
    public ObservableList<TreeItem<SelectedDirectory>> getChildren() {
        this.ensureChildren();
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        this.ensureChildren();
        return super.isLeaf();
    }

    private synchronized void ensureChildren() {
        ObservableList<TreeItem<SelectedDirectory>> children = super.getChildren();
        if(!this.isChildrenLoaded()) {
            this.setChildrenLoaded(true);
            children.setAll(Arrays.asList(new TreeItem<>(new SelectedDirectory(null, "..."))));
            new Thread(() -> this.ensureChildren(2)).start();
        }
    }

    private void ensureChildren(int level) {
        this.setChildrenLoaded(true);
        try {
            List<FileSelectionDirectoryTreeItem> items = FXCollections.observableArrayList(Files
                .list(this.getValue().getPath())
                .filter(Files::isReadable)
                .filter(Files::isDirectory)
                .filter(path -> { try { return !Files.isHidden(path); } catch (Exception e) { return false; } })
                .sorted()
                .map(path -> new SelectedDirectory(path, path.getFileName().toString()))
                .map(FileSelectionDirectoryTreeItem::new)
                .collect(Collectors.toList())
            );
            if (level > 0) {
                items.stream().forEach(item -> item.ensureChildren(level - 1));
            }
            super.getChildren().setAll(items);
        } catch (IOException e) {
            super.getChildren().setAll(FXCollections.observableArrayList());
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