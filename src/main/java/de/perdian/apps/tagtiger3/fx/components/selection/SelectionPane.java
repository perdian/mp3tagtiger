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

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.tagtiger3.fx.components.selection.directories.DirectorySelectionPane;
import de.perdian.apps.tagtiger3.fx.components.selection.directories.DirectoryTreeView;
import de.perdian.apps.tagtiger3.fx.components.selection.songs.SongActionsPane;
import de.perdian.apps.tagtiger3.fx.components.selection.songs.SongTableView;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class SelectionPane extends GridPane {

    public SelectionPane(Preferences preferences) {

        DirectorySelectionPane directorySelectionPane = new DirectorySelectionPane(preferences);
        DirectoryTreeView directoryTreeView = new DirectoryTreeView();
        directoryTreeView.setMinWidth(200);
        directoryTreeView.setPrefWidth(300);
        directoryTreeView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> directorySelectionPane.getSelectedPathProperty().setValue(newValue == null ? null : newValue.getValue().getPath()));
        directorySelectionPane.getSelectedPathProperty().addListener((o, oldValue, newValue) -> directoryTreeView.selectDirectory(newValue));
        GridPane.setVgrow(directoryTreeView, Priority.ALWAYS);

        SongActionsPane songActionsPane = new SongActionsPane();
        GridPane.setHgrow(songActionsPane, Priority.ALWAYS);
        SongTableView songTableView = new SongTableView();
        songTableView.setMinWidth(300);
        songTableView.setPrefWidth(400);
        GridPane.setHgrow(songTableView, Priority.ALWAYS);
        GridPane.setVgrow(songTableView, Priority.ALWAYS);

        this.add(directorySelectionPane, 0, 0, 2, 1);
        this.add(directoryTreeView, 0, 1, 1, 2);
        this.add(songActionsPane, 1, 1, 1, 1);
        this.add(songTableView, 1, 2, 1, 1);
        this.setHgap(5);
        this.setVgap(5);
        this.setPadding(new Insets(5, 5, 5, 5));

        StringProperty selectedPathProperty = preferences.getStringProperty("SelectionPane.selectedPath");
        Path selectedPath = StringUtils.isEmpty(selectedPathProperty.getValue()) ? null : Path.of(selectedPathProperty.getValue());
        if (selectedPath != null && Files.exists(selectedPath)) {
            directoryTreeView.selectDirectory(selectedPath);
        }
        directoryTreeView.getSelectedDirectory().addListener((o, oldValue, newValue) -> selectedPathProperty.setValue(newValue == null ? null : newValue.getPath().toString()));

    }

}
