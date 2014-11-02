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
package de.perdian.apps.tagtiger.fx.handlers.batchupdate;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;

/**
 * Shows a dialog to update a specific set of files
 *
 * @author Christian Robert
 */

public abstract class AbstractDialogActionEventHandler extends AbstractActionEventHandler {

    private Localization localization = null;

    public AbstractDialogActionEventHandler(Property<TaggableFile> currentFile, ObservableList<TaggableFile> otherFiles, Localization localization) {
        super(currentFile, otherFiles);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {

        BatchUpdateDialog dialog = this.createDialog();

        VBox contentPane = new VBox(10);
        contentPane.setPadding(new Insets(10, 10, 10, 10));
        contentPane.getChildren().addAll(dialog.getActionPane(), dialog.getLegendPane());
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
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    public static class BatchUpdateDialog {

        private int dialogPrefWidth = 500;
        private String dialogTitle = null;
        private Parent actionPane = null;
        private Parent legendPane = null;

        // -------------------------------------------------------------------------
        // --- Property access methods ---------------------------------------------
        // -------------------------------------------------------------------------

        public String getDialogTitle() {
            return this.dialogTitle;
        }
        public void setDialogTitle(String dialogTitle) {
            this.dialogTitle = dialogTitle;
        }

        public Parent getActionPane() {
            return this.actionPane;
        }
        public void setActionPane(Parent actionPane) {
            this.actionPane = actionPane;
        }

        public int getDialogPrefWidth() {
            return this.dialogPrefWidth;
        }
        public void setDialogPrefWidth(int dialogPrefWidth) {
            this.dialogPrefWidth = dialogPrefWidth;
        }

        public Parent getLegendPane() {
            return this.legendPane;
        }
        public void setLegendPane(Parent legendPane) {
            this.legendPane = legendPane;
        }

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    protected Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}