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
package de.perdian.apps.tagtiger.fx.panels.selection;

import java.io.File;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.components.directories.DirectorySelectionPane;
import de.perdian.apps.tagtiger.fx.components.files.FileSelectionPane;

public class SelectionPane extends BorderPane {

    static final Logger log = LoggerFactory.getLogger(SelectionPane.class);

    private final ObjectProperty<EventHandler<ActionEvent>> onSaveAction = new SimpleObjectProperty<>();
    private FileSelectionPane selectionPane = null;

    public SelectionPane(Selection selection, Localization localization) {

        TextField directoryField = new TextField();
        HBox.setHgrow(directoryField, Priority.ALWAYS);
        HBox directoryFieldWrapper = new HBox(directoryField);
        directoryFieldWrapper.setPadding(new Insets(0, 0, 5, 0));

        DirectorySelectionPane directorySelectionPane = new DirectorySelectionPane(localization);
        directorySelectionPane.selectedDirectoryProperty().bindBidirectional(selection.currentDirectoryProperty());

        FileSelectionPane fileSelectionPane = new FileSelectionPane(localization);
        fileSelectionPane.availableFilesProperty().bindBidirectional(selection.availableFilesProperty());
        fileSelectionPane.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> selection.selectedFilesProperty().setAll(change.getList()));
        fileSelectionPane.selectedFileProperty().addListener((o, oldValue, newValue) -> selection.currentFileProperty().set(newValue));
        fileSelectionPane.setOnSaveAction(event -> Optional.ofNullable(this.onSaveActionProperty().get()).ifPresent(handler -> handler.handle(event)));
        this.setSelectionPane(fileSelectionPane);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(directorySelectionPane);
        splitPane.getItems().add(fileSelectionPane);
        splitPane.setDividerPositions(0.4d);

        this.setTop(directoryFieldWrapper);
        this.setCenter(splitPane);
        this.setPadding(new Insets(5, 5, 5, 5));

        selection.changedFilesProperty().addListener((o, oldValue, newValue) -> fileSelectionPane.saveEnabledProperty().set(newValue != null && !newValue.isEmpty()));
        selection.currentFileProperty().addListener((o, oldValue, newValue) -> fileSelectionPane.selectedFileProperty().set(newValue));

        // Add listeners to connect the GUI components with the underlying
        // data structures
        directoryField.setOnAction(event -> {
            String directoryValue = ((TextField)event.getSource()).getText();
            directorySelectionPane.selectedDirectoryProperty().set(new File(directoryValue));
        });
        directoryField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> directoryField.selectAll());
            }
        });
        directorySelectionPane.selectedDirectoryProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                directoryField.setText(newValue == null ? "" : newValue.getAbsolutePath());
                if (directoryField.isFocused()) {
                    directoryField.selectAll();
                }
            });
        });

    }

    // -------------------------------------------------------------------------
    // --- Virtual property access methods -------------------------------------
    // -------------------------------------------------------------------------

    public void setListDisable(boolean disable) {
        this.getSelectionPane().setDisable(disable);
    }
    public boolean isListDisable() {
        return this.getSelectionPane().isDisable();
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObjectProperty<EventHandler<ActionEvent>> onSaveActionProperty() {
        return this.onSaveAction;
    }
    public EventHandler<ActionEvent> getOnSaveAction() {
        return this.onSaveAction.get();
    }
    public void setOnSaveAction(EventHandler<ActionEvent> eventHandler) {
        this.onSaveAction.set(eventHandler);
    }

    private FileSelectionPane getSelectionPane() {
        return this.selectionPane;
    }
    private void setSelectionPane(FileSelectionPane selectionPane) {
        this.selectionPane = selectionPane;
    }

}