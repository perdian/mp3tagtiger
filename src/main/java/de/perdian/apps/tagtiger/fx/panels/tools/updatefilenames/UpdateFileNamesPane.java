/*
 * Copyright 2014-2017 Christian Robert
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
package de.perdian.apps.tagtiger.fx.panels.tools.updatefilenames;

import java.util.Arrays;
import java.util.List;

import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

class UpdateFileNamesPane extends BorderPane {

    UpdateFileNamesPane(List<UpdateFileNamesItem> items, Localization localization) {

        Parent patternFieldPane = new UpdateFileNamesPatternFieldPane(items, localization);
        BorderPane.setMargin(patternFieldPane, new Insets(5, 5, 5, 5));

        TableColumn<UpdateFileNamesItem, String> currentFileNameColumn = new TableColumn<>(localization.currentFileName());
        currentFileNameColumn.setSortable(false);
        currentFileNameColumn.setCellValueFactory(p -> p.getValue().getCurrentFileName());
        TableColumn<UpdateFileNamesItem, String> newFileNameColumn = new TableColumn<>(localization.newFileName());
        newFileNameColumn.setSortable(false);
        newFileNameColumn.setCellValueFactory(p -> p.getValue().getNewFileName());
        TableView<UpdateFileNamesItem> tableView = new TableView<>(FXCollections.observableArrayList(items));
        tableView.getColumns().addAll(Arrays.asList(currentFileNameColumn, newFileNameColumn));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        BorderPane.setMargin(tableView, new Insets(5, 5, 5, 5));

        Parent legendPane = new UpdateFileNamesLegendPane(localization);
        BorderPane.setMargin(legendPane, new Insets(5, 5, 5, 5));

        this.setPrefWidth(800);
        this.setTop(patternFieldPane);
        this.setCenter(tableView);
        this.setBottom(legendPane);

    }

}
