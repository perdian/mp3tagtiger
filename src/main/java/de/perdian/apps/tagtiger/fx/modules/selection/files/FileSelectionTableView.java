/*
 * Copyright 2014 Christian Seifert
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
package de.perdian.apps.tagtiger.fx.modules.selection.files;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

class FileSelectionTableView extends TableView<TaggableFile> {

    FileSelectionTableView(Localization localization) {

        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.setBorder(null);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Image flagIconImage = new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/flag-red.png"));
        TableColumn<TaggableFile, Boolean> changedColumn = new TableColumn<>();
        changedColumn.setSortable(false);
        changedColumn.setCellValueFactory(p -> p.getValue().dirtyProperty());
        changedColumn.setCellFactory(item -> {
            TableCell<TaggableFile, Boolean> tableCell = new TableCell<>() {
                @Override protected void updateItem(Boolean item, boolean empty) {
                    if (!empty) {
                        this.setGraphic(item.booleanValue() ? new Label("", new ImageView(flagIconImage)) : null);
                    } else {
                        this.setGraphic(null);
                    }
                }
            };
            return tableCell;
        });
        changedColumn.setMinWidth(24);
        changedColumn.setMaxWidth(24);
        this.getColumns().add(changedColumn);

        Image editIconImage = new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/edit.png"));
        TableColumn<TaggableFile, Boolean> activeColumn = new TableColumn<>();
        activeColumn.setSortable(false);
        activeColumn.setCellValueFactory(p -> p.getValue().activeProperty());
        activeColumn.setCellFactory(item -> {
            TableCell<TaggableFile, Boolean> tableCell = new TableCell<>() {
                @Override protected void updateItem(Boolean item, boolean empty) {
                    if (!empty) {
                        this.setGraphic(item.booleanValue() ? new Label("", new ImageView(editIconImage)) : null);
                    } else {
                        this.setGraphic(null);
                    }
                }
            };
            return tableCell;
        });
        activeColumn.setMinWidth(24);
        activeColumn.setMaxWidth(24);
        this.getColumns().add(activeColumn);

        TableColumn<TaggableFile, String> fileNameColumn = new TableColumn<>(localization.fileName());
        fileNameColumn.setSortable(false);
        fileNameColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().toString()));
        fileNameColumn.setMaxWidth(Double.MAX_VALUE);
        this.getColumns().add(fileNameColumn);

    }

}
