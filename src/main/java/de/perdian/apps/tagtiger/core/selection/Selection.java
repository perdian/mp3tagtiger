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
package de.perdian.apps.tagtiger.core.selection;

import java.io.File;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

/**
 * Wrapper around a list of selected files
 *
 * @author Christian Robert
 */

public class Selection {

    private final Property<File> currentDirectory = new SimpleObjectProperty<>();
    private final Property<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> changedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Selection() {
        this.availableFilesProperty().addListener((o, oldValue, newValue) -> this.changedFilesProperty().clear());
    }

    public Property<File> currentDirectoryProperty() {
        return this.currentDirectory;
    }

    public Property<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    public ListProperty<TaggableFile> availableFilesProperty() {
        return this.availableFiles;
    }

    public ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

    public ListProperty<TaggableFile> changedFilesProperty() {
        return this.changedFiles;
    }

}