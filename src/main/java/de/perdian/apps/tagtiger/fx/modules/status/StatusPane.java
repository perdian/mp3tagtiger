/*
 * Copyright 2014-2017 Christian Seifert
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
package de.perdian.apps.tagtiger.fx.modules.status;

import de.perdian.apps.tagtiger.core.jobs.JobExecutor;
import de.perdian.apps.tagtiger.core.jobs.JobListener;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger3.fx.jobs.Job;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

public class StatusPane extends HBox {

    private Label statusLabel = null;
    private ProgressBar progressBar = null;
    private Button cancelButton = null;

    public StatusPane(Selection selection, Localization localization, JobExecutor jobExecutor) {

        Label statusLabel = new Label(localization.noFilesSelectedYet());
        statusLabel.setPadding(new Insets(5, 5, 0, 5));
        statusLabel.setTextAlignment(TextAlignment.LEFT);
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        this.setStatusLabel(statusLabel);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setMaxHeight(Double.MAX_VALUE);
        progressBar.setMinWidth(200);
        this.setProgressBar(progressBar);

        Button cancelButton = new Button(localization.cancel());
        cancelButton.setDisable(true);
        cancelButton.setMaxHeight(Double.MAX_VALUE);
        cancelButton.setOnAction(event -> {
            cancelButton.setDisable(true);
            jobExecutor.cancelCurrentJob();
        });
        this.setCancelButton(cancelButton);

        this.setPadding(new Insets(5, 5, 5, 5));
        this.setSpacing(5);
        this.getChildren().addAll(statusLabel, progressBar, cancelButton);

        jobExecutor.addListener(new StatusPaneJobListener());

    }

    class StatusPaneJobListener implements JobListener {

        @Override
        public void jobStarted(Job job) {
            Platform.runLater(() -> {
                StatusPane.this.getCancelButton().setDisable(false);
            });
        }

        @Override
        public void jobProgress(Job job, String progressMessage, Integer progressStep, Integer totalProgressSteps) {
            Platform.runLater(() -> {
                if (progressStep != null && totalProgressSteps != null) {
                    if (progressStep.intValue() < 0) {
                        StatusPane.this.getProgressBar().setProgress(-1d);
                    } else  if (totalProgressSteps.intValue() == 0) {
                        StatusPane.this.getProgressBar().setProgress(0d);
                    } else {
                        StatusPane.this.getProgressBar().setProgress((1d / totalProgressSteps) * (progressStep + 1));
                    }
                }
                StatusPane.this.getStatusLabel().setText(progressMessage);
            });
        }

        @Override
        public void jobCompleted(Job job, boolean otherJobsActive) {
            if (!otherJobsActive) {
                Platform.runLater(() -> {
                    StatusPane.this.getCancelButton().setDisable(true);
                    StatusPane.this.getStatusLabel().setText("");
                    StatusPane.this.getProgressBar().setProgress(0d);
                });
            }
        }

    }

    Label getStatusLabel() {
        return this.statusLabel;
    }
    void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    ProgressBar getProgressBar() {
        return this.progressBar;
    }
    void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    Button getCancelButton() {
        return this.cancelButton;
    }
    void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

}
