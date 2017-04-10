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

import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.handlers.files.UpdateFileNamesFromTagsAction;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class TagTigerMenuBar extends MenuBar {

    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    TagTigerMenuBar(Localization localization) {

    	this.useSystemMenuBarProperty().set(true);

        Menu actionsMenu = new Menu(localization.actions());
        MenuItem updateFileNamesFromTagsItem = new MenuItem(localization.updateFileNames());
        updateFileNamesFromTagsItem.setGraphic(new ImageView(new Image(TagTigerMenuBar.class.getClassLoader().getResourceAsStream("icons/16/file-list.png"))));
        updateFileNamesFromTagsItem.setOnAction(new UpdateFileNamesFromTagsAction(localization).asEventHandler(this.currentFileProperty(), this.selectedFilesProperty()));
        updateFileNamesFromTagsItem.setDisable(true);
        this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> updateFileNamesFromTagsItem.setDisable(change.getList().isEmpty()));
        actionsMenu.getItems().add(updateFileNamesFromTagsItem);

        this.getMenus().add(actionsMenu);

    }

    ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    ListProperty<TaggableFile> availableFilesProperty() {
        return this.availableFiles;
    }

    ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

}