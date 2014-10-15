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
package de.perdian.apps.tagtiger.fx.components.files;

import java.util.Optional;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

/**
 * Displays a list of selected files that are in use by the user
 *
 * @author Christian Robert
 */

public class FileSelectionPane extends VBox {

    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<TaggableFile> selectedFile = new SimpleObjectProperty<>();
    private final ObjectProperty<EventHandler<ActionEvent>> onSaveActionProperty = new SimpleObjectProperty<>();
    private final BooleanProperty saveEnabledProperty = new SimpleBooleanProperty();

    public FileSelectionPane(Localization localization) {

        FileSelectionTableView filesTable = new FileSelectionTableView(localization);
        filesTable.itemsProperty().bind(this.availableFilesProperty());
        filesTable.getSelectionModel().getSelectedItems().addListener((Change<? extends TaggableFile> change) -> this.selectedFilesProperty().setAll(change.getList()));
        filesTable.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> this.selectedFileProperty().set(newValue));
        VBox.setVgrow(filesTable, Priority.ALWAYS);

        FileSelectionActionPane actionPane = new FileSelectionActionPane(localization, this::handleOnSaveActionEvent);
        actionPane.setPadding(new Insets(5, 5, 5, 5));
        actionPane.disableProperty().bind(this.saveEnabledProperty().not());

        this.getChildren().addAll(filesTable, actionPane);

        this.selectedFileProperty().addListener((o, oldValue, newValue) -> {
            if (!this.selectedFilesProperty().contains(newValue)) {
                if (newValue == null) {
                    filesTable.getSelectionModel().clearSelection();
                } else {
                    filesTable.getSelectionModel().clearAndSelect(this.availableFilesProperty().indexOf(newValue));
                }
            }
        });

    }

    private void handleOnSaveActionEvent(ActionEvent event) {
        Optional.ofNullable(this.onSaveActionProperty.get()).ifPresent(handler -> handler.handle(event));
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ListProperty<TaggableFile> availableFilesProperty() {
        return this.availableFiles;
    }

    public ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

    public ObjectProperty<TaggableFile> selectedFileProperty() {
        return this.selectedFile;
    }

    public BooleanProperty saveEnabledProperty() {
        return this.saveEnabledProperty;
    }

    public EventHandler<ActionEvent> getOnSaveAction() {
        return this.onSaveActionProperty.get();
    }
    public void setOnSaveAction(EventHandler<ActionEvent> eventHandler) {
        this.onSaveActionProperty.set(eventHandler);
    }

}