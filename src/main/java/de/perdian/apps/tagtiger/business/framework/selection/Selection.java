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
package de.perdian.apps.tagtiger.business.framework.selection;

import java.io.File;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.perdian.apps.tagtiger.business.framework.tagging.FileWithTags;

/**
 * Wrapper around a list of selected files
 *
 * @author Christian Robert
 */

public class Selection {

    private ObjectProperty<File> selectedDirectory = null;
    private ObservableList<FileWithTags> availableFiles = null;
    private ObservableList<FileWithTags> selectedFiles = null;

    public Selection() {
        this.setAvailableFiles(FXCollections.observableArrayList());
        this.setSelectedFiles(FXCollections.observableArrayList());
        this.setSelectedDirectory(new SimpleObjectProperty<>());
    }

    public void updateDirectory(File directory) {
        if (this.getSelectedDirectory().get() == null || !this.getSelectedDirectory().getValue().equals(directory)) {
            this.getSelectedDirectory().setValue(directory);
        }
    }

    public void updateFiles(List<FileWithTags> newFiles) {
        this.getAvailableFiles().setAll(newFiles);
        this.getSelectedFiles().clear();
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObservableList<FileWithTags> getAvailableFiles() {
        return this.availableFiles;
    }
    private void setAvailableFiles(ObservableList<FileWithTags> availableFiles) {
        this.availableFiles = availableFiles;
    }

    public ObservableList<FileWithTags> getSelectedFiles() {
        return this.selectedFiles;
    }
    private void setSelectedFiles(ObservableList<FileWithTags> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    public ObjectProperty<File> getSelectedDirectory() {
        return this.selectedDirectory;
    }
    private void setSelectedDirectory(ObjectProperty<File> selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

}