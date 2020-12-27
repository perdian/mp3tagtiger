/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.selection;

import java.util.List;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.tagtiger3.model.SongFile;
import de.perdian.apps.tagtiger3.model.SongProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

class SelectionTableView extends TableView<SongFile> {

    SelectionTableView(ObservableList<SongFile> selectedFiles) {
        super(selectedFiles);

        TableColumn<SongFile, Boolean> selectedColumn = new TableColumn<>("");
        selectedColumn.setCellValueFactory(callback -> callback.getValue().getMarker());
        selectedColumn.setCellFactory(this.createIconCellCallback(FontAwesomeIcon.ARROW_RIGHT, null));
        selectedColumn.setSortable(false);
        selectedColumn.setReorderable(false);
        selectedColumn.setMinWidth(30);
        selectedColumn.setMaxWidth(30);

        TableColumn<SongFile, Boolean> dirtyColumn = new TableColumn<>("");
        dirtyColumn.setCellValueFactory(callback -> callback.getValue().getProperties().getDirty());
        dirtyColumn.setCellFactory(this.createIconCellCallback(FontAwesomeIcon.FLAG, null));
        dirtyColumn.setSortable(false);
        dirtyColumn.setReorderable(false);
        dirtyColumn.setMinWidth(30);
        dirtyColumn.setMaxWidth(30);

        TableColumn<SongFile, String> fileNameColumn = new TableColumn<>("File name");
        fileNameColumn.setCellValueFactory(callback -> callback.getValue().getProperties().getValue(SongProperty.FILENAME, String.class).getValue());

        TableColumn<SongFile, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setMinWidth(150);
        titleColumn.setMaxWidth(200);
        titleColumn.setCellValueFactory(callback -> callback.getValue().getProperties().getValue(SongProperty.TITLE, String.class).getValue());

        TableColumn<SongFile, String> artistColumn = new TableColumn<>("Artist");
        artistColumn.setMinWidth(150);
        artistColumn.setMaxWidth(200);
        artistColumn.setCellValueFactory(callback -> callback.getValue().getProperties().getValue(SongProperty.ARTIST, String.class).getValue());

        TableColumn<SongFile, String> albumColumn = new TableColumn<>("Album");
        albumColumn.setMinWidth(150);
        albumColumn.setMaxWidth(200);
        albumColumn.setCellValueFactory(callback -> callback.getValue().getProperties().getValue(SongProperty.ALBUM, String.class).getValue());

        this.getColumns().setAll(List.of(selectedColumn, dirtyColumn, fileNameColumn, titleColumn, artistColumn, albumColumn));
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private Callback<TableColumn<SongFile, Boolean>, TableCell<SongFile, Boolean>> createIconCellCallback(FontAwesomeIcon trueIcon, FontAwesomeIcon falseIcon) {
        return column -> {
            TableCell<SongFile, Boolean> tableCell = new TableCell<>() {
                @Override protected void updateItem(Boolean item, boolean empty) {
                    if (!empty) {
                        if (item != null && item.booleanValue()) {
                            this.setGraphic(trueIcon == null ? null : new Label("", new FontAwesomeIconView(trueIcon)));
                        } else {
                            this.setGraphic(falseIcon == null ? null : new Label("", new FontAwesomeIconView(falseIcon)));
                        }
                    } else {
                        this.setGraphic(null);
                    }
                }
            };
            return tableCell;
        };
    }

}
