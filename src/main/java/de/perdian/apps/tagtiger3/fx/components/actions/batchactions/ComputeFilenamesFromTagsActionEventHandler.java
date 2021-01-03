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
package de.perdian.apps.tagtiger3.fx.components.actions.batchactions;

import java.util.List;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class ComputeFilenamesFromTagsActionEventHandler extends BatchActionEventHandler {

    public ComputeFilenamesFromTagsActionEventHandler(List<SongFile> files) {
        super("Compute file names from tags", files);
    }

    @Override
    protected Pane createPane(List<SongFile> files) {
        GridPane mainPane = new GridPane();

        TableColumn<ComputeFilenamesFromTagsItem, Boolean> dirtyColumn = new TableColumn<>("");
        dirtyColumn.setCellValueFactory(callback -> callback.getValue().getDirty());
        dirtyColumn.setCellFactory(BatchActionHelpers.createIconCellCallback(FontAwesomeIcon.FLAG, null));
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

        ObservableList<ComputeFilenamesFromTagsItem> items = FXCollections.observableArrayList(files.stream().map(ComputeFilenamesFromTagsItem::new).collect(Collectors.toList()));
        TableView<ComputeFilenamesFromTagsItem> itemsTableView = new TableView<>(items);
        itemsTableView.setEditable(true);
        itemsTableView.getColumns().addAll(List.of(dirtyColumn, oldFilenameColumn, newFilenameColumn));
        itemsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        GridPane.setHgrow(itemsTableView, Priority.ALWAYS);
        GridPane.setVgrow(itemsTableView, Priority.ALWAYS);

        BatchActionLegendPane legendPane = new BatchActionLegendPane();
        GridPane.setHgrow(legendPane, Priority.ALWAYS);

        mainPane.add(itemsTableView, 0, 0, 1, 1);
        mainPane.add(legendPane, 0, 1, 1, 1);
        mainPane.setHgap(10);
        mainPane.setVgap(10);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        return mainPane;
    }

}
