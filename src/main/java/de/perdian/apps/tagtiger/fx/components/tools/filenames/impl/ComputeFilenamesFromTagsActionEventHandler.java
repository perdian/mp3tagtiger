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
package de.perdian.apps.tagtiger.fx.components.tools.filenames.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.perdian.apps.tagtiger.fx.components.tools.filenames.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.perdian.apps.tagtiger.model.SongAttribute;
import de.perdian.apps.tagtiger.model.SongFile;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

public class ComputeFilenamesFromTagsActionEventHandler extends FilenamesToolActionEventHandler {

    public ComputeFilenamesFromTagsActionEventHandler(ObservableList<SongFile> files) {
        super("Compute file names from tags", files);
    }

    @Override
    protected Pane createDialogPane(ObservableList<FilenamesToolItem> items) {

        Map<String, String> legendVariables = Arrays.stream(FilenamesToolAttribute.values())
            .filter(attribute -> attribute.getSongFileResolver() != null)
            .collect(Collectors.toMap(variable -> variable.name().toLowerCase(), variable -> variable.getAttribute().getTitle(), (e1, e2) -> e1, LinkedHashMap::new));

        FilenamesToolPatternPane patternPane = new FilenamesToolPatternPane();
        patternPane.patternProperty().addListener((o, oldValue, newValue) -> {
            try {
                this.updateToItems(newValue, items);
                patternPane.errorProperty().setValue(null);
            } catch (Exception e) {
                patternPane.errorProperty().setValue(e);
            }
        });
        patternPane.setOnActionEvent(event -> {
            this.updateToSongFiles(items);
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        });

        FilenamesToolLegendPane legendPane = new FilenamesToolLegendPane("Available filename variables", legendVariables, "${", "}");
        GridPane.setHgrow(legendPane, Priority.ALWAYS);
        ComputeFilenamesFromTagsTemplatePane templatePane = new ComputeFilenamesFromTagsTemplatePane(patternPane.patternProperty());
        GridPane topPane = new GridPane(10, 10);
        topPane.add(legendPane, 0, 0, 1, 1);
        topPane.add(templatePane, 1, 0, 1, 1);

        TableView<FilenamesToolItem> tableView = this.createTableView(items);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        VBox dialogPane = new VBox();
        dialogPane.getChildren().add(topPane);
        dialogPane.getChildren().add(patternPane);
        dialogPane.getChildren().add(tableView);
        dialogPane.setPadding(new Insets(10, 10, 10, 10));
        dialogPane.setSpacing(10);
        return dialogPane;

    }

    private TableView<FilenamesToolItem> createTableView(ObservableList<FilenamesToolItem> items) {

        TableColumn<FilenamesToolItem, Boolean> dirtyColumn = new TableColumn<>("");
        dirtyColumn.setCellValueFactory(callback -> callback.getValue().getDirty());
        dirtyColumn.setCellFactory(FilenamesToolHelpers.createIconCellCallback(FontAwesomeIcon.FLAG, null));
        dirtyColumn.setEditable(false);
        dirtyColumn.setSortable(false);
        dirtyColumn.setReorderable(false);
        dirtyColumn.setMinWidth(25);
        dirtyColumn.setMaxWidth(25);
        TableColumn<FilenamesToolItem, String> oldFilenameColumn = new TableColumn<>("Original file name");
        oldFilenameColumn.setEditable(false);
        oldFilenameColumn.setCellValueFactory(features -> features.getValue().originalValueProperty(SongAttribute.FILENAME));
        oldFilenameColumn.setSortable(false);
        oldFilenameColumn.setReorderable(false);
        TableColumn<FilenamesToolItem, String> newFilenameColumn = new TableColumn<>("New file name");
        newFilenameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        newFilenameColumn.setEditable(true);
        newFilenameColumn.setSortable(false);
        newFilenameColumn.setReorderable(false);
        newFilenameColumn.setCellValueFactory(features -> features.getValue().newValueProperty(SongAttribute.FILENAME));

        TableView<FilenamesToolItem> itemsTableView = new TableView<>(items);
        itemsTableView.setEditable(true);
        itemsTableView.getColumns().addAll(List.of(dirtyColumn, oldFilenameColumn, newFilenameColumn));
        itemsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return itemsTableView;

    }

    private void updateToSongFiles(ObservableList<FilenamesToolItem> items) {
        for (FilenamesToolItem item : items) {
            for (Map.Entry<SongAttribute, FilenamesToolItemValue> itemValue : item.getValues().entrySet()) {
                item.getSongFile().getAttributeValueProperty(itemValue.getKey(), String.class).setValue(itemValue.getValue().getNewValue().getValue());
            }
        }
    }

    private void updateToItems(String pattern, ObservableList<FilenamesToolItem> items) {
        for (FilenamesToolItem item : items) {
            Map<String, String> replacementValues = new HashMap<>();
            for (FilenamesToolAttribute attribute : FilenamesToolAttribute.values()) {
                if (attribute.getSongFileResolver() != null) {
                    replacementValues.put(attribute.name().toLowerCase(), attribute.getSongFileResolver().resolveAttributeValue(attribute.getAttribute(), item.getSongFile()).orElse(""));
                }
            }

            StringSubstitutor substitutor = new StringSubstitutor(replacementValues);
            String substitutionEvaluationResult = substitutor.replace(pattern);
            String substitutionSanitizedResult = this.sanitizeFileName(substitutionEvaluationResult);
            if (StringUtils.isNotEmpty(substitutionSanitizedResult)) {
                item.newValueProperty(SongAttribute.FILENAME).setValue(substitutionSanitizedResult);
            } else {
                item.newValueProperty(SongAttribute.FILENAME).setValue(item.originalValueProperty(SongAttribute.FILENAME).getValue());
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
        return result.toString().strip();
    }

}
