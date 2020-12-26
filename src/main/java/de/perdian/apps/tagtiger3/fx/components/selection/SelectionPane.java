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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class SelectionPane extends GridPane {

    private ObservableList<SongFile> availableSongs = FXCollections.observableArrayList();

    public SelectionPane(Preferences preferences, JobExecutor jobExecutor) {

        SelectionTableView selectionTableView = new SelectionTableView(this.getAvailableSongs());
        GridPane.setHgrow(selectionTableView, Priority.ALWAYS);
        GridPane.setVgrow(selectionTableView, Priority.ALWAYS);
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(selectionTableView.disableProperty()));

        this.add(selectionTableView, 0, 0, 1, 1);
        this.setHgap(5);
        this.setVgap(5);

    }

    public ObservableList<SongFile> getAvailableSongs() {
        return this.availableSongs;
    }
    void setAvailableSongs(ObservableList<SongFile> availableSongs) {
        this.availableSongs = availableSongs;
    }

}
