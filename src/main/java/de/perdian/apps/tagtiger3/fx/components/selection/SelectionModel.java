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
package de.perdian.apps.tagtiger3.fx.components.selection;

import java.util.Objects;

import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

public class SelectionModel {

    private ObservableList<SongFile> availableFiles = null;
    private ObservableList<SongFile> selectedFiles = null;
    private ObjectProperty<SongFile> focusFile = new SimpleObjectProperty<>();

    public SelectionModel(ObservableList<SongFile> availableFiles, ObservableList<SongFile> selectedFiles) {
        this.setAvailableFiles(availableFiles);
        this.setSelectedFiles(selectedFiles);

        this.focusFileProperty().addListener((o, oldValue, newValue) -> {
            if(!Objects.equals(oldValue, newValue)) {
                if (oldValue != null) {
                    oldValue.getMarker().setValue(false);
                }
                if (newValue != null) {
                    newValue.getMarker().setValue(true);
                }
            }
        });
    }

    public ObservableList<SongFile> getAvailableFiles() {
        return this.availableFiles;
    }
    private void setAvailableFiles(ObservableList<SongFile> availableFiles) {
        this.availableFiles = availableFiles;
    }

    public ObservableList<SongFile> getSelectedFiles() {
        return this.selectedFiles;
    }
    private void setSelectedFiles(ObservableList<SongFile> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    public ObjectProperty<SongFile> focusFileProperty() {
        return this.focusFile;
    }

}
