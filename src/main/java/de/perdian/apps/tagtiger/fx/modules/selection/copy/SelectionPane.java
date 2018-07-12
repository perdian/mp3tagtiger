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
package de.perdian.apps.tagtiger.fx.modules.selection.copy;

import java.io.File;

import de.perdian.apps.tagtiger.core.jobs.JobExecutor;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.modules.selection.directories.DirectoryTreeView;
import de.perdian.apps.tagtiger.fx.modules.selection.files.FileSelectionPane;
import de.perdian.apps.tagtiger.fx.support.joblisteners.DisableWhileJobRunningJobListener;
import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SelectionPane extends BorderPane {

    public SelectionPane(Selection selection, Localization localization, JobExecutor jobExecutor) {

        DirectoryTreeView directoryTreeView = new DirectoryTreeView(localization);
        directoryTreeView.setMinWidth(225d);
        directoryTreeView.selectedDirectoryProperty().addListener((o, oldValue, newValue) -> selection.currentDirectoryProperty().setValue(newValue));

        FileSelectionPane fileSelectionPane = new FileSelectionPane(selection, localization, jobExecutor);
        fileSelectionPane.setMinWidth(300d);
        fileSelectionPane.availableFilesProperty().bindBidirectional(selection.availableFilesProperty());
        fileSelectionPane.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> selection.selectedFilesProperty().setAll(change.getList()));
        fileSelectionPane.selectedFileProperty().addListener((o, oldValue, newValue) -> selection.currentFileProperty().setValue(newValue));
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(fileSelectionPane.disableProperty()));

        SplitPane splitPane = new SplitPane(directoryTreeView, fileSelectionPane);
        splitPane.setDividerPositions(0.4d);

        TextField directoryField = new TextField();
        HBox directoryFieldWrapper = new HBox(directoryField);
        directoryFieldWrapper.setPadding(new Insets(0, 0, 5, 0));
        HBox.setHgrow(directoryField, Priority.ALWAYS);

        this.setTop(directoryFieldWrapper);
        this.setCenter(splitPane);
        this.setPadding(new Insets(5, 5, 5, 5));

        // Connect input from the selection to the different panes
        selection.currentFileProperty().addListener((o, oldValue, newValue) -> fileSelectionPane.selectedFileProperty().setValue(newValue));
        selection.currentDirectoryProperty().addListener((o, oldValue, newValue) -> directoryTreeView.selectDirectory(newValue));

        directoryField.setOnAction(event -> directoryTreeView.selectDirectory(new File(((TextField)event.getSource()).getText())));
        directoryField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> directoryField.selectAll());
            }
        });
        directoryTreeView.selectedDirectoryProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                directoryField.setText(newValue == null ? "" : newValue.getAbsolutePath());
                if (directoryField.isFocused()) {
                    directoryField.selectAll();
                }
            });
        });

    }

}
