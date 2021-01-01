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

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.tagtiger3.fx.components.directories.DirectoryPane;
import de.perdian.apps.tagtiger3.fx.components.editor.EditorPane;
import de.perdian.apps.tagtiger3.fx.components.selection.SelectionPane;
import de.perdian.apps.tagtiger3.fx.components.status.StatusPane;
import de.perdian.apps.tagtiger3.fx.jobs.JobExecutor;
import de.perdian.apps.tagtiger3.fx.model.Selection;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class TagTigerPane extends BorderPane {

    public TagTigerPane(Preferences preferences, JobExecutor jobExecutor) {

        Selection selection = new Selection(jobExecutor);

        DirectoryPane directoryPane = new DirectoryPane();
        directoryPane.setMinWidth(200);
        directoryPane.setPrefWidth(300);
        Bindings.bindBidirectional(selection.selectedDirectoryProperty(), directoryPane.selectedDirectoryProperty());

        GridPane.setVgrow(directoryPane, Priority.ALWAYS);

        SelectionPane selectionPane = new SelectionPane(selection);
        GridPane.setHgrow(selectionPane, Priority.ALWAYS);
        GridPane.setVgrow(selectionPane, Priority.ALWAYS);

        EditorPane editorPane = new EditorPane(selection);
        GridPane.setHgrow(editorPane, Priority.ALWAYS);

        StatusPane statusPane = new StatusPane(jobExecutor);
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

}
