/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.editor;

import java.io.ByteArrayInputStream;

import de.perdian.apps.tagtiger3.model.SongFile;
import de.perdian.apps.tagtiger3.model.SongImages;
import de.perdian.apps.tagtiger3.model.SongProperty;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

class EditorImagePane extends BorderPane {

    EditorImagePane(EditorComponentBuilder componentBuilder) {

        EditorImageButtonPane buttonPane = new EditorImageButtonPane(componentBuilder);
        buttonPane.setPadding(new Insets(5, 0, 0, 0));
        this.setBottom(buttonPane);

        this.updateImage(componentBuilder.getSelectionModel().focusFileProperty(), null);

        ChangeListener<SongImages> songImagesChangeListener = (o, oldValue, newValue) -> this.updateImage(componentBuilder.getSelectionModel().focusFileProperty(), newValue);
        componentBuilder.getSelectionModel().focusFileProperty().addListener((o, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.getProperties().getValue(SongProperty.IMAGES, SongImages.class).getValue().removeListener(songImagesChangeListener);
                this.updateImage(componentBuilder.getSelectionModel().focusFileProperty(), null);
            }
            if (newValue != null) {
                Property<SongImages> imagesProperty = newValue.getProperties().getValue(SongProperty.IMAGES, SongImages.class).getValue();
                imagesProperty.addListener(songImagesChangeListener);
                this.updateImage(componentBuilder.getSelectionModel().focusFileProperty(), imagesProperty.getValue());
            }
        });

        this.setMinWidth(190);
        this.setMaxWidth(190);

    }

    private void updateImage(Property<SongFile> focusFileProperty, SongImages images) {
        Platform.runLater(() -> {
            this.setTop(null);
            if (images != null) {
                if (images.getImages().isEmpty()) {
                    Label noImagesLabel = new Label("No image");
                    noImagesLabel.setPadding(new Insets(5, 0, 0, 0));
                    noImagesLabel.setAlignment(Pos.CENTER);
                    noImagesLabel.setMaxWidth(Double.MAX_VALUE);
                    this.setTop(noImagesLabel);
                } else {

                    ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(images.getImages().get(0).getBytes()), 180, 180, true, true));
                    imageView.setPreserveRatio(true);
                    imageView.fitWidthProperty().bind(this.widthProperty());

                    Label imageLabel = new Label("", imageView);
                    imageLabel.setAlignment(Pos.TOP_CENTER);
                    this.setTop(imageLabel);

                }
            }
        });
    }

}
