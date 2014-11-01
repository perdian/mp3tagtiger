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
package de.perdian.apps.tagtiger.fx;

import java.util.List;
import java.util.Optional;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.handlers.selection.ChangeCurrentFileDirection;
import de.perdian.apps.tagtiger.fx.handlers.selection.ChangeCurrentFileEventHandler;

class TagTigerToolBar extends ToolBar {

    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> changedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<EventHandler<ActionEvent>> onSaveAction = new SimpleObjectProperty<>();

    TagTigerToolBar(Localization localization) {

        Button firstButton = new Button("", new ImageView(new Image(TagTigerToolBar.class.getClassLoader().getResourceAsStream("icons/16/go-first.png"))));
        firstButton.setTooltip(new Tooltip(localization.firstFile()));
        firstButton.setDisable(true);
        firstButton.setOnAction(new ChangeCurrentFileEventHandler<>(this.currentFileProperty(), this.availableFilesProperty(), event -> ChangeCurrentFileDirection.FIRST));
        this.getItems().add(firstButton);
        Button prevButton = new Button("", new ImageView(new Image(TagTigerToolBar.class.getClassLoader().getResourceAsStream("icons/16/go-previous.png"))));
        prevButton.setTooltip(new Tooltip(localization.previousFile()));
        prevButton.setDisable(true);
        prevButton.setOnAction(new ChangeCurrentFileEventHandler<>(this.currentFileProperty(), this.availableFilesProperty(), event -> ChangeCurrentFileDirection.PREVIOUS));
        this.getItems().add(prevButton);
        Button nextButton = new Button("", new ImageView(new Image(TagTigerToolBar.class.getClassLoader().getResourceAsStream("icons/16/go-next.png"))));
        nextButton.setTooltip(new Tooltip(localization.nextFile()));
        nextButton.setDisable(true);
        nextButton.setOnAction(new ChangeCurrentFileEventHandler<>(this.currentFileProperty(), this.availableFilesProperty(), event -> ChangeCurrentFileDirection.NEXT));
        this.getItems().add(nextButton);
        Button lastButton = new Button("", new ImageView(new Image(TagTigerToolBar.class.getClassLoader().getResourceAsStream("icons/16/go-last.png"))));
        lastButton.setTooltip(new Tooltip(localization.lastFile()));
        lastButton.setDisable(true);
        lastButton.setOnAction(new ChangeCurrentFileEventHandler<>(this.currentFileProperty(), this.availableFilesProperty(), event -> ChangeCurrentFileDirection.LAST));
        this.getItems().add(lastButton);

        this.currentFileProperty().addListener((o, oldValue, newValue) -> this.computeButtonStates(firstButton, prevButton, nextButton, lastButton));
        this.availableFilesProperty().addListener((o, oldValue, newValue) -> this.computeButtonStates(firstButton, prevButton, nextButton, lastButton));

        this.getItems().add(new Separator());

        Button saveChangedFilesButton = new Button(localization.saveChangedFiles(), new ImageView(new Image(TagTigerToolBar.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        saveChangedFilesButton.setOnAction(event -> Optional.ofNullable(this.getOnSaveAction()).ifPresent(handler -> handler.handle(event)));
        saveChangedFilesButton.setMaxWidth(Double.MAX_VALUE);
        this.changedFilesProperty().addListener((Change<? extends TaggableFile> change) -> saveChangedFilesButton.setDisable(change.getList().isEmpty()));
        this.getItems().add(saveChangedFilesButton);

    }

    private void computeButtonStates(Button firstButton, Button prevButton, Button nextButton, Button lastButton) {

        List<TaggableFile> availableFiles = this.availableFilesProperty().get();
        TaggableFile currentFile = this.currentFileProperty().get();
        int maxFiles = this.availableFilesProperty().get() == null ? 0 : this.availableFilesProperty().size();
        int currentFileIndex = currentFile == null || availableFiles == null ? -1 : availableFiles.indexOf(currentFile);
        firstButton.setDisable(maxFiles <= 1 || currentFileIndex <= 0);
        prevButton.setDisable(maxFiles <= 1 || currentFileIndex <= 0);
        nextButton.setDisable(maxFiles <= 1 || currentFileIndex < 0 || currentFileIndex >= maxFiles - 1);
        lastButton.setDisable(maxFiles <= 1 || currentFileIndex < 0 || currentFileIndex >= maxFiles - 1);

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    EventHandler<ActionEvent> getOnSaveAction() {
        return this.onSaveActionProperty().get();
    }
    void setOnSaveAction(EventHandler<ActionEvent> eventHandler) {
        this.onSaveActionProperty().set(eventHandler);
    }
    ObjectProperty<EventHandler<ActionEvent>> onSaveActionProperty() {
        return this.onSaveAction;
    }

    ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    ListProperty<TaggableFile> availableFilesProperty() {
        return this.availableFiles;
    }

    ListProperty<TaggableFile> changedFilesProperty() {
        return this.changedFiles;
    }

}
