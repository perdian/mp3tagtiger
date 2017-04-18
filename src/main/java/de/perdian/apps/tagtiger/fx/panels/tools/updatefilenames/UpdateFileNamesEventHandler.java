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

import java.util.List;
import java.util.stream.Collectors;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UpdateFileNamesEventHandler implements EventHandler<ActionEvent> {

    private Selection selection = null;
    private Localization localization = null;

    public UpdateFileNamesEventHandler(Selection selection, Localization localization) {
        this.setSelection(selection);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {

        List<UpdateFileNamesItem> fileNameItems = this.getSelection().selectedFilesProperty().stream()
            .map(UpdateFileNamesItem::new)
            .collect(Collectors.toList());

        UpdateFileNamesPane updateFileNamesPane = new UpdateFileNamesPane(fileNameItems, this.getLocalization());

        Stage dialogStage = new Stage();
        dialogStage.setMinWidth(400);
        dialogStage.setMinHeight(100);
        dialogStage.setTitle(this.getLocalization().updateFileNames());
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(updateFileNamesPane));
        dialogStage.show();

    }

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
