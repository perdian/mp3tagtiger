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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TagImage;
import de.perdian.apps.tagtiger.business.framework.tagging.TagImageList;

class EditorTaggingImagesPane extends BorderPane {

    private final ObjectProperty<TagImageList> images = new SimpleObjectProperty<>();

    EditorTaggingImagesPane(Localization localization) {

        ListView<TagImage> listView = new ListView<>();
        listView.setCellFactory(e -> new ImageListCell(localization));
        this.imagesProperty().addListener((o, oldValue, newValue) -> listView.setItems(newValue == null ? null : newValue.getTagImages()));

        HBox buttonPane = new HBox(new Button("DUMMY"));
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
                imagePane.setOnDeleteActionHandler(event -> EditorTaggingImagesPane.this.imagesProperty().get().getTagImages().remove(item));
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

    ObjectProperty<TagImageList> imagesProperty() {
        return this.images;
    }

}