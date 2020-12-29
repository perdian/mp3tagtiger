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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class TagTigerPane extends BorderPane {

    private static final Logger log = LoggerFactory.getLogger(TagTigerPane.class);

    public TagTigerPane(Preferences preferences, JobExecutor jobExecutor) {

        ObservableList<SongFile> availableSongs = FXCollections.observableArrayList();

        DirectoryPane directoryPane = new DirectoryPane(preferences);
        directoryPane.setMinWidth(200);
        directoryPane.setPrefWidth(300);
        directoryPane.selectedDirectoryProperty().addListener((o, oldValue, newValue) -> jobExecutor.executeJob(jobContext -> this.loadSongsFromDirectory(newValue, availableSongs, jobContext)));
        GridPane.setVgrow(directoryPane, Priority.ALWAYS);

        SelectionPane selectionPane = new SelectionPane(availableSongs, preferences, jobExecutor);
        GridPane.setHgrow(selectionPane, Priority.ALWAYS);
        GridPane.setVgrow(selectionPane, Priority.ALWAYS);

        EditorPane editorPane = new EditorPane(selectionPane.getSelectionModel());
        GridPane.setHgrow(editorPane, Priority.ALWAYS);

        StatusPane statusPane = new StatusPane(preferences, jobExecutor);
        GridPane.setHgrow(statusPane, Priority.ALWAYS);

        GridPane innerPane = new GridPane();
        innerPane.add(directoryPane, 0, 0, 1, 2);
        innerPane.add(selectionPane, 1, 0, 1, 1);
        innerPane.add(editorPane, 1, 1, 1, 1);
        innerPane.setPadding(new Insets(10, 10, 10, 10));
        innerPane.setHgap(10);
        innerPane.setVgap(10);
        innerPane.getColumnConstraints().add(new ColumnConstraints(300));
        innerPane.getColumnConstraints().add(new ColumnConstraints());

        this.setCenter(innerPane);
        this.setBottom(statusPane);

        StringProperty selectedPathProperty = preferences.getStringProperty("TagTigerApplication.selectedPath");
        File selectedPath = StringUtils.isEmpty(selectedPathProperty.getValue()) ? null : new File(selectedPathProperty.getValue());
        if (selectedPath != null && selectedPath.exists()) {
            directoryPane.setSelectedDirectory(selectedPath);
        }
        directoryPane.selectedDirectoryProperty().addListener((o, oldValue, newValue) -> selectedPathProperty.setValue(newValue == null ? null : newValue.toString()));

    }

    private void loadSongsFromDirectory(File sourceDirectory, ObservableList<SongFile> targetList, JobContext jobContext) {
        jobContext.updateProgress("Loading available songs in directory: " + sourceDirectory);
        try {
            List<File> files = this.loadSongs(sourceDirectory);
            List<SongFile> songFiles = new ArrayList<>(files.size());
            for (int i=0; i < files.size() && !jobContext.isCancelled(); i++) {
                jobContext.updateProgress("Loading file: " + files.get(i).getName(), i, files.size());
                try {
                    songFiles.add(new SongFile(files.get(i)));
                } catch (Exception e) {
                    log.warn("Cannot load MP3 data from file at: {}", files.get(i), e);
                }
            }
            if (!jobContext.isCancelled()) {
                Platform.runLater(() -> targetList.setAll(songFiles));
            }
        } catch (IOException e) {
            log.warn("Cannot load files from directory: {}", sourceDirectory, e);
        }
    }

    private List<File> loadSongs(File sourceDirectory) throws IOException {
        return Files.walk(sourceDirectory.toPath())
            .parallel()
            .map(Path::toFile)
            .filter(file -> !file.isDirectory())
            .filter(file -> !file.isHidden() && file.canRead())
            .filter(file -> file.getName().toLowerCase().endsWith(".mp3"))
            .sorted((f1, f2) -> f1.getAbsolutePath().compareToIgnoreCase(f2.getAbsolutePath()))
            .collect(Collectors.toList());
    }

}
