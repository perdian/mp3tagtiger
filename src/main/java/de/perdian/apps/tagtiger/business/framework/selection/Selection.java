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
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

/**
 * Wrapper around a list of selected files
 *
 * @author Christian Robert
 */

public class Selection {

    private final ObjectProperty<File> selectedDirectory = new SimpleObjectProperty<>();
    private final ObservableList<TaggableFile> availableFiles = FXCollections.observableArrayList();
    private final ObservableList<TaggableFile> selectedFiles = FXCollections.observableArrayList();
    private final ObservableList<TaggableFile> changedFiles = FXCollections.observableArrayList();
    private final ObjectProperty<TaggableFile> selectedFile = new SimpleObjectProperty<>();
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty(-1);

    public void updateAvailableFiles(List<TaggableFile> newList) {
        this.getAvailableFiles().setAll(newList);
        this.getChangedFiles().clear();
        this.updateSelectedFiles(Collections.emptyList());
        this.updateSelectedFile(null, -1);
    }

    public void updateSelectedFiles(List<? extends TaggableFile> list) {
        boolean listHasChanged = false;
        listHasChanged |= this.getSelectedFiles().size() != list.size();
        listHasChanged |= !this.getSelectedFiles().containsAll(list);
        if (listHasChanged) {
            this.getSelectedFiles().setAll(list);
        }
    }

    public void updateSelectedFile(TaggableFile selectedItem, int selectedIndex) {
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
        List<TaggableFile> availableFiles = this.getAvailableFiles();
        if (!availableFiles.isEmpty()) {
            int cleanIndex = Math.max(0, Math.min(index, availableFiles.size() - 1));
            this.updateSelectedFile(availableFiles.get(cleanIndex), cleanIndex);
        }
    }

    public boolean hasChangedFiles() {
        return !this.getChangedFiles().isEmpty();
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObservableList<TaggableFile> getAvailableFiles() {
        return this.availableFiles;
    }

    public ObservableList<TaggableFile> getSelectedFiles() {
        return this.selectedFiles;
    }

    public ObjectProperty<File> getSelectedDirectory() {
        return this.selectedDirectory;
    }

    public IntegerProperty getSelectedIndex() {
        return this.selectedIndex;
    }

    public ObjectProperty<TaggableFile> getSelectedFile() {
        return this.selectedFile;
    }

    public ObservableList<TaggableFile> getChangedFiles() {
        return this.changedFiles;
    }

}