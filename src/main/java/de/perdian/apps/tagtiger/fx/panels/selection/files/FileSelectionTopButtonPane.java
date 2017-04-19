/*
 * Copyright 2014-2017 Christian Robert
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
package de.perdian.apps.tagtiger.fx.panels.selection.files;

import java.io.File;

import de.perdian.apps.tagtiger.core.jobs.JobExecutor;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.jobs.SaveChangedFilesJob;
import de.perdian.apps.tagtiger.fx.listeners.DisableWhileJobRunningJobListener;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.panels.selection.SelectionPane;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

class FileSelectionTopButtonPane extends BorderPane {

    FileSelectionTopButtonPane(FileSelectionTableView fileSelectionTableView, Selection selection, Localization localization, JobExecutor jobExecutor) {

        Button saveChangedFilesButton = new Button(localization.saveChangedFiles());
        saveChangedFilesButton.setGraphic(new ImageView(new Image(SelectionPane.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        saveChangedFilesButton.setDisable(selection.changedFilesProperty().isEmpty());
        saveChangedFilesButton.setOnAction(event -> jobExecutor.executeJob(new SaveChangedFilesJob(selection, localization)));
        selection.changedFilesProperty().addListener((o, oldValue, newValue) -> saveChangedFilesButton.setDisable(newValue == null || newValue.isEmpty()));

        Button reloadButton = new Button();
        reloadButton.setTooltip(new Tooltip(localization.reload()));
        reloadButton.setGraphic(new ImageView(new Image(SelectionPane.class.getClassLoader().getResourceAsStream("icons/16/refresh.png"))));
        reloadButton.setOnAction(event -> {
            File currentDirectoryProperty = selection.currentDirectoryProperty().getValue();
            selection.currentDirectoryProperty().setValue(null);
            selection.currentDirectoryProperty().setValue(currentDirectoryProperty);
        });
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(reloadButton.disableProperty()));

        Button selectAllButton = new Button();
        selectAllButton.setTooltip(new Tooltip(localization.selectAllFiles()));
        selectAllButton.setGraphic(new ImageView(new Image(SelectionPane.class.getClassLoader().getResourceAsStream("icons/16/select-all.png"))));
        selectAllButton.setOnAction(action -> fileSelectionTableView.getSelectionModel().selectAll());
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(selectAllButton.disableProperty()));
        HBox leftButtonBox = new HBox(5, reloadButton, selectAllButton);

        ListChangeListener<TaggableFile> saveChangedFileChaneListener = c -> saveChangedFilesButton.setDisable(c.getList().isEmpty());
        selection.changedFilesProperty().addListener(saveChangedFileChaneListener);

        this.setPadding(new Insets(5, 5, 5, 5));
        this.setLeft(leftButtonBox);
        this.setRight(saveChangedFilesButton);

    }

}
