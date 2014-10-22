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
package de.perdian.apps.tagtiger.fx.panels.editor;

import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.panels.editor.groupactions.CopyValuesGroupAction;
import de.perdian.apps.tagtiger.fx.util.EditorComponentFactory;

class EditorInformationPane extends GridPane {

    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    EditorInformationPane(EditorComponentFactory<TaggableFile> componentFactory, Localization localization) {

        Label indexLabel = new Label();
        indexLabel.setMinWidth(50);
        indexLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        indexLabel.setPadding(new Insets(0, 5, 0, 0));

        this.currentFileProperty().addListener((o, oldValue, newValue) -> this.handleIndexLabelChange(indexLabel, newValue, this.availableFilesProperty().get()));
        this.availableFilesProperty().addListener((o, oldValue, newValue) -> this.handleIndexLabelChange(indexLabel, this.currentFileProperty().get(), newValue));

        TextField fileNameField = componentFactory.createTextField(TaggableFile::fileNameProperty);
        fileNameField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(fileNameField, Priority.ALWAYS);

        Label fileExtensionLabel = new Label(".");
        fileExtensionLabel.setPadding(new Insets(0, 2, 0, 2));
        TextField fileExtensionField = componentFactory.createTextField(TaggableFile::fileExtensionProperty);
        Pane fileExtensionFieldWrapper = EditorPaneHelper.createControlWrapper(fileExtensionField, new CopyValuesGroupAction<>("icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), TaggableFile::fileExtensionProperty), this.currentFileProperty(), this.selectedFilesProperty());
        fileExtensionFieldWrapper.setPrefWidth(90);
        GridPane.setHgrow(fileExtensionFieldWrapper, Priority.NEVER);

        this.add(indexLabel, 0, 1);
        this.add(new Label(localization.fileName()), 4, 0);
        this.add(fileNameField, 4, 1);
        this.add(fileExtensionLabel, 5, 1);
        this.add(new Label(localization.fileExtension()), 6, 0);
        this.add(fileExtensionFieldWrapper, 6, 1);

    }

    private void handleIndexLabelChange(Label targetLabel, TaggableFile currentFile, List<TaggableFile> availableFiles) {
        StringBuilder targetLabelContent = new StringBuilder();
        int selectedIndex = currentFile == null || availableFiles == null ? -1 : availableFiles.indexOf(currentFile);
        if (selectedIndex > -1 && availableFiles != null) {
            targetLabelContent.append(selectedIndex + 1).append(" / ").append(availableFiles.size()).append(":");
        }
        Platform.runLater(() -> targetLabel.setText(targetLabelContent.toString()));
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    ListProperty<TaggableFile> availableFilesProperty() {
        return this.availableFiles;
    }

    ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

}