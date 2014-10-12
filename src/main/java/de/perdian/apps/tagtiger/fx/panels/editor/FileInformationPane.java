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
package de.perdian.apps.tagtiger.fx.panels.editor;

import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.business.framework.tagging.FileWithTags;
import de.perdian.apps.tagtiger.fx.panels.selection.SelectionKeyEventHandler;

class FileInformationPane extends FileDataPanel {

    FileInformationPane(TagTiger tagTiger) {
        super(tagTiger);
    }

    @Override
    protected void initializePane(EditorPropertyFactory propertyFactory, TagTiger tagTiger) {

        TextField selectedIndexField = new TextField();
        selectedIndexField.setAlignment(Pos.CENTER);
        selectedIndexField.setEditable(true);
        selectedIndexField.setMaxWidth(40);
        tagTiger.getSelection().getSelectedIndex().addListener((o, oldValue, newValue) -> Platform.runLater(() -> selectedIndexField.setText(newValue.intValue() < 0 ? "" : String.valueOf(newValue.intValue() + 1))));

        Label selectedIndexSeparatorLabel = new Label("/");
        selectedIndexSeparatorLabel.setPadding(new Insets(0, 5, 0, 5));

        TextField availableFilesSizeField = new TextField();
        availableFilesSizeField.setAlignment(Pos.CENTER);
        availableFilesSizeField.setEditable(true);
        availableFilesSizeField.setMaxWidth(40);
        tagTiger.getSelection().getAvailableFiles().addListener((Change<? extends FileWithTags> change) -> Platform.runLater(() -> availableFilesSizeField.setText(String.valueOf(change.getList().size()))));

        Label availableFilesSeparatorLabel = new Label(":");
        availableFilesSeparatorLabel.setPadding(new Insets(0, 15, 0, 5));

        TextField fileNameField = propertyFactory.createTextField(FileWithTags::getFileName);
        fileNameField.setMaxWidth(Double.MAX_VALUE);
        fileNameField.setOnKeyPressed(new SelectionKeyEventHandler(tagTiger.getSelection()));
        GridPane.setHgrow(fileNameField, Priority.ALWAYS);

        this.add(selectedIndexField, 0, 0);
        this.add(selectedIndexSeparatorLabel, 1, 0);
        this.add(availableFilesSizeField, 2, 0);
        this.add(availableFilesSeparatorLabel, 3, 0);
        this.add(fileNameField, 4, 0);
        this.setDisable(true);

    }

}