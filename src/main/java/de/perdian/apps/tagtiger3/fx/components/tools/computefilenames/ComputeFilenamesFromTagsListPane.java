/*
 * Copyright 2014-2021 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.tools.computefilenames;

import java.util.List;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.perdian.apps.tagtiger3.fx.components.tools.ToolActionHelpers;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;

class ComputeFilenamesFromTagsListPane extends BorderPane {

    ComputeFilenamesFromTagsListPane(ObservableList<ComputeFilenamesFromTagsItem> items) {

        TableColumn<ComputeFilenamesFromTagsItem, Boolean> dirtyColumn = new TableColumn<>("");
        dirtyColumn.setCellValueFactory(callback -> callback.getValue().getDirty());
        dirtyColumn.setCellFactory(ToolActionHelpers.createIconCellCallback(FontAwesomeIcon.FLAG, null));
        dirtyColumn.setEditable(false);
        dirtyColumn.setSortable(false);
        dirtyColumn.setReorderable(false);
        dirtyColumn.setMinWidth(25);
        dirtyColumn.setMaxWidth(25);
        TableColumn<ComputeFilenamesFromTagsItem, String> oldFilenameColumn = new TableColumn<>("Current file name");
        oldFilenameColumn.setEditable(false);
        oldFilenameColumn.setCellValueFactory(features -> features.getValue().getOriginalFilename());
        oldFilenameColumn.setSortable(false);
        oldFilenameColumn.setReorderable(false);
        TableColumn<ComputeFilenamesFromTagsItem, String> newFilenameColumn = new TableColumn<>("New file name");
        newFilenameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        newFilenameColumn.setEditable(true);
        newFilenameColumn.setSortable(false);
        newFilenameColumn.setReorderable(false);
        newFilenameColumn.setCellValueFactory(features -> features.getValue().getNewFilename());

        TableView<ComputeFilenamesFromTagsItem> itemsTableView = new TableView<>(items);
        itemsTableView.setEditable(true);
        itemsTableView.getColumns().addAll(List.of(dirtyColumn, oldFilenameColumn, newFilenameColumn));
        itemsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.setCenter(itemsTableView);

    }

}
