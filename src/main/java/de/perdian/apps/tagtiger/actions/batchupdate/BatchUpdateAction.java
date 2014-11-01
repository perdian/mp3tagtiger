/*
 * Copyright 2014 Christian Robert
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
package de.perdian.apps.tagtiger.actions.batchupdate;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;

public abstract class BatchUpdateAction implements EventHandler<ActionEvent> {

    private ObservableList<TaggableFile> files = null;
    private Localization localization = null;

    public BatchUpdateAction(ObservableList<TaggableFile> files, Localization localization) {
        this.setFiles(files);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {

        BatchUpdateDialog dialog = this.createDialog();

        BatchUpdateLegendPane legendPane = new BatchUpdateLegendPane(this.getLocalization());
        TitledPane legendTitlePane = new TitledPane(this.getLocalization().legend(), legendPane);
        legendTitlePane.setCollapsible(false);

        VBox contentPane = new VBox(10);
        contentPane.setPadding(new Insets(10, 10, 10, 10));
        contentPane.getChildren().addAll(dialog.getActionPane(), legendTitlePane);
        contentPane.setMinWidth(dialog.getDialogPrefWidth());

        Stage dialogStage = new Stage();
        dialogStage.setMinWidth(400);
        dialogStage.setMinHeight(100);
        dialogStage.setTitle(dialog.getDialogTitle());
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(contentPane));
        dialogStage.show();

    }

    protected abstract BatchUpdateDialog createDialog();

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    protected ObservableList<TaggableFile> getFiles() {
        return this.files;
    }
    private void setFiles(ObservableList<TaggableFile> files) {
        this.files = files;
    }

    protected Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}