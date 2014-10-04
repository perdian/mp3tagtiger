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
package de.perdian.apps.tagtiger.fx.panels;

import java.nio.file.Path;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import de.perdian.apps.tagtiger.business.TagTiger;
import de.perdian.apps.tagtiger.business.TagTigerLocalization;
import de.perdian.apps.tagtiger.business.TagTigerSelection;
import de.perdian.apps.tagtiger.business.model.MpFileWrapper;

class FileSelectionStatusPane extends HBox implements TagTigerSelection.SelectionProgressListener {

    private TagTigerLocalization localization = null;
    private Label statusLabel = null;
    private ProgressBar progressBar = null;
    private Button cancelButton = null;

    FileSelectionStatusPane(TagTiger tagTiger) {

        Label statusLabel = new Label(tagTiger.getLocalization().noFilesSelectedYet());
        statusLabel.setPadding(new Insets(5, 5, 0, 5));
        statusLabel.setTextAlignment(TextAlignment.LEFT);
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        this.setStatusLabel(statusLabel);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setMaxHeight(Double.MAX_VALUE);
        this.setProgressBar(progressBar);

        Button cancelButton = new Button(tagTiger.getLocalization().cancel());
        cancelButton.setDisable(true);
        cancelButton.setMaxHeight(Double.MAX_VALUE);
        cancelButton.setOnAction(event -> {
            cancelButton.setDisable(true);
            tagTiger.getSelection().cancelFileLoading();
        });
        this.setCancelButton(cancelButton);

        this.setSpacing(5);
        this.getChildren().addAll(statusLabel, progressBar, cancelButton);
        this.setLocalization(tagTiger.getLocalization());

        tagTiger.getSelection().addProgressListener(this);

    }

    // -------------------------------------------------------------------------
    // --- ProgressListener ----------------------------------------------------
    // -------------------------------------------------------------------------

    @Override
    public void startProcessing(Path directory) {
        Platform.runLater(() -> {
            this.getCancelButton().setDisable(false);
            this.getStatusLabel().setText(this.getLocalization().analyzingFilesFromDirectory(directory.getFileName().toString()));
        });
    }

    @Override
    public void startProcessingFiles(List<Path> originalFiles) {
        Platform.runLater(() -> this.getStatusLabel().setText(this.getLocalization().startProcessingOfFiles(originalFiles.size())));
    }

    @Override
    public void startProcessingFile(Path path, int index, int totalFiles) {
        Platform.runLater(() -> {
            this.getProgressBar().setProgress((1d / totalFiles) * (index + 1));
            this.getStatusLabel().setText(this.getLocalization().processingFile(path.getFileName().toString()));
        });
    }

    @Override
    public void finishedProcessingFile(Path path, MpFileWrapper fileWrapper, int index, int totalFiles) {
    }

    @Override
    public void finishedProcessing(List<Path> originalFiles, List<MpFileWrapper> wrappedFiles) {
        Platform.runLater(() -> {
            this.getCancelButton().setDisable(true);
            this.getStatusLabel().setText(this.getLocalization().noFilesSelectedYet());
            this.getProgressBar().setProgress(0d);
        });
    }

    @Override
    public void cancelProcessing() {
        this.getCancelButton().setDisable(true);
        this.getStatusLabel().setText(this.getLocalization().noFilesSelectedYet());
        this.getProgressBar().setProgress(0d);
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private Label getStatusLabel() {
        return this.statusLabel;
    }
    private void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    private ProgressBar getProgressBar() {
        return this.progressBar;
    }
    private void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private TagTigerLocalization getLocalization() {
        return this.localization;
    }
    private void setLocalization(TagTigerLocalization localization) {
        this.localization = localization;
    }

    private Button getCancelButton() {
        return this.cancelButton;
    }
    private void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

}