/*
 * Copyright 2014-2018 Christian Robert
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
package de.perdian.apps.tagtiger.fx.modules.tools.updatetags.components;

import java.util.List;

import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.modules.tools.updatetags.model.PreviewItem;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class PreviewItemsPane extends BorderPane {

    public PreviewItemsPane(ObservableList<PreviewItem> previewItems, Localization localization) {

        TableColumn<PreviewItem, ImageView> matchColumn = new TableColumn<>(localization.match());
        matchColumn.setSortable(false);
        matchColumn.setCellValueFactory(p -> p.getValue().getMatchesImageView());
        matchColumn.setMaxWidth(50);
        matchColumn.setMinWidth(50);

        TableColumn<PreviewItem, String> fileNameColumn = new TableColumn<>(localization.fileName());
        fileNameColumn.setSortable(false);
        fileNameColumn.setCellValueFactory(p -> p.getValue().getFileName());

        TableColumn<PreviewItem, String> newArtistColumn = new TableColumn<>(localization.artist());
        newArtistColumn.setSortable(false);
        newArtistColumn.setCellValueFactory(p -> p.getValue().property(TaggablePropertyKey.ARTIST));
        TableColumn<PreviewItem, String> newTitleColumn = new TableColumn<>(localization.title());
        newTitleColumn.setSortable(false);
        newTitleColumn.setCellValueFactory(p -> p.getValue().property(TaggablePropertyKey.TITLE));

        TableView<PreviewItem> tableView = new TableView<>(previewItems);
        tableView.getColumns().addAll(List.of(matchColumn, fileNameColumn, newArtistColumn, newTitleColumn));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.setCenter(tableView);

    }

}
