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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

class TagTigerToolBar extends ToolBar {

    private final ListProperty<TaggableFile> changedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<EventHandler<ActionEvent>> onSaveAction = new SimpleObjectProperty<>();

    TagTigerToolBar(Localization localization) {

        Button firstButton = new Button("<<");
        firstButton.setDisable(true);
        this.getItems().add(firstButton);
        Button prevButton = new Button("<");
        prevButton.setDisable(true);
        this.getItems().add(prevButton);
        Button nextButton = new Button(">");
        nextButton.setDisable(true);
        this.getItems().add(nextButton);
        Button lastButton = new Button(">>");
        lastButton.setDisable(true);
        this.getItems().add(lastButton);

        this.getItems().add(new Separator());

        Button saveChangedFilesButton = new Button(localization.saveChangedFiles(), new ImageView(new Image(TagTigerToolBar.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        saveChangedFilesButton.setOnAction(event -> Optional.ofNullable(this.getOnSaveAction()).ifPresent(handler -> handler.handle(event)));
        saveChangedFilesButton.setMaxWidth(Double.MAX_VALUE);
        this.changedFilesProperty().addListener((Change<? extends TaggableFile> change) -> saveChangedFilesButton.setDisable(change.getList().isEmpty()));
        this.getItems().add(saveChangedFilesButton);

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    ListProperty<TaggableFile> changedFilesProperty() {
        return this.changedFiles;
    }

    EventHandler<ActionEvent> getOnSaveAction() {
        return this.onSaveActionProperty().get();
    }
    void setOnSaveAction(EventHandler<ActionEvent> eventHandler) {
        this.onSaveActionProperty().set(eventHandler);
    }
    ObjectProperty<EventHandler<ActionEvent>> onSaveActionProperty() {
        return this.onSaveAction;
    }

}