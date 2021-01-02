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
package de.perdian.apps.tagtiger3.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.images.Artwork;

public class SongImages implements Serializable {

    static final long serialVersionUID = 1L;

    private List<SongImage> images = null;

    SongImages(AudioFile audioFile) {
        List<Artwork> artworkList = audioFile.getTag() == null ? null : audioFile.getTag().getArtworkList();
        if (artworkList == null || artworkList.isEmpty()) {
            this.setImages(Collections.emptyList());
        } else {
            List<SongImage> images = new ArrayList<>(artworkList.size());
            for (Artwork artwork : artworkList) {
                images.add(new SongImage(artwork));
            }
            this.setImages(images);
        }
    }

    SongImages(List<SongImage> images) {
        this.setImages(Collections.unmodifiableList(images));
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof SongImages) {
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(this.getImages(), ((SongImages)that).getImages());
            return equalsBuilder.isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.getImages());
        return hashCodeBuilder.toHashCode();
    }

    List<Artwork> toArtworkList() {
        return this.getImages().stream()
            .map(image -> image.toArtwork())
            .collect(Collectors.toList());
    }

    public SongImages withRemovedImage(SongImage image) {
        int imageIndex = this.getImages().indexOf(image);
        if (imageIndex < 0) {
            return this;
        } else {
            return this.withRemovedImage(imageIndex);
        }
    }

    public SongImages withRemovedImage(int index) {
        if (index < 0 || index >= this.getImages().size()) {
            throw new IndexOutOfBoundsException(index);
        } else {
            List<SongImage> newImages = new ArrayList<>(this.getImages());
            newImages.remove(index);
            return new SongImages(newImages);
        }
    }

    public SongImages withRemovedImages() {
        return new SongImages(Collections.emptyList());
    }

    public SongImages withReplacedImage(int index, SongImage newImage) {
        if (index < 0 || index >= this.getImages().size()) {
            throw new IndexOutOfBoundsException(index);
        } else {
            List<SongImage> newImages = new ArrayList<>(this.getImages());
            newImages.set(index, newImage);
            return new SongImages(newImages);
        }
    }

    public SongImages withReplacedImage(SongImage oldImage, SongImage newImage) {
        int oldImageIndex = this.getImages().indexOf(oldImage);
        if (oldImageIndex < 0) {
            return this.withNewImage(newImage);
        } else {
            return this.withReplacedImage(oldImageIndex, newImage);
        }
    }

    public SongImages withNewImage(SongImage newImage) {
        List<SongImage> newImages = new ArrayList<>(this.getImages());
        newImages.add(newImage);
        return new SongImages(newImages);
    }

    public List<SongImage> getImages() {
        return this.images;
    }
    private void setImages(List<SongImage> images) {
        this.images = images;
    }

}
