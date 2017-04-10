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

import java.util.Arrays;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.handlers.selection.ChangeCurrentFileEventHandler;
import de.perdian.apps.tagtiger.fx.panels.editor.components.CommonEditorTab;
import de.perdian.apps.tagtiger.fx.panels.editor.components.ImagesEditorTab;
import de.perdian.apps.tagtiger.fx.panels.editor.components.InformationEditorPane;
import de.perdian.apps.tagtiger.fx.util.EditorComponentFactory;

public class EditorPane extends VBox {

    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    public EditorPane(Localization localization) {

        EditorComponentFactory<TaggableFile> componentFactory = new EditorComponentFactory<>(this.currentFileProperty());
        componentFactory.addControlCustomizer(component -> component.addEventHandler(KeyEvent.KEY_PRESSED, new ChangeCurrentFileEventHandler<>(this.currentFileProperty(), this.availableFilesProperty(), new ChangeCurrentFileEventHandler.KeyEventDirectionFunction())));

        InformationEditorPane informationEditorPane = new InformationEditorPane(componentFactory, localization);
        informationEditorPane.currentFileProperty().bind(this.currentFileProperty());
        informationEditorPane.selectedFilesProperty().bind(this.selectedFilesProperty());
        informationEditorPane.availableFilesProperty().bind(this.availableFilesProperty());

        CommonEditorTab commonEditorTab = new CommonEditorTab(componentFactory, localization);
        commonEditorTab.currentFileProperty().bind(this.currentFileProperty());
        commonEditorTab.selectedFilesProperty().bind(this.selectedFilesProperty());

        ImagesEditorTab imagesEditorPane = new ImagesEditorTab(localization);
        imagesEditorPane.currentFileProperty().bind(this.currentFileProperty());
        imagesEditorPane.selectedFilesProperty().bind(this.selectedFilesProperty());

        TabPane taggingPane = new TabPane();
        taggingPane.getTabs().addAll(commonEditorTab, imagesEditorPane);
        taggingPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane taggingWrapperPane = new TitledPane(localization.tags(), taggingPane);
        taggingWrapperPane.setCollapsible(false);
        taggingWrapperPane.setMaxHeight(Double.MAX_VALUE);
        taggingWrapperPane.setDisable(true);
        VBox.setVgrow(taggingWrapperPane, Priority.ALWAYS);

        this.setSpacing(5);
        this.getChildren().addAll(informationEditorPane, taggingWrapperPane);

        this.currentFileProperty().addListener((o, oldValue, newValue) -> Arrays.asList(informationEditorPane, taggingWrapperPane).forEach(pane -> pane.setDisable(newValue == null)));
        this.currentFileProperty().addListener((o, oldValue, newValue) -> imagesEditorPane.imagesProperty().set(newValue == null ? null : newValue.imagesProperty().getValue().getTagImages()));

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