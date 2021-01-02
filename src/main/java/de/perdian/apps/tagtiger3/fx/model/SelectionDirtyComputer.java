/*
 * Copyright 2014-2021 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

class SelectionDirtyComputer  {

    private ReadOnlyBooleanProperty dirty = new SimpleBooleanProperty();
    private List<ChangeListener<?>> changeListeners = null;
    private ObservableSet<SongFile> allFiles = null;
    private ObservableSet<SongFile> dirtyFiles = null;

    SelectionDirtyComputer(ObservableList<SongFile> availableFiles) {
        this.setAllFiles(FXCollections.observableSet());
        ObservableSet<SongFile> dirtyFiles = FXCollections.observableSet();
        this.setDirtyFiles(dirtyFiles);
        BooleanProperty dirtyProperty = new SimpleBooleanProperty();
        dirtyProperty.bind(Bindings.isNotEmpty(dirtyFiles));
        this.setDirty(dirtyProperty);
        availableFiles.addListener(new InternalListChangeListener());
        this.setChangeListeners(new ArrayList<>());
    }

    private class InternalListChangeListener implements ListChangeListener<SongFile> {

        @Override
        public void onChanged(Change<? extends SongFile> change) {
            Set<SongFile> newAllFiles = new HashSet<>();
            Set<SongFile> newDirtyFiles = new HashSet<>();
            List<ChangeListener<?>> newChangeListeners = new ArrayList<>();
            for (SongFile newFile : change.getList()) {
                ChangeListener<Boolean> dirtyChangeListener = (o, oldValue, newValue) -> {
                    if (SelectionDirtyComputer.this.getAllFiles().contains(newFile)) {
                        if (newValue.booleanValue()) {
                            SelectionDirtyComputer.this.getDirtyFiles().add(newFile);
                        } else {
                            SelectionDirtyComputer.this.getDirtyFiles().remove(newFile);
                        }
                    }
                };
                newChangeListeners.add(dirtyChangeListener);
                newFile.getProperties().getDirty().addListener(new WeakChangeListener<>(dirtyChangeListener));
                if (newFile.getProperties().getDirty().getValue()) {
                    newDirtyFiles.add(newFile);
                }
                newAllFiles.add(newFile);
            }
            SelectionDirtyComputer.this.getAllFiles().clear();
            SelectionDirtyComputer.this.getAllFiles().addAll(newAllFiles);
            SelectionDirtyComputer.this.getDirtyFiles().clear();
            SelectionDirtyComputer.this.getDirtyFiles().addAll(newDirtyFiles);
            SelectionDirtyComputer.this.getChangeListeners().clear();
            SelectionDirtyComputer.this.getChangeListeners().addAll(newChangeListeners);
        }

    }

    ReadOnlyBooleanProperty getDirty() {
        return this.dirty;
    }
    private void setDirty(ReadOnlyBooleanProperty dirty) {
        this.dirty = dirty;
    }

    private ObservableSet<SongFile> getAllFiles() {
        return this.allFiles;
    }
    private void setAllFiles(ObservableSet<SongFile> allFiles) {
        this.allFiles = allFiles;
    }

    private ObservableSet<SongFile> getDirtyFiles() {
        return this.dirtyFiles;
    }
    private void setDirtyFiles(ObservableSet<SongFile> dirtyFiles) {
        this.dirtyFiles = dirtyFiles;
    }

    private List<ChangeListener<?>> getChangeListeners() {
        return this.changeListeners;
    }
    private void setChangeListeners(List<ChangeListener<?>> changeListeners) {
        this.changeListeners = changeListeners;
    }

}
