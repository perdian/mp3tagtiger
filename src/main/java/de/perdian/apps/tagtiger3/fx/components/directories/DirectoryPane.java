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

import java.io.File;

import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class DirectoryPane extends GridPane {

    private ObjectProperty<File> selectedDirectory = new SimpleObjectProperty<>();

    public DirectoryPane(Preferences preferences) {

        TextField directoryPathField = new TextField();
        directoryPathField.setOnAction(event -> this.setSelectedDirectory(new File(((TextField)event.getSource()).getText())));
        GridPane.setHgrow(directoryPathField, Priority.ALWAYS);

        DirectoryTreeView pathTreeView = new DirectoryTreeView();
        pathTreeView.selectDirectory(new File(System.getProperty("user.home")));
        pathTreeView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> this.selectedDirectoryProperty().setValue(newValue == null ? null : newValue.getValue()));
        GridPane.setHgrow(pathTreeView, Priority.ALWAYS);
        GridPane.setVgrow(pathTreeView, Priority.ALWAYS);

        this.add(directoryPathField, 0, 0, 1, 1);
        this.add(pathTreeView, 0, 1, 1, 1);
        this.setVgap(5);

        this.selectedDirectoryProperty().addListener((o, oldValue, newValue) -> pathTreeView.selectDirectory(newValue));
        this.selectedDirectoryProperty().addListener((o, oldValue, newValue) -> directoryPathField.setText(newValue.toString()));

    }

    public ObjectProperty<File> selectedDirectoryProperty() {
        return this.selectedDirectory;
    }
    public File getSelectedDirectory() {
        return this.selectedDirectoryProperty().getValue();
    }
    public void setSelectedDirectory(File path) {
        this.selectedDirectoryProperty().setValue(path);
    }

}
