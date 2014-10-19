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
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.components.EditorComponentFactory;

public class EditorPane extends VBox {

    private Consumer<TaggableFile> updateFileConsumer = file -> {};
    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> availableFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    public EditorPane(Localization localization) {

        EditorComponentFactory<TaggableFile> componentFactory = new EditorComponentFactory<>(this.currentFileProperty());
        componentFactory.addControlCustomizer(this::customizeTextFieldControl);

        EditorInformationPane informationPane = new EditorInformationPane(componentFactory, localization);
        informationPane.setPadding(new Insets(5, 5, 5, 5));
        informationPane.availableFilesProperty().bind(this.availableFilesProperty());
        informationPane.currentFileProperty().bind(this.currentFileProperty());
        TitledPane informationWrapperPane = new TitledPane(localization.mp3File(), informationPane);
        informationWrapperPane.setExpanded(true);
        informationWrapperPane.setDisable(true);

        EditorTaggingCommonPane taggingCommonPane = new EditorTaggingCommonPane(componentFactory, localization);
        taggingCommonPane.initialize();
        taggingCommonPane.setPadding(new Insets(5, 5, 5, 5));
        taggingCommonPane.currentFileProperty().bind(this.currentFileProperty());
        taggingCommonPane.selectedFilesProperty().bind(this.selectedFilesProperty());
        Tab taggingCommonTab = new Tab(localization.common());
        taggingCommonTab.setContent(taggingCommonPane);
        taggingCommonTab.setClosable(false);

        EditorTaggingImagesPane taggingImagesPane = new EditorTaggingImagesPane(localization);
        taggingImagesPane.setPadding(new Insets(5, 5, 5, 5));
        taggingImagesPane.currentFileProperty().bind(this.currentFileProperty());
        Tab imagesTab = new Tab(localization.images());
        imagesTab.setContent(taggingImagesPane);
        imagesTab.setClosable(false);

        TabPane taggingPane = new TabPane();
        taggingPane.getTabs().addAll(taggingCommonTab, imagesTab);
        taggingPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane taggingWrapperPane = new TitledPane(localization.tags(), taggingPane);
        taggingWrapperPane.setCollapsible(false);
        taggingWrapperPane.setMaxHeight(Double.MAX_VALUE);
        taggingWrapperPane.setDisable(true);
        VBox.setVgrow(taggingWrapperPane, Priority.ALWAYS);

        this.setSpacing(5);
        this.getChildren().addAll(informationWrapperPane, taggingWrapperPane);

        this.currentFileProperty().addListener((o, oldValue, newValue) -> Arrays.asList(informationWrapperPane, taggingWrapperPane).forEach(pane -> pane.setDisable(newValue == null)));
        this.currentFileProperty().addListener((o, oldValue, newValue) -> taggingImagesPane.imagesProperty().set(newValue == null ? null : newValue.imagesProperty().getValue().getTagImages()));

    }

    private void customizeTextFieldControl(Control control) {
        control.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.PAGE_UP) {
                this.handleTextFieldUpOrDown(-1, event.isShiftDown());
            } else if (event.getCode() == KeyCode.PAGE_DOWN) {
                this.handleTextFieldUpOrDown(1, event.isShiftDown());
            }
        });
    }

    private void handleTextFieldUpOrDown(int direction, boolean absolute) {
        List<TaggableFile> currentFiles = this.availableFilesProperty().get();
        int currentFileIndex = this.availableFilesProperty().indexOf(this.currentFileProperty().get());
        int newFileIndex = 0;
        if (direction < 0) {
            newFileIndex = absolute ? 0 : Math.max(0, currentFileIndex - 1);
        } else if (direction > 0) {
            newFileIndex = absolute ? currentFiles.size() - 1 : Math.min(currentFiles.size(), currentFileIndex + 1);
        }
        if (newFileIndex >= 0 && newFileIndex < currentFiles.size()) {
            this.getUpdateFileConsumer().accept(currentFiles.get(newFileIndex));
        }
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    public ListProperty<TaggableFile> availableFilesProperty() {
        return this.availableFiles;
    }

    public ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

    public Consumer<TaggableFile> getUpdateFileConsumer() {
        return this.updateFileConsumer;
    }
    public void setUpdateFileConsumer(Consumer<TaggableFile> updateFileConsumer) {
        this.updateFileConsumer = updateFileConsumer;
    }

}