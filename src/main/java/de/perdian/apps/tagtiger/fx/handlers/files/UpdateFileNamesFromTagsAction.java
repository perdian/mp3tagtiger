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
package de.perdian.apps.tagtiger.fx.handlers.files;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.StrSubstitutor;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Updates a series of file names from information presented in the tags
 *
 * @author Christian Robert
 */

public class UpdateFileNamesFromTagsAction extends AbstractDialogAction {

    public UpdateFileNamesFromTagsAction(Localization localization) {
        super(localization);
    }

    @Override
    @SuppressWarnings("unused")
    protected BatchUpdateDialog createDialog(Property<TaggableFile> sourceFileProperty, ObservableList<TaggableFile> targetFiles) {

        ObservableList<UpdateFileNamesFromTagsItem> items = FXCollections.observableArrayList(targetFiles.stream().map(UpdateFileNamesFromTagsItem::new).collect(Collectors.toList()));
        StringProperty patternFieldProperty = new SimpleStringProperty();

        List<String> patternItems = Arrays.asList("${track} ${title}");
        ComboBox<String> patternBox = new ComboBox<>(FXCollections.observableArrayList(patternItems));
        patternBox.setEditable(true);
        patternBox.setMaxWidth(Double.MAX_VALUE);
        Bindings.bindBidirectional(patternFieldProperty, patternBox.editorProperty().get().textProperty());
        HBox.setHgrow(patternBox, Priority.ALWAYS);

        Button executeButton = new Button(this.getLocalization().executeRename(), new ImageView(new Image(UpdateFileNamesFromTagsAction.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        executeButton.setDisable(true);
        patternFieldProperty.addListener((o, oldValue, newValue) -> executeButton.setDisable(newValue.length() <= 0));

        HBox patternFieldPane = new HBox(10, patternBox, executeButton);
        patternFieldPane.setPadding(new Insets(5, 5, 5, 5));

        TitledPane patternFieldTitlePane = new TitledPane(this.getLocalization().fileNamePattern(), patternFieldPane);
        patternFieldTitlePane.setCollapsible(false);

        TableView<?> newFileNamesPane = this.createNewFileNamesPane(items);
        newFileNamesPane.setPrefHeight(400);
        VBox.setVgrow(newFileNamesPane, Priority.ALWAYS);

        VBox actionPane = new VBox(10, patternFieldTitlePane, newFileNamesPane);

        // Create the dialog and finish
        BatchUpdateDialog dialog = new BatchUpdateDialog();
        dialog.setDialogPrefWidth(800);
        dialog.setDialogTitle(this.getLocalization().updateFileNames());
        dialog.setActionPane(actionPane);
        dialog.setLegendPane(this.createLegendPane());

        // Add listeners
        targetFiles.addListener(new WeakListChangeListener<TaggableFile>((Change<? extends TaggableFile> change) -> items.setAll(change.getList().stream().map(UpdateFileNamesFromTagsItem::new).collect(Collectors.toList()))));
        patternFieldProperty.addListener((o, oldValue, newValue) -> this.computeNewFileNames(items, newValue));
        executeButton.setOnAction(event -> {
            this.updateNewFileNames(items);
            ((Stage)executeButton.getScene().getWindow()).close();
        });

        return dialog;

    }

    private Parent createLegendPane() {

        GridPane legendPane = new GridPane();
        legendPane.setPadding(new Insets(5, 5, 5, 5));
        int columnCount = 3;
        int currentRow = 0;
        int currentColumn = 0;
        for (UpdateFileNamesPlaceholder placeholder : UpdateFileNamesPlaceholder.values()) {

            StringBuilder placeholderText = new StringBuilder();
            placeholderText.append("${").append(placeholder.getPlaceholder()).append("}: ");
            placeholderText.append(placeholder.resolveLocalization(this.getLocalization()));

            Label placeholderLabel = new Label(placeholderText.toString());
            placeholderLabel.setMaxWidth(Double.MAX_VALUE);
            placeholderLabel.setPadding(new Insets(3, 3, 3, 3));
            placeholderLabel.setAlignment(Pos.TOP_LEFT);
            legendPane.add(placeholderLabel, currentColumn, currentRow);
            GridPane.setFillWidth(placeholderLabel, Boolean.TRUE);
            GridPane.setHgrow(placeholderLabel, Priority.ALWAYS);

            currentColumn++;
            if (currentColumn >= columnCount) {
                currentRow++;
                currentColumn = 0;
            }

        }

        TitledPane legendTitlePane = new TitledPane(this.getLocalization().legend(), legendPane);
        legendTitlePane.setCollapsible(false);
        return legendTitlePane;

    }

    private TableView<?> createNewFileNamesPane(ObservableList<UpdateFileNamesFromTagsItem> items) {

        TableColumn<UpdateFileNamesFromTagsItem, String> currentFileNameColumn = new TableColumn<>(this.getLocalization().currentFileName());
        currentFileNameColumn.setSortable(false);
        currentFileNameColumn.setCellValueFactory(p -> p.getValue().getCurrentFileName());
        TableColumn<UpdateFileNamesFromTagsItem, String> newFileNameColumn = new TableColumn<>(this.getLocalization().newFileName());
        newFileNameColumn.setSortable(false);
        newFileNameColumn.setCellValueFactory(p -> p.getValue().getNewFileName());

        TableView<UpdateFileNamesFromTagsItem> tableView = new TableView<>(items);
        tableView.getColumns().addAll(Arrays.asList(currentFileNameColumn, newFileNameColumn));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tableView;

    }

    private void computeNewFileNames(List<UpdateFileNamesFromTagsItem> items, String fileNamePattern) {
        for (UpdateFileNamesFromTagsItem item : items) {

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

    private void updateNewFileNames(ObservableList<UpdateFileNamesFromTagsItem> items) {
        for (UpdateFileNamesFromTagsItem item : items) {
            TaggableFile itemFile = item.getFile();
            if (!Objects.equals(itemFile.fileNameProperty().getValue(), item.getNewFileName().getValue())) {
                itemFile.fileNameProperty().setValue(item.getNewFileName().getValue());
            }
        }
    }

}