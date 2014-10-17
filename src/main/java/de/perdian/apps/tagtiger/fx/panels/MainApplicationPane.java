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

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import de.perdian.apps.tagtiger.business.framework.TagTiger;
import de.perdian.apps.tagtiger.business.framework.jobs.Job;
import de.perdian.apps.tagtiger.business.framework.jobs.JobListener;
import de.perdian.apps.tagtiger.business.impl.jobs.SaveChangedFilesInSelectionJob;
import de.perdian.apps.tagtiger.fx.components.status.StatusPane;
import de.perdian.apps.tagtiger.fx.panels.editor.EditorPane;
import de.perdian.apps.tagtiger.fx.panels.selection.SelectionPane;

/**
 * The central pane in which the main components of the application are being
 * layed out
 *
 * @author Christian Robert
 */

public class MainApplicationPane extends VBox {

    public MainApplicationPane(TagTiger tagTiger) {

        SelectionPane selectionPane = new SelectionPane(tagTiger.getSelection(), tagTiger.getLocalization());
        selectionPane.setMinWidth(250d);
        selectionPane.setPrefWidth(250d);
        selectionPane.setOnSaveAction(event -> tagTiger.getJobExecutor().executeJob(new SaveChangedFilesInSelectionJob(tagTiger.getSelection(), tagTiger.getLocalization())));
        TitledPane fileSelectionWrapperPane = new TitledPane(tagTiger.getLocalization().selectFiles(), selectionPane);
        fileSelectionWrapperPane.setMaxHeight(Double.MAX_VALUE);
        fileSelectionWrapperPane.setCollapsible(false);
        fileSelectionWrapperPane.setPadding(new Insets(5, 5, 5, 5));
        VBox.setVgrow(fileSelectionWrapperPane, Priority.ALWAYS);

        EditorPane editorPane = new EditorPane(tagTiger.getLocalization());
        editorPane.setMinWidth(400d);
        editorPane.setPadding(new Insets(5, 5, 5, 5));
        editorPane.setUpdateFileConsumer(newFile -> tagTiger.getSelection().currentFileProperty().setValue(newFile));
        editorPane.currentFileProperty().bind(tagTiger.getSelection().currentFileProperty());
        editorPane.availableFilesProperty().bind(tagTiger.getSelection().availableFilesProperty());
        editorPane.selectedFilesProperty().bind(tagTiger.getSelection().selectedFilesProperty());
        VBox.setVgrow(editorPane, Priority.ALWAYS);
        tagTiger.getJobExecutor().addListener(new EnableDisableJobListener(editorPane));

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(fileSelectionWrapperPane);
        splitPane.getItems().add(editorPane);
        splitPane.setDividerPositions(0.25d);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        StatusPane statusPane = new StatusPane(tagTiger);
        statusPane.setPadding(new Insets(2.5, 5, 2.5, 5));

        this.getChildren().add(splitPane);
        this.getChildren().add(statusPane);

    }

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    static class EnableDisableJobListener implements JobListener {

        private Node node = null;

        EnableDisableJobListener(Node node) {
            this.setNode(node);
        }

        @Override
        public void jobStarted(Job job) {
            Platform.runLater(() -> this.getNode().setDisable(true));
        }

        @Override
        public void jobCompleted(Job job, boolean otherJobsActive) {
            if (!otherJobsActive) {
                Platform.runLater(() -> this.getNode().setDisable(false));
            }
        }

        // ---------------------------------------------------------------------
        // --- Property access methods -----------------------------------------
        // ---------------------------------------------------------------------

        private Node getNode() {
            return this.node;
        }
        private void setNode(Node node) {
            this.node = node;
        }

    }

}