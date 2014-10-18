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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TagImage;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;

class EditorTaggingImagesPane extends BorderPane {

    private static final Logger log = LoggerFactory.getLogger(EditorTaggingImagesPane.class);

    private final ListProperty<TagImage> images = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<TaggableFile> currentFile = new SimpleObjectProperty<>();

    EditorTaggingImagesPane(Localization localization) {

        ListView<TagImage> listView = new ListView<>();
        listView.setCellFactory(e -> new ImageListCell(localization));
        this.imagesProperty().addListener((Change<? extends TagImage> change) -> listView.itemsProperty().get().setAll(change.getList()));

        Button removeImagesButton = new Button(localization.clearImages());
        removeImagesButton.setDisable(true);
        removeImagesButton.setGraphic(new ImageView(new Image(EditorTaggingImagesPane.class.getClassLoader().getResourceAsStream("icons/16/delete.png"))));
        removeImagesButton.setOnAction(event -> this.imagesProperty().get().clear() );
        this.imagesProperty().addListener((Change<? extends TagImage> change) -> removeImagesButton.setDisable(change.getList() == null || change.getList().isEmpty()));

        Button addImageButton = new Button(localization.addImage());
        addImageButton.setGraphic(new ImageView(new Image(EditorTaggingImagesPane.class.getClassLoader().getResourceAsStream("icons/16/add.png"))));
        addImageButton.setOnAction(event -> {
            File tagFile = this.currentFileProperty().get().getFile();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(localization.openImage());
            fileChooser.setInitialDirectory(tagFile.getParentFile());
            File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (selectedFile != null) {
                try {
                    TagImage newTagImage = new TagImage(selectedFile);
                    this.imagesProperty().add(newTagImage);
                } catch (Exception e) {
                    log.warn("Cannot read image from file: {}", selectedFile, e);
                }
            }
        });

        HBox buttonPane = new HBox(removeImagesButton, addImageButton);
        buttonPane.setSpacing(5);
        buttonPane.setAlignment(Pos.CENTER_RIGHT);
        buttonPane.setPadding(new Insets(5, 0, 0, 0));

        this.setCenter(listView);
        this.setBottom(buttonPane);

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
                EditorTaggingImagePane imagePane = new EditorTaggingImagePane(item, this.getLocalization());
                imagePane.setOnDeleteActionHandler(event -> EditorTaggingImagesPane.this.imagesProperty().get().remove(item));
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

    ListProperty<TagImage> imagesProperty() {
        return this.images;
    }

    ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFile;
    }

}