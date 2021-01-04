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
import java.util.stream.Collectors;

import de.perdian.apps.tagtiger3.fx.components.tools.ToolActionEventHandler;
import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ComputeFilenamesFromTagsActionEventHandler extends ToolActionEventHandler {

    public ComputeFilenamesFromTagsActionEventHandler(List<SongFile> files) {
        super("Compute file names from tags", files);
    }

    @Override
    protected Pane createPane(List<SongFile> files) {
        ObservableList<ComputeFilenamesFromTagsItem> items = FXCollections.observableArrayList(files.stream().map(ComputeFilenamesFromTagsItem::new).collect(Collectors.toList()));

        ComputeFilenamesFromTagsActionPane actionPane = new ComputeFilenamesFromTagsActionPane(items);
        ComputeFilenamesFromTagsLegendPane legendPane = new ComputeFilenamesFromTagsLegendPane();
        ComputeFilenamesFromTagsListPane listPane = new ComputeFilenamesFromTagsListPane(items);
        VBox.setVgrow(listPane, Priority.ALWAYS);

        VBox mainPane = new VBox(actionPane, legendPane, listPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        mainPane.setSpacing(10);
        return mainPane;

    }

}
