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
import java.util.Collections;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.perdian.apps.tagtiger.business.framework.preferences.PreferencesKey;
import de.perdian.apps.tagtiger.business.framework.preferences.PreferencesLookup;
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
    private ObservableList<FileWithTags> changedFiles = null;
    private ObjectProperty<FileWithTags> selectedFile = null;
    private IntegerProperty selectedIndex = null;
    private PreferencesLookup preferences = null;

    public Selection(PreferencesLookup preferences) {
        this.setPreferences(preferences);
        this.setAvailableFiles(FXCollections.observableArrayList());
        this.setSelectedFile(new SimpleObjectProperty<>());
        this.setSelectedFiles(FXCollections.observableArrayList());
        this.setChangedFiles(FXCollections.observableArrayList());
        this.setSelectedDirectory(new SimpleObjectProperty<>());
        this.setSelectedIndex(new SimpleIntegerProperty(-1));
    }

    public void updateDirectory(File directory) {
        if (this.getSelectedDirectory().get() == null || !this.getSelectedDirectory().getValue().equals(directory)) {
            this.getSelectedDirectory().setValue(directory);
            this.getPreferences().setString(PreferencesKey.CURRENT_DIRECTORY, directory.getAbsolutePath());
        }
    }

    public void updateAvailableFiles(List<FileWithTags> newList) {
        this.getAvailableFiles().setAll(newList);
        this.updateSelectedFiles(Collections.emptyList());
        this.updateSelectedFile(null, -1);
    }

    public void updateSelectedFiles(List<? extends FileWithTags> list) {
        boolean listHasChanged = false;
        listHasChanged |= this.getSelectedFiles().size() != list.size();
        listHasChanged |= !this.getSelectedFiles().containsAll(list);
        if (listHasChanged) {
            this.getSelectedFiles().setAll(list);
        }
    }

    public void updateSelectedFile(FileWithTags selectedItem, int selectedIndex) {
        if (selectedItem == null && this.getSelectedFile() == null) {
            // Do nothing - no change in selection
        } else if (selectedItem != null && selectedItem.equals(this.getSelectedFile())) {
            // Do nothing - no change in selection
        } else {
            this.getSelectedFile().set(selectedItem);
            this.getSelectedIndex().set(selectedIndex);
        }
    }

    public void updateSelectedFileToIndex(int index) {
        List<FileWithTags> availableFiles = this.getAvailableFiles();
        if (!availableFiles.isEmpty()) {
            int cleanIndex = Math.max(0, Math.min(index, availableFiles.size() - 1));
            this.updateSelectedFile(availableFiles.get(cleanIndex), cleanIndex);
        }
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

    private PreferencesLookup getPreferences() {
        return this.preferences;
    }
    private void setPreferences(PreferencesLookup preferences) {
        this.preferences = preferences;
    }

    public IntegerProperty getSelectedIndex() {
        return this.selectedIndex;
    }
    private void setSelectedIndex(IntegerProperty selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public ObjectProperty<FileWithTags> getSelectedFile() {
        return this.selectedFile;
    }
    private void setSelectedFile(ObjectProperty<FileWithTags> selectedFile) {
        this.selectedFile = selectedFile;
    }

    public ObservableList<FileWithTags> getChangedFiles() {
        return this.changedFiles;
    }
    private void setChangedFiles(ObservableList<FileWithTags> changedFiles) {
        this.changedFiles = changedFiles;
    }

}