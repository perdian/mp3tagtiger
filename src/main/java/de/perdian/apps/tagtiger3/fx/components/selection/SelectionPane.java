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

import de.perdian.apps.tagtiger3.fx.jobs.JobExecutor;
import de.perdian.apps.tagtiger3.fx.jobs.listeners.DisableWhileJobRunningJobListener;
import de.perdian.apps.tagtiger3.model.SongFile;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class SelectionPane extends GridPane {

    private SelectionModel selectionModel = null;

    public SelectionPane(ObservableList<SongFile> availableSongs, Preferences preferences, JobExecutor jobExecutor) {

        SelectionTableView selectionTableView = new SelectionTableView(availableSongs);
        GridPane.setHgrow(selectionTableView, Priority.ALWAYS);
        GridPane.setVgrow(selectionTableView, Priority.ALWAYS);
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(selectionTableView.disableProperty()));

        SelectionModel selectionModel = new SelectionModel(availableSongs, selectionTableView.getSelectionModel().getSelectedItems());
        selectionTableView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> selectionModel.focusFileProperty().setValue(newValue));
        selectionModel.focusFileProperty().addListener((o, oldValue, newValue) -> this.scrollTo(newValue, selectionTableView));
        this.setSelectionModel(selectionModel);

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

    public SelectionModel getSelectionModel() {
        return this.selectionModel;
    }
    private void setSelectionModel(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

}
