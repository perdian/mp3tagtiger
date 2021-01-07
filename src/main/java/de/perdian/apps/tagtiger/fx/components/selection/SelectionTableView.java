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
package de.perdian.apps.tagtiger.fx.components.selection;

import java.util.List;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.tagtiger.model.SongAttribute;
import de.perdian.apps.tagtiger.model.SongFile;
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

        TableColumn<SongFile, Boolean> dirtyColumn = new TableColumn<>("");
        dirtyColumn.setCellValueFactory(callback -> callback.getValue().getDirty());
        dirtyColumn.setCellFactory(this.createIconCellCallback(FontAwesomeIcon.FLAG, null));
        dirtyColumn.setSortable(false);
        dirtyColumn.setReorderable(false);
        dirtyColumn.setMinWidth(25);
        dirtyColumn.setMaxWidth(25);

        TableColumn<SongFile, Boolean> focusColumn = new TableColumn<>("");
        focusColumn.setCellValueFactory(callback -> callback.getValue().getFocus());
        focusColumn.setCellFactory(this.createIconCellCallback(FontAwesomeIcon.ARROW_RIGHT, null));
        focusColumn.setSortable(false);
        focusColumn.setReorderable(false);
        focusColumn.setMinWidth(25);
        focusColumn.setMaxWidth(25);

        TableColumn<SongFile, String> fileNameColumn = new TableColumn<>("File name");
        fileNameColumn.setCellValueFactory(callback -> callback.getValue().getAttributeValueProperty(SongAttribute.FILENAME, String.class));

        TableColumn<SongFile, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setMinWidth(250);
        titleColumn.setMaxWidth(300);
        titleColumn.setCellValueFactory(callback -> callback.getValue().getAttributeValueProperty(SongAttribute.TITLE, String.class));

        TableColumn<SongFile, String> artistColumn = new TableColumn<>("Artist");
        artistColumn.setMinWidth(150);
        artistColumn.setMaxWidth(200);
        artistColumn.setCellValueFactory(callback -> callback.getValue().getAttributeValueProperty(SongAttribute.ARTIST, String.class));

        TableColumn<SongFile, String> albumColumn = new TableColumn<>("Album");
        albumColumn.setMinWidth(150);
        albumColumn.setMaxWidth(200);
        albumColumn.setCellValueFactory(callback -> callback.getValue().getAttributeValueProperty(SongAttribute.ALBUM, String.class));

        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.getColumns().setAll(List.of(dirtyColumn, focusColumn, fileNameColumn, titleColumn, artistColumn, albumColumn));
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
