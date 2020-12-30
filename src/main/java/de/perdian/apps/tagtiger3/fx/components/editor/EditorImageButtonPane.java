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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.tagtiger3.model.SongImage;
import de.perdian.apps.tagtiger3.model.SongImages;
import de.perdian.apps.tagtiger3.model.SongProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

class EditorImageButtonPane extends HBox {

    EditorImageButtonPane(EditorComponentBuilder componentBuilder) {

        Button addNewImagesButton = new Button("", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addNewImagesButton.setTooltip(new Tooltip("Add/replace image"));
        addNewImagesButton.disableProperty().bind(componentBuilder.getSelectionModel().focusFileProperty().isNull());
        addNewImagesButton.setOnAction(event -> {
            Property<SongImages> songImagesProperty = componentBuilder.getSelectionModel().focusFileProperty().getValue().getProperties().getValue(SongProperty.IMAGES, SongImages.class).getValue();
            SongImage songImage = new SongImage(null, null, null, null);
            songImagesProperty.setValue(songImagesProperty.getValue().withRemovedImages().withNewImage(songImage));
        });

        BooleanProperty imagesAvailableProperty = new SimpleBooleanProperty(false);
        Button clearImagesButton = new Button("", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        clearImagesButton.setTooltip(new Tooltip("Clear image"));
        clearImagesButton.disableProperty().bind(componentBuilder.getSelectionModel().focusFileProperty().isNull().or(imagesAvailableProperty.not()));
        clearImagesButton.setOnAction(event -> {
            Property<SongImages> songImagesProperty = componentBuilder.getSelectionModel().focusFileProperty().getValue().getProperties().getValue(SongProperty.IMAGES, SongImages.class).getValue();
            songImagesProperty.setValue(songImagesProperty.getValue().withRemovedImages());
        });
        ChangeListener<SongImages> songImagesChangeListener = (o, oldValue, newValue) -> imagesAvailableProperty.setValue(!newValue.getImages().isEmpty());
        componentBuilder.getSelectionModel().focusFileProperty().addListener((o, oldValue, newValue) -> {
            if (oldValue != null) {
                Property<SongImages> songImagesProperty = oldValue.getProperties().getValue(SongProperty.IMAGES, SongImages.class).getValue();
                songImagesProperty.removeListener(songImagesChangeListener);
                imagesAvailableProperty.setValue(false);
            }
            if (newValue != null) {
                Property<SongImages> songImagesProperty = newValue.getProperties().getValue(SongProperty.IMAGES, SongImages.class).getValue();
                songImagesProperty.addListener(songImagesChangeListener);
                imagesAvailableProperty.setValue(!songImagesProperty.getValue().getImages().isEmpty());
            }
        });

        Button copyToOtherSongsButton = componentBuilder.createCopyToOtherSongsButton(SongProperty.IMAGES);

        this.setSpacing(5);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(addNewImagesButton, clearImagesButton, copyToOtherSongsButton);

    }

}
