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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.tagtiger.fx.model.Selection;
import de.perdian.apps.tagtiger.model.SongFile;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class SelectionPane extends GridPane {

    public SelectionPane(Selection selection) {

        SelectionTableView selectionTableView = new SelectionTableView(selection.getAvailableFiles());
        selectionTableView.disableProperty().bind(selection.busyProperty());
        GridPane.setHgrow(selectionTableView, Priority.ALWAYS);
        GridPane.setVgrow(selectionTableView, Priority.ALWAYS);

        selectionTableView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> selection.focusFileProperty().setValue(newValue));
        selection.focusFileProperty().addListener((o, oldValue, newValue) -> this.scrollTo(newValue, selectionTableView));
        Bindings.bindContent(selection.getSelectedFiles(), selectionTableView.getSelectionModel().getSelectedItems());

        MenuItem reloadMenuItem = new MenuItem("Reload files", new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        reloadMenuItem.disableProperty().bind(selection.selectedDirectoryProperty().isNull());
        reloadMenuItem.setOnAction(event -> selection.reloadAvailableSongs());
        MenuItem selectAllMenuItem = new MenuItem("Select all files", new FontAwesomeIconView(FontAwesomeIcon.LIST));
        selectAllMenuItem.disableProperty().bind(Bindings.isEmpty(selectionTableView.getSelectionModel().getSelectedItems()));
        selectAllMenuItem.setOnAction(event -> selectionTableView.getSelectionModel().selectAll());
        ContextMenu selectionContextMenu = new ContextMenu();
        selectionContextMenu.getItems().addAll(reloadMenuItem, selectAllMenuItem);
        selectionTableView.setContextMenu(selectionContextMenu);

        this.add(selectionTableView, 0, 0, 1, 1);
        this.setHgap(5);
        this.setVgap(5);

    }

    private void scrollTo(SongFile file, TableView<SongFile> tableView) {
        if (file != null) {
            TableViewSkin<?> tableViewSkin = (TableViewSkin<?>)tableView.getSkin();
            VirtualFlow<?> virtualFlow = (VirtualFlow<?>)tableViewSkin.getChildren().get(1);
            int firstCellIndex = virtualFlow.getFirstVisibleCell().getIndex();
            int lastCellIndex = virtualFlow.getLastVisibleCell().getIndex();
            int fileIndex = tableView.getItems().indexOf(file);
            if (fileIndex <= firstCellIndex) {
                tableView.scrollTo(Math.max(0, fileIndex - 1));
            } else if (fileIndex > 0 && fileIndex >= lastCellIndex) {
                tableView.scrollTo(fileIndex);
            }
        }
    }

}
