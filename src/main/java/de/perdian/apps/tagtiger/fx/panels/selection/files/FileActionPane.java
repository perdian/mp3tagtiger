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
package de.perdian.apps.tagtiger.fx.panels.selection.files;

import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.business.impl.jobs.SaveChangedFilesInSelectionJob;

class FileActionPane extends HBox {

    FileActionPane(TagTiger tagTiger) {

        Button saveChangedFilesButton = new Button(tagTiger.getLocalization().saveChangedFiles(), new ImageView(new Image(FileActionPane.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        tagTiger.getSelection().getChangedFiles().addListener((Change<? extends TaggableFile> change) -> {
            Platform.runLater(() -> saveChangedFilesButton.setDisable(change.getList().isEmpty()));
        });
        saveChangedFilesButton.setOnAction(event -> tagTiger.getJobExecutor().executeJob(new SaveChangedFilesInSelectionJob(tagTiger.getSelection().getAvailableFiles(), tagTiger.getSelection().getChangedFiles(), tagTiger.getLocalization())));
        saveChangedFilesButton.setDisable(true);
        saveChangedFilesButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(saveChangedFilesButton, Priority.ALWAYS);

        this.getChildren().add(saveChangedFilesButton);

    }

}