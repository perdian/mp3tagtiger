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
import java.util.stream.Collectors;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UpdateFileNamesOpenDialogEventHandler implements EventHandler<ActionEvent> {

    private Selection selection = null;
    private Localization localization = null;

    public UpdateFileNamesOpenDialogEventHandler(Selection selection, Localization localization) {
        this.setSelection(selection);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {

        List<UpdateFileNamesItem> fileNameItems = this.getSelection().selectedFilesProperty().stream()
            .map(UpdateFileNamesItem::new)
            .collect(Collectors.toList());

        Stage dialogStage = new Stage();
        dialogStage.setMinWidth(400);
        dialogStage.setMinHeight(100);
        dialogStage.setTitle(this.getLocalization().updateFileNames());
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(this.createDialogPane(fileNameItems)));
        dialogStage.show();

    }

    private Parent createDialogPane(List<UpdateFileNamesItem> items) {

        Parent topPane = this.createTopPane(items);
        BorderPane.setMargin(topPane, new Insets(5, 5, 5, 5));

        TableColumn<UpdateFileNamesItem, String> currentFileNameColumn = new TableColumn<>(this.getLocalization().currentFileName());
        currentFileNameColumn.setSortable(false);
        currentFileNameColumn.setCellValueFactory(p -> p.getValue().getCurrentFileName());
        TableColumn<UpdateFileNamesItem, String> newFileNameColumn = new TableColumn<>(this.getLocalization().newFileName());
        newFileNameColumn.setSortable(false);
        newFileNameColumn.setCellValueFactory(p -> p.getValue().getNewFileName());
        TableView<UpdateFileNamesItem> tableView = new TableView<>(FXCollections.observableArrayList(items));
        tableView.getColumns().addAll(Arrays.asList(currentFileNameColumn, newFileNameColumn));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        BorderPane.setMargin(tableView, new Insets(5, 5, 5, 5));

        Parent bottomPane = this.createBottomPane();
        BorderPane.setMargin(bottomPane, new Insets(5, 5, 5, 5));

        BorderPane dialogPane = new BorderPane();
        dialogPane.setPrefWidth(800);
        dialogPane.setTop(topPane);
        dialogPane.setCenter(tableView);
        dialogPane.setBottom(bottomPane);
        return dialogPane;

    }

    private Parent createTopPane(List<UpdateFileNamesItem> items) {

        StringProperty patternFieldProperty = new SimpleStringProperty();
        patternFieldProperty.addListener(new UpdateFileNamesComputeChangeListener(items));

        List<String> patternItems = Arrays.asList("${track} ${title}");
        ComboBox<String> patternBox = new ComboBox<>(FXCollections.observableArrayList(patternItems));
        patternBox.setEditable(true);
        patternBox.setMaxWidth(Double.MAX_VALUE);
        Bindings.bindBidirectional(patternFieldProperty, patternBox.editorProperty().get().textProperty());
        HBox.setHgrow(patternBox, Priority.ALWAYS);

        Button executeButton = new Button(this.getLocalization().executeRename(), new ImageView(new Image(UpdateFileNamesOpenDialogEventHandler.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        executeButton.setOnAction(new UpdateFileNamesExecuteEventHandler(items));
        executeButton.setDisable(true);
        patternFieldProperty.addListener((o, oldValue, newValue) -> executeButton.setDisable(newValue.length() <= 0));

        HBox patternFieldPane = new HBox(10, patternBox, executeButton);
        patternFieldPane.setPadding(new Insets(5, 5, 5, 5));

        TitledPane patternFieldTitlePane = new TitledPane(this.getLocalization().fileNamePattern(), patternFieldPane);
        patternFieldTitlePane.setCollapsible(false);

        BorderPane topPane = new BorderPane();
        topPane.setCenter(patternFieldTitlePane);
        return topPane;

    }

    private Parent createBottomPane() {

        GridPane legendPane = new GridPane();
        legendPane.setPadding(new Insets(5, 5, 5, 5));
        int columnCount = 3;
        int currentRow = 0;
        int currentColumn = 0;
        for (TaggablePropertyKey propertyKey : TaggablePropertyKey.values()) {

            StringBuilder placeholderText = new StringBuilder();
            placeholderText.append("${").append(propertyKey.getName()).append("}: ");
            placeholderText.append(propertyKey.getTitleKey().apply(this.getLocalization()));

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

        TitledPane titlePane = new TitledPane(this.getLocalization().legend(), legendPane);
        titlePane.setCollapsible(false);

        BorderPane bottomPane = new BorderPane();
        bottomPane.setCenter(titlePane);
        return bottomPane;

    }

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
