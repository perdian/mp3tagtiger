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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.reference.PictureTypes;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Represents an image that is saved within a tag
 *
 * @author Christian Robert
 */

public class TagImage {

    private Artwork artwork = null;
    private final List<ChangeListener<Object>> changeListeners = new CopyOnWriteArrayList<>();
    private final Property<Image> image = new SimpleObjectProperty<>();
    private final Property<Integer> imageSize = new SimpleObjectProperty<>();
    private final Property<String> pictureType = new SimpleStringProperty();
    private final Property<String> description = new SimpleStringProperty();

    public TagImage(TagImage sourceImage) {
        this.imageProperty().setValue(sourceImage.imageProperty().getValue());
        this.imageSizeProperty().setValue(sourceImage.imageSizeProperty().getValue());
        this.pictureTypeProperty().setValue(sourceImage.pictureTypeProperty().getValue());
        this.descriptionProperty().setValue(sourceImage.descriptionProperty().getValue());
        this.registerListeners();
    }

    public TagImage(Artwork artwork) throws IOException {
        this.setArtwork(artwork);
        this.imageProperty().setValue(new Image(new ByteArrayInputStream(artwork.getBinaryData())));
        this.imageSizeProperty().setValue(artwork.getBinaryData().length);
        this.pictureTypeProperty().setValue(PictureTypes.getInstanceOf().getValueForId(artwork.getPictureType()));
        this.descriptionProperty().setValue(artwork.getDescription());
        this.registerListeners();
    }

    public TagImage(File imageFile) throws Exception {
        try (InputStream imageFileStream = new BufferedInputStream(new FileInputStream(imageFile))) {
            Image image = new Image(imageFileStream);
            this.imageProperty().setValue(image);
            this.imageSizeProperty().setValue((int)imageFile.length());
            this.descriptionProperty().setValue(imageFile.getName());
            this.pictureTypeProperty().setValue(PictureTypes.DEFAULT_VALUE);
            this.registerListeners();
        }
    }

    private void registerListeners() {
        ChangeListener<Object> delegatingChangeListener = (o, oldValue, newValue) -> this.getChangeListeners().forEach(listener -> listener.changed(o, oldValue, newValue));
        this.imageProperty().addListener(delegatingChangeListener);
        this.pictureTypeProperty().addListener(delegatingChangeListener);
        this.descriptionProperty().addListener(delegatingChangeListener);
    }

    Artwork toArtwork() {
        if (this.imageProperty().getValue() == null) {
            return null;
        } else {

            Integer pictureTypeId = PictureTypes.getInstanceOf().getIdForValue(this.pictureTypeProperty().getValue());
            Artwork artwork = this.getArtwork() == null ? new Artwork() : this.getArtwork();
            artwork.setBinaryData(this.toPngBytes());
            artwork.setDescription(this.descriptionProperty().getValue());
            artwork.setMimeType("image/png");
            artwork.setPictureType(pictureTypeId == null ? PictureTypes.DEFAULT_ID : pictureTypeId.intValue());
            return artwork;

        }
    }

    byte[] toPngBytes() {
        try {
            if (this.imageProperty().getValue() == null) {
                return null;
            } else {
                BufferedImage awtImage = SwingFXUtils.fromFXImage(this.imageProperty().getValue(), null);
                ByteArrayOutputStream imageOutStream = new ByteArrayOutputStream();
                ImageIO.write(awtImage, "png", imageOutStream);
                return imageOutStream.toByteArray();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot encode image as PNG", e);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName());
        result.append("[description=").append(this.descriptionProperty().getValue());
        return result.append("]").toString();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.imageProperty().getValue());
        hashCodeBuilder.append(this.imageSizeProperty().getValue());
        hashCodeBuilder.append(this.pictureTypeProperty().getValue());
        hashCodeBuilder.append(this.descriptionProperty().getValue());
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof TagImage) {
            TagImage thatImage = (TagImage)that;
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(this.imageSizeProperty().getValue(), thatImage.imageSizeProperty().getValue());
            equalsBuilder.append(this.pictureTypeProperty().getValue(), thatImage.pictureTypeProperty().getValue());
            equalsBuilder.append(this.descriptionProperty().getValue(), thatImage.descriptionProperty().getValue());
            return equalsBuilder.isEquals() && this.equalsImageContent(thatImage);
        } else {
            return false;
        }
    }

    private boolean equalsImageContent(TagImage thatImage) {
        byte[] thisBytes = this.toPngBytes();
        byte[] thatBytes = thatImage.toPngBytes();
        if (thisBytes == null && thatBytes == null) {
            return true;
        } else if (thisBytes == null || thatBytes == null) {
            return false;
        } else if (thisBytes.length != thatBytes.length) {
            return false;
        } else {
            return Arrays.equals(thisBytes, thatBytes);
        }
    }

    Artwork getArtwork() {
        return this.artwork;
    }
    private void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    public Property<Image> imageProperty() {
        return this.image;
    }

    public Property<Integer> imageSizeProperty() {
        return this.imageSize;
    }

    public Property<String> pictureTypeProperty() {
        return this.pictureType;
    }

    public Property<String> descriptionProperty() {
        return this.description;
    }

    public void addChangeListener(ChangeListener<Object> changeListener) {
        this.getChangeListeners().add(changeListener);
    }
    public void removeChangeListener(ChangeListener<Object> changeListener) {
        this.getChangeListeners().remove(changeListener);
    }
    private List<ChangeListener<Object>> getChangeListeners() {
        return this.changeListeners;
    }

}