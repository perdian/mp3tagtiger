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
package de.perdian.apps.tagtiger3.fx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger3.fx.components.directories.Directory;
import de.perdian.apps.tagtiger3.fx.components.directories.DirectoryPane;
import de.perdian.apps.tagtiger3.fx.components.editor.EditorPane;
import de.perdian.apps.tagtiger3.fx.components.selection.SelectionPane;
import de.perdian.apps.tagtiger3.fx.components.status.StatusPane;
import de.perdian.apps.tagtiger3.fx.jobs.JobContext;
import de.perdian.apps.tagtiger3.fx.jobs.JobExecutor;
import de.perdian.apps.tagtiger3.model.SongFile;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class TagTigerPane extends GridPane {

    private static final Logger log = LoggerFactory.getLogger(TagTigerPane.class);

    public TagTigerPane(Preferences preferences, JobExecutor jobExecutor) {

        ObservableList<SongFile> availableSongs = FXCollections.observableArrayList();

        DirectoryPane directoryPane = new DirectoryPane(preferences);
        directoryPane.getSelectedDirectory().addListener((o, oldValue, newValue) -> jobExecutor.executeJob(jobContext -> this.loadSongsFromDirectory(newValue, availableSongs, jobContext)));
        GridPane.setVgrow(directoryPane, Priority.ALWAYS);

        SelectionPane selectionPane = new SelectionPane(availableSongs, preferences, jobExecutor);
        GridPane.setHgrow(selectionPane, Priority.ALWAYS);
        GridPane.setVgrow(selectionPane, Priority.ALWAYS);

        EditorPane editorPane = new EditorPane();
        GridPane.setHgrow(editorPane, Priority.ALWAYS);

        StatusPane statusPane = new StatusPane(preferences, jobExecutor);
        GridPane.setHgrow(statusPane, Priority.ALWAYS);

        this.add(directoryPane, 0, 0, 1, 2);
        this.add(selectionPane, 1, 0, 1, 1);
        this.add(editorPane, 1, 1, 1, 1);
        this.add(statusPane, 0, 3, 2, 1);
        this.setHgap(10);
        this.setVgap(10);
        this.getColumnConstraints().add(new ColumnConstraints(300));
        this.getColumnConstraints().add(new ColumnConstraints());

        StringProperty selectedPathProperty = preferences.getStringProperty("TagTigerApplication.selectedPath");
        Path selectedPath = StringUtils.isEmpty(selectedPathProperty.getValue()) ? null : Path.of(selectedPathProperty.getValue());
        if (selectedPath != null && Files.exists(selectedPath)) {
            directoryPane.selectPath(selectedPath);
        }
        directoryPane.getSelectedDirectory().addListener((o, oldValue, newValue) -> selectedPathProperty.setValue(newValue == null ? null : newValue.getPath().toString()));

    }

    private void loadSongsFromDirectory(Directory sourceDirectory, ObservableList<SongFile> targetList, JobContext jobContext) {
        jobContext.updateProgress("Loading available songs in directory: " + sourceDirectory.getPath());
        try {
            List<Path> files = sourceDirectory.loadFiles();
            List<SongFile> songFiles = new ArrayList<>(files.size());
            for (int i=0; i < files.size() && !jobContext.isCancelled(); i++) {
                jobContext.updateProgress("Loading file: " + files.get(i).getFileName().toString(), i, files.size());
                try {
                    songFiles.add(new SongFile(files.get(i).toFile()));
                } catch (Exception e) {
                    log.warn("Cannot load MP3 data from file at: {}", files.get(i));
                }
            }
            if (!jobContext.isCancelled()) {
                Platform.runLater(() -> targetList.setAll(songFiles));
            }
        } catch (IOException e) {
            log.warn("Cannot load files from directory: {}", sourceDirectory, e);
        }
    }

}
