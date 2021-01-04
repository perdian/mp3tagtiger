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
package de.perdian.apps.tagtiger3.fx.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger3.fx.jobs.JobExecutor;
import de.perdian.apps.tagtiger3.fx.jobs.listeners.DisableWhileJobRunningJobListener;
import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public class Selection {

    private static final Logger log = LoggerFactory.getLogger(Selection.class);

    private final ObjectProperty<File> selectedDirectory = new SimpleObjectProperty<>();
    private final ObservableList<SongFile> availableFiles = FXCollections.observableArrayList();
    private final ObservableSet<SongFile> dirtyFiles = FXCollections.observableSet(Collections.newSetFromMap(new IdentityHashMap<>()));
    private final ObservableList<SongFile> selectedFiles = FXCollections.observableArrayList();
    private final ObjectProperty<SongFile> focusFile = new SimpleObjectProperty<>();
    private final BooleanProperty busy = new SimpleBooleanProperty();
    private final BooleanProperty dirty = new SimpleBooleanProperty();
    private JobExecutor jobExecutor = null;
    private List<ChangeListener<?>> changeListeners = null;

    public Selection(JobExecutor jobExecutor) {
        this.focusFileProperty().addListener((o, oldValue, newValue) -> this.handleFocusFileChanged(oldValue, newValue));
        this.selectedDirectoryProperty().addListener((o, oldValue, newValue) -> this.handleSelectedDirectoryChanged(oldValue, newValue));
        this.dirtyProperty().bind(Bindings.isNotEmpty(this.getDirtyFiles()));
        this.setJobExecutor(jobExecutor);
        this.setChangeListeners(new ArrayList<>());
        this.getAvailableFiles().addListener((ListChangeListener.Change<? extends SongFile> change) -> this.handleAvailableFilesChanged(change.getList()));
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(this.busyProperty()));
    }

    private void handleFocusFileChanged(SongFile oldFocusFile, SongFile newFocusFile) {
        if(!Objects.equals(oldFocusFile, newFocusFile)) {
            if (oldFocusFile != null) {
                oldFocusFile.getFocus().setValue(false);
            }
            if (newFocusFile != null) {
                newFocusFile.getFocus().setValue(true);
            }
        }
    }

    private void handleAvailableFilesChanged(ObservableList<? extends SongFile> newSongFiles) {
        Set<SongFile> newDirtyFiles = new HashSet<>();
        List<ChangeListener<?>> newChangeListeners = new ArrayList<>();
        for (SongFile newFile : newSongFiles) {
            ChangeListener<Boolean> dirtyChangeListener = (o, oldValue, newValue) -> {
                if (this.getAvailableFiles().contains(newFile)) {
                    if (newValue.booleanValue()) {
                        this.getDirtyFiles().add(newFile);
                    } else {
                        this.getDirtyFiles().remove(newFile);
                    }
                }
            };
            newChangeListeners.add(dirtyChangeListener);
            newFile.getDirty().addListener(new WeakChangeListener<>(dirtyChangeListener));
            if (newFile.getDirty().getValue()) {
                newDirtyFiles.add(newFile);
            }
        }
        this.getDirtyFiles().clear();
        this.getDirtyFiles().addAll(newDirtyFiles);
        this.getChangeListeners().clear();
        this.getChangeListeners().addAll(newChangeListeners);

    }

    public void reloadAvailableSongs() {
        this.handleSelectedDirectoryChanged(null, this.selectedDirectoryProperty().getValue());
    }

    private void handleSelectedDirectoryChanged(File oldDirectory, File newDirectory) {
        if (!Objects.equals(oldDirectory, newDirectory)) {
            this.getJobExecutor().executeJob(jobContext -> {
                jobContext.updateProgress("Loading available songs in directory: " + newDirectory);
                try {

                    List<File> osFiles = Files.walk(newDirectory.toPath())
                        .parallel()
                        .map(Path::toFile)
                        .filter(file -> !file.isDirectory())
                        .filter(file -> !file.isHidden() && file.canRead())
                        .filter(file -> file.getName().toLowerCase().endsWith(".mp3"))
                        .sorted((f1, f2) -> f1.getAbsolutePath().compareToIgnoreCase(f2.getAbsolutePath()))
                        .collect(Collectors.toList());

                    List<SongFile> songFiles = new ArrayList<>(osFiles.size());
                    for (int i=0; i < osFiles.size() && !jobContext.isCancelled(); i++) {
                        jobContext.updateProgress("Loading file: " + osFiles.get(i).getName(), i, osFiles.size());
                        try {
                            songFiles.add(new SongFile(osFiles.get(i)));
                        } catch (Exception e) {
                            log.warn("Cannot load MP3 data from file at: {}", osFiles.get(i), e);
                        }
                    }
                    if (!jobContext.isCancelled()) {
                        Platform.runLater(() -> this.getAvailableFiles().setAll(songFiles));
                    }

                } catch (IOException e) {
                    log.warn("Cannot load files from directory: {}", newDirectory, e);
                }

            });
        }
    }

    public void saveDirtySongs() {
        if (!this.getDirtyFiles().isEmpty()) {
            this.getJobExecutor().executeJob(jobContext -> {

                jobContext.updateProgress("Computing list of changes files", -1, null);
                List<SongFile> dirtyFilesInOriginalOrder = this.getAvailableFiles().stream()
                    .filter(songFile -> this.getDirtyFiles().contains(songFile))
                    .collect(Collectors.toList());

                if (jobContext.isActive()) {
                    for (int i=0; i < dirtyFilesInOriginalOrder.size() && jobContext.isActive(); i++) {
                        SongFile songFile = dirtyFilesInOriginalOrder.get(i);
                        try {
                            jobContext.updateProgress("Saving file: " + songFile, i, dirtyFilesInOriginalOrder.size());
                            songFile.persistChanges();
                        } catch (Exception e) {
                            throw new RuntimeException("Cannot save changed file: " + songFile, e);
                        }
                    }
                }

            });
        }
    }

    public ObjectProperty<File> selectedDirectoryProperty() {
        return this.selectedDirectory;
    }

    public ObservableList<SongFile> getAvailableFiles() {
        return this.availableFiles;
    }

    private ObservableSet<SongFile> getDirtyFiles() {
        return this.dirtyFiles;
    }

    public ObservableList<SongFile> getSelectedFiles() {
        return this.selectedFiles;
    }

    public ObjectProperty<SongFile> focusFileProperty() {
        return this.focusFile;
    }

    public BooleanProperty dirtyProperty() {
        return this.dirty;
    }

    public BooleanProperty busyProperty() {
        return this.busy;
    }

    private JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    private void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    private List<ChangeListener<?>> getChangeListeners() {
        return this.changeListeners;
    }
    private void setChangeListeners(List<ChangeListener<?>> changeListeners) {
        this.changeListeners = changeListeners;
    }

}
