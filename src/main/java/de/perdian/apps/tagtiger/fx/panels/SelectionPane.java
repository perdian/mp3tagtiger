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
package de.perdian.apps.tagtiger.fx.panels;

import java.io.File;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.framework.TagTiger;


class SelectionPane extends BorderPane {

    static final Logger log = LoggerFactory.getLogger(SelectionPane.class);

    SelectionPane(TagTiger tagTiger) {

        TextField directoryField = new TextField();
        HBox.setHgrow(directoryField, Priority.ALWAYS);
        HBox directoryFieldWrapper = new HBox(directoryField);
        directoryFieldWrapper.setPadding(new Insets(0, 0, 5, 0));
        tagTiger.getSelection().getSelectedDirectory().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                directoryField.setText(newValue == null ? "" : newValue.getAbsolutePath());
                if (directoryField.isFocused()) {
                    directoryField.selectAll();
                }
            });
        });

        DirectoryTreePane directoryTreePane = new DirectoryTreePane(tagTiger);
        directoryField.setOnAction(event -> {
            String directoryValue = ((TextField)event.getSource()).getText();
            tagTiger.getSelection().updateDirectory(new File(directoryValue));
        });
        directoryField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> directoryField.selectAll());
            }
        });

        FileListPane fileListPane = new FileListPane(tagTiger);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(directoryTreePane);
        splitPane.getItems().add(fileListPane);

        this.setTop(directoryFieldWrapper);
        this.setCenter(splitPane);
        this.setPadding(new Insets(5, 5, 5, 5));

    }

}