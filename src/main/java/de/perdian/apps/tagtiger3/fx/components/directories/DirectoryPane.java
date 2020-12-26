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
package de.perdian.apps.tagtiger3.fx.components.directories;

import java.nio.file.Path;
import java.util.Objects;

import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class DirectoryPane extends GridPane {

    private ObjectProperty<Directory> selectedDirectory = new SimpleObjectProperty<>();
    private DirectoryTreeView directoryTreeView = null;

    public DirectoryPane(Preferences preferences) {

        DirectorySelectionPane directorySelectionPane = new DirectorySelectionPane(preferences);
        GridPane.setHgrow(directorySelectionPane, Priority.ALWAYS);

        DirectoryTreeView directoryTreeView = new DirectoryTreeView();
        directoryTreeView.setMinWidth(200);
        directoryTreeView.setPrefWidth(300);
        directoryTreeView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> directorySelectionPane.getSelectedPathProperty().setValue(newValue == null ? null : newValue.getValue().getPath()));
        directoryTreeView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> this.getSelectedDirectory().setValue(newValue == null ? null : newValue.getValue()));
        directorySelectionPane.getSelectedPathProperty().addListener((o, oldValue, newValue) -> directoryTreeView.selectDirectory(newValue));
        GridPane.setHgrow(directoryTreeView, Priority.ALWAYS);
        GridPane.setVgrow(directoryTreeView, Priority.ALWAYS);

        this.add(directorySelectionPane, 0, 0, 1, 1);
        this.add(directoryTreeView, 0, 1, 1, 1);
        this.setVgap(5);
        this.setDirectoryTreeView(directoryTreeView);

    }

    public void selectPath(Path path) {
        TreeItem<Directory> selectedTreeItem = this.getDirectoryTreeView().getSelectionModel().getSelectedItem();
        if (!Objects.equals(path, selectedTreeItem == null || selectedTreeItem.getValue() == null ? null : selectedTreeItem.getValue().getPath())) {
            this.getDirectoryTreeView().selectDirectory(path);
        }
    }

    private DirectoryTreeView getDirectoryTreeView() {
        return this.directoryTreeView;
    }
    private void setDirectoryTreeView(DirectoryTreeView directoryTreeView) {
        this.directoryTreeView = directoryTreeView;
    }

    public ObjectProperty<Directory> getSelectedDirectory() {
        return this.selectedDirectory;
    }
    void setSelectedDirectory(ObjectProperty<Directory> selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

}
