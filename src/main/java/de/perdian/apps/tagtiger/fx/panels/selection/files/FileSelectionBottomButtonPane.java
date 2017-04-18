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
package de.perdian.apps.tagtiger.fx.panels.selection.files;

import de.perdian.apps.tagtiger.core.jobs.JobExecutor;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.panels.tools.updatefilenames.UpdateFileNamesEventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

class FileSelectionBottomButtonPane extends HBox {

    FileSelectionBottomButtonPane(Selection selection, Localization localization, JobExecutor jobExecutor) {

        Button updateFileNamesButton = new Button(localization.updateFileNames());
        updateFileNamesButton.setOnAction(new UpdateFileNamesEventHandler(selection, localization));
        updateFileNamesButton.setDisable(true);
        updateFileNamesButton.setGraphic(new ImageView(new Image(FileSelectionBottomButtonPane.class.getClassLoader().getResourceAsStream("icons/16/file-list.png"))));
        selection.selectedFilesProperty().addListener((o, oldValue, newValue) -> updateFileNamesButton.setDisable(newValue == null || newValue.isEmpty()));

        this.getChildren().add(updateFileNamesButton);
        this.setPadding(new Insets(5, 5, 5, 5));
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setSpacing(5);

    }

}
