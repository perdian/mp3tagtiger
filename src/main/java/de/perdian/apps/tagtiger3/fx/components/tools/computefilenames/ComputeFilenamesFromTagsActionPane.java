/*
 * Copyright 2014-2021 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.tools.computefilenames;

import java.util.Optional;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.tagtiger3.tools.FilenameComputer;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

class ComputeFilenamesFromTagsActionPane extends TitledPane {

    ComputeFilenamesFromTagsActionPane(ObservableList<ComputeFilenamesFromTagsItem> items) {

        TextField filenamePatternField = new TextField();
        filenamePatternField.setOnKeyPressed(event -> this.updateItems(filenamePatternField.textProperty().getValue(), items));
        GridPane.setHgrow(filenamePatternField, Priority.ALWAYS);
        GridPane.setFillHeight(filenamePatternField, Boolean.TRUE);
        Button executeButton = new Button("Update file names", new FontAwesomeIconView(FontAwesomeIcon.PLAY));
        executeButton.setOnAction(event -> this.updateFiles((Node)event.getSource(), items));
        executeButton.disableProperty().bind(filenamePatternField.textProperty().isEmpty());
        GridPane.setFillHeight(executeButton, Boolean.TRUE);
        GridPane filenamePatternPane = new GridPane();
        filenamePatternPane.add(filenamePatternField, 0, 0, 1, 1);
        filenamePatternPane.add(executeButton, 1, 0, 1, 1);
        filenamePatternPane.setHgap(5);
        filenamePatternPane.setPadding(new Insets(10, 10, 10, 10));

        this.setText("File name pattern");
        this.setContent(filenamePatternPane);
        this.setCollapsible(false);

    }

    private void updateItems(String pattern, ObservableList<ComputeFilenamesFromTagsItem> items) {
        FilenameComputer filenameComputer = new FilenameComputer();
        for (ComputeFilenamesFromTagsItem item : items) {
            Optional<String> newFilename = filenameComputer.computeFilename(pattern, item.getSongFile());
            if (newFilename.isPresent()) {
                item.getNewFilename().setValue(newFilename.get());
            }
        }
    }

    private void updateFiles(Node sourceNode, ObservableList<ComputeFilenamesFromTagsItem> items) {
        items.forEach(item -> item.updateToFile());
        ((Stage)sourceNode.getScene().getWindow()).close();
    }

}
