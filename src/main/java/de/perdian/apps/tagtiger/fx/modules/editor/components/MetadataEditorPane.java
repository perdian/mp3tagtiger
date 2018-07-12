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
package de.perdian.apps.tagtiger.fx.modules.editor.components;

import java.util.List;

import de.perdian.apps.tagtiger.core.jobs.JobExecutor;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.modules.editor.EditorComponentBuilderFactory;
import de.perdian.apps.tagtiger.fx.support.joblisteners.DisableWhileJobRunningJobListener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class MetadataEditorPane extends TitledPane {

    public MetadataEditorPane(EditorComponentBuilderFactory componentBuilderFactory, Selection selection, Localization localization, JobExecutor jobExecutor) {

        Label indexLabel = new Label();
        indexLabel.setMinWidth(65);
        indexLabel.setPadding(new Insets(0, 5, 0, 0));

        selection.currentFileProperty().addListener((o, oldValue, newValue) -> this.handleIndexLabelChange(indexLabel, newValue, selection.availableFilesProperty().getValue()));
        selection.availableFilesProperty().addListener((o, oldValue, newValue) -> this.handleIndexLabelChange(indexLabel, selection.currentFileProperty().getValue(), newValue));
        jobExecutor.addListener(new DisableWhileJobRunningJobListener(this.disableProperty()));

        Parent fileNameComponent = componentBuilderFactory.componentBuilder(file -> file.fileNameProperty())
            .useTextField()
            .build();
        GridPane.setHgrow(fileNameComponent, Priority.ALWAYS);

        Label fileExtensionLabel = new Label(".");
        fileExtensionLabel.setPadding(new Insets(0, 2, 0, 2));
        Parent fileExtensionComponent = componentBuilderFactory.componentBuilder(file -> file.fileExtensionProperty())
            .useTextField(textField -> textField.setPrefWidth(60))
            .actionCopyPropertyValue()
            .build();
        GridPane.setHgrow(fileExtensionComponent, Priority.NEVER);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.add(indexLabel, 0, 1);
        gridPane.add(new Label(localization.fileName()), 4, 0);
        gridPane.add(fileNameComponent, 4, 1);
        gridPane.add(fileExtensionLabel, 5, 1);
        gridPane.add(new Label(localization.fileExtension()), 6, 0);
        gridPane.add(fileExtensionComponent, 6, 1);

        this.setContent(gridPane);
        this.setText(localization.mp3File());
        this.setCollapsible(false);
        this.setExpanded(true);

    }

    private void handleIndexLabelChange(Label targetLabel, TaggableFile currentFile, List<TaggableFile> availableFiles) {
        StringBuilder targetLabelContent = new StringBuilder();
        int selectedIndex = currentFile == null || availableFiles == null ? -1 : availableFiles.indexOf(currentFile);
        if (selectedIndex > -1 && availableFiles != null) {
            targetLabelContent.append(selectedIndex + 1).append(" / ").append(availableFiles.size()).append(":");
        }
        Platform.runLater(() -> targetLabel.setText(targetLabelContent.toString()));
    }

}