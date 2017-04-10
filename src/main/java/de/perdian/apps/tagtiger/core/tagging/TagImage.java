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
package de.perdian.apps.tagtiger.core.tagging;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.reference.PictureTypes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Represents an image that is saved within a tag
 *
 * @author Christian Robert
 */

public class TagImage {

    private Artwork artwork = null;
    private final Property<Image> image = new SimpleObjectProperty<>();
    private final IntegerProperty imageSize = new SimpleIntegerProperty();
    private final StringProperty pictureType = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final BooleanProperty changed = new SimpleBooleanProperty();

    public TagImage() {
        this.pictureTypeProperty().set(PictureTypes.DEFAULT_VALUE);
    }

    public TagImage(Artwork artwork) throws IOException {
        this.setArtwork(artwork);
        this.imageProperty().setValue(new Image(new ByteArrayInputStream(artwork.getBinaryData())));
        this.imageSizeProperty().set(artwork.getBinaryData().length);
        this.pictureTypeProperty().set(PictureTypes.getInstanceOf().getValueForId(artwork.getPictureType()));
        this.descriptionProperty().set(artwork.getDescription());
        this.registerListeners();
    }

    public TagImage(File imageFile) throws Exception {
        try (InputStream imageFileStream = new BufferedInputStream(new FileInputStream(imageFile))) {
            Image image = new Image(imageFileStream);
            this.imageProperty().setValue(image);
            this.imageSizeProperty().set((int)imageFile.length());
            this.descriptionProperty().set(imageFile.getName());
            this.pictureTypeProperty().set(PictureTypes.DEFAULT_VALUE);
            this.registerListeners();
        }
    }
    private void registerListeners() {
        this.imageProperty().addListener((o, oldValue, newValue) -> this.changedProperty().set(true));
        this.pictureTypeProperty().addListener((o, oldValue, newValue) -> this.changedProperty().set(true));
        this.descriptionProperty().addListener((o, oldValue, newValue) -> this.changedProperty().set(true));
    }

    public Artwork toArtwork() throws IOException {
        if (this.imageProperty().getValue() == null) {
            return null;
        } else {

            BufferedImage awtImage = SwingFXUtils.fromFXImage(this.imageProperty().getValue(), null);
            ByteArrayOutputStream imageOutStream = new ByteArrayOutputStream();
            ImageIO.write(awtImage, "png", imageOutStream);

            Integer pictureTypeId = PictureTypes.getInstanceOf().getIdForValue(this.pictureTypeProperty().get());
            Artwork artwork = this.getArtwork() == null ? new Artwork() : this.getArtwork();
            artwork.setBinaryData(imageOutStream.toByteArray());
            artwork.setDescription(this.descriptionProperty().get());
            artwork.setMimeType("image/png");
            artwork.setPictureType(pictureTypeId == null ? PictureTypes.DEFAULT_ID : pictureTypeId.intValue());
            return artwork;

        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName());
        result.append("[description=").append(this.descriptionProperty().get());
        return result.append("]").toString();
    }

    public Artwork getArtwork() {
        return this.artwork;
    }
    private void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    public Property<Image> imageProperty() {
        return this.image;
    }

    public IntegerProperty imageSizeProperty() {
        return this.imageSize;
    }

    public StringProperty pictureTypeProperty() {
        return this.pictureType;
    }

    public StringProperty descriptionProperty() {
        return this.description;
    }

    public BooleanProperty changedProperty() {
        return this.changed;
    }

}