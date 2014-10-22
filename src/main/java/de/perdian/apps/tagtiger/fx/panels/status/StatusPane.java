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
package de.perdian.apps.tagtiger.fx.panels.status;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import de.perdian.apps.tagtiger.business.framework.jobs.Job;
import de.perdian.apps.tagtiger.business.framework.jobs.JobListener;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;

public class StatusPane extends HBox implements JobListener {

    private final ObjectProperty<EventHandler<ActionEvent>> onCancelAction = new SimpleObjectProperty<>();
    private Label statusLabel = null;
    private ProgressBar progressBar = null;
    private Button cancelButton = null;

    public StatusPane(Localization localization) {

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
            Optional.ofNullable(this.onCancelActionProperty().get()).ifPresent(action -> action.handle(event));
        });
        this.setCancelButton(cancelButton);

        this.setSpacing(5);
        this.getChildren().addAll(statusLabel, progressBar, cancelButton);

    }

    // -------------------------------------------------------------------------
    // --- ProgressListener ----------------------------------------------------
    // -------------------------------------------------------------------------

    @Override
    public void jobStarted(Job job) {
        Platform.runLater(() -> {
            this.getCancelButton().setDisable(false);
        });
    }

    @Override
    public void jobProgress(Job job, String progressMessage, Integer progressStep, Integer totalProgressSteps) {
        Platform.runLater(() -> {
            if (progressStep != null && totalProgressSteps != null) {
                if (progressStep.intValue() < 0) {
                    this.getProgressBar().setProgress(-1d);
                } else  if (totalProgressSteps.intValue() == 0) {
                    this.getProgressBar().setProgress(0d);
                } else {
                    this.getProgressBar().setProgress((1d / totalProgressSteps) * (progressStep + 1));
                }
            }
            this.getStatusLabel().setText(progressMessage);
        });
    }

    @Override
    public void jobCompleted(Job job, boolean otherJobsActive) {
        if (!otherJobsActive) {
            Platform.runLater(() -> {
                this.getCancelButton().setDisable(true);
                this.getStatusLabel().setText("");
                this.getProgressBar().setProgress(0d);
            });
        }
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObjectProperty<EventHandler<ActionEvent>> onCancelActionProperty() {
        return this.onCancelAction;
    }
    public EventHandler<ActionEvent> getOnCancelAction() {
        return this.onCancelActionProperty().get();
    }
    public void setOnCancelAction(EventHandler<ActionEvent> action) {
        this.onCancelActionProperty().set(action);
    }

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

    private Button getCancelButton() {
        return this.cancelButton;
    }
    private void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

}