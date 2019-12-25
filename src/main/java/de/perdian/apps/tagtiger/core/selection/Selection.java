/*
 * Copyright 2014 Christian Seifert
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

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

/**
 * Wrapper around a list of selected files
 *
 * @author Christian Seifert
 */

public class Selection {

    private final Property<Path> currentDirectory = new SimpleObjectProperty<>();
    private final Property<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> changedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Selection() {
        this.availableFilesProperty().addListener(this::onAvailableFilesChanged);
        this.currentFileProperty().addListener(this::onCurrentFileChanged);
    }

    private void onAvailableFilesChanged(Object source, List<TaggableFile> oldValue, List<TaggableFile> newValue) {
        this.changedFilesProperty().clear();
    }

    private void onCurrentFileChanged(Object source, TaggableFile oldValue, TaggableFile newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            if (oldValue != null) {
                oldValue.activeProperty().setValue(Boolean.FALSE);
            }
            if (newValue != null) {
                newValue.activeProperty().setValue(Boolean.TRUE);
            }
        }
    }

    public Property<Path> currentDirectoryProperty() {
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