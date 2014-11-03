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

import java.io.File;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TagImage;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.handlers.batchupdate.CopyPropertyValueActionEventHandler;

public class ImagesEditorTab extends Tab {

    private static final Logger log = LoggerFactory.getLogger(ImagesEditorTab.class);

    private final ListProperty<TagImage> images = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();
    private final ListProperty<TaggableFile> selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ImagesEditorTab(Localization localization) {

        ListView<TagImage> listView = new ListView<>();
        listView.setCellFactory(e -> new ImageListCell(localization));
        this.imagesProperty().addListener((Change<? extends TagImage> change) -> listView.itemsProperty().get().setAll(change.getList()));

        Button copyImagesButton = new Button(localization.copyImages());
        copyImagesButton.setGraphic(new ImageView(new Image(ImagesEditorTab.class.getClassLoader().getResourceAsStream("icons/16/copy.png"))));
        copyImagesButton.setOnAction(new CopyPropertyValueActionEventHandler<>(this.currentFileProperty(), this.selectedFilesProperty(), TaggableFile::imagesProperty, new CopyPropertyValueActionEventHandler.CopyImagesPropertyConsumer()));
        copyImagesButton.setTooltip(new Tooltip(localization.copyImagesToOtherFiles()));

        Button removeImagesButton = new Button(localization.clearImages());
        removeImagesButton.setGraphic(new ImageView(new Image(ImagesEditorTab.class.getClassLoader().getResourceAsStream("icons/16/delete.png"))));
        removeImagesButton.setTooltip(new Tooltip(localization.removeAllImages()));
        removeImagesButton.setOnAction(event -> this.imagesProperty().get().clear());
        removeImagesButton.setDisable(true);
        this.imagesProperty().addListener((Change<? extends TagImage> change) -> removeImagesButton.setDisable(change.getList() == null || change.getList().isEmpty()));

        Button addImageButton = new Button(localization.addImage());
        addImageButton.setGraphic(new ImageView(new Image(ImagesEditorTab.class.getClassLoader().getResourceAsStream("icons/16/add.png"))));
        addImageButton.setTooltip(new Tooltip(localization.addImage()));
        addImageButton.setOnAction(event -> {
            File tagFile = this.currentFileProperty().get().getFile();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(localization.openImage());
            fileChooser.setInitialDirectory(tagFile.getParentFile());
            File selectedFile = fileChooser.showOpenDialog(this.getContent().getScene().getWindow());
            if (selectedFile != null) {
                try {
                    TagImage newTagImage = new TagImage(selectedFile);
                    this.imagesProperty().add(newTagImage);
                } catch (Exception e) {
                    log.warn("Cannot read image from file: {}", selectedFile, e);
                }
            }
        });

        HBox buttonPane = new HBox(copyImagesButton, removeImagesButton, addImageButton);
        buttonPane.setSpacing(5);
        buttonPane.setAlignment(Pos.CENTER_RIGHT);
        buttonPane.setPadding(new Insets(5, 0, 0, 0));

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(listView);
        borderPane.setBottom(buttonPane);
        borderPane.setPadding(new Insets(5, 5, 5, 5));

        this.setText(localization.images());
        this.setContent(borderPane);
        this.setClosable(false);

    }

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    class ImageListCell extends ListCell<TagImage> {

        private Localization localization = null;

        ImageListCell(Localization localization) {
            this.setLocalization(localization);
        }

        @Override
        protected void updateItem(TagImage item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                ImagesImageEditorPane imagePane = new ImagesImageEditorPane(item, this.getLocalization());
                imagePane.setOnDeleteActionHandler(event -> ImagesEditorTab.this.imagesProperty().get().remove(item));
                imagePane.changedProperty().addListener((o, oldValue, newValue) -> {
                    if (newValue) {
                        item.changedProperty().set(true);
                    }
                });
                this.setGraphic(imagePane);
            } else {
                this.setGraphic(null);
            }
        }

        // ---------------------------------------------------------------------
        // --- Property access methods -----------------------------------------
        // ---------------------------------------------------------------------

        private Localization getLocalization() {
            return this.localization;
        }
        private void setLocalization(Localization localization) {
            this.localization = localization;
        }

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ListProperty<TagImage> imagesProperty() {
        return this.images;
    }

    public ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

    public ListProperty<TaggableFile> selectedFilesProperty() {
        return this.selectedFiles;
    }

}