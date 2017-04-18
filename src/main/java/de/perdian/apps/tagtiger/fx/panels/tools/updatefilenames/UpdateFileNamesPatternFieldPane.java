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
package de.perdian.apps.tagtiger.fx.panels.tools.updatefilenames;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.StrSubstitutor;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

class UpdateFileNamesPatternFieldPane extends BorderPane {

    UpdateFileNamesPatternFieldPane(List<UpdateFileNamesItem> items, Localization localization) {

        StringProperty patternFieldProperty = new SimpleStringProperty();
        patternFieldProperty.addListener((o, oldValue, newValue) -> this.computeNewFileNames(items, newValue));

        List<String> patternItems = Arrays.asList("${track} ${title}");
        ComboBox<String> patternBox = new ComboBox<>(FXCollections.observableArrayList(patternItems));
        patternBox.setEditable(true);
        patternBox.setMaxWidth(Double.MAX_VALUE);
        Bindings.bindBidirectional(patternFieldProperty, patternBox.editorProperty().get().textProperty());
        HBox.setHgrow(patternBox, Priority.ALWAYS);

        Button executeButton = new Button(localization.executeRename(), new ImageView(new Image(UpdateFileNamesPane.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        executeButton.setDisable(true);
        patternFieldProperty.addListener((o, oldValue, newValue) -> executeButton.setDisable(newValue.length() <= 0));

        HBox patternFieldPane = new HBox(10, patternBox, executeButton);
        patternFieldPane.setPadding(new Insets(5, 5, 5, 5));

        TitledPane patternFieldTitlePane = new TitledPane(localization.fileNamePattern(), patternFieldPane);
        patternFieldTitlePane.setCollapsible(false);
        this.setCenter(patternFieldTitlePane);

        executeButton.setOnAction(event -> {
            this.updateNewFileNames(items);
            ((Stage)executeButton.getScene().getWindow()).close();
        });

    }

    private void updateNewFileNames(List<UpdateFileNamesItem> items) {
        for (UpdateFileNamesItem item : items) {
            TaggableFile itemFile = item.getFile();
            if (!Objects.equals(itemFile.fileNameProperty().getValue(), item.getNewFileName().getValue())) {
                itemFile.fileNameProperty().setValue(item.getNewFileName().getValue());
            }
        }
    }

    private void computeNewFileNames(List<UpdateFileNamesItem> items, String fileNamePattern) {
        for (UpdateFileNamesItem item : items) {

            Map<String, String> replacementValues = Arrays
                .stream(UpdateFileNamesPlaceholder.values())
                .collect(Collectors.toMap(p -> p.getPlaceholder(), p -> p.resolveValue(item.getFile())));

            StrSubstitutor substitutor = new StrSubstitutor(replacementValues);
            String substitutionEvaluationResult = substitutor.replace(fileNamePattern);
            String substitutionSanitizedResult = this.sanitizeFileName(substitutionEvaluationResult).trim();

            if (!Objects.equals(substitutionSanitizedResult, item.getNewFileName().getValue())) {
                item.getNewFileName().setValue(substitutionSanitizedResult);
            }

        }
    }

    private String sanitizeFileName(String fileName) {
        StringBuilder result = new StringBuilder(fileName.length());
        boolean lastCharacterWasIllegal = false;
        String invalidCharacters = ":/\\";
        for (char c : fileName.toCharArray()) {
            if (invalidCharacters.indexOf(c) > -1) {
                if(!lastCharacterWasIllegal) {
                    lastCharacterWasIllegal = true;
                    result.append("_");
                }
            } else {
                lastCharacterWasIllegal = false;
                result.append(c);
            }
        }
        return result.toString();
    }

}
