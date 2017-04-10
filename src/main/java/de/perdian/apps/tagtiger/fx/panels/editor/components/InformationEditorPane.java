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
package de.perdian.apps.tagtiger.fx.panels.editor.components;

import java.util.List;

import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.handlers.batchupdate.CopyPropertyValueActionEventHandler;
import de.perdian.apps.tagtiger.fx.util.EditorComponentFactory;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class InformationEditorPane extends TitledPane {

    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    public InformationEditorPane(EditorComponentFactory<TaggableFile> componentFactory, Localization localization) {

        Label indexLabel = new Label();
        indexLabel.setMinWidth(50);
        indexLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        indexLabel.setPadding(new Insets(0, 5, 0, 0));

        this.currentFileProperty().addListener((o, oldValue, newValue) -> this.handleIndexLabelChange(indexLabel, newValue, this.availableFilesProperty().get()));
        this.availableFilesProperty().addListener((o, oldValue, newValue) -> this.handleIndexLabelChange(indexLabel, this.currentFileProperty().get(), newValue));

        TextField fileNameField = componentFactory.createTextField(TaggableFile::fileNameProperty);
        fileNameField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(fileNameField, Priority.ALWAYS);

        BooleanProperty disableProperty = new SimpleBooleanProperty(true);
        this.selectedFilesProperty().addListener((Change<? extends TaggableFile> change) -> disableProperty.setValue(change.getList().size() <= 1));

        Label fileExtensionLabel = new Label(".");
        fileExtensionLabel.setPadding(new Insets(0, 2, 0, 2));
        Pane fileExtensionFieldWrapper = ComponentBuilder.create()
            .control(componentFactory.createTextField(TaggableFile::fileExtensionProperty))
            .disableProperty(disableProperty)
            .button(true, "icons/16/copy.png", localization.copyToAllOtherSelectedFiles(), new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::fileExtensionProperty, null))
            .buildControlPane();
        fileExtensionFieldWrapper.setPrefWidth(90);
        GridPane.setHgrow(fileExtensionFieldWrapper, Priority.NEVER);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.add(indexLabel, 0, 1);
        gridPane.add(new Label(localization.fileName()), 4, 0);
        gridPane.add(fileNameField, 4, 1);
        gridPane.add(fileExtensionLabel, 5, 1);
        gridPane.add(new Label(localization.fileExtension()), 6, 0);
        gridPane.add(fileExtensionFieldWrapper, 6, 1);

        this.setContent(gridPane);
        this.setText(localization.mp3File());
        this.setExpanded(true);
        this.setExpanded(true);
        this.setDisable(true);

    }

    private void handleIndexLabelChange(Label targetLabel, TaggableFile currentFile, List<TaggableFile> availableFiles) {
        StringBuilder targetLabelContent = new StringBuilder();
        int selectedIndex = currentFile == null || availableFiles == null ? -1 : availableFiles.indexOf(currentFile);
        if (selectedIndex > -1 && availableFiles != null) {
            targetLabelContent.append(selectedIndex + 1).append(" / ").append(availableFiles.size()).append(":");
        }
        Platform.runLater(() -> targetLabel.setText(targetLabelContent.toString()));
    }

    public ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    public ListProperty<TaggableFile> availableFilesProperty() {
        return this.availableFiles;
    }

    public ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

}