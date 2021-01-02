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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger3.fx.jobs.JobExecutor;
import de.perdian.apps.tagtiger3.fx.jobs.listeners.DisableWhileJobRunningJobListener;
import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Selection {

    private static final Logger log = LoggerFactory.getLogger(Selection.class);

    private final ObjectProperty<File> selectedDirectory = new SimpleObjectProperty<>();
    private final ObservableList<SongFile> availableFiles = FXCollections.observableArrayList();
    private final ObservableList<SongFile> selectedFiles = FXCollections.observableArrayList();
    private final ObjectProperty<SongFile> focusFile = new SimpleObjectProperty<>();
    private final BooleanProperty busy = new SimpleBooleanProperty();
    private SelectionDirtyComputer dirtyComputer = null;
    private JobExecutor jobExecutor = null;

    public Selection(JobExecutor jobExecutor) {
        this.focusFileProperty().addListener((o, oldValue, newValue) -> this.handleFocusFileChanged(oldValue, newValue));
        this.selectedDirectoryProperty().addListener((o, oldValue, newValue) -> this.handleSelectedDirectoryChanged(oldValue, newValue));
        this.setDirtyComputer(new SelectionDirtyComputer(this.getAvailableFiles()));
        this.setJobExecutor(jobExecutor);
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(this.busy));
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

    public ObjectProperty<File> selectedDirectoryProperty() {
        return this.selectedDirectory;
    }

    public ObservableList<SongFile> getAvailableFiles() {
        return this.availableFiles;
    }

    public ObservableList<SongFile> getSelectedFiles() {
        return this.selectedFiles;
    }

    public ObjectProperty<SongFile> focusFileProperty() {
        return this.focusFile;
    }

    public ReadOnlyBooleanProperty dirtyProperty() {
        return this.getDirtyComputer().getDirty();
    }

    public ReadOnlyBooleanProperty busyProperty() {
        return this.busy;
    }

    private SelectionDirtyComputer getDirtyComputer() {
        return this.dirtyComputer;
    }
    private void setDirtyComputer(SelectionDirtyComputer dirtyComputer) {
        this.dirtyComputer = dirtyComputer;
    }

    private JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    private void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

}
