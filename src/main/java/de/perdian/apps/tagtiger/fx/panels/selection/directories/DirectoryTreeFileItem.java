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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class DirectoryTreeFileItem extends TreeItem<DirectoryTreeFile> {

    private boolean childrenLoaded = false;

    private DirectoryTreeFileItem(DirectoryTreeFile path) {
        super(path, new ImageView(new Image(DirectoryTreeFileItem.class.getClassLoader().getResourceAsStream("icons/16/folder.png"))));
        this.addEventHandler(TreeItem.branchExpandedEvent(), new ChangeIconHandler(new Image(DirectoryTreeFileItem.class.getClassLoader().getResourceAsStream("icons/16/folder-open.png"))));
        this.addEventHandler(TreeItem.branchCollapsedEvent(), new ChangeIconHandler(new Image(DirectoryTreeFileItem.class.getClassLoader().getResourceAsStream("icons/16/folder.png"))));
    }

    static List<DirectoryTreeFileItem> listRoots() {
        return DirectoryTreeFile.listRoots().stream()
                .map(DirectoryTreeFileItem::new)
                .peek(item -> item.ensureChildrenInternal(0))
                .collect(Collectors.toList());
    }

    @Override
    public ObservableList<TreeItem<DirectoryTreeFile>> getChildren() {
        this.ensureChildren(1);
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        this.ensureChildren(1);
        return super.isLeaf();
    }

    synchronized void ensureChildren(int level) {
        if(!this.isChildrenLoaded()) {
            this.setChildrenLoaded(true);
            super.getChildren().setAll(Arrays.asList(new TreeItem<>(new DirectoryTreeFile(null, "..."))));
            new Thread(() -> this.ensureChildrenInternal(level)).start();
        }
    }

    void ensureChildrenInternalIfNotLoaded(int level) {
        if(!this.isChildrenLoaded()) {
            this.setChildrenLoaded(true);
            this.ensureChildrenInternal(level);
        }
    }

    void ensureChildrenInternal(int level) {
        this.setChildrenLoaded(true);
        List<DirectoryTreeFileItem> items = this.getValue().listChildren().stream().map(DirectoryTreeFileItem::new).collect(Collectors.toList());
        if (level > 0) {
            items.stream().forEach(item -> item.ensureChildrenInternal(level - 1));
        }
        super.getChildren().setAll(items);
    }

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    static class ChangeIconHandler implements EventHandler<TreeModificationEvent<DirectoryTreeFileItem>> {

        private Image image = null;

        ChangeIconHandler(Image image) {
            this.setImage(image);
        }

        @Override
        public void handle(TreeModificationEvent<DirectoryTreeFileItem> event) {
            event.getSource().setGraphic(new ImageView(this.getImage()));
        }

        // ---------------------------------------------------------------------
        // --- Property access methods -----------------------------------------
        // ---------------------------------------------------------------------

        private Image getImage() {
            return this.image;
        }
        private void setImage(Image image) {
            this.image = image;
        }

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